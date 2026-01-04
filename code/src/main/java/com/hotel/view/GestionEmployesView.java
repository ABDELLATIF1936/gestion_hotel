package com.hotel.view;

import com.hotel.controller.EmployeController;
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
 * Vue pour la gestion des employés (accessible uniquement aux admins).
 */
public class GestionEmployesView extends VBox {
    private static final Logger logger = Logger.getLogger(GestionEmployesView.class);
    
    private EmployeController controller;
    
    // Composants du formulaire
    private TextField txtNom;
    private TextField txtPrenom;
    private ComboBox<Employe.Role> cmbRole;
    private TextField txtTelephone;
    private TextField txtEmail;
    private CheckBox chkActif;
    private TextField txtSearch;
    
    // TableView
    private TableView<Employe> tableView;
    private ObservableList<Employe> employeList;
    
    // Boutons
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnRechercher;
    private Button btnReinitialiser;
    
    // Labels de statut
    private Label lblStatus;

    public GestionEmployesView() {
        try {
            controller = new EmployeController();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadEmployes();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de GestionEmployesView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        // Champs de formulaire
        txtNom = new TextField();
        txtNom.setPromptText("Nom");
        
        txtPrenom = new TextField();
        txtPrenom.setPromptText("Prénom");
        
        cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll(Employe.Role.values());
        cmbRole.setValue(Employe.Role.RECEPTIONNISTE);
        
        txtTelephone = new TextField();
        txtTelephone.setPromptText("Téléphone (optionnel)");
        
        txtEmail = new TextField();
        txtEmail.setPromptText("Email (optionnel)");
        
        chkActif = new CheckBox("Employé actif");
        chkActif.setSelected(true);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Rechercher par nom ou prénom...");
        
        // TableView
        tableView = new TableView<>();
        employeList = FXCollections.observableArrayList();
        tableView.setItems(employeList);
        
        // Colonnes avec configuration flexible
        TableColumn<Employe, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idEmploye"));
        TableViewHelper.configureFixedColumn(colId, 60);
        
        TableColumn<Employe, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableViewHelper.configureFlexibleColumn(colNom, 100, 150);
        
        TableColumn<Employe, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        TableViewHelper.configureFlexibleColumn(colPrenom, 100, 150);
        
        TableColumn<Employe, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(cellData -> {
            Employe.Role role = cellData.getValue().getRole();
            return new javafx.beans.property.SimpleStringProperty(role != null ? role.name() : "");
        });
        TableViewHelper.configureFlexibleColumn(colRole, 100, 150);
        
        TableColumn<Employe, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        TableViewHelper.configureFlexibleColumn(colTelephone, 100, 120);
        
        TableColumn<Employe, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableViewHelper.configureFlexibleColumn(colEmail, 150, 250);
        
        TableColumn<Employe, Boolean> colActif = new TableColumn<>("Actif");
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));
        TableViewHelper.configureFixedColumn(colActif, 70);
        
        tableView.getColumns().addAll(colId, colNom, colPrenom, colRole, colTelephone, colEmail, colActif);
        
        // Configurer le tableau pour qu'il s'adapte à la taille disponible
        TableViewHelper.configureAutoResizeTableView(tableView);
        tableView.setMinHeight(220);
        
        // Boutons
        btnAjouter = new Button("➕ Ajouter");
        btnModifier = new Button("✏️ Modifier");
        btnSupprimer = new Button("🗑️ Supprimer");
        btnRechercher = new Button("🔍 Rechercher");
        btnReinitialiser = new Button("🔄 Réinitialiser");
        
