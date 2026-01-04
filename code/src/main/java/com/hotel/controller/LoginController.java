package com.hotel.controller;

import com.hotel.dao.interfaces.IUtilisateurDAO;
import com.hotel.factory.DAOFactory;
import com.hotel.model.Utilisateur;
import com.hotel.security.AuthenticationService;
import com.hotel.util.Logger;
import com.hotel.view.MainView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Contrôleur pour la page de connexion.
 */
public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class);
    private final IUtilisateurDAO utilisateurDAO;
    private Stage primaryStage;

    public LoginController() {
        this.utilisateurDAO = DAOFactory.getUtilisateurDAO();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Authentifie un utilisateur.
     *
     * @param username le nom d'utilisateur
     * @param password le mot de passe
     * @return true si l'authentification réussit
     */
    public boolean authentifier(String username, String password) {
        try {
            return AuthenticationService.authentifier(username, password, utilisateurDAO);
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification", e);
            return false;
        }
    }

    /**
     * Redirige vers le dashboard approprié selon le rôle de l'utilisateur.
     */
    public void redirigerVersDashboard() {
        if (primaryStage == null) {
            logger.error("PrimaryStage n'est pas défini");
            return;
        }

        Utilisateur utilisateur = AuthenticationService.getUtilisateurConnecte();
        if (utilisateur == null) {
            logger.error("Aucun utilisateur connecté");
            return;
        }

        try {
            MainView mainView = new MainView();
            
            Scene scene = new Scene(mainView, 1200, 800);
            try {
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            } catch (Exception e) {
                logger.warn("Impossible de charger le fichier CSS", e);
            }
            
            primaryStage.setTitle("Système de Gestion d'Hôtel - " + utilisateur.getRole().getLibelle());
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
            logger.info("Redirection vers le dashboard pour: " + utilisateur.getUsername() + " (Rôle: " + utilisateur.getRole() + ")");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la redirection vers le dashboard", e);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du chargement");
            alert.setContentText("Impossible de charger l'interface: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Déconnecte l'utilisateur actuel.
     */
    public void deconnecter() {
        AuthenticationService.deconnecter();
        logger.info("Utilisateur déconnecté");
    }
}


