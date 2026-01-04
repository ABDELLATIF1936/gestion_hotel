package com.hotel.view;

import com.hotel.model.RoleUtilisateur;
import com.hotel.model.TacheEntretien;
import com.hotel.security.AuthenticationService;
import com.hotel.service.interfaces.IChambreService;
import com.hotel.service.interfaces.IEntretienService;
import com.hotel.factory.ServiceFactory;
import com.hotel.util.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vue du tableau de bord spécifique pour le personnel d'entretien.
 */
public class DashboardEntretienView extends VBox {
    private static final Logger logger = Logger.getLogger(DashboardEntretienView.class);
    
    private IEntretienService entretienService;
    private IChambreService chambreService;
    
    // Labels pour les statistiques
    private Label lblTachesAssignees;
    private Label lblTachesEnAttente;
    private Label lblTachesEnCours;
    private Label lblTachesTermineesAujourdhui;
    private Label lblChambresHorsService;
    private Label lblTachesAujourdhui;

    public DashboardEntretienView() {
        initializeServices();
        setupLayout();
        loadStatistics();
    }

    private void initializeServices() {
        try {
            entretienService = ServiceFactory.getEntretienService();
            chambreService = ServiceFactory.getChambreService();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation des services", e);
        }
    }

    private void setupLayout() {
        setSpacing(20);
        setPadding(new Insets(20));
        
        // Titre
        Label title = new Label("Tableau de Bord - Personnel d'Entretien");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        
        // Grille de statistiques
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);
        
        // Cartes de statistiques spécifiques à l'entretien
        statsGrid.add(createStatCard("Tâches Assignées", "0", "#3498db"), 0, 0);
        statsGrid.add(createStatCard("Tâches en Attente", "0", "#e67e22"), 1, 0);
        statsGrid.add(createStatCard("Tâches en Cours", "0", "#f39c12"), 2, 0);
        statsGrid.add(createStatCard("Terminées Aujourd'hui", "0", "#27ae60"), 0, 1);
        statsGrid.add(createStatCard("Chambres Hors Service", "0", "#e74c3c"), 1, 1);
        statsGrid.add(createStatCard("Tâches Aujourd'hui", "0", "#9b59b6"), 2, 1);
        
        // Stocker les labels pour mise à jour
        lblTachesAssignees = (Label) ((VBox) statsGrid.getChildren().get(0)).getChildren().get(1);
        lblTachesEnAttente = (Label) ((VBox) statsGrid.getChildren().get(1)).getChildren().get(1);
        lblTachesEnCours = (Label) ((VBox) statsGrid.getChildren().get(2)).getChildren().get(1);
        lblTachesTermineesAujourdhui = (Label) ((VBox) statsGrid.getChildren().get(3)).getChildren().get(1);
        lblChambresHorsService = (Label) ((VBox) statsGrid.getChildren().get(4)).getChildren().get(1);
        lblTachesAujourdhui = (Label) ((VBox) statsGrid.getChildren().get(5)).getChildren().get(1);
        
        getChildren().addAll(title, statsGrid);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.WHITE);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.WHITE);
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    /**
     * Méthode publique pour rafraîchir les statistiques.
     * Peut être appelée depuis l'extérieur pour mettre à jour les données.
     */
    public void refreshStatistics() {
        loadStatistics();
    }
    
    private void loadStatistics() {
        try {
            // Récupérer l'ID de l'employé connecté
            Integer idEmploye = getCurrentEmployeId();
            LocalDate aujourdhui = LocalDate.now();
            
            // Tâches assignées à l'employé connecté
            try {
                if (entretienService != null && idEmploye != null) {
                    List<TacheEntretien> tachesAssignees = entretienService.getTachesByEmploye(idEmploye);
                    lblTachesAssignees.setText(String.valueOf(tachesAssignees.size()));
                } else {
                    lblTachesAssignees.setText("0");
                }
            } catch (Exception e) {
                logger.error("Erreur lors du chargement des tâches assignées", e);
                lblTachesAssignees.setText("0");
            }
            
            // Tâches en attente
            try {
                if (entretienService != null) {
                    List<TacheEntretien> tachesEnAttente = entretienService.getPendingTaches();
                    lblTachesEnAttente.setText(String.valueOf(tachesEnAttente.size()));
                } else {
                    lblTachesEnAttente.setText("0");
                }
            } catch (Exception e) {
                logger.error("Erreur lors du chargement des tâches en attente", e);
                lblTachesEnAttente.setText("0");
            }
            
            // Tâches en cours
            try {
                if (entretienService != null) {
                    List<TacheEntretien> toutesTaches = entretienService.getAllTaches();
                    long tachesEnCours = toutesTaches.stream()
                            .filter(t -> t.getStatut() == TacheEntretien.Statut.EN_COURS)
                            .count();
                    lblTachesEnCours.setText(String.valueOf(tachesEnCours));
                } else {
                    lblTachesEnCours.setText("0");
                }
            } catch (Exception e) {
                logger.error("Erreur lors du chargement des tâches en cours", e);
                lblTachesEnCours.setText("0");
            }
            
            // Tâches terminées aujourd'hui
            try {
                if (entretienService != null) {
                    List<TacheEntretien> toutesTaches = entretienService.getAllTaches();
                    long tachesTermineesAujourdhui = toutesTaches.stream()
                            .filter(t -> t.getStatut() == TacheEntretien.Statut.TERMINEE
                                    && t.getDate() != null
                                    && t.getDate().isEqual(aujourdhui))
                            .count();
                    lblTachesTermineesAujourdhui.setText(String.valueOf(tachesTermineesAujourdhui));
                } else {
                    lblTachesTermineesAujourdhui.setText("0");
                }
            } catch (Exception e) {
                logger.error("Erreur lors du chargement des tâches terminées aujourd'hui", e);
                lblTachesTermineesAujourdhui.setText("0");
            }
            
            // Chambres hors service
            try {
                if (chambreService != null) {
                    List<com.hotel.model.Chambre> toutesChambres = chambreService.getAllChambres();
                    long chambresHorsService = toutesChambres.stream()
                            .filter(c -> c.getStatut() == com.hotel.model.Chambre.Statut.HORS_SERVICE)
                            .count();
                    lblChambresHorsService.setText(String.valueOf(chambresHorsService));
                } else {
                    lblChambresHorsService.setText("0");
                }
            } catch (Exception e) {
                logger.error("Erreur lors du chargement des chambres hors service", e);
                lblChambresHorsService.setText("0");
            }
            
            // Tâches prévues pour aujourd'hui
            try {
                if (entretienService != null) {
                    List<TacheEntretien> tachesAujourdhui = entretienService.getTachesByDate(aujourdhui);
                    lblTachesAujourdhui.setText(String.valueOf(tachesAujourdhui.size()));
                } else {
                    lblTachesAujourdhui.setText("0");
                }
            } catch (Exception e) {
                logger.error("Erreur lors du chargement des tâches d'aujourd'hui", e);
                lblTachesAujourdhui.setText("0");
            }
            
        } catch (Exception e) {
            logger.error("Erreur générale lors du chargement des statistiques", e);
        }
    }
    
    /**
     * Récupère l'ID de l'employé connecté.
     * @return l'ID de l'employé ou null si non trouvé
     */
    private Integer getCurrentEmployeId() {
        try {
            com.hotel.model.Utilisateur utilisateur = AuthenticationService.getUtilisateurConnecte();
            if (utilisateur != null) {
                return utilisateur.getIdEmploye();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'ID employé", e);
        }
        return null;
    }
}


