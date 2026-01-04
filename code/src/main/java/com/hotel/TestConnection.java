package com.hotel;

import com.hotel.exception.DatabaseException;
import com.hotel.util.ConfigReader;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Classe de test pour vérifier la connexion à la base de données et le logger.
 * Peut être exécutée directement pour tester l'infrastructure.
 */
public class TestConnection {
    private static final Logger logger = Logger.getLogger(TestConnection.class);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST DE CONNEXION ET LOGGER");
        System.out.println("========================================\n");

        // Test 1: Logger
        testLogger();

        // Test 2: ConfigReader
        testConfigReader();

        // Test 3: DatabaseConnection
        testDatabaseConnection();

        System.out.println("========================================");
        System.out.println("  TESTS TERMINÉS");
        System.out.println("========================================");
    }

    private static void testLogger() {
        System.out.println("1. TEST DU LOGGER");
        System.out.println("-------------------");
        try {
            logger.debug("Message de debug");
            logger.info("Message d'information");
            logger.warn("Message d'avertissement");
            logger.error("Message d'erreur");
            logger.error("Message d'erreur avec exception", new Exception("Exception de test"));
            System.out.println("✓ Logger fonctionne correctement\n");
        } catch (Exception e) {
            System.err.println("✗ Erreur avec le logger: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    private static void testConfigReader() {
        System.out.println("2. TEST DE CONFIGREADER");
        System.out.println("------------------------");
        try {
            ConfigReader config = ConfigReader.getInstance();
            
            String dbUrl = config.getProperty("db.url");
            String dbUsername = config.getProperty("db.username");
            String dbPassword = config.getProperty("db.password");
            String dbDriver = config.getProperty("db.driver");
            
            System.out.println("URL: " + dbUrl);
            System.out.println("Username: " + dbUsername);
            System.out.println("Password: " + (dbPassword != null && !dbPassword.isEmpty() ? "***" : "(vide)"));
            System.out.println("Driver: " + dbDriver);
            
            int minPoolSize = config.getIntProperty("db.pool.minSize", 5);
            int maxPoolSize = config.getIntProperty("db.pool.maxSize", 20);
            System.out.println("Pool min: " + minPoolSize + ", max: " + maxPoolSize);
            
            System.out.println("✓ ConfigReader fonctionne correctement\n");
        } catch (Exception e) {
            System.err.println("✗ Erreur avec ConfigReader: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    private static void testDatabaseConnection() {
        System.out.println("3. TEST DE DATABASECONNECTION");
        System.out.println("-------------------------------");
        try {
            // Test de l'instance
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            System.out.println("✓ Instance DatabaseConnection créée");
            
            // Test de l'état du pool
            boolean isActive = dbConnection.isActive();
            System.out.println("✓ Pool actif: " + isActive);
            
            // Test d'obtention d'une connexion
            System.out.println("Tentative de connexion à la base de données...");
            Connection connection = dbConnection.getConnection();
            System.out.println("✓ Connexion obtenue avec succès");
            
            // Test d'une requête simple
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT 1 as test, DATABASE() as db_name");
            
            if (resultSet.next()) {
                int testValue = resultSet.getInt("test");
                String dbName = resultSet.getString("db_name");
                System.out.println("✓ Requête SQL exécutée avec succès");
                System.out.println("  - Test value: " + testValue);
                System.out.println("  - Base de données: " + dbName);
            }
            
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("✓ Connexion fermée proprement");
            
            // Fermeture du pool
            dbConnection.close();
            System.out.println("✓ Pool de connexions fermé");
            System.out.println("✓ DatabaseConnection fonctionne correctement\n");
            
        } catch (DatabaseException e) {
            System.err.println("✗ Erreur DatabaseException: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause: " + e.getCause().getMessage());
            }
            System.out.println("\nVérifiez:");
            System.out.println("  1. Que MySQL est démarré");
            System.out.println("  2. Que la base de données 'gestion_hotel' existe");
            System.out.println("  3. Les credentials dans database.properties sont corrects");
            System.out.println();
        } catch (Exception e) {
            System.err.println("✗ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }
}

