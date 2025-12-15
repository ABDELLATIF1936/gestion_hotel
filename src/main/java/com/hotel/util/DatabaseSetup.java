package com.hotel.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire pour exécuter automatiquement les scripts SQL de configuration.
 */
public class DatabaseSetup {
    private static final Logger logger = Logger.getLogger(DatabaseSetup.class);

    /**
     * Configure la base de données (authentification + mises à jour du schéma).
     */
    public static void setupDatabase() {
        setupAuthentication();
        updateChambreStatutEnum();
    }

    /**
     * Met à jour l'ENUM du statut de la chambre pour inclure RESERVEE.
     */
    public static void updateChambreStatutEnum() {
        try {
            logger.info("Mise à jour de l'ENUM du statut de la chambre...");
            
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (Statement statement = connection.createStatement()) {
                // Vérifier si la colonne existe et mettre à jour l'ENUM
                String sql = "ALTER TABLE chambre " +
                            "MODIFY COLUMN statut ENUM('DISPONIBLE', 'OCCUPEE', 'HORS_SERVICE', 'RESERVEE') " +
                            "NOT NULL DEFAULT 'DISPONIBLE'";
                
                try {
                    statement.execute(sql);
                    connection.commit();
                    logger.info("ENUM du statut de la chambre mis à jour avec succès!");
                } catch (Exception e) {
                    // Ignorer si la colonne n'existe pas encore ou si elle a déjà la bonne valeur
                    if (e.getMessage() != null && 
                        (e.getMessage().contains("Unknown column") || 
                         e.getMessage().contains("Duplicate") ||
                         e.getMessage().contains("already exists"))) {
                        logger.debug("L'ENUM du statut est déjà à jour ou la table n'existe pas encore");
                    } else {
                        logger.warn("Erreur lors de la mise à jour de l'ENUM: " + e.getMessage());
                    }
                    connection.rollback();
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'ENUM du statut", e);
        }
    }

    /**
     * Exécute le script SQL de configuration de l'authentification.
     */
    public static void setupAuthentication() {
        try {
            logger.info("Démarrage de la configuration automatique de l'authentification...");
            
            // Charger le script SQL
            InputStream inputStream = DatabaseSetup.class.getResourceAsStream("/sql/setup_auth.sql");
            if (inputStream == null) {
                logger.error("Impossible de charger le script setup_auth.sql");
                return;
            }

            // Lire le script
            List<String> statements = readSQLScript(inputStream);
            
            // Exécuter les requêtes
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try (Statement statement = connection.createStatement()) {
                for (String sql : statements) {
                    if (!sql.trim().isEmpty() && !sql.trim().startsWith("--")) {
                        try {
                            statement.execute(sql);
                            logger.debug("Requête exécutée: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                        } catch (Exception e) {
                            // Ignorer les erreurs de "table already exists" ou "duplicate key"
                            if (!e.getMessage().contains("already exists") && 
                                !e.getMessage().contains("Duplicate entry")) {
                                logger.warn("Erreur lors de l'exécution d'une requête: " + e.getMessage());
                            }
                        }
                    }
                }
                connection.commit();
                logger.info("Configuration de l'authentification terminée avec succès!");
            } catch (Exception e) {
                connection.rollback();
                logger.error("Erreur lors de l'exécution du script SQL", e);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la configuration de l'authentification", e);
        }
    }

    /**
     * Lit un script SQL et le divise en instructions individuelles.
     */
    private static List<String> readSQLScript(InputStream inputStream) throws Exception {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignorer les lignes vides et les commentaires
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                currentStatement.append(line).append(" ");
                
                // Si la ligne se termine par un point-virgule, c'est la fin d'une instruction
                if (line.endsWith(";")) {
                    String statement = currentStatement.toString().trim();
                    if (!statement.isEmpty()) {
                        statements.add(statement);
                    }
                    currentStatement.setLength(0);
                }
            }
            
            // Ajouter la dernière instruction si elle n'a pas de point-virgule
            String lastStatement = currentStatement.toString().trim();
            if (!lastStatement.isEmpty()) {
                statements.add(lastStatement);
            }
        }
        
        return statements;
    }
}

