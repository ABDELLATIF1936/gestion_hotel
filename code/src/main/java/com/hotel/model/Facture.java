package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

import java.time.LocalDate;

/**
 * Classe représentant une facture.
 */
public class Facture {
    public enum Statut {
        EN_ATTENTE, PAYEE, ANNULEE
    }

    private int idFacture;
    private int idReservation;
    private LocalDate dateEmission;
    private double montantTotal;
    private Statut statut;
    private String notes;

    // Constructeurs
    public Facture() {
        this.dateEmission = LocalDate.now();
        this.statut = Statut.EN_ATTENTE;
    }

    public Facture(int idReservation, double montantTotal) throws ValidationException {
        this();
        setIdReservation(idReservation);
        setMontantTotal(montantTotal);
    }

    // Getters et Setters
    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) throws ValidationException {
        ValidationUtil.validatePositive(idReservation, "L'ID de réservation");
        this.idReservation = idReservation;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission != null ? dateEmission : LocalDate.now();
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) throws ValidationException {
        ValidationUtil.validateNonNegative(montantTotal, "Le montant total");
        this.montantTotal = montantTotal;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Vérifie si la facture est payée.
     *
     * @return true si la facture est payée
     */
    public boolean isPayee() {
        return statut == Statut.PAYEE;
    }

    /**
     * Vérifie si la facture peut être payée.
     *
     * @return true si la facture peut être payée
     */
    public boolean peutEtrePayee() {
        return statut == Statut.EN_ATTENTE;
    }

    /**
     * Convertit le statut en String pour la base de données.
     *
     * @return le statut en String
     */
    public String getStatutAsString() {
        return statut.name();
    }

    /**
     * Crée un Statut à partir d'une String.
     *
     * @param statutString le statut en String
     * @return le Statut correspondant
     */
    public static Statut parseStatut(String statutString) {
        if (statutString == null) {
            return Statut.EN_ATTENTE;
        }
        try {
            return Statut.valueOf(statutString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Statut.EN_ATTENTE;
        }
    }

    @Override
    public String toString() {
        return "Facture{" +
                "idFacture=" + idFacture +
                ", idReservation=" + idReservation +
                ", dateEmission=" + dateEmission +
                ", montantTotal=" + montantTotal +
                ", statut=" + statut +
                ", notes='" + notes + '\'' +
                '}';
    }
}

