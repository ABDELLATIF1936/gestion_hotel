package com.hotel.view;

import com.hotel.controller.FacturationController;
import com.hotel.controller.ReservationController;
import com.hotel.factory.ServiceFactory;
import com.hotel.model.Chambre;
import com.hotel.model.Facture;
import com.hotel.model.Reservation;
import com.hotel.service.interfaces.IChambreService;
import com.hotel.service.interfaces.IReservationService;
import com.hotel.util.DateUtil;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Vue pour la gestion de la facturation avec CRUD complet.
 */
public class FacturationView extends VBox {
    private static final Logger logger = Logger.getLogger(FacturationView.class);
    
    private FacturationController controller;
    private ReservationController reservationController;
    private IChambreService chambreService;
    private IReservationService reservationService;
    
    // Composants du formulaire
    private TextField txtIdReservation;
    private DatePicker dpDateEmission;
    private TextField txtMontant;
    private Label lblMontantChambre;
    private Label lblMontantServices;
    private Label lblMontantTotal;
    private Button btnCalculerMontant;
    private ComboBox<Facture.Statut> cmbStatut;
    private TextArea txtNotes;
    private TextField txtSearch;
    
    // TableView
    private TableView<Facture> tableView;
    private ObservableList<Facture> factureList;
    
    // Boutons
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnMarquerPayee;
    private Button btnRechercher;
    private Button btnReinitialiser;
    
    // Labels de statut
    private Label lblStatus;
    
    public FacturationView() {
        try {
            controller = new FacturationController();
            reservationController = new ReservationController();
            chambreService = ServiceFactory.getChambreService();
            reservationService = ServiceFactory.getReservationService();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadFactures();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de FacturationView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        txtIdReservation = new TextField();
        txtIdReservation.setPromptText("ID Réservation");
        
        dpDateEmission = new DatePicker();
        dpDateEmission.setValue(LocalDate.now());
        
        txtMontant = new TextField();
        txtMontant.setPromptText("Montant total (calculé automatiquement)");
        txtMontant.setEditable(false);
        
        lblMontantChambre = new Label("Chambre: 0.00 €");
        lblMontantServices = new Label("Services: 0.00 €");
        lblMontantTotal = new Label("Total: 0.00 €");
        lblMontantTotal.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblMontantTotal.setTextFill(Color.web("#2c3e50"));
        
        btnCalculerMontant = new Button("💰 Calculer Montant");
        
        cmbStatut = new ComboBox<>();
        cmbStatut.getItems().addAll(Facture.Statut.values());
        cmbStatut.setValue(Facture.Statut.EN_ATTENTE);
        
        txtNotes = new TextArea();
        txtNotes.setPromptText("Notes");
        txtNotes.setPrefRowCount(2);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Rechercher par ID...");
        
        // TableView
        tableView = new TableView<>();
        factureList = FXCollections.observableArrayList();
        tableView.setItems(factureList);
        
        // Colonnes avec configuration flexible
        TableColumn<Facture, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idFacture"));
        TableViewHelper.configureFixedColumn(colId, 60);
        
        TableColumn<Facture, Integer> colReservation = new TableColumn<>("Réservation");
        colReservation.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        TableViewHelper.configureFlexibleColumn(colReservation, 90, 110);
        
        TableColumn<Facture, LocalDate> colDate = new TableColumn<>("Date Émission");
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEmission"));
        TableViewHelper.configureFlexibleColumn(colDate, 110, 130);
        
        TableColumn<Facture, Double> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        TableViewHelper.configureFlexibleColumn(colMontant, 110, 130);
        colMontant.setCellFactory(column -> new TableCell<Facture, Double>() {
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
        
        TableColumn<Facture, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> {
            Facture.Statut stat = cellData.getValue().getStatut();
            return new javafx.beans.property.SimpleStringProperty(stat != null ? stat.name() : "");
        });
        TableViewHelper.configureFlexibleColumn(colStatut, 90, 120);
        
        TableColumn<Facture, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        TableViewHelper.configureFlexibleColumn(colNotes, 150, 250);
        
        tableView.getColumns().addAll(colId, colReservation, colDate, colMontant, colStatut, colNotes);
        
        // Configurer le tableau pour qu'il s'adapte à la taille disponible
        TableViewHelper.configureAutoResizeTableView(tableView);
        tableView.setMinHeight(220);
        
        // Boutons
        btnAjouter = new Button("➕ Ajouter");
        btnModifier = new Button("✏️ Modifier");
        btnSupprimer = new Button("🗑️ Supprimer");
        btnMarquerPayee = new Button("💰 Marquer Payée");
        btnRechercher = new Button("🔍 Rechercher");
        btnReinitialiser = new Button("🔄 Réinitialiser");
        
        lblStatus = new Label();
        lblStatus.setWrapText(true);
    }

    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(10));
        
        // Conteneur principal avec scroll
        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(5));
        
