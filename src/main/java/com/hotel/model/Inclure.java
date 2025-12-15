package com.hotel.model;

/**
 * Classe représentant la relation entre une réservation et un service supplémentaire.
 * Cette classe modélise la table de liaison "inclure".
 */
public class Inclure {
    private int idReservation;
    private int idService;
    private int quantite;
    private double prixUnitaire;

    // Constructeurs
    public Inclure() {
        this.quantite = 1;
    }

    public Inclure(int idReservation, int idService, int quantite, double prixUnitaire) {
        this.idReservation = idReservation;
        this.idService = idService;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters et Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    /**
     * Calcule le montant total (quantité × prix unitaire).
     *
     * @return le montant total
     */
    public double getMontantTotal() {
        return quantite * prixUnitaire;
    }

    @Override
    public String toString() {
        return "Inclure{" +
                "idReservation=" + idReservation +
                ", idService=" + idService +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", montantTotal=" + getMontantTotal() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inclure inclure = (Inclure) o;
        return idReservation == inclure.idReservation && idService == inclure.idService;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idReservation) * 31 + Integer.hashCode(idService);
    }
}

