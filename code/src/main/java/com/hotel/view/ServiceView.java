package com.hotel.view;

import com.hotel.controller.ServiceController;
import com.hotel.model.ServiceSupplementaire;
import com.hotel.util.Logger;
import com.hotel.util.TableViewHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Vue pour la gestion des services supplémentaires avec CRUD complet.
 */
public class ServiceView extends VBox {
    private static final Logger logger = Logger.getLogger(ServiceView.class);

    private ServiceController controller;

    // Composants du formulaire
    private TextField txtNom;
    private TextField txtPrix;
    private TextArea txtDescription;
    private CheckBox chkActif;
    private TextField txtSearch;

    // TableView
    private TableView<ServiceSupplementaire> tableView;
    private ObservableList<ServiceSupplementaire> serviceList;

    // Boutons
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnRechercher;
    private Button btnReinitialiser;
    private Button btnActifs;

    // Labels de statut
    private Label lblStatus;

    public ServiceView() {
        try {
            controller = new ServiceController();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            adaptInterfaceToRole(); // Add this line
            loadServices();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de ServiceView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        txtNom = new TextField();
        txtNom.setPromptText("Nom du service");

        txtPrix = new TextField();
        txtPrix.setPromptText("Prix");

        txtDescription = new TextArea();
        txtDescription.setPromptText("Description");
        txtDescription.setPrefRowCount(3);

        chkActif = new CheckBox("Service actif");
        chkActif.setSelected(true);

        txtSearch = new TextField();
        txtSearch.setPromptText("Rechercher par nom...");

        // TableView
        tableView = new TableView<>();
        serviceList = FXCollections.observableArrayList();
        tableView.setItems(serviceList);

        // Colonnes avec configuration flexible
        TableColumn<ServiceSupplementaire, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idService"));
        TableViewHelper.configureFixedColumn(colId, 60);

        TableColumn<ServiceSupplementaire, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableViewHelper.configureFlexibleColumn(colNom, 150, 200);

        TableColumn<ServiceSupplementaire, Double> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        TableViewHelper.configureFlexibleColumn(colPrix, 90, 110);
        colPrix.setCellFactory(column -> new TableCell<ServiceSupplementaire, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                }
            }
        });

        TableColumn<ServiceSupplementaire, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableViewHelper.configureFlexibleColumn(colDescription, 200, 300);

        TableColumn<ServiceSupplementaire, Boolean> colActif = new TableColumn<>("Actif");
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));
        TableViewHelper.configureFixedColumn(colActif, 70);

        tableView.getColumns().addAll(colId, colNom, colPrix, colDescription, colActif);

        // Configurer le tableau pour qu'il s'adapte à la taille disponible
        TableViewHelper.configureAutoResizeTableView(tableView);
        tableView.setMinHeight(220);

        // Boutons
        btnAjouter = new Button("➕ Ajouter");
        btnModifier = new Button("✏️ Modifier");
        btnSupprimer = new Button("🗑️ Supprimer");
        btnRechercher = new Button("🔍 Rechercher");
        btnReinitialiser = new Button("🔄 Réinitialiser");
        btnActifs = new Button("✅ Actifs");

        lblStatus = new Label();
        lblStatus.setWrapText(true);
    }

    private void adaptInterfaceToRole() {
        if (com.hotel.security.AuthenticationService.getUtilisateurConnecte() == null) {
            return;
        }

        boolean isAdmin = com.hotel.security.AuthenticationService.getUtilisateurConnecte()
                .getRole() == com.hotel.model.RoleUtilisateur.ADMIN;

        if (!isAdmin) {
            // Hide admin-only controls for non-admins (e.g. Receptionists)
            btnAjouter.setVisible(false);
            btnAjouter.setManaged(false);
            btnModifier.setVisible(false);
            btnModifier.setManaged(false);
            btnSupprimer.setVisible(false);
            btnSupprimer.setManaged(false);

            txtNom.setDisable(true);
            txtPrix.setDisable(true);
            txtDescription.setDisable(true);
            chkActif.setDisable(true);
        }
    }

    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(10));

        // Conteneur principal avec scroll
        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(5));

        // Titre
        Label title = new Label("Gestion des Services Supplémentaires");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        // Zone de recherche
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(
                new Label("Recherche:"),
                txtSearch,
                btnRechercher,
                btnReinitialiser,
                btnActifs);
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
        form.add(new Label("Prix *:"), 0, 1);
        form.add(txtPrix, 1, 1);
        form.add(new Label("Description:"), 0, 2);
        form.add(txtDescription, 1, 2);
        form.add(new Label("Statut:"), 0, 3);
        form.add(chkActif, 1, 3);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(col1, col2);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
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
        getChildren().add(mainScrollPane);
    }

    private void setupStyles() {
        String buttonStyle = "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;";

        btnAjouter.setStyle(buttonStyle);
        btnModifier.setStyle(buttonStyle);
        btnRechercher.setStyle(buttonStyle);
        btnReinitialiser.setStyle(buttonStyle);
        btnActifs.setStyle(buttonStyle);

        btnSupprimer.setStyle(
                "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

        String fieldStyle = "-fx-font-size: 14px; -fx-padding: 5;";
        txtNom.setStyle(fieldStyle);
        txtPrix.setStyle(fieldStyle);
        txtSearch.setStyle(fieldStyle);

        tableView.setStyle("-fx-font-size: 13px;");
    }

    private void setupEventHandlers() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });

        btnAjouter.setOnAction(e -> addService());
        btnModifier.setOnAction(e -> updateService());
        btnSupprimer.setOnAction(e -> deleteService());
        btnRechercher.setOnAction(e -> searchServices());
        btnReinitialiser.setOnAction(e -> {
            clearForm();
            loadServices();
        });
        btnActifs.setOnAction(e -> loadActiveServices());
    }

    private void fillForm(ServiceSupplementaire service) {
        txtNom.setText(service.getNom());
        txtPrix.setText(String.valueOf(service.getPrix()));
        txtDescription.setText(service.getDescription() != null ? service.getDescription() : "");
        chkActif.setSelected(service.isActif());
    }

    private void clearForm() {
        txtNom.clear();
        txtPrix.clear();
        txtDescription.clear();
        chkActif.setSelected(true);
        txtSearch.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void addService() {
        try {
            ServiceSupplementaire service = new ServiceSupplementaire();
            service.setNom(txtNom.getText());
            service.setPrix(Double.parseDouble(txtPrix.getText()));
            service.setDescription(txtDescription.getText());
            service.setActif(chkActif.isSelected());

            ServiceSupplementaire created = controller.createService(service);
            showSuccess("Service ajouté avec succès (ID: " + created.getIdService() + ")");
            clearForm();
            loadServices();
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du service", e);
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private void updateService() {
        ServiceSupplementaire selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un service à modifier");
            return;
        }

        try {
            selected.setNom(txtNom.getText());
            selected.setPrix(Double.parseDouble(txtPrix.getText()));
            selected.setDescription(txtDescription.getText());
            selected.setActif(chkActif.isSelected());

            controller.updateService(selected);
            showSuccess("Service modifié avec succès");
            clearForm();
            loadServices();
        } catch (Exception e) {
            logger.error("Erreur lors de la modification du service", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void deleteService() {
        ServiceSupplementaire selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un service à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le service");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNom() + " ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                controller.deleteService(selected.getIdService());
                showSuccess("Service supprimé avec succès");
                clearForm();
                loadServices();
            } catch (Exception e) {
                logger.error("Erreur lors de la suppression du service", e);
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void searchServices() {
        String searchTerm = txtSearch.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadServices();
            return;
        }

        try {
            List<ServiceSupplementaire> services = controller.getAllServices();
            serviceList.clear();
            for (ServiceSupplementaire service : services) {
                if (service.getNom().toLowerCase().contains(searchTerm)) {
                    serviceList.add(service);
                }
            }
            showInfo(serviceList.size() + " service(s) trouvé(s)");
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
        loadServices();
    }

    private void loadServices() {
        try {
            List<ServiceSupplementaire> services = controller.getAllServices();
            serviceList.clear();
            serviceList.addAll(services);
            showInfo(services.size() + " service(s) chargé(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des services", e);
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void loadActiveServices() {
        try {
            List<ServiceSupplementaire> services = controller.getAllServices();
            serviceList.clear();
            for (ServiceSupplementaire service : services) {
                if (service.isActif()) {
                    serviceList.add(service);
                }
            }
            showInfo(serviceList.size() + " service(s) actif(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des services actifs", e);
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
