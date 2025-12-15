package com.hotel.view;

import com.hotel.security.AuthenticationService;
import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Vue principale de l'application avec navigation entre les modules.
 */
public class MainView extends BorderPane {
    private VBox sidebar;
    private StackPane contentArea;
    private HBox header;
    private Label currentModuleLabel;
    
    // Boutons de navigation
    private Button btnDashboard;
    private Button btnClients;
    private Button btnChambres;
    private Button btnReservations;
    private Button btnFacturation;
    private Button btnServices;
    private Button btnEntretien;
    private Button btnUtilisateurs;
    private Button btnParametres;
    private Button btnDeconnexion;
    
    // Utilisateur connecté
    private Utilisateur utilisateurConnecte;
    
    // Vues
    private DashboardView dashboardView;
    private DashboardEntretienView dashboardEntretienView;
    private ClientView clientView;
    private ChambreView chambreView;
    private ReservationView reservationView;
    private FacturationView facturationView;
    private ServiceView serviceView;
    private EntretienView entretienView;
    private GestionUtilisateursView gestionUtilisateursView;
    private ParametresView parametresView;

    public MainView() {
        // Récupérer l'utilisateur connecté
        utilisateurConnecte = AuthenticationService.getUtilisateurConnecte();
        
        initializeComponents();
        setupLayout();
        setupStyles();
        setupEventHandlers();
        adaptInterfaceToRole();
        
        // Afficher le dashboard par défaut
        showDashboard();
    }

    private void initializeComponents() {
        // Initialisation des vues
        dashboardView = new DashboardView();
        dashboardEntretienView = new DashboardEntretienView();
        clientView = new ClientView();
        chambreView = new ChambreView();
        reservationView = new ReservationView();
        facturationView = new FacturationView();
        serviceView = new ServiceView();
        entretienView = new EntretienView();
        
        // Initialisation des boutons
        btnDashboard = new Button("📊 Tableau de bord");
        btnClients = new Button("👥 Clients");
        btnChambres = new Button("🛏️ Chambres");
        btnReservations = new Button("📅 Réservations");
        btnFacturation = new Button("💰 Facturation");
        btnServices = new Button("⭐ Services");
        btnEntretien = new Button("🔧 Entretien");
        btnUtilisateurs = new Button("👤 Utilisateurs");
        btnParametres = new Button("⚙️ Paramètres");
        btnDeconnexion = new Button("🚪 Déconnexion");
        
        // Afficher le nom de l'utilisateur
        String username = utilisateurConnecte != null ? utilisateurConnecte.getUsername() : "Utilisateur";
        String role = utilisateurConnecte != null ? utilisateurConnecte.getRole().getLibelle() : "";
        currentModuleLabel = new Label("Système de Gestion d'Hôtel - " + username + " (" + role + ")");
        currentModuleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    }