        Label title = new Label("Gestion de la Facturation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));
        
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
        
        form.add(new Label("ID Réservation *:"), 0, 0);
        form.add(txtIdReservation, 1, 0);
        form.add(btnCalculerMontant, 2, 0);
        form.add(new Label("Date Émission:"), 0, 1);
        form.add(dpDateEmission, 1, 1);
        form.add(new Label("Détail:"), 0, 2);
        VBox detailBox = new VBox(5);
        detailBox.getChildren().addAll(lblMontantChambre, lblMontantServices, lblMontantTotal);
        form.add(detailBox, 1, 2);
        form.add(new Label("Montant *:"), 0, 3);
        form.add(txtMontant, 1, 3);
        form.add(new Label("Statut:"), 0, 4);
        form.add(cmbStatut, 1, 4);
        form.add(new Label("Notes:"), 0, 5);
        form.add(txtNotes, 1, 5);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(col1, col2);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer, btnMarquerPayee);
        
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
        btnMarquerPayee.setStyle(buttonStyle);
        
        btnSupprimer.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        String fieldStyle = "-fx-font-size: 14px; -fx-padding: 5;";
        txtIdReservation.setStyle(fieldStyle);
        txtMontant.setStyle(fieldStyle);
        txtSearch.setStyle(fieldStyle);
        
