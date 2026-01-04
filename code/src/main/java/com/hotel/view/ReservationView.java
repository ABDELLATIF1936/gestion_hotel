package com.hotel.view;

import com.hotel.controller.ReservationController;
import com.hotel.exception.ServiceException;
import com.hotel.exception.ValidationException;
import com.hotel.factory.ServiceFactory;
import com.hotel.model.Inclure;
import com.hotel.model.Reservation;
import com.hotel.model.ServiceSupplementaire;
import com.hotel.service.interfaces.IServiceSupplementaireService;
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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vue pour la gestion des réservations avec CRUD complet.
 */
public class ReservationView extends VBox {
    private static final Logger logger = Logger.getLogger(ReservationView.class);

    private ReservationController controller;

    // Composants du formulaire
    private TextField txtIdClient;
    private TextField txtNumeroChambre;
    private DatePicker dpDateDebut;
    private DatePicker dpDateFin;
    private DatePicker dpCheckIn;
    private DatePicker dpCheckOut;
    private TextField txtNbPersonnes;
    private ComboBox<Reservation.Statut> cmbStatut;
    private TextArea txtNotes;
    private TextField txtSearch;

    // TableView
    private TableView<Reservation> tableView;
    private ObservableList<Reservation> reservationList;

    // Boutons
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnAnnuler;
    private Button btnRechercher;
    private Button btnReinitialiser;
    private Button btnActives;

    // Labels de statut
    private Label lblStatus;

    // Composants pour les services
    private ComboBox<ServiceSupplementaire> cmbService;
    private TextField txtQuantiteService;
    private TableView<Inclure> tableViewServices;
    private ObservableList<Inclure> serviceList;
    private Button btnAjouterService;
    private Button btnSupprimerService;
    private IServiceSupplementaireService serviceService;
    private Reservation reservationSelectionnee;
    private VBox servicesSection; // Référence à la section services

