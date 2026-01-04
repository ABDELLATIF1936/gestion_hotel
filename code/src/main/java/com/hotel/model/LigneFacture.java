package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

/**
 * Classe représentant une ligne de facture.
 */
public class LigneFacture {
    private int idLigne;
    private int idFacture;
    private Integer idService; // Peut être null si c'est une ligne pour la chambre
    private String description;
    private int quantite;
    private double prixUnitaire;
    private double montantTotal;

    // Constructeurs
    public LigneFacture() {
        this.quantite = 1;
    }

    public LigneFacture(int idFacture, String description, int quantite, double prixUnitaire) 
            throws ValidationException {
        setIdFacture(idFacture);
        setDescription(description);
        setQuantite(quantite);
        setPrixUnitaire(prixUnitaire);
        calculerMontantTotal();
    }

    // Getters et Setters
    public int getIdLigne() {
        return idLigne;
    }

    public void setIdLigne(int idLigne) {
        this.idLigne = idLigne;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) throws ValidationException {
        ValidationUtil.validatePositive(idFacture, "L'ID de facture");
        this.idFacture = idFacture;
    }

    public Integer getIdService() {
        return idService;
    }

    public void setIdService(Integer idService) {
        this.idService = idService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws ValidationException {
        ValidationUtil.validateNotEmpty(description, "La description");
        this.description = description.trim();
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) throws ValidationException {
        ValidationUtil.validatePositive(quantite, "La quantité");
        this.quantite = quantite;
        calculerMontantTotal();
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) throws ValidationException {
        ValidationUtil.validateNonNegative(prixUnitaire, "Le prix unitaire");
        this.prixUnitaire = prixUnitaire;
        calculerMontantTotal();
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    /**
     * Calcule le montant total de la ligne (quantité × prix unitaire).
     */
    private void calculerMontantTotal() {
        this.montantTotal = quantite * prixUnitaire;
    }

    @Override
    public String toString() {
        return "LigneFacture{" +
                "idLigne=" + idLigne +
                ", idFacture=" + idFacture +
                ", idService=" + idService +
                ", description='" + description + '\'' +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", montantTotal=" + montantTotal +
                '}';
    }
}

