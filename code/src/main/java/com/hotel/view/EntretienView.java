package com.hotel.view;

import com.hotel.controller.EntretienController;
import com.hotel.model.TacheEntretien;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Vue pour la gestion de l'entretien avec CRUD complet.
 */
public class EntretienView extends VBox {
    private static final Logger logger = Logger.getLogger(EntretienView.class);
    
    private EntretienController controller;
    
    // Composants du formulaire
    private TextField txtIdEmploye;
    private TextField txtNumeroChambre;
    private ComboBox<TacheEntretien.Type> cmbType;
    private DatePicker dpDate;
    private ComboBox<TacheEntretien.Statut> cmbStatut;
    private TextArea txtDescription;
    private TextArea txtNotes;
    private TextField txtSearch;
    
    // TableView
    private TableView<TacheEntretien> tableView;
    private ObservableList<TacheEntretien> tacheList;
    
    // Boutons
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnRechercher;
    private Button btnReinitialiser;
    
    // Labels de statut
    private Label lblStatus;
    
    public EntretienView() {
        try {
            controller = new EntretienController();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadTaches();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de EntretienView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        txtIdEmploye = new TextField();
        txtIdEmploye.setPromptText("ID Employé (optionnel)");
        
        txtNumeroChambre = new TextField();
        txtNumeroChambre.setPromptText("Numéro chambre");
        
        cmbType = new ComboBox<>();
        cmbType.getItems().addAll(TacheEntretien.Type.values());
        cmbType.setValue(TacheEntretien.Type.NETTOYAGE);
        
        dpDate = new DatePicker();
        dpDate.setValue(LocalDate.now());
        
        cmbStatut = new ComboBox<>();
        cmbStatut.getItems().addAll(TacheEntretien.Statut.values());
        cmbStatut.setValue(TacheEntretien.Statut.EN_ATTENTE);
        
        txtDescription = new TextArea();
        txtDescription.setPromptText("Description");
        txtDescription.setPrefRowCount(2);
        
        txtNotes = new TextArea();
        txtNotes.setPromptText("Notes");
        txtNotes.setPrefRowCount(2);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Rechercher par ID...");
        
        // TableView
        tableView = new TableView<>();
        tacheList = FXCollections.observableArrayList();
        tableView.setItems(tacheList);
        
        // Colonnes avec configuration flexible
        TableColumn<TacheEntretien, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idTache"));
        TableViewHelper.configureFixedColumn(colId, 60);
        
        TableColumn<TacheEntretien, Integer> colEmploye = new TableColumn<>("Employé");
        colEmploye.setCellValueFactory(new PropertyValueFactory<>("idEmploye"));
        TableViewHelper.configureFlexibleColumn(colEmploye, 70, 90);
        
        TableColumn<TacheEntretien, Integer> colChambre = new TableColumn<>("Chambre");
        colChambre.setCellValueFactory(new PropertyValueFactory<>("numeroChambre"));
        TableViewHelper.configureFlexibleColumn(colChambre, 70, 90);
        
        TableColumn<TacheEntretien, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cellData -> {
            TacheEntretien.Type type = cellData.getValue().getType();
            return new javafx.beans.property.SimpleStringProperty(type != null ? type.name() : "");
        });
        TableViewHelper.configureFlexibleColumn(colType, 90, 120);
        
        TableColumn<TacheEntretien, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableViewHelper.configureFlexibleColumn(colDate, 100, 120);
        
        TableColumn<TacheEntretien, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> {
            TacheEntretien.Statut stat = cellData.getValue().getStatut();
            return new javafx.beans.property.SimpleStringProperty(stat != null ? stat.name() : "");
        });
        TableViewHelper.configureFlexibleColumn(colStatut, 90, 120);
        
        TableColumn<TacheEntretien, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableViewHelper.configureFlexibleColumn(colDescription, 150, 250);
        
        tableView.getColumns().addAll(colId, colEmploye, colChambre, colType, colDate, colStatut, colDescription);
        
        // Configurer le tableau pour qu'il s'adapte à la taille disponible
        TableViewHelper.configureAutoResizeTableView(tableView);
        tableView.setMinHeight(220);
        
        // Boutons
        btnAjouter = new Button("➕ Ajouter");
        btnModifier = new Button("✏️ Modifier");
        btnSupprimer = new Button("🗑️ Supprimer");
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
        
        // Titre
        Label title = new Label("Gestion de l'Entretien");
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
        
        form.add(new Label("ID Employé:"), 0, 0);
        form.add(txtIdEmploye, 1, 0);
        form.add(new Label("Chambre *:"), 0, 1);
        form.add(txtNumeroChambre, 1, 1);
        form.add(new Label("Type *:"), 0, 2);
        form.add(cmbType, 1, 2);
        form.add(new Label("Date *:"), 0, 3);
        form.add(dpDate, 1, 3);
        form.add(new Label("Statut:"), 0, 4);
        form.add(cmbStatut, 1, 4);
        form.add(new Label("Description:"), 0, 5);
        form.add(txtDescription, 1, 5);
        form.add(new Label("Notes:"), 0, 6);
        form.add(txtNotes, 1, 6);
        
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
        