        // Label de statut
        lblStatus = new Label();
        lblStatus.setWrapText(true);
    }

    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(10));
        
        // Conteneur principal avec scroll
        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(5));
        
        // Titre
        Label title = new Label("Gestion des Employés");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));
        
        // Zone de recherche
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(
            new Label("Recherche:"),
            txtSearch,
            btnRechercher,
            btnReinitialiser
        );
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        
        // Formulaire dans un Accordion (pliable)
        Accordion accordion = new Accordion();
        TitledPane formPane = new TitledPane("📝 Formulaire d'ajout/modification", null);
        formPane.setExpanded(false); // Fermé par défaut pour économiser l'espace
        
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");
        
        form.add(new Label("Nom *:"), 0, 0);
        form.add(txtNom, 1, 0);
        form.add(new Label("Prénom *:"), 0, 1);
        form.add(txtPrenom, 1, 1);
        form.add(new Label("Rôle *:"), 0, 2);
        form.add(cmbRole, 1, 2);
        form.add(new Label("Téléphone:"), 0, 3);
        form.add(txtTelephone, 1, 3);
        form.add(new Label("Email:"), 0, 4);
        form.add(txtEmail, 1, 4);
        form.add(new Label("Statut:"), 0, 5);
        form.add(chkActif, 1, 5);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(col1, col2);
        
        // Boutons d'action
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer);
        
        VBox formContainer = new VBox(10);
        formContainer.getChildren().addAll(form, buttonBox);
        formPane.setContent(formContainer);
        accordion.getPanes().add(formPane);
        
        // TableView - prend tout l'espace disponible
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        // Assemblage
        contentContainer.getChildren().addAll(title, searchBox, accordion, tableView, lblStatus);
        getChildren().add(contentContainer);
        VBox.setVgrow(contentContainer, Priority.ALWAYS);
    }

    private void setupStyles() {
        // Styles des boutons
        String buttonStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; " +
                            "-fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;";
        String deleteButtonStyle = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; " +
                                   "-fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;";
        
        btnAjouter.setStyle(buttonStyle);
        btnModifier.setStyle(buttonStyle);
        btnSupprimer.setStyle(deleteButtonStyle);
        btnRechercher.setStyle(buttonStyle);
        btnReinitialiser.setStyle(buttonStyle);
        
        // Style du label de statut
        lblStatus.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 12px;");
    }

    private void setupEventHandlers() {
        btnAjouter.setOnAction(e -> addEmploye());
        btnModifier.setOnAction(e -> updateEmploye());
        btnSupprimer.setOnAction(e -> deleteEmploye());
        btnRechercher.setOnAction(e -> searchEmployes());
        btnReinitialiser.setOnAction(e -> {
            clearForm();
            loadEmployes();
        });
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillForm(newSelection);
            }
        });
    }

    private void loadEmployes() {
        try {
            List<Employe> employes = controller.getAllEmployes();
            employeList.clear();
            employeList.addAll(employes);
            showSuccess("Chargement réussi: " + employes.size() + " employé(s) trouvé(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des employés", e);
            showError("Erreur lors du chargement des employés: " + e.getMessage());
        }
    }

    private void addEmploye() {
        try {
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            Employe.Role role = cmbRole.getValue();
            
            if (nom.isEmpty()) {
                showError("Le nom est requis");
                return;
            }
            if (prenom.isEmpty()) {
                showError("Le prénom est requis");
                return;
            }
            if (role == null) {
                showError("Le rôle est requis");
                return;
            }
            
            Employe employe = new Employe();
            employe.setNom(nom);
            employe.setPrenom(prenom);
            employe.setRole(role);
            employe.setTelephone(txtTelephone.getText().trim().isEmpty() ? null : txtTelephone.getText().trim());
            employe.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
            employe.setActif(chkActif.isSelected());
            
            controller.createEmploye(employe);
            showSuccess("Employé créé avec succès!");
            clearForm();
            loadEmployes();
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'employé", e);
            showError("Erreur lors de la création: " + e.getMessage());
        }
    }

    private void updateEmploye() {
        Employe selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un employé à modifier");
            return;
        }
        
        try {
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            Employe.Role role = cmbRole.getValue();
            
            if (nom.isEmpty()) {
                showError("Le nom est requis");
                return;
            }
            if (prenom.isEmpty()) {
                showError("Le prénom est requis");
                return;
            }
            if (role == null) {
                showError("Le rôle est requis");
                return;
            }
            
            selected.setNom(nom);
            selected.setPrenom(prenom);
            selected.setRole(role);
            selected.setTelephone(txtTelephone.getText().trim().isEmpty() ? null : txtTelephone.getText().trim());
            selected.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
            selected.setActif(chkActif.isSelected());
            
            controller.updateEmploye(selected);
            showSuccess("Employé modifié avec succès!");
            clearForm();
            loadEmployes();
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de l'employé", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void deleteEmploye() {
        Employe selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un employé à supprimer");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer l'employé");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'employé '" + selected.getNomComplet() + "' ?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    controller.deleteEmploye(selected.getIdEmploye());
                    showSuccess("Employé supprimé avec succès!");
                    clearForm();
                    loadEmployes();
                } catch (Exception e) {
                    logger.error("Erreur lors de la suppression de l'employé", e);
                    showError("Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    private void searchEmployes() {
        String searchTerm = txtSearch.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadEmployes();
            return;
        }
        
        try {
            List<Employe> allEmployes = controller.getAllEmployes();
            employeList.clear();
            for (Employe employe : allEmployes) {
                if (employe.getNom().toLowerCase().contains(searchTerm) ||
                    employe.getPrenom().toLowerCase().contains(searchTerm)) {
                    employeList.add(employe);
                }
            }
            showInfo(employeList.size() + " employé(s) trouvé(s)");
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche", e);
            showError("Erreur lors de la recherche: " + e.getMessage());
        }
    }

    private void fillForm(Employe employe) {
        txtNom.setText(employe.getNom());
        txtPrenom.setText(employe.getPrenom());
        cmbRole.setValue(employe.getRole());
        txtTelephone.setText(employe.getTelephone() != null ? employe.getTelephone() : "");
        txtEmail.setText(employe.getEmail() != null ? employe.getEmail() : "");
        chkActif.setSelected(employe.isActif());
    }

    private void clearForm() {
        txtNom.clear();
        txtPrenom.clear();
        cmbRole.setValue(Employe.Role.RECEPTIONNISTE);
        txtTelephone.clear();
        txtEmail.clear();
        chkActif.setSelected(true);
        txtSearch.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void showSuccess(String message) {
        lblStatus.setText("✓ " + message);
        lblStatus.setTextFill(Color.web("#27ae60"));
    }

    private void showError(String message) {
        lblStatus.setText("✗ " + message);
        lblStatus.setTextFill(Color.web("#e74c3c"));
    }

    private void showInfo(String message) {
        lblStatus.setText("ℹ " + message);
        lblStatus.setTextFill(Color.web("#3498db"));
    }
    
    public void refreshData() {
        loadEmployes();
    }
}