    public ReservationView() {
        try {
            controller = new ReservationController();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadReservations();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de ReservationView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        txtIdClient = new TextField();
        txtIdClient.setPromptText("ID Client");

        txtNumeroChambre = new TextField();
        txtNumeroChambre.setPromptText("Numéro chambre");

        dpDateDebut = new DatePicker();
        dpDateDebut.setValue(LocalDate.now());

        dpDateFin = new DatePicker();
        dpDateFin.setValue(LocalDate.now().plusDays(1));

        dpCheckIn = new DatePicker();
        dpCheckIn.setPromptText("Date d'arrivée réelle");

        dpCheckOut = new DatePicker();
        dpCheckOut.setPromptText("Date de départ réelle");

        txtNbPersonnes = new TextField();
        txtNbPersonnes.setText("1");

        cmbStatut = new ComboBox<>();
        cmbStatut.getItems().addAll(Reservation.Statut.values());
        cmbStatut.setValue(Reservation.Statut.EN_ATTENTE);

        txtNotes = new TextArea();
        txtNotes.setPromptText("Notes");
        txtNotes.setPrefRowCount(2);

        txtSearch = new TextField();
        txtSearch.setPromptText("Rechercher par ID...");

        // Composants pour les services
        try {
            serviceService = ServiceFactory.getServiceSupplementaireService();
            cmbService = new ComboBox<>();
            cmbService.setPromptText("Sélectionner un service");
            txtQuantiteService = new TextField();
            txtQuantiteService.setText("1");
            txtQuantiteService.setPromptText("Quantité");
            txtQuantiteService.setPrefWidth(80);

            tableViewServices = new TableView<>();
            serviceList = FXCollections.observableArrayList();
            tableViewServices.setItems(serviceList);

            // Colonnes pour les services avec configuration flexible
            TableColumn<Inclure, String> colServiceNom = new TableColumn<>("Service");
            colServiceNom.setCellValueFactory(cellData -> {
                try {
                    ServiceSupplementaire service = serviceService.findServiceById(cellData.getValue().getIdService());
                    return new javafx.beans.property.SimpleStringProperty(service != null ? service.getNom() : "N/A");
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            });
            TableViewHelper.configureFlexibleColumn(colServiceNom, 120, 180);

            TableColumn<Inclure, Integer> colQuantite = new TableColumn<>("Quantité");
            colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            TableViewHelper.configureFlexibleColumn(colQuantite, 70, 90);

            TableColumn<Inclure, Double> colPrixUnitaire = new TableColumn<>("Prix Unitaire");
            colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
            TableViewHelper.configureFlexibleColumn(colPrixUnitaire, 110, 130);
            colPrixUnitaire.setCellFactory(column -> new TableCell<Inclure, Double>() {
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

            TableColumn<Inclure, Double> colMontantTotal = new TableColumn<>("Total");
            colMontantTotal.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(
                    cellData.getValue().getMontantTotal()));
            TableViewHelper.configureFlexibleColumn(colMontantTotal, 90, 110);
            colMontantTotal.setCellFactory(column -> new TableCell<Inclure, Double>() {
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

            tableViewServices.getColumns().addAll(colServiceNom, colQuantite, colPrixUnitaire, colMontantTotal);

            // Configurer le tableau pour qu'il s'adapte à la taille disponible
            TableViewHelper.configureAutoResizeTableView(tableViewServices);
            tableViewServices.setPrefHeight(250);
            tableViewServices.setMinHeight(200);
            tableViewServices.setMaxHeight(400);

            btnAjouterService = new Button("➕ Ajouter Service");
            btnSupprimerService = new Button("➖ Supprimer");

            loadServices();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation des composants de service", e);
        }

        // TableView
        tableView = new TableView<>();
        reservationList = FXCollections.observableArrayList();
        tableView.setItems(reservationList);

        // Colonnes avec configuration flexible
        TableColumn<Reservation, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        TableViewHelper.configureFixedColumn(colId, 60);

        TableColumn<Reservation, Integer> colClient = new TableColumn<>("ID Client");
        colClient.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        TableViewHelper.configureFlexibleColumn(colClient, 70, 90);

        TableColumn<Reservation, Integer> colChambre = new TableColumn<>("Chambre");
        colChambre.setCellValueFactory(new PropertyValueFactory<>("numeroChambre"));
        TableViewHelper.configureFlexibleColumn(colChambre, 70, 90);

        TableColumn<Reservation, LocalDate> colDateDebut = new TableColumn<>("Date Début");
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        TableViewHelper.configureFlexibleColumn(colDateDebut, 100, 120);

        TableColumn<Reservation, LocalDate> colDateFin = new TableColumn<>("Date Fin");
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        TableViewHelper.configureFlexibleColumn(colDateFin, 100, 120);

        TableColumn<Reservation, LocalDate> colCheckIn = new TableColumn<>("Check-in");
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        TableViewHelper.configureFlexibleColumn(colCheckIn, 100, 120);

        TableColumn<Reservation, LocalDate> colCheckOut = new TableColumn<>("Check-out");
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        TableViewHelper.configureFlexibleColumn(colCheckOut, 100, 120);

        TableColumn<Reservation, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> {
            Reservation.Statut stat = cellData.getValue().getStatut();
            return new javafx.beans.property.SimpleStringProperty(stat != null ? stat.name() : "");
        });
        TableViewHelper.configureFlexibleColumn(colStatut, 90, 120);

        TableColumn<Reservation, Integer> colNbPersonnes = new TableColumn<>("Personnes");
        colNbPersonnes.setCellValueFactory(new PropertyValueFactory<>("nbPersonnes"));
        TableViewHelper.configureFlexibleColumn(colNbPersonnes, 70, 90);

        tableView.getColumns().addAll(colId, colClient, colChambre, colDateDebut, colDateFin, colCheckIn, colCheckOut,
                colStatut, colNbPersonnes);

        // Configurer le tableau pour qu'il s'adapte à la taille disponible
        TableViewHelper.configureAutoResizeTableView(tableView);
        tableView.setMinHeight(220);

        // Boutons
        btnAjouter = new Button("➕ Ajouter");
        btnModifier = new Button("✏️ Modifier");
        btnSupprimer = new Button("🗑️ Supprimer");
        btnAnnuler = new Button("❌ Annuler");
        btnRechercher = new Button("🔍 Rechercher");
        btnReinitialiser = new Button("🔄 Réinitialiser");
        btnActives = new Button("✅ Actives");

        lblStatus = new Label();
        lblStatus.setWrapText(true);
    }

    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(10));

        // Conteneur principal avec scroll
        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(5));

        Label title = new Label("Gestion des Réservations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(
                new Label("Recherche:"),
                txtSearch,
                btnRechercher,
                btnReinitialiser,
                btnActives);
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

        form.add(new Label("ID Client *:"), 0, 0);
        form.add(txtIdClient, 1, 0);
        form.add(new Label("Chambre *:"), 0, 1);
        form.add(txtNumeroChambre, 1, 1);
        form.add(new Label("Date Début *:"), 0, 2);
        form.add(dpDateDebut, 1, 2);
        form.add(new Label("Date Fin *:"), 0, 3);
        form.add(dpDateFin, 1, 3);
        form.add(new Label("Personnes *:"), 0, 4);
        form.add(txtNbPersonnes, 1, 4);
        form.add(new Label("Statut:"), 0, 5);
        form.add(cmbStatut, 1, 5);
        form.add(new Label("Notes:"), 0, 6);
        form.add(txtNotes, 1, 6);
        form.add(new Label("Check-in:"), 0, 7);
        form.add(dpCheckIn, 1, 7);
        form.add(new Label("Check-out:"), 0, 8);
        form.add(dpCheckOut, 1, 8);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(col1, col2);

        // Section Services (toujours visible mais désactivée si aucune réservation
        // sélectionnée)
        servicesSection = new VBox(15);
        servicesSection.setPadding(new Insets(15));
        servicesSection.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #f0f8ff, #e6f3ff); " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #3498db; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8;");

        // Titre avec icône
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label servicesTitle = new Label("⭐ Services de la Réservation");
        servicesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        servicesTitle.setTextFill(Color.web("#2c3e50"));
        titleBox.getChildren().add(servicesTitle);

        // Zone d'ajout de service avec design amélioré
        VBox addServiceBox = new VBox(10);
        addServiceBox.setPadding(new Insets(10));
        addServiceBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label addServiceLabel = new Label("Ajouter un nouveau service:");
        addServiceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        addServiceLabel.setTextFill(Color.web("#34495e"));

        GridPane serviceForm = new GridPane();
        serviceForm.setHgap(10);
        serviceForm.setVgap(10);
        serviceForm.setPadding(new Insets(5));

        Label lblService = new Label("Service:");
        lblService.setFont(Font.font("Arial", 12));
        lblService.setTextFill(Color.web("#34495e"));

        Label lblQuantite = new Label("Quantité:");
        lblQuantite.setFont(Font.font("Arial", 12));
        lblQuantite.setTextFill(Color.web("#34495e"));

        // Style pour les champs
        cmbService.setPrefWidth(250);
        cmbService.setStyle(
                "-fx-font-size: 13px; " +
                        "-fx-padding: 8; " +
                        "-fx-background-radius: 5;");

        txtQuantiteService.setPrefWidth(80);
        txtQuantiteService.setStyle(
                "-fx-font-size: 13px; " +
                        "-fx-padding: 8; " +
                        "-fx-background-radius: 5;");

        // Style pour le bouton ajouter
        btnAjouterService.setStyle(
                "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        btnAjouterService.setOnMouseEntered(e -> btnAjouterService.setStyle(
                "-fx-background-color: #229954; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 7, 0, 0, 3);"));
        btnAjouterService.setOnMouseExited(e -> btnAjouterService.setStyle(
                "-fx-background-color: #27ae60; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"));

        serviceForm.add(lblService, 0, 0);
        serviceForm.add(cmbService, 1, 0);
        serviceForm.add(lblQuantite, 0, 1);
        serviceForm.add(txtQuantiteService, 1, 1);
        serviceForm.add(btnAjouterService, 1, 2);

        ColumnConstraints serviceCol1 = new ColumnConstraints();
        serviceCol1.setPrefWidth(80);
        ColumnConstraints serviceCol2 = new ColumnConstraints();
        serviceCol2.setHgrow(Priority.ALWAYS);
        serviceForm.getColumnConstraints().addAll(serviceCol1, serviceCol2);

        addServiceBox.getChildren().addAll(addServiceLabel, serviceForm);

        // Tableau des services avec titre
        VBox servicesTableBox = new VBox(8);
        Label servicesListLabel = new Label("Services ajoutés:");
        servicesListLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        servicesListLabel.setTextFill(Color.web("#34495e"));

        // Style pour le tableau
        tableViewServices.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #bdc3c7; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 5;");

        // Style pour le bouton supprimer
        btnSupprimerService.setStyle(
                "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        btnSupprimerService.setOnMouseEntered(e -> btnSupprimerService.setStyle(
                "-fx-background-color: #c0392b; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 7, 0, 0, 3);"));
        btnSupprimerService.setOnMouseExited(e -> btnSupprimerService.setStyle(
                "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"));

