package com.hotel.view;

import com.hotel.controller.UtilisateurController;
import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;
import com.hotel.model.Employe;
import com.hotel.util.Logger;
import com.hotel.util.TableViewHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Vue pour la gestion des utilisateurs (accessible uniquement aux admins).
 */
public class GestionUtilisateursView extends VBox {
    private static final Logger logger = Logger.getLogger(GestionUtilisateursView.class);
    
    private UtilisateurController controller;
    
    // Composants du formulaire
    private TextField txtUsername;
    private PasswordField txtPassword;
    private ComboBox<Employe> cmbEmploye;
    private ComboBox<RoleUtilisateur> cmbRole;
    private ObservableList<Employe> employeList;
    
    // TableView
    private TableView<Utilisateur> tableView;
    private ObservableList<Utilisateur> utilisateurList;
    
    // Boutons
    private Button btnAjouter;
    private Button btnSupprimer;
    private Button btnActiver;
    private Button btnDesactiver;
    
    // Labels de statut
    private Label lblStatus;

    public GestionUtilisateursView() {
        try {
            controller = new UtilisateurController();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadUtilisateurs();
            loadEmployes();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de GestionUtilisateursView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        // Champs de formulaire
        txtUsername = new TextField();
        txtUsername.setPromptText("Nom d'utilisateur (3-50 caractères)");
        
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Mot de passe (min 8 caractères, majuscule, minuscule, chiffre)");
        
        cmbEmploye = new ComboBox<>();
        employeList = FXCollections.observableArrayList();
        cmbEmploye.setItems(employeList);
        cmbEmploye.setCellFactory(param -> new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNomComplet() + " (" + item.getRole().name() + ")");
                }
            }
        });
        cmbEmploye.setButtonCell(new ListCell<Employe>() {
            @Override
            protected void updateItem(Employe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNomComplet() + " (" + item.getRole().name() + ")");
                }
            }
        });
        
        cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll(RoleUtilisateur.ADMIN, RoleUtilisateur.RECEPTIONNISTE);
        cmbRole.setValue(RoleUtilisateur.RECEPTIONNISTE);
        
        // TableView
        tableView = new TableView<>();
        utilisateurList = FXCollections.observableArrayList();
        tableView.setItems(utilisateurList);
        
        // Colonnes
        TableColumn<Utilisateur, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        TableViewHelper.configureFixedColumn(colId, 60);
        
        TableColumn<Utilisateur, String> colUsername = new TableColumn<>("Nom d'utilisateur");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableViewHelper.configureFlexibleColumn(colUsername, 120, 200);
        
        TableColumn<Utilisateur, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(cellData -> {
            RoleUtilisateur role = cellData.getValue().getRole();
            return new javafx.beans.property.SimpleStringProperty(role != null ? role.getLibelle() : "");
        });
        TableViewHelper.configureFlexibleColumn(colRole, 100, 150);
        
        TableColumn<Utilisateur, String> colActif = new TableColumn<>("Statut");
        colActif.setCellValueFactory(cellData -> {
            boolean actif = cellData.getValue().isActif();
            return new javafx.beans.property.SimpleStringProperty(actif ? "Actif" : "Inactif");
        });
        TableViewHelper.configureFlexibleColumn(colActif, 80, 100);
        
        TableColumn<Utilisateur, Integer> colIdEmploye = new TableColumn<>("ID Employé");
        colIdEmploye.setCellValueFactory(new PropertyValueFactory<>("idEmploye"));
        TableViewHelper.configureFixedColumn(colIdEmploye, 100);
        
        tableView.getColumns().addAll(colId, colUsername, colRole, colActif, colIdEmploye);
        TableViewHelper.configureAutoResizeTableView(tableView);
        
        // Boutons
        btnAjouter = new Button("➕ Créer un compte");
        btnSupprimer = new Button("🗑️ Supprimer");
        btnActiver = new Button("✅ Activer");
        btnDesactiver = new Button("❌ Désactiver");
        
        // Label de statut
        lblStatus = new Label();
        lblStatus.setWrapText(true);
    }

    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(10));
        
        // Titre
        Label title = new Label("Gestion des Utilisateurs");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));
        
        // Formulaire dans un Accordion
        Accordion accordion = new Accordion();
        TitledPane formPane = new TitledPane("📝 Créer un nouveau compte utilisateur", null);
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(15));
        
        formGrid.add(new Label("Nom d'utilisateur:"), 0, 0);
        formGrid.add(txtUsername, 1, 0);
        
        formGrid.add(new Label("Mot de passe:"), 0, 1);
        formGrid.add(txtPassword, 1, 1);
        
        formGrid.add(new Label("Employé:"), 0, 2);
        formGrid.add(cmbEmploye, 1, 2);
        
        formGrid.add(new Label("Rôle:"), 0, 3);
        formGrid.add(cmbRole, 1, 3);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(btnAjouter);
        formGrid.add(buttonBox, 0, 4, 2, 1);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        formGrid.getColumnConstraints().addAll(col1, col2);
        
        formPane.setContent(formGrid);
        accordion.getPanes().add(formPane);
        accordion.setExpandedPane(formPane);
        
        // Zone de boutons pour les actions sur les utilisateurs sélectionnés
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        actionButtons.getChildren().addAll(btnActiver, btnDesactiver, btnSupprimer);
        
        // TableView
        VBox tableContainer = new VBox(10);
        tableContainer.getChildren().addAll(
            new Label("Liste des utilisateurs:"),
            tableView,
            actionButtons
        );
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        // Label de statut
        VBox statusBox = new VBox(5);
        statusBox.getChildren().add(lblStatus);
        
        // Assemblage
        getChildren().addAll(title, accordion, tableContainer, statusBox);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
    }

    private void setupStyles() {
        // Styles des boutons
        String buttonStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; " +
                            "-fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;";
        String deleteButtonStyle = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; " +
                                   "-fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;";
        String activateButtonStyle = "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; " +
                                    "-fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;";
        
        btnAjouter.setStyle(buttonStyle);
        btnActiver.setStyle(activateButtonStyle);
        btnDesactiver.setStyle(buttonStyle);
        btnSupprimer.setStyle(deleteButtonStyle);
        
        // Style du label de statut
        lblStatus.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 12px;");
        
        // Hauteur du tableau
        tableView.setPrefHeight(400);
        tableView.setMinHeight(200);
        tableView.setMaxHeight(Double.MAX_VALUE);
    }

    private void setupEventHandlers() {
        btnAjouter.setOnAction(e -> addUtilisateur());
        btnSupprimer.setOnAction(e -> deleteUtilisateur());
        btnActiver.setOnAction(e -> activateUtilisateur());
        btnDesactiver.setOnAction(e -> deactivateUtilisateur());
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            btnSupprimer.setDisable(!hasSelection);
            btnActiver.setDisable(!hasSelection || newSelection.isActif());
            btnDesactiver.setDisable(!hasSelection || !newSelection.isActif());
        });
    }

    private void loadUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = controller.getAllUtilisateurs();
            utilisateurList.clear();
            utilisateurList.addAll(utilisateurs);
            showSuccess("Chargement réussi: " + utilisateurs.size() + " utilisateur(s) trouvé(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des utilisateurs", e);
            showError("Erreur lors du chargement des utilisateurs: " + e.getMessage());
        }
    }

    private void loadEmployes() {
        try {
            List<Employe> employes = controller.getAllEmployes();
            employeList.clear();
            employeList.addAll(employes);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des employés", e);
            showError("Erreur lors du chargement des employés: " + e.getMessage());
        }
    }

    private void addUtilisateur() {
        try {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText();
            Employe employe = cmbEmploye.getValue();
            RoleUtilisateur role = cmbRole.getValue();
            
            if (username.isEmpty()) {
                showError("Le nom d'utilisateur est requis");
                return;
            }
            
            if (password.isEmpty()) {
                showError("Le mot de passe est requis");
                return;
            }
            
            if (employe == null) {
                showError("Veuillez sélectionner un employé");
                return;
            }
            
            if (role == null) {
                showError("Veuillez sélectionner un rôle");
                return;
            }
            
            controller.createUtilisateur(username, password, employe.getIdEmploye(), role);
            showSuccess("Utilisateur créé avec succès!");
            clearForm();
            loadUtilisateurs();
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur", e);
            showError("Erreur lors de la création: " + e.getMessage());
        }
    }

    private void deleteUtilisateur() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un utilisateur à supprimer");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer l'utilisateur");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur '" + selected.getUsername() + "' ?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    controller.deleteUtilisateur(selected.getIdUtilisateur());
                    showSuccess("Utilisateur supprimé avec succès!");
                    loadUtilisateurs();
                } catch (Exception e) {
                    logger.error("Erreur lors de la suppression de l'utilisateur", e);
                    showError("Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    private void activateUtilisateur() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            controller.activateUtilisateur(selected.getIdUtilisateur());
            showSuccess("Utilisateur activé avec succès!");
            loadUtilisateurs();
        } catch (Exception e) {
            logger.error("Erreur lors de l'activation de l'utilisateur", e);
            showError("Erreur lors de l'activation: " + e.getMessage());
        }
    }

    private void deactivateUtilisateur() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            controller.deactivateUtilisateur(selected.getIdUtilisateur());
            showSuccess("Utilisateur désactivé avec succès!");
            loadUtilisateurs();
        } catch (Exception e) {
            logger.error("Erreur lors de la désactivation de l'utilisateur", e);
            showError("Erreur lors de la désactivation: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtUsername.clear();
        txtPassword.clear();
        cmbEmploye.setValue(null);
        cmbRole.setValue(RoleUtilisateur.RECEPTIONNISTE);
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
        loadUtilisateurs();
        loadEmployes();
    }
}