        tableView.setStyle("-fx-font-size: 13px;");
    }

    private void setupEventHandlers() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });
        
        btnAjouter.setOnAction(e -> addFacture());
        btnModifier.setOnAction(e -> updateFacture());
        btnSupprimer.setOnAction(e -> deleteFacture());
        btnMarquerPayee.setOnAction(e -> markAsPaid());
        btnRechercher.setOnAction(e -> searchFactures());
        btnReinitialiser.setOnAction(e -> {
            clearForm();
            loadFactures();
        });
        btnCalculerMontant.setOnAction(e -> calculateTotal());
    }
    
    private void calculateTotal() {
        try {
            String idReservationText = txtIdReservation.getText().trim();
            if (idReservationText.isEmpty()) {
                showError("Veuillez entrer un ID de réservation");
                return;
            }
            
            int idReservation = Integer.parseInt(idReservationText);
            Reservation reservation = reservationService.findReservationById(idReservation);
            if (reservation == null) {
                showError("Réservation non trouvée");
                return;
            }
            
            // Calculer le montant de la chambre
            Chambre chambre = chambreService.findChambreByNumero(reservation.getNumeroChambre());
            int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
            double montantChambre = chambre.getPrixNuit() * nbNuits;
            
            // Calculer le montant des services
            double montantServices = reservationService.getTotalServicesAmount(idReservation);
            
            // Total
            double montantTotal = montantChambre + montantServices;
            
            // Mettre à jour les labels
            lblMontantChambre.setText(String.format("Chambre (%d nuits): %.2f €", nbNuits, montantChambre));
            lblMontantServices.setText(String.format("Services: %.2f €", montantServices));
            lblMontantTotal.setText(String.format("Total: %.2f €", montantTotal));
            
            // Mettre à jour le champ montant
            txtMontant.setText(String.valueOf(montantTotal));
            
            showSuccess("Montant calculé avec succès");
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un ID de réservation valide");
        } catch (Exception e) {
            logger.error("Erreur lors du calcul du montant", e);
            showError("Erreur lors du calcul: " + e.getMessage());
        }
    }

    private void fillForm(Facture facture) {
        txtIdReservation.setText(String.valueOf(facture.getIdReservation()));
        dpDateEmission.setValue(facture.getDateEmission());
        txtMontant.setText(String.valueOf(facture.getMontantTotal()));
        cmbStatut.setValue(facture.getStatut());
        txtNotes.setText(facture.getNotes() != null ? facture.getNotes() : "");
        
        // Recalculer et afficher le détail du montant
        try {
            int idReservation = facture.getIdReservation();
            Reservation reservation = reservationService.findReservationById(idReservation);
            if (reservation != null) {
                Chambre chambre = chambreService.findChambreByNumero(reservation.getNumeroChambre());
                int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
                double montantChambre = chambre.getPrixNuit() * nbNuits;
                double montantServices = reservationService.getTotalServicesAmount(idReservation);
                
                lblMontantChambre.setText(String.format("Chambre (%d nuits): %.2f €", nbNuits, montantChambre));
                lblMontantServices.setText(String.format("Services: %.2f €", montantServices));
                lblMontantTotal.setText(String.format("Total: %.2f €", montantChambre + montantServices));
            }
        } catch (Exception e) {
            logger.warn("Impossible de calculer le détail du montant", e);
        }
    }

    private void clearForm() {
        txtIdReservation.clear();
        dpDateEmission.setValue(LocalDate.now());
        txtMontant.clear();
        cmbStatut.setValue(Facture.Statut.EN_ATTENTE);
        txtNotes.clear();
        txtSearch.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void addFacture() {
        try {
            int idReservation = Integer.parseInt(txtIdReservation.getText());
            
            // Calculer automatiquement le montant total si le champ est vide ou si on veut recalculer
            double montantTotal;
            String montantText = txtMontant.getText().trim();
            if (montantText.isEmpty() || montantText.equals("0") || montantText.equals("0.0")) {
                // Calculer automatiquement le montant (chambre + services)
                Reservation reservation = reservationService.findReservationById(idReservation);
                if (reservation == null) {
                    showError("Réservation non trouvée");
                    return;
                }
                
                Chambre chambre = chambreService.findChambreByNumero(reservation.getNumeroChambre());
                int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
                double montantChambre = chambre.getPrixNuit() * nbNuits;
                double montantServices = reservationService.getTotalServicesAmount(idReservation);
                montantTotal = montantChambre + montantServices;
                
                // Mettre à jour le champ montant
                txtMontant.setText(String.valueOf(montantTotal));
            } else {
                montantTotal = Double.parseDouble(montantText);
            }
            
            Facture facture = new Facture();
            facture.setIdReservation(idReservation);
            facture.setDateEmission(dpDateEmission.getValue());
            facture.setMontantTotal(montantTotal);
            facture.setStatut(cmbStatut.getValue());
            facture.setNotes(txtNotes.getText());
            
            Facture created = controller.createFacture(facture);
            showSuccess("Facture ajoutée avec succès (ID: " + created.getIdFacture() + ")");
            clearForm();
            loadFactures();
        } catch (NumberFormatException e) {
            showError("Veuillez entrer des valeurs numériques valides");
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la facture", e);
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private void updateFacture() {
        Facture selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une facture à modifier");
            return;
        }
        
        try {
            int idReservation = Integer.parseInt(txtIdReservation.getText());
            
            // Recalculer le montant si nécessaire (si l'ID de réservation a changé ou si on veut recalculer)
            double montantTotal;
            String montantText = txtMontant.getText().trim();
            if (montantText.isEmpty() || montantText.equals("0") || montantText.equals("0.0") || 
                selected.getIdReservation() != idReservation) {
                // Calculer automatiquement le montant (chambre + services)
                Reservation reservation = reservationService.findReservationById(idReservation);
                if (reservation == null) {
                    showError("Réservation non trouvée");
                    return;
                }
                
                Chambre chambre = chambreService.findChambreByNumero(reservation.getNumeroChambre());
                int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
                double montantChambre = chambre.getPrixNuit() * nbNuits;
                double montantServices = reservationService.getTotalServicesAmount(idReservation);
                montantTotal = montantChambre + montantServices;
                
                // Mettre à jour le champ montant
                txtMontant.setText(String.valueOf(montantTotal));
            } else {
                montantTotal = Double.parseDouble(montantText);
            }
            
            selected.setIdReservation(idReservation);
            selected.setDateEmission(dpDateEmission.getValue());
            selected.setMontantTotal(montantTotal);
            selected.setStatut(cmbStatut.getValue());
            selected.setNotes(txtNotes.getText());
            
            controller.updateFacture(selected);
            showSuccess("Facture modifiée avec succès");
            clearForm();
            loadFactures();
        } catch (NumberFormatException e) {
            showError("Veuillez entrer des valeurs numériques valides");
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de la facture", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void deleteFacture() {
        Facture selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une facture à supprimer");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la facture");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer la facture " + selected.getIdFacture() + " ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                controller.deleteFacture(selected.getIdFacture());
                showSuccess("Facture supprimée avec succès");
                clearForm();
                loadFactures();
            } catch (Exception e) {
                logger.error("Erreur lors de la suppression de la facture", e);
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void markAsPaid() {
        Facture selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une facture à marquer comme payée");
            return;
        }
        
        try {
            selected.setStatut(Facture.Statut.PAYEE);
            controller.updateFacture(selected);
            showSuccess("Facture marquée comme payée");
            clearForm();
            loadFactures();
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la facture", e);
            showError("Erreur: " + e.getMessage());
        }
    }

    private void searchFactures() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadFactures();
            return;
        }
        
        try {
            int id = Integer.parseInt(searchTerm);
            // Pour l'instant, on recharge tout et filtre
            loadFactures();
            factureList.removeIf(f -> f.getIdFacture() != id);
            showInfo(factureList.size() + " facture(s) trouvée(s)");
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
        loadFactures();
    }
    
    private void loadFactures() {
        try {
            List<Facture> factures = controller.getAllFactures();
            factureList.clear();
            factureList.addAll(factures);
            showInfo(factures.size() + " facture(s) chargée(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des factures", e);
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