    private void setupLayout() {
        // Sidebar
        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setSpacing(10);
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(200);
        
        Label title = new Label("HOTEL MANAGER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);
        
        sidebar.getChildren().addAll(
            title,
            new Separator(),
            btnDashboard,
            btnClients,
            btnChambres,
            btnReservations,
            btnFacturation,
            btnServices,
            btnEntretien,
            new Separator(),
            btnUtilisateurs,
            btnParametres,
            new Separator(),
            btnDeconnexion
        );
        
        // Zone de contenu
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        
        // Header
        header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setSpacing(10);
        header.getChildren().add(currentModuleLabel);
        HBox.setHgrow(currentModuleLabel, Priority.ALWAYS);
        
        // Layout principal
        setLeft(sidebar);
        setCenter(contentArea);
        setTop(header);
    }

    private void setupStyles() {
        // Styles de la sidebar
        sidebar.setStyle(
            "-fx-background-color: #2c3e50; " +
            "-fx-border-color: #34495e; " +
            "-fx-border-width: 0 1 0 0;"
        );
        
        // Styles des boutons
        String buttonStyle = 
            "-fx-background-color: #34495e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 12 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-alignment: center-left;";
        
        String buttonHoverStyle = 
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white;";
        
        btnDashboard.setStyle(buttonStyle);
        btnClients.setStyle(buttonStyle);
        btnChambres.setStyle(buttonStyle);
        btnReservations.setStyle(buttonStyle);
        btnFacturation.setStyle(buttonStyle);
        btnServices.setStyle(buttonStyle);
        btnEntretien.setStyle(buttonStyle);
        btnUtilisateurs.setStyle(buttonStyle);
        btnParametres.setStyle(buttonStyle);
        
        // Style spécial pour le bouton de déconnexion
        String logoutStyle = 
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 12 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-alignment: center-left;";
        btnDeconnexion.setStyle(logoutStyle);
        
        // Effet hover
        setupButtonHover(btnDashboard, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnClients, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnChambres, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnReservations, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnFacturation, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnServices, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnEntretien, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnUtilisateurs, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnParametres, buttonStyle, buttonHoverStyle);
        setupButtonHover(btnDeconnexion, logoutStyle, "-fx-background-color: #c0392b; -fx-text-fill: white;");
        
        // Header
        header.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0;");
        
        // Zone de contenu
        contentArea.setStyle("-fx-background-color: #ffffff;");
    }

    private void setupButtonHover(Button button, String normalStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }

    private void setupEventHandlers() {
        btnDashboard.setOnAction(e -> showDashboard());
        btnClients.setOnAction(e -> showClients());
        btnChambres.setOnAction(e -> showChambres());
        btnReservations.setOnAction(e -> showReservations());
        btnFacturation.setOnAction(e -> showFacturation());
        btnServices.setOnAction(e -> showServices());
        btnEntretien.setOnAction(e -> showEntretien());
        btnUtilisateurs.setOnAction(e -> showUtilisateurs());
        btnParametres.setOnAction(e -> showParametres());
        btnDeconnexion.setOnAction(e -> handleDeconnexion());
    }
    
    /**
     * Adapte l'interface selon le rôle de l'utilisateur.
     */
    private void adaptInterfaceToRole() {
        if (utilisateurConnecte == null) {
            return;
        }
        
        // Masquer/désactiver les boutons selon les permissions
        btnClients.setVisible(AuthenticationService.hasPermission("gestion.clients"));
        btnClients.setManaged(AuthenticationService.hasPermission("gestion.clients"));
        
        btnChambres.setVisible(AuthenticationService.hasPermission("gestion.chambres"));
        btnChambres.setManaged(AuthenticationService.hasPermission("gestion.chambres"));
        
        btnReservations.setVisible(AuthenticationService.hasPermission("gestion.reservations"));
        btnReservations.setManaged(AuthenticationService.hasPermission("gestion.reservations"));
        
        btnFacturation.setVisible(AuthenticationService.hasPermission("gestion.factures"));
        btnFacturation.setManaged(AuthenticationService.hasPermission("gestion.factures"));
        
        btnServices.setVisible(AuthenticationService.hasPermission("gestion.services"));
        btnServices.setManaged(AuthenticationService.hasPermission("gestion.services"));
        
        btnEntretien.setVisible(AuthenticationService.hasPermission("gestion.entretien"));
        btnEntretien.setManaged(AuthenticationService.hasPermission("gestion.entretien"));
        
        // Bouton Utilisateurs visible uniquement pour les admins
        boolean isAdmin = utilisateurConnecte.getRole() == RoleUtilisateur.ADMIN;
        btnUtilisateurs.setVisible(isAdmin);
        btnUtilisateurs.setManaged(isAdmin);
        
        // Bouton Paramètres visible pour tous
        btnParametres.setVisible(true);
        btnParametres.setManaged(true);
    }
    
    /**
     * Gère la déconnexion de l'utilisateur.
     */
    private void handleDeconnexion() {
        AuthenticationService.deconnecter();
        
        // Retourner à l'écran de connexion
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Déconnexion réussie");
        alert.setContentText("Vous avez été déconnecté avec succès.");
        alert.showAndWait();
        
        // Recharger la vue de connexion
        com.hotel.view.LoginView loginView = new com.hotel.view.LoginView();
        loginView.setPrimaryStage((javafx.stage.Stage) getScene().getWindow());
        
        javafx.scene.Scene scene = new javafx.scene.Scene(loginView, 600, 500);
        try {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            // Ignorer si le CSS n'existe pas
        }
        
        ((javafx.stage.Stage) getScene().getWindow()).setScene(scene);
    }

    private void showDashboard() {
        currentModuleLabel.setText("📊 Tableau de bord");
        contentArea.getChildren().clear();
        
        // Afficher le tableau de bord approprié selon le rôle
        if (utilisateurConnecte != null && utilisateurConnecte.getRole() == RoleUtilisateur.ENTRETIEN) {
            // Tableau de bord spécifique pour le personnel d'entretien
            dashboardEntretienView.refreshStatistics();
            contentArea.getChildren().add(dashboardEntretienView);
        } else {
            // Tableau de bord standard pour admin et réceptionniste
            dashboardView.refreshStatistics();
            contentArea.getChildren().add(dashboardView);
        }
        
        resetButtonStyles();
        btnDashboard.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showClients() {
        currentModuleLabel.setText("👥 Gestion des Clients");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (clientView != null) {
            clientView.refreshData();
        } else {
            clientView = new ClientView();
        }
        contentArea.getChildren().add(clientView);
        resetButtonStyles();
        btnClients.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showChambres() {
        currentModuleLabel.setText("🛏️ Gestion des Chambres");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (chambreView != null) {
            chambreView.refreshData();
        } else {
            chambreView = new ChambreView();
        }
        contentArea.getChildren().add(chambreView);
        resetButtonStyles();
        btnChambres.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showReservations() {
        currentModuleLabel.setText("📅 Gestion des Réservations");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (reservationView != null) {
            reservationView.refreshData();
        } else {
            reservationView = new ReservationView();
        }
        contentArea.getChildren().add(reservationView);
        resetButtonStyles();
        btnReservations.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showFacturation() {
        currentModuleLabel.setText("💰 Gestion de la Facturation");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (facturationView != null) {
            facturationView.refreshData();
        } else {
            facturationView = new FacturationView();
        }
        contentArea.getChildren().add(facturationView);
        resetButtonStyles();
        btnFacturation.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showServices() {
        currentModuleLabel.setText("⭐ Services Supplémentaires");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (serviceView != null) {
            serviceView.refreshData();
        } else {
            serviceView = new ServiceView();
        }
        contentArea.getChildren().add(serviceView);
        resetButtonStyles();
        btnServices.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showEntretien() {
        currentModuleLabel.setText("🔧 Gestion de l'Entretien");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (entretienView != null) {
            entretienView.refreshData();
        } else {
            entretienView = new EntretienView();
        }
        contentArea.getChildren().add(entretienView);
        resetButtonStyles();
        btnEntretien.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showUtilisateurs() {
        currentModuleLabel.setText("👤 Gestion des Utilisateurs");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (gestionUtilisateursView != null) {
            gestionUtilisateursView.refreshData();
        } else {
            gestionUtilisateursView = new GestionUtilisateursView();
        }
        contentArea.getChildren().add(gestionUtilisateursView);
        resetButtonStyles();
        btnUtilisateurs.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void showParametres() {
        currentModuleLabel.setText("⚙️ Paramètres du compte");
        contentArea.getChildren().clear();
        // Rafraîchir les données avant d'afficher
        if (parametresView != null) {
            parametresView.refreshData();
        } else {
            parametresView = new ParametresView();
        }
        contentArea.getChildren().add(parametresView);
        resetButtonStyles();
        btnParametres.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void resetButtonStyles() {
        String normalStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 5; -fx-cursor: hand;";
        btnDashboard.setStyle(normalStyle);
        btnClients.setStyle(normalStyle);
        btnChambres.setStyle(normalStyle);
        btnReservations.setStyle(normalStyle);
        btnFacturation.setStyle(normalStyle);
        btnServices.setStyle(normalStyle);
        btnEntretien.setStyle(normalStyle);
        btnUtilisateurs.setStyle(normalStyle);
        btnParametres.setStyle(normalStyle);
    }
}
