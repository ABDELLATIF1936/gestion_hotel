package com.hotel.view;

import com.hotel.controller.ClientController;
import com.hotel.model.Client;
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
 * Vue pour la gestion des clients avec CRUD complet.
 */
public class ClientView extends VBox {
    private static final Logger logger = Logger.getLogger(ClientView.class);
    
    private ClientController controller;
    
    // Composants du formulaire
    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtTelephone;
    private TextField txtEmail;
    private ComboBox<String> cmbTypeClient;
    private TextField txtSearch;
    
    // TableView
    private TableView<Client> tableView;
    private ObservableList<Client> clientList;
    
    // Boutons
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnRechercher;
    private Button btnReinitialiser;
    
    // Labels de statut
    private Label lblStatus;

    public ClientView() {
        try {
            controller = new ClientController();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadClients();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de ClientView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        // Champs de formulaire
        txtNom = new TextField();
        txtPrenom = new TextField();
        txtTelephone = new TextField();
        txtEmail = new TextField();
        cmbTypeClient = new ComboBox<>();
        cmbTypeClient.getItems().addAll("REGULIER", "VIP", "ENTREPRISE");
        cmbTypeClient.setValue("REGULIER");
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Rechercher par nom ou prénom...");
        
        // TableView
        tableView = new TableView<>();
        clientList = FXCollections.observableArrayList();
        tableView.setItems(clientList);
        
        // Colonnes avec configuration flexible
        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        TableViewHelper.configureFixedColumn(colId, 60);
        
        TableColumn<Client, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableViewHelper.configureFlexibleColumn(colNom, 100, 150);
        
        TableColumn<Client, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        TableViewHelper.configureFlexibleColumn(colPrenom, 100, 150);
        
        TableColumn<Client, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        TableViewHelper.configureFlexibleColumn(colTelephone, 100, 120);
        
        TableColumn<Client, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableViewHelper.configureFlexibleColumn(colEmail, 150, 250);
        
        TableColumn<Client, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("typeClient"));
        TableViewHelper.configureFlexibleColumn(colType, 80, 100);
        
        tableView.getColumns().addAll(colId, colNom, colPrenom, colTelephone, colEmail, colType);
        
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
        Label title = new Label("Gestion des Clients");
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
        form.add(new Label("Téléphone *:"), 0, 2);
        form.add(txtTelephone, 1, 2);
        form.add(new Label("Email *:"), 0, 3);
        form.add(txtEmail, 1, 3);
        form.add(new Label("Type *:"), 0, 4);
        form.add(cmbTypeClient, 1, 4);
        
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
        tableView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        contentContainer.getChildren().addAll(title, searchBox, accordion, tableView, lblStatus);
        
        // ScrollPane pour tout le contenu
        ScrollPane mainScrollPane = new ScrollPane(contentContainer);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        getChildren().add(mainScrollPane);
        VBox.setVgrow(mainScrollPane, Priority.ALWAYS);
    }

    private void setupStyles() {
        // Styles des boutons
        String buttonStyle = 
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;";
        
        btnAjouter.setStyle(buttonStyle);
        btnModifier.setStyle(buttonStyle);
        btnRechercher.setStyle(buttonStyle);
        btnReinitialiser.setStyle(buttonStyle);
        
        btnSupprimer.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        // Styles des champs
        String fieldStyle = "-fx-font-size: 14px; -fx-padding: 5;";
        txtNom.setStyle(fieldStyle);
        txtPrenom.setStyle(fieldStyle);
        txtTelephone.setStyle(fieldStyle);
        txtEmail.setStyle(fieldStyle);
        txtSearch.setStyle(fieldStyle);
        
        // TableView
        tableView.setStyle("-fx-font-size: 13px;");
    }

    private void setupEventHandlers() {
        // Sélection dans la table
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });
        
        // Boutons
        btnAjouter.setOnAction(e -> addClient());
        btnModifier.setOnAction(e -> updateClient());
        btnSupprimer.setOnAction(e -> deleteClient());
        btnRechercher.setOnAction(e -> searchClients());
        btnReinitialiser.setOnAction(e -> {
            clearForm();
            loadClients();
        });
    }

    private void fillForm(Client client) {
        txtNom.setText(client.getNom());
        txtPrenom.setText(client.getPrenom());
        txtTelephone.setText(client.getTelephone());
        txtEmail.setText(client.getEmail());
        cmbTypeClient.setValue(client.getTypeClient());
    }

    private void clearForm() {
        txtNom.clear();
        txtPrenom.clear();
        txtTelephone.clear();
        txtEmail.clear();
        cmbTypeClient.setValue("REGULIER");
        txtSearch.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void addClient() {
        try {
            Client client = new Client();
            client.setNom(txtNom.getText());
            client.setPrenom(txtPrenom.getText());
            client.setTelephone(txtTelephone.getText());
            client.setEmail(txtEmail.getText());
            client.setTypeClient(cmbTypeClient.getValue());
            
            Client created = controller.createClient(client);
            showSuccess("Client ajouté avec succès (ID: " + created.getIdClient() + ")");
            clearForm();
            loadClients();
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du client", e);
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private void updateClient() {
        Client selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un client à modifier");
            return;
        }
        
        try {
            selected.setNom(txtNom.getText());
            selected.setPrenom(txtPrenom.getText());
            selected.setTelephone(txtTelephone.getText());
            selected.setEmail(txtEmail.getText());
            selected.setTypeClient(cmbTypeClient.getValue());
            
            controller.updateClient(selected);
            showSuccess("Client modifié avec succès");
            clearForm();
            loadClients();
        } catch (Exception e) {
            logger.error("Erreur lors de la modification du client", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void deleteClient() {
        Client selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un client à supprimer");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le client");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                controller.deleteClient(selected.getIdClient());
                showSuccess("Client supprimé avec succès");
                clearForm();
                loadClients();
            } catch (Exception e) {
                logger.error("Erreur lors de la suppression du client", e);
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void searchClients() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadClients();
            return;
        }
        
        try {
            List<Client> results = controller.searchClients(searchTerm);
            clientList.clear();
            clientList.addAll(results);
            showInfo(results.size() + " client(s) trouvé(s)");
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche", e);
            showError("Erreur lors de la recherche: " + e.getMessage());
        }
    }

    /**
     * Méthode publique pour rafraîchir les données.
     * Peut être appelée depuis l'extérieur pour mettre à jour la liste.
     */
    public void refreshData() {
        loadClients();
    }
    
    private void loadClients() {
        try {
            List<Client> clients = controller.getAllClients();
            clientList.clear();
            clientList.addAll(clients);
            showInfo(clients.size() + " client(s) chargé(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des clients", e);
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        lblStatus.setText("✓ " + message);
        lblStatus.setTextFill(Color.web("#27ae60"));
        lblStatus.setStyle("-fx-font-weight: bold;");
    }

    private void showError(String message) {
        lblStatus.setText("✗ " + message);
        lblStatus.setTextFill(Color.web("#e74c3c"));
        lblStatus.setStyle("-fx-font-weight: bold;");
    }

    private void showInfo(String message) {
        lblStatus.setText("ℹ " + message);
        lblStatus.setTextFill(Color.web("#3498db"));
        lblStatus.setStyle("-fx-font-weight: normal;");
    }
}
