package com.hotel.view;

import com.hotel.controller.UtilisateurController;
import com.hotel.security.AuthenticationService;
import com.hotel.model.Utilisateur;
import com.hotel.util.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Vue pour les paramètres utilisateur (modification du nom et du mot de passe).
 */
public class ParametresView extends VBox {
    private static final Logger logger = Logger.getLogger(ParametresView.class);
    
    private UtilisateurController controller;
    private Utilisateur utilisateurConnecte;
    
    // Composants du formulaire
    private TextField txtUsername;
    private PasswordField txtCurrentPassword;
    private PasswordField txtNewPassword;
    private PasswordField txtConfirmPassword;
    
    // Boutons
    private Button btnUpdateUsername;
    private Button btnUpdatePassword;
    
    // Labels de statut
    private Label lblStatus;
    private Label lblInfo;

    public ParametresView() {
        try {
            controller = new UtilisateurController();
            utilisateurConnecte = AuthenticationService.getUtilisateurConnecte();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadUserInfo();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de ParametresView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        // Champs de formulaire
        txtUsername = new TextField();
        txtUsername.setPromptText("Nom d'utilisateur (3-50 caractères)");
        
        txtCurrentPassword = new PasswordField();
        txtCurrentPassword.setPromptText("Mot de passe actuel");
        
        txtNewPassword = new PasswordField();
        txtNewPassword.setPromptText("Nouveau mot de passe (min 8 caractères, majuscule, minuscule, chiffre)");
        
        txtConfirmPassword = new PasswordField();
        txtConfirmPassword.setPromptText("Confirmer le nouveau mot de passe");
        
        // Boutons
        btnUpdateUsername = new Button("💾 Modifier le nom d'utilisateur");
        btnUpdatePassword = new Button("🔒 Modifier le mot de passe");
        
        // Labels de statut
        lblStatus = new Label();
        lblStatus.setWrapText(true);
        
        lblInfo = new Label();
        lblInfo.setWrapText(true);
    }

    private void setupLayout() {
        setSpacing(20);
        setPadding(new Insets(20));
        setMaxWidth(600);
        setAlignment(Pos.CENTER);
        
        // Titre
        Label title = new Label("⚙️ Paramètres du compte");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        
        // Section modification du nom d'utilisateur
        VBox usernameSection = new VBox(10);
        usernameSection.setPadding(new Insets(15));
        usernameSection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-color: #dee2e6; -fx-border-radius: 5;");
        
        Label usernameTitle = new Label("Modifier le nom d'utilisateur");
        usernameTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        usernameTitle.setTextFill(Color.web("#2c3e50"));
        
        GridPane usernameGrid = new GridPane();
        usernameGrid.setHgap(10);
        usernameGrid.setVgap(10);
        usernameGrid.setPadding(new Insets(10));
        
        usernameGrid.add(new Label("Nom d'utilisateur:"), 0, 0);
        usernameGrid.add(txtUsername, 1, 0);
        
        HBox usernameButtonBox = new HBox(10);
        usernameButtonBox.setAlignment(Pos.CENTER_RIGHT);
        usernameButtonBox.getChildren().add(btnUpdateUsername);
        usernameGrid.add(usernameButtonBox, 0, 1, 2, 1);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        usernameGrid.getColumnConstraints().addAll(col1, col2);
        
        usernameSection.getChildren().addAll(usernameTitle, usernameGrid);
        
        // Section modification du mot de passe
        VBox passwordSection = new VBox(10);
        passwordSection.setPadding(new Insets(15));
        passwordSection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-color: #dee2e6; -fx-border-radius: 5;");
        
        Label passwordTitle = new Label("Modifier le mot de passe");
        passwordTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        passwordTitle.setTextFill(Color.web("#2c3e50"));
        
        GridPane passwordGrid = new GridPane();
        passwordGrid.setHgap(10);
        passwordGrid.setVgap(10);
        passwordGrid.setPadding(new Insets(10));
        
        passwordGrid.add(new Label("Mot de passe actuel:"), 0, 0);
        passwordGrid.add(txtCurrentPassword, 1, 0);
        
        passwordGrid.add(new Label("Nouveau mot de passe:"), 0, 1);
        passwordGrid.add(txtNewPassword, 1, 1);
        
        passwordGrid.add(new Label("Confirmer:"), 0, 2);
        passwordGrid.add(txtConfirmPassword, 1, 2);
        
        HBox passwordButtonBox = new HBox(10);
        passwordButtonBox.setAlignment(Pos.CENTER_RIGHT);
        passwordButtonBox.getChildren().add(btnUpdatePassword);
        passwordGrid.add(passwordButtonBox, 0, 3, 2, 1);
        
        ColumnConstraints pcol1 = new ColumnConstraints();
        pcol1.setMinWidth(150);
        ColumnConstraints pcol2 = new ColumnConstraints();
        pcol2.setHgrow(Priority.ALWAYS);
        passwordGrid.getColumnConstraints().addAll(pcol1, pcol2);
        
        passwordSection.getChildren().addAll(passwordTitle, passwordGrid);
        
        // Label d'information
        VBox infoBox = new VBox(5);
        infoBox.getChildren().add(lblInfo);
        
        // Label de statut
        VBox statusBox = new VBox(5);
        statusBox.getChildren().add(lblStatus);
        
        // Assemblage
        getChildren().addAll(title, usernameSection, passwordSection, infoBox, statusBox);
    }

    private void setupStyles() {
        // Styles des boutons
        String buttonStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; " +
                            "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;";
        
        btnUpdateUsername.setStyle(buttonStyle);
        btnUpdatePassword.setStyle(buttonStyle);
        
        // Style du label de statut
        lblStatus.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 12px;");
        lblInfo.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 11px; -fx-font-style: italic;");
    }

    private void setupEventHandlers() {
        btnUpdateUsername.setOnAction(e -> updateUsername());
        btnUpdatePassword.setOnAction(e -> updatePassword());
    }

    private void loadUserInfo() {
        if (utilisateurConnecte != null) {
            txtUsername.setText(utilisateurConnecte.getUsername());
            lblInfo.setText("Connecté en tant que: " + utilisateurConnecte.getUsername() + 
                          " (" + utilisateurConnecte.getRole().getLibelle() + ")");
        }
    }

    private void updateUsername() {
        try {
            String newUsername = txtUsername.getText().trim();
            
            if (newUsername.isEmpty()) {
                showError("Le nom d'utilisateur ne peut pas être vide");
                return;
            }
            
            if (newUsername.length() < 3 || newUsername.length() > 50) {
                showError("Le nom d'utilisateur doit contenir entre 3 et 50 caractères");
                return;
            }
            
            if (newUsername.equals(utilisateurConnecte.getUsername())) {
                showError("Le nouveau nom d'utilisateur est identique à l'actuel");
                return;
            }
            
            controller.updateUsername(utilisateurConnecte.getIdUtilisateur(), newUsername);
            showSuccess("Nom d'utilisateur modifié avec succès! Veuillez vous reconnecter.");
            
            // Mettre à jour l'utilisateur connecté
            utilisateurConnecte = controller.getUtilisateurById(utilisateurConnecte.getIdUtilisateur());
            AuthenticationService.setUtilisateurConnecte(utilisateurConnecte);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du nom d'utilisateur", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void updatePassword() {
        try {
            String currentPassword = txtCurrentPassword.getText();
            String newPassword = txtNewPassword.getText();
            String confirmPassword = txtConfirmPassword.getText();
            
            if (currentPassword.isEmpty()) {
                showError("Veuillez entrer votre mot de passe actuel");
                return;
            }
            
            if (newPassword.isEmpty()) {
                showError("Le nouveau mot de passe ne peut pas être vide");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showError("Les mots de passe ne correspondent pas");
                return;
            }
            
            // Vérifier le mot de passe actuel
            if (!com.hotel.security.PasswordUtils.verifyPassword(currentPassword, utilisateurConnecte.getPasswordHash())) {
                showError("Le mot de passe actuel est incorrect");
                return;
            }
            
            controller.updatePassword(utilisateurConnecte.getIdUtilisateur(), newPassword);
            showSuccess("Mot de passe modifié avec succès!");
            
            // Réinitialiser les champs
            txtCurrentPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();
            
            // Mettre à jour l'utilisateur connecté
            utilisateurConnecte = controller.getUtilisateurById(utilisateurConnecte.getIdUtilisateur());
            
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du mot de passe", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        lblStatus.setText("✓ " + message);
        lblStatus.setTextFill(Color.web("#27ae60"));
    }

    private void showError(String message) {
        lblStatus.setText("✗ " + message);
        lblStatus.setTextFill(Color.web("#e74c3c"));
    }
    
    public void refreshData() {
        utilisateurConnecte = AuthenticationService.getUtilisateurConnecte();
        loadUserInfo();
    }
}

