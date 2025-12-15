package com.hotel.view;

import com.hotel.controller.LoginController;
import com.hotel.util.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Vue de connexion pour l'authentification des utilisateurs.
 */
public class LoginView extends VBox {
    private static final Logger logger = Logger.getLogger(LoginView.class);
    
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label errorLabel;
    private LoginController controller;
    private Stage primaryStage;

    public LoginView() {
        this.controller = new LoginController();
        initializeComponents();
        setupLayout();
        setupStyles();
        setupEventHandlers();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        controller.setPrimaryStage(stage);
    }

    private void initializeComponents() {
        usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        
        loginButton = new Button("Se connecter");
        loginButton.setDefaultButton(true);
        
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
    }

    private void setupLayout() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        setPrefWidth(400);
        setPrefHeight(500);

        // Titre
        Label titleLabel = new Label("Système de Gestion d'Hôtel");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        Label subtitleLabel = new Label("Connexion");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setTextFill(Color.web("#7f8c8d"));

        // Formulaire
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(20));
        formBox.setMaxWidth(350);

        Label usernameLabel = new Label("Nom d'utilisateur:");
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        usernameField.setPrefHeight(35);
        passwordField.setPrefHeight(35);
        loginButton.setPrefHeight(40);
        loginButton.setPrefWidth(Double.MAX_VALUE);

        formBox.getChildren().addAll(
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            errorLabel,
            loginButton
        );

        getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Separator(),
            formBox
        );
    }

    private void setupStyles() {
        setStyle("-fx-background-color: #ecf0f1;");
        
        usernameField.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 5;"
        );
        
        passwordField.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 5;"
        );
        
        loginButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        loginButton.setOnMouseEntered(e -> 
            loginButton.setStyle(
                "-fx-background-color: #2980b9; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        loginButton.setOnMouseExited(e -> 
            loginButton.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
    }

    private void setupEventHandlers() {
        loginButton.setOnAction(e -> handleLogin());
        
        passwordField.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            boolean success = controller.authentifier(username, password);
            
            if (success) {
                errorLabel.setVisible(false);
                logger.info("Connexion réussie pour: " + username);
                controller.redirigerVersDashboard();
            } else {
                showError("Nom d'utilisateur ou mot de passe incorrect");
                passwordField.clear();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion", e);
            showError("Erreur lors de la connexion: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}