        String fieldStyle = "-fx-font-size: 14px; -fx-padding: 5;";
        txtIdEmploye.setStyle(fieldStyle);
        txtNumeroChambre.setStyle(fieldStyle);
        txtSearch.setStyle(fieldStyle);
        
        tableView.setStyle("-fx-font-size: 13px;");
    }

    private void setupEventHandlers() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });
        
        btnAjouter.setOnAction(e -> addTache());
        btnModifier.setOnAction(e -> updateTache());
        btnSupprimer.setOnAction(e -> deleteTache());
        btnRechercher.setOnAction(e -> searchTaches());
        btnReinitialiser.setOnAction(e -> {
            clearForm();
            loadTaches();
        });
    }

    private void fillForm(TacheEntretien tache) {
        txtIdEmploye.setText(tache.getIdEmploye() != null ? String.valueOf(tache.getIdEmploye()) : "");
        txtNumeroChambre.setText(String.valueOf(tache.getNumeroChambre()));
        cmbType.setValue(tache.getType());
        dpDate.setValue(tache.getDate());
        cmbStatut.setValue(tache.getStatut());
        txtDescription.setText(tache.getDescription() != null ? tache.getDescription() : "");
        txtNotes.setText(tache.getNotes() != null ? tache.getNotes() : "");
    }

    private void clearForm() {
        txtIdEmploye.clear();
        txtNumeroChambre.clear();
        cmbType.setValue(TacheEntretien.Type.NETTOYAGE);
        dpDate.setValue(LocalDate.now());
        cmbStatut.setValue(TacheEntretien.Statut.EN_ATTENTE);
        txtDescription.clear();
        txtNotes.clear();
        txtSearch.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void addTache() {
        try {
            TacheEntretien tache = new TacheEntretien();
            if (!txtIdEmploye.getText().trim().isEmpty()) {
                tache.setIdEmploye(Integer.parseInt(txtIdEmploye.getText()));
            }
            tache.setNumeroChambre(Integer.parseInt(txtNumeroChambre.getText()));
            tache.setType(cmbType.getValue());
            tache.setDate(dpDate.getValue());
            tache.setStatut(cmbStatut.getValue());
            tache.setDescription(txtDescription.getText());
            tache.setNotes(txtNotes.getText());
            
            TacheEntretien created = controller.createTache(tache);
            showSuccess("Tâche ajoutée avec succès (ID: " + created.getIdTache() + ")");
            clearForm();
            loadTaches();
            // Rafraîchir aussi la vue des chambres pour voir le changement de statut
            refreshChambreViewIfNeeded(created.getNumeroChambre());
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la tâche", e);
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private void updateTache() {
        TacheEntretien selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une tâche à modifier");
            return;
        }
        
        try {
            if (!txtIdEmploye.getText().trim().isEmpty()) {
                selected.setIdEmploye(Integer.parseInt(txtIdEmploye.getText()));
            } else {
                selected.setIdEmploye(null);
            }
            selected.setNumeroChambre(Integer.parseInt(txtNumeroChambre.getText()));
            selected.setType(cmbType.getValue());
            selected.setDate(dpDate.getValue());
            selected.setStatut(cmbStatut.getValue());
            selected.setDescription(txtDescription.getText());
            selected.setNotes(txtNotes.getText());
            
            controller.updateTache(selected);
            showSuccess("Tâche modifiée avec succès");
            clearForm();
            loadTaches();
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de la tâche", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void deleteTache() {
        TacheEntretien selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une tâche à supprimer");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la tâche");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer la tâche " + selected.getIdTache() + " ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                int numeroChambre = selected.getNumeroChambre();
                controller.deleteTache(selected.getIdTache());
                showSuccess("Tâche supprimée avec succès");
                clearForm();
                loadTaches();
                // Rafraîchir aussi la vue des chambres pour voir le changement de statut
                refreshChambreViewIfNeeded(numeroChambre);
            } catch (Exception e) {
                logger.error("Erreur lors de la suppression de la tâche", e);
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void searchTaches() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadTaches();
            return;
        }
        
        try {
            int id = Integer.parseInt(searchTerm);
            // Pour l'instant, on recharge tout et filtre
            loadTaches();
            tacheList.removeIf(t -> t.getIdTache() != id);
            showInfo(tacheList.size() + " tâche(s) trouvée(s)");
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
        loadTaches();
    }
    
    private void loadTaches() {
        try {
            List<TacheEntretien> taches = controller.getAllTaches();
            tacheList.clear();
            tacheList.addAll(taches);
            showInfo(taches.size() + " tâche(s) chargée(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des tâches", e);
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
    
    /**
     * Rafraîchit la vue des chambres si elle est ouverte pour voir le changement de statut.
     * Cette méthode est appelée après création/suppression d'une tâche d'entretien.
     */
    private void refreshChambreViewIfNeeded(int numeroChambre) {
        // Cette méthode peut être étendue pour rafraîchir automatiquement la vue des chambres
        // Pour l'instant, le rafraîchissement se fera lors du changement de page
        logger.info("Chambre " + numeroChambre + " devrait être rafraîchie pour voir le changement de statut");
    }
}
