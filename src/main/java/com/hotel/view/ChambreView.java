package com.hotel.view;

import com.hotel.controller.ChambreController;
import com.hotel.model.Chambre;
import com.hotel.model.RoleUtilisateur;
import com.hotel.security.AuthenticationService;
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
 * Vue pour la gestion des chambres avec CRUD complet.
 */
public class ChambreView extends VBox {
    private static final Logger logger = Logger.getLogger(ChambreView.class);
    
    private ChambreController controller;
    
    // Composants du formulaire
    private TextField txtNumero;
    private ComboBox<Chambre.Categorie> cmbCategorie;
    private ComboBox<Chambre.Statut> cmbStatut;
    private TextField txtPrix;
    private TextArea txtDescription;
    private TextField txtSearch;
    
    // TableView
    private TableView<Chambre> tableView;
    private ObservableList<Chambre> chambreList;
    
    // Boutons
    private Button btnAjouter;
    private Button btnModifier;
    private Button btnSupprimer;
    private Button btnRechercher;
    private Button btnReinitialiser;
    private Button btnDisponibles;
    
    // Labels de statut
    private Label lblStatus;

    public ChambreView() {
        try {
            controller = new ChambreController();
            initializeComponents();
            setupLayout();
            setupStyles();
            setupEventHandlers();
            loadChambres();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de ChambreView", e);
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        // Champs de formulaire
        txtNumero = new TextField();
        txtNumero.setPromptText("Numéro de chambre");
        
        cmbCategorie = new ComboBox<>();
        cmbCategorie.getItems().addAll(Chambre.Categorie.values());
        cmbCategorie.setValue(Chambre.Categorie.SIMPLE);
        
        cmbStatut = new ComboBox<>();
        cmbStatut.getItems().addAll(Chambre.Statut.values());
        cmbStatut.setValue(Chambre.Statut.DISPONIBLE);
        
        txtPrix = new TextField();
        txtPrix.setPromptText("Prix par nuit");
        
        txtDescription = new TextArea();
        txtDescription.setPromptText("Description de la chambre");
        txtDescription.setPrefRowCount(3);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Rechercher par numéro...");
        
        // TableView
        tableView = new TableView<>();
        chambreList = FXCollections.observableArrayList();
        tableView.setItems(chambreList);
        
        // Colonnes
        TableColumn<Chambre, Integer> colNumero = new TableColumn<>("Numéro");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroChambre"));
        TableViewHelper.configureFixedColumn(colNumero, 80);
        
        TableColumn<Chambre, String> colCategorie = new TableColumn<>("Catégorie");
        colCategorie.setCellValueFactory(cellData -> {
            Chambre.Categorie cat = cellData.getValue().getCategorie();
            return new javafx.beans.property.SimpleStringProperty(cat != null ? cat.name() : "");
        });
        TableViewHelper.configureFlexibleColumn(colCategorie, 90, 120);
        
        TableColumn<Chambre, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> {
            Chambre.Statut stat = cellData.getValue().getStatut();
            return new javafx.beans.property.SimpleStringProperty(stat != null ? stat.name() : "");
        });
        TableViewHelper.configureFlexibleColumn(colStatut, 100, 140);
        
        TableColumn<Chambre, Double> colPrix = new TableColumn<>("Prix/Nuit");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixNuit"));
        TableViewHelper.configureFlexibleColumn(colPrix, 90, 110);
        colPrix.setCellFactory(column -> new TableCell<Chambre, Double>() {
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
        
        TableColumn<Chambre, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableViewHelper.configureFlexibleColumn(colDescription, 200, 300);
        
        tableView.getColumns().addAll(colNumero, colCategorie, colStatut, colPrix, colDescription);
        
        // Configurer le tableau pour qu'il s'adapte à la taille disponible
        TableViewHelper.configureAutoResizeTableView(tableView);
        tableView.setMinHeight(220);
        
        // Boutons
        btnAjouter = new Button("➕ Ajouter");
        btnModifier = new Button("✏️ Modifier");
        btnSupprimer = new Button("🗑️ Supprimer");
        btnRechercher = new Button("🔍 Rechercher");
        btnReinitialiser = new Button("🔄 Réinitialiser");
        btnDisponibles = new Button("✅ Disponibles");
        
        // Label de statut
        lblStatus = new Label();
        lblStatus.setWrapText(true);
    }
    
    /**
     * Adapte l'interface selon le rôle de l'utilisateur.
     * Les réceptionnistes ne peuvent que consulter et changer le statut.
     */
    private void adaptInterfaceToRole() {
        if (AuthenticationService.getUtilisateurConnecte() == null) {
            return;
        }
        
        boolean isReceptionniste = AuthenticationService.getUtilisateurConnecte().getRole() == RoleUtilisateur.RECEPTIONNISTE;
        
        if (isReceptionniste) {
            // Désactiver les boutons d'ajout, modification et suppression
            btnAjouter.setDisable(true);
            btnAjouter.setVisible(false);
            btnModifier.setDisable(true);
            btnModifier.setVisible(false);
            btnSupprimer.setDisable(true);
            btnSupprimer.setVisible(false);
            
            // Désactiver les champs du formulaire sauf le statut
            txtNumero.setDisable(true);
            cmbCategorie.setDisable(true);
            txtPrix.setDisable(true);
            txtDescription.setDisable(true);
            
            // Le statut reste modifiable
            cmbStatut.setDisable(false);
        }
    }

    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(10));
        
        // Conteneur principal avec scroll
        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(5));
        
