package com.hotel.view;

import com.hotel.service.interfaces.*;
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

/**
 * Vue du tableau de bord avec statistiques.
 */
public class DashboardView extends VBox {
    private static final Logger logger = Logger.getLogger(DashboardView.class);
    
    private IClientService clientService;
    private IChambreService chambreService;
    private IReservationService reservationService;
    private IFacturationService facturationService;
    
    // Labels pour les statistiques
    private Label lblTotalClients;
    private Label lblChambresDisponibles;
    private Label lblReservationsActives;
    private Label lblFacturesEnAttente;
    private Label lblTauxOccupation;
    private Label lblRevenusMois;

    public DashboardView() {
        initializeServices();
        setupLayout();
        loadStatistics();
    }

    private void initializeServices() {
        try {
            clientService = ServiceFactory.getClientService();
            chambreService = ServiceFactory.getChambreService();
            reservationService = ServiceFactory.getReservationService();
            facturationService = ServiceFactory.getFacturationService();
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation des services", e);
        }
    }

    private void setupLayout() {
        setSpacing(20);
        setPadding(new Insets(20));
        
        // Titre
        Label title = new Label("Tableau de Bord");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        
        // Grille de statistiques
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);
        
        // Cartes de statistiques
        statsGrid.add(createStatCard("Total Clients", "0", "#3498db"), 0, 0);
        statsGrid.add(createStatCard("Chambres Disponibles", "0", "#27ae60"), 1, 0);
        statsGrid.add(createStatCard("Réservations Actives", "0", "#e67e22"), 2, 0);
        statsGrid.add(createStatCard("Factures en Attente", "0", "#e74c3c"), 0, 1);
        statsGrid.add(createStatCard("Taux d'Occupation", "0%", "#9b59b6"), 1, 1);
        statsGrid.add(createStatCard("Revenus du Mois", "0 €", "#16a085"), 2, 1);
        
        // Stocker les labels pour mise à jour
        lblTotalClients = (Label) ((VBox) statsGrid.getChildren().get(0)).getChildren().get(1);
        lblChambresDisponibles = (Label) ((VBox) statsGrid.getChildren().get(1)).getChildren().get(1);
        lblReservationsActives = (Label) ((VBox) statsGrid.getChildren().get(2)).getChildren().get(1);
        lblFacturesEnAttente = (Label) ((VBox) statsGrid.getChildren().get(3)).getChildren().get(1);
        lblTauxOccupation = (Label) ((VBox) statsGrid.getChildren().get(4)).getChildren().get(1);
        lblRevenusMois = (Label) ((VBox) statsGrid.getChildren().get(5)).getChildren().get(1);
        
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
        // Total clients
        try {
            if (clientService != null) {
                int totalClients = clientService.getAllClients().size();
                lblTotalClients.setText(String.valueOf(totalClients));
            } else {
                lblTotalClients.setText("0");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du total clients", e);
            lblTotalClients.setText("0");
        }
        
        // Chambres disponibles
        try {
            if (chambreService != null) {
                int chambresDisponibles = chambreService.getAvailableChambres().size();
                lblChambresDisponibles.setText(String.valueOf(chambresDisponibles));
            } else {
                lblChambresDisponibles.setText("0");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des chambres disponibles", e);
            lblChambresDisponibles.setText("0");
        }
        
        // Réservations actives
        try {
            if (reservationService != null) {
                int reservationsActives = reservationService.getActiveReservations().size();
                lblReservationsActives.setText(String.valueOf(reservationsActives));
            } else {
                lblReservationsActives.setText("0");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des réservations actives", e);
            lblReservationsActives.setText("0");
        }
        
        // Factures en attente
        try {
            if (facturationService != null) {
                int facturesEnAttente = facturationService.getPendingFactures().size();
                lblFacturesEnAttente.setText(String.valueOf(facturesEnAttente));
            } else {
                lblFacturesEnAttente.setText("0");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des factures en attente", e);
            lblFacturesEnAttente.setText("0");
        }
        
        // Taux d'occupation
        try {
            if (chambreService != null) {
                int totalChambres = chambreService.getAllChambres().size();
                int chambresDisponibles = chambreService.getAvailableChambres().size();
                if (totalChambres > 0) {
                    double tauxOccupation = ((double)(totalChambres - chambresDisponibles) / totalChambres) * 100;
                    lblTauxOccupation.setText(String.format("%.1f%%", tauxOccupation));
                } else {
                    lblTauxOccupation.setText("0%");
                }
            } else {
                lblTauxOccupation.setText("0%");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du calcul du taux d'occupation", e);
            lblTauxOccupation.setText("0%");
        }
        
        // Revenus du mois
        try {
            if (facturationService != null) {
                java.time.LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
                java.time.LocalDate finMois = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                List<com.hotel.model.Facture> factures = facturationService.getFacturesByPeriod(debutMois, finMois);
                double revenus = factures.stream()
                    .filter(f -> f.getStatut() == com.hotel.model.Facture.Statut.PAYEE)
                    .mapToDouble(com.hotel.model.Facture::getMontantTotal)
                    .sum();
                lblRevenusMois.setText(String.format("%.2f €", revenus));
            } else {
                lblRevenusMois.setText("0 €");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du calcul des revenus du mois", e);
            lblRevenusMois.setText("0 €");
        }
    }
}