        HBox serviceButtonBox = new HBox(10);
        serviceButtonBox.setAlignment(Pos.CENTER_LEFT);
        serviceButtonBox.getChildren().add(btnSupprimerService);

        servicesTableBox.getChildren().addAll(servicesListLabel, tableViewServices, serviceButtonBox);

        servicesSection.getChildren().addAll(titleBox, addServiceBox, servicesTableBox);

        // Message d'information quand aucune réservation n'est sélectionnée
        Label infoLabel = new Label("ℹ️ Sélectionnez une réservation pour ajouter des services");
        infoLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 11));
        infoLabel.setTextFill(Color.web("#7f8c8d"));
        infoLabel.setPadding(new Insets(5, 0, 0, 0));
        infoLabel.setVisible(true);
        servicesSection.getChildren().add(infoLabel);

        // Désactiver les contrôles par défaut (seront activés quand une réservation est
        // sélectionnée)
        cmbService.setDisable(true);
        txtQuantiteService.setDisable(true);
        btnAjouterService.setDisable(true);
        btnSupprimerService.setDisable(true);

        // Stocker la référence au label d'info pour pouvoir le masquer plus tard
        servicesSection.getProperties().put("infoLabel", infoLabel);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer, btnAnnuler);

        VBox formContainer = new VBox(10);
        formContainer.getChildren().addAll(form, servicesSection, buttonBox);
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
        btnActives.setStyle(buttonStyle);
        btnAnnuler.setStyle(buttonStyle);

        btnSupprimer.setStyle(
                "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

        String fieldStyle = "-fx-font-size: 14px; -fx-padding: 5;";
        txtIdClient.setStyle(fieldStyle);
        txtNumeroChambre.setStyle(fieldStyle);
        txtNbPersonnes.setStyle(fieldStyle);
        txtSearch.setStyle(fieldStyle);

        tableView.setStyle("-fx-font-size: 13px;");
    }

    private void setupEventHandlers() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
                reservationSelectionnee = newVal;
                loadServicesForReservation(newVal.getIdReservation());
            } else {
                reservationSelectionnee = null;
                serviceList.clear();
            }
        });

        btnAjouter.setOnAction(e -> addReservation());
        btnModifier.setOnAction(e -> updateReservation());
        btnSupprimer.setOnAction(e -> deleteReservation());
        btnAnnuler.setOnAction(e -> cancelReservation());
        btnRechercher.setOnAction(e -> searchReservations());
        btnReinitialiser.setOnAction(e -> {
            clearForm();
            loadReservations();
        });
        btnActives.setOnAction(e -> loadActiveReservations());

        // Handlers pour les services
        btnAjouterService.setOnAction(e -> addServiceToReservation());
        btnSupprimerService.setOnAction(e -> removeServiceFromReservation());
    }

    private void fillForm(Reservation reservation) {
        txtIdClient.setText(String.valueOf(reservation.getIdClient()));
        txtNumeroChambre.setText(String.valueOf(reservation.getNumeroChambre()));
        dpDateDebut.setValue(reservation.getDateDebut());
        dpDateFin.setValue(reservation.getDateFin());
        dpCheckIn.setValue(reservation.getCheckIn());
        dpCheckOut.setValue(reservation.getCheckOut());
        txtNbPersonnes.setText(String.valueOf(reservation.getNbPersonnes()));
        cmbStatut.setValue(reservation.getStatut());
        txtNotes.setText(reservation.getNotes() != null ? reservation.getNotes() : "");

        // Activer la section services et charger les services de la réservation
        reservationSelectionnee = reservation;
        cmbService.setDisable(false);
        txtQuantiteService.setDisable(false);
        btnAjouterService.setDisable(false);
        btnSupprimerService.setDisable(false);

        // Masquer le message d'information
        Object infoLabelObj = servicesSection.getProperties().get("infoLabel");
        if (infoLabelObj instanceof Label) {
            ((Label) infoLabelObj).setVisible(false);
        }

        // Charger les services de la réservation
        loadServicesForReservation(reservation.getIdReservation());

        // Ouvrir l'accordion pour que la section soit visible
        try {
            Accordion accordion = (Accordion) lookup(".accordion");
            if (accordion != null && !accordion.getPanes().isEmpty()) {
                accordion.setExpandedPane(accordion.getPanes().get(0));
            }
        } catch (Exception e) {
            logger.warn("Impossible d'ouvrir l'accordion", e);
        }
    }

    private void clearForm() {
        txtIdClient.clear();
        txtNumeroChambre.clear();
        dpDateDebut.setValue(LocalDate.now());
        dpDateFin.setValue(LocalDate.now().plusDays(1));
        dpCheckIn.setValue(null);
        dpCheckOut.setValue(null);
        txtNbPersonnes.setText("1");
        cmbStatut.setValue(Reservation.Statut.EN_ATTENTE);
        txtNotes.clear();
        txtSearch.clear();
        tableView.getSelectionModel().clearSelection();
        serviceList.clear();
        reservationSelectionnee = null;

        // Désactiver la section services
        if (cmbService != null) {
            cmbService.setDisable(true);
            cmbService.getSelectionModel().clearSelection();
        }
        if (txtQuantiteService != null) {
            txtQuantiteService.setDisable(true);
            txtQuantiteService.setText("1");
        }
        if (btnAjouterService != null) {
            btnAjouterService.setDisable(true);
        }
        if (btnSupprimerService != null) {
            btnSupprimerService.setDisable(true);
        }

        // Afficher le message d'information
        if (servicesSection != null) {
            Object infoLabelObj = servicesSection.getProperties().get("infoLabel");
            if (infoLabelObj instanceof Label) {
                ((Label) infoLabelObj).setVisible(true);
            }
        }
    }

    private void loadServices() {
        try {
            if (serviceService != null) {
                List<ServiceSupplementaire> services = serviceService.getActiveServices();
                cmbService.getItems().clear();
                cmbService.getItems().addAll(services);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des services", e);
        }
    }

    private void loadServicesForReservation(int idReservation) {
        try {
            List<Inclure> services = controller.getServicesByReservation(idReservation);
            serviceList.clear();
            serviceList.addAll(services);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des services de la réservation", e);
            showError("Erreur lors du chargement des services: " + e.getMessage());
        }
    }

    private void addServiceToReservation() {
        if (reservationSelectionnee == null) {
            showError("Veuillez sélectionner une réservation d'abord");
            return;
        }

        ServiceSupplementaire service = cmbService.getSelectionModel().getSelectedItem();
        if (service == null) {
            showError("Veuillez sélectionner un service");
            return;
        }

        try {
            int quantite = Integer.parseInt(txtQuantiteService.getText().trim());
            if (quantite <= 0) {
                showError("La quantité doit être supérieure à 0");
                return;
            }

            controller.addServiceToReservation(reservationSelectionnee.getIdReservation(),
                    service.getIdService(), quantite);
            showSuccess("Service ajouté avec succès");
            loadServicesForReservation(reservationSelectionnee.getIdReservation());
            txtQuantiteService.setText("1");
            cmbService.getSelectionModel().clearSelection();
        } catch (NumberFormatException e) {
            showError("Veuillez entrer une quantité valide");
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du service", e);
            showError("Erreur lors de l'ajout du service: " + e.getMessage());
        }
    }

    private void removeServiceFromReservation() {
        if (reservationSelectionnee == null) {
            showError("Veuillez sélectionner une réservation d'abord");
            return;
        }

        Inclure selected = tableViewServices.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un service à supprimer");
            return;
        }

        try {
            controller.removeServiceFromReservation(reservationSelectionnee.getIdReservation(),
                    selected.getIdService());
            showSuccess("Service supprimé avec succès");
            loadServicesForReservation(reservationSelectionnee.getIdReservation());
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du service", e);
            showError("Erreur lors de la suppression du service: " + e.getMessage());
        }
    }

    private void addReservation() {
        // Validation des champs
        if (txtIdClient.getText().trim().isEmpty()) {
            showError("Veuillez entrer l'ID du client");
            return;
        }
        if (txtNumeroChambre.getText().trim().isEmpty()) {
            showError("Veuillez entrer le numéro de chambre");
            return;
        }
        if (dpDateDebut.getValue() == null) {
            showError("Veuillez sélectionner la date de début");
            return;
        }
        if (dpDateFin.getValue() == null) {
            showError("Veuillez sélectionner la date de fin");
            return;
        }
        if (txtNbPersonnes.getText().trim().isEmpty()) {
            showError("Veuillez entrer le nombre de personnes");
            return;
        }

        try {
            Reservation reservation = new Reservation();
            reservation.setIdClient(Integer.parseInt(txtIdClient.getText().trim()));
            reservation.setNumeroChambre(Integer.parseInt(txtNumeroChambre.getText().trim()));
            reservation.setDateDebut(dpDateDebut.getValue());
            reservation.setDateFin(dpDateFin.getValue());
            reservation.setCheckIn(dpCheckIn.getValue());
            reservation.setCheckOut(dpCheckOut.getValue());
            reservation.setNbPersonnes(Integer.parseInt(txtNbPersonnes.getText().trim()));
            reservation.setStatut(cmbStatut.getValue());
            reservation.setNotes(txtNotes.getText() != null ? txtNotes.getText().trim() : "");

            Reservation created = controller.createReservation(reservation);
            showSuccess("Réservation ajoutée avec succès (ID: " + created.getIdReservation() + ")");
            clearForm();
            loadReservations();
        } catch (NumberFormatException e) {
            logger.error("Erreur de format numérique", e);
            showError("Veuillez entrer des valeurs numériques valides (ID Client, Chambre, Nombre de personnes)");
        } catch (com.hotel.exception.ValidationException e) {
            logger.error("Erreur de validation", e);
            showError("Erreur de validation: " + e.getMessage());
        } catch (com.hotel.exception.ServiceException e) {
            logger.error("Erreur de service", e);
            showError(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la réservation", e);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "Une erreur inattendue s'est produite. Vérifiez les logs pour plus de détails.";
            }
            showError("Erreur lors de l'ajout: " + errorMsg);
        }
    }

    private void updateReservation() {
        Reservation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une réservation à modifier");
            return;
        }

        // Validation des champs
        if (txtIdClient.getText().trim().isEmpty()) {
            showError("Veuillez entrer l'ID du client");
            return;
        }
        if (txtNumeroChambre.getText().trim().isEmpty()) {
            showError("Veuillez entrer le numéro de chambre");
            return;
        }
        if (dpDateDebut.getValue() == null) {
            showError("Veuillez sélectionner la date de début");
            return;
        }
        if (dpDateFin.getValue() == null) {
            showError("Veuillez sélectionner la date de fin");
            return;
        }
        if (txtNbPersonnes.getText().trim().isEmpty()) {
            showError("Veuillez entrer le nombre de personnes");
            return;
        }

        try {
            selected.setIdClient(Integer.parseInt(txtIdClient.getText().trim()));
            selected.setNumeroChambre(Integer.parseInt(txtNumeroChambre.getText().trim()));
            selected.setDateDebut(dpDateDebut.getValue());
            selected.setDateFin(dpDateFin.getValue());
            selected.setCheckIn(dpCheckIn.getValue());
            selected.setCheckOut(dpCheckOut.getValue());
            selected.setNbPersonnes(Integer.parseInt(txtNbPersonnes.getText().trim()));
            selected.setStatut(cmbStatut.getValue());
            selected.setNotes(txtNotes.getText() != null ? txtNotes.getText().trim() : "");

            controller.updateReservation(selected);
            showSuccess("Réservation modifiée avec succès");
            clearForm();
            loadReservations();
        } catch (NumberFormatException e) {
            logger.error("Erreur de format numérique", e);
            showError("Veuillez entrer des valeurs numériques valides (ID Client, Chambre, Nombre de personnes)");
        } catch (com.hotel.exception.ValidationException e) {
            logger.error("Erreur de validation", e);
            showError("Erreur de validation: " + e.getMessage());
        } catch (com.hotel.exception.ServiceException e) {
            logger.error("Erreur de service", e);
            showError(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de la réservation", e);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "Une erreur inattendue s'est produite. Vérifiez les logs pour plus de détails.";
            }
            showError("Erreur lors de la modification: " + errorMsg);
        }
    }

    private void deleteReservation() {
        Reservation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une réservation à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la réservation");
        confirm.setContentText(
                "Êtes-vous sûr de vouloir supprimer la réservation " + selected.getIdReservation() + " ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                controller.deleteReservation(selected.getIdReservation());
                showSuccess("Réservation supprimée avec succès");
                clearForm();
                loadReservations();
            } catch (com.hotel.exception.ServiceException e) {
                logger.error("Erreur de service", e);
                showError(e.getMessage());
            } catch (Exception e) {
                logger.error("Erreur lors de la suppression de la réservation", e);
                String errorMsg = e.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Une erreur inattendue s'est produite. Vérifiez les logs pour plus de détails.";
                }
                showError("Erreur lors de la suppression: " + errorMsg);
            }
        }
    }

    private void cancelReservation() {
        Reservation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une réservation à annuler");
            return;
        }

        try {
            controller.cancelReservation(selected.getIdReservation());
            showSuccess("Réservation annulée avec succès");
            clearForm();
            loadReservations();
        } catch (Exception e) {
            logger.error("Erreur lors de l'annulation de la réservation", e);
            showError("Erreur lors de l'annulation: " + e.getMessage());
        }
    }

    private void searchReservations() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadReservations();
            return;
        }

        try {
            int id = Integer.parseInt(searchTerm);
            Reservation reservation = controller.getReservationById(id);
            reservationList.clear();
            if (reservation != null) {
                reservationList.add(reservation);
                showInfo("1 réservation trouvée");
            } else {
                showInfo("Aucune réservation trouvée");
            }
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un ID valide");
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
        loadReservations();
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = controller.getAllReservations();
            reservationList.clear();
            reservationList.addAll(reservations);
            showInfo(reservations.size() + " réservation(s) chargée(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des réservations", e);
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void loadActiveReservations() {
        try {
            List<Reservation> reservations = controller.getActiveReservations();
            reservationList.clear();
            reservationList.addAll(reservations);
            showInfo(reservations.size() + " réservation(s) active(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des réservations actives", e);
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
