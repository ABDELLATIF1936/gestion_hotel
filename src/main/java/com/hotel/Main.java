package com.hotel;

import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée principal de l'application de gestion d'hôtel.
 */
public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialisation de la connexion à la base de données
            DatabaseConnection.getInstance();
            logger.info("Application démarrée avec succès");

            // Configuration automatique de la base de données
            try {
                com.hotel.util.DatabaseSetup.setupDatabase();
            } catch (Exception e) {
                logger.warn("Erreur lors de la configuration automatique (peut être normal si déjà configuré): " + e.getMessage());
            }

            // Création de la vue de connexion
            com.hotel.view.LoginView loginView = new com.hotel.view.LoginView();
            loginView.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(loginView, 600, 500);
            try {
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            } catch (Exception e) {
                logger.warn("Impossible de charger le fichier CSS", e);
            }
            
            primaryStage.setTitle("Connexion - Système de Gestion d'Hôtel");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(500);
            primaryStage.setMinHeight(400);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
            logger.info("Interface de connexion chargée avec succès");
            
        } catch (Exception e) {
            logger.error("Erreur lors du démarrage de l'application", e);
            e.printStackTrace();
            
            // Afficher une alerte en cas d'erreur
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du démarrage");
            alert.setContentText("Impossible de démarrer l'application: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void stop() {
        // Fermeture de la connexion à la base de données
        DatabaseConnection.getInstance().close();
        logger.info("Application arrêtée");
    }

    /**
     * Point d'entrée principal.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}