        // Titre
        Label title = new Label("Gestion des Chambres");
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
            btnDisponibles
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
        
        form.add(new Label("Numéro *:"), 0, 0);
        form.add(txtNumero, 1, 0);
        form.add(new Label("Catégorie *:"), 0, 1);
        form.add(cmbCategorie, 1, 1);
        form.add(new Label("Statut *:"), 0, 2);
        form.add(cmbStatut, 1, 2);
        form.add(new Label("Prix/Nuit *:"), 0, 3);
        form.add(txtPrix, 1, 3);
        form.add(new Label("Description:"), 0, 4);
        form.add(txtDescription, 1, 4);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
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
        btnDisponibles.setStyle(buttonStyle);
        
        btnSupprimer.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        String fieldStyle = "-fx-font-size: 14px; -fx-padding: 5;";
        txtNumero.setStyle(fieldStyle);
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
        
        btnAjouter.setOnAction(e -> addChambre());
        btnModifier.setOnAction(e -> updateChambre());
        btnSupprimer.setOnAction(e -> deleteChambre());
        btnRechercher.setOnAction(e -> searchChambres());
        btnReinitialiser.setOnAction(e -> {
            clearForm();
            loadChambres();
        });
        btnDisponibles.setOnAction(e -> loadAvailableChambres());
    }

    private void fillForm(Chambre chambre) {
        txtNumero.setText(String.valueOf(chambre.getNumeroChambre()));
        cmbCategorie.setValue(chambre.getCategorie());
        cmbStatut.setValue(chambre.getStatut());
        txtPrix.setText(String.valueOf(chambre.getPrixNuit()));
        txtDescription.setText(chambre.getDescription() != null ? chambre.getDescription() : "");
    }

    private void clearForm() {
        txtNumero.clear();
        cmbCategorie.setValue(Chambre.Categorie.SIMPLE);
        cmbStatut.setValue(Chambre.Statut.DISPONIBLE);
        txtPrix.clear();
        txtDescription.clear();
        txtSearch.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void addChambre() {
        try {
            Chambre chambre = new Chambre();
            chambre.setNumeroChambre(Integer.parseInt(txtNumero.getText()));
            chambre.setCategorie(cmbCategorie.getValue());
            chambre.setStatut(cmbStatut.getValue());
            chambre.setPrixNuit(Double.parseDouble(txtPrix.getText()));
            chambre.setDescription(txtDescription.getText());
            
            Chambre created = controller.createChambre(chambre);
            showSuccess("Chambre ajoutée avec succès (Numéro: " + created.getNumeroChambre() + ")");
            clearForm();
            loadChambres();
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la chambre", e);
            showError("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private void updateChambre() {
        Chambre selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une chambre à modifier");
            return;
        }
        
        try {
            boolean isReceptionniste = AuthenticationService.getUtilisateurConnecte() != null &&
                                       AuthenticationService.getUtilisateurConnecte().getRole() == RoleUtilisateur.RECEPTIONNISTE;
            
            if (isReceptionniste) {
                // Les réceptionnistes ne peuvent modifier que le statut
                selected.setStatut(cmbStatut.getValue());
                // Utiliser updateChambreStatut au lieu de updateChambre
                controller.updateChambreStatut(selected.getNumeroChambre(), selected.getStatut());
                showSuccess("Statut de la chambre modifié avec succès");
            } else {
                // Les admins peuvent tout modifier
                selected.setCategorie(cmbCategorie.getValue());
                selected.setStatut(cmbStatut.getValue());
                selected.setPrixNuit(Double.parseDouble(txtPrix.getText()));
                selected.setDescription(txtDescription.getText());
                
                controller.updateChambre(selected);
                showSuccess("Chambre modifiée avec succès");
            }
            clearForm();
            loadChambres();
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de la chambre", e);
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void deleteChambre() {
        Chambre selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une chambre à supprimer");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la chambre");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer la chambre " + selected.getNumeroChambre() + " ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                controller.deleteChambre(selected.getNumeroChambre());
                showSuccess("Chambre supprimée avec succès");
                clearForm();
                loadChambres();
            } catch (Exception e) {
                logger.error("Erreur lors de la suppression de la chambre", e);
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void searchChambres() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadChambres();
            return;
        }
        
        try {
            int numero = Integer.parseInt(searchTerm);
            Chambre chambre = controller.getChambreByNumero(numero);
            chambreList.clear();
            if (chambre != null) {
                chambreList.add(chambre);
                showInfo("1 chambre trouvée");
            } else {
                showInfo("Aucune chambre trouvée");
            }
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un numéro valide");
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
        loadChambres();
    }
    
    private void loadChambres() {
        try {
            List<Chambre> chambres = controller.getAllChambres();
            chambreList.clear();
            chambreList.addAll(chambres);
            showInfo(chambres.size() + " chambre(s) chargée(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des chambres", e);
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void loadAvailableChambres() {
        try {
            List<Chambre> chambres = controller.getAvailableChambres();
            chambreList.clear();
            chambreList.addAll(chambres);
            showInfo(chambres.size() + " chambre(s) disponible(s)");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des chambres disponibles", e);
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
