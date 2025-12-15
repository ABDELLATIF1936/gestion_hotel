package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.DateUtil;
import com.hotel.util.ValidationUtil;

import java.time.LocalDate;

/**
 * Classe représentant une réservation.
 */
public class Reservation {
    public enum Statut {
        EN_ATTENTE, CONFIRMEE, EN_COURS, TERMINEE, ANNULEE
    }

    private int idReservation;
    private int idClient;
    private int numeroChambre;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Statut statut;
    private int nbPersonnes;
    private String notes;

    // Constructeurs
    public Reservation() {
    }

    public Reservation(int idClient, int numeroChambre, LocalDate dateDebut, 
                      LocalDate dateFin, int nbPersonnes) throws ValidationException {
        setIdClient(idClient);
        setNumeroChambre(numeroChambre);
        setDateDebut(dateDebut);
        setDateFin(dateFin);
        setNbPersonnes(nbPersonnes);
        this.statut = Statut.EN_ATTENTE;
    }

    // Getters et Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) throws ValidationException {
        ValidationUtil.validatePositive(idClient, "L'ID du client");
        this.idClient = idClient;
    }

    public int getNumeroChambre() {
        return numeroChambre;
    }

    public void setNumeroChambre(int numeroChambre) throws ValidationException {
        ValidationUtil.validatePositive(numeroChambre, "Le numéro de chambre");
        this.numeroChambre = numeroChambre;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) throws ValidationException {
        if (dateDebut == null) {
            throw new ValidationException("La date de début ne peut pas être nulle");
        }
        if (DateUtil.isPastDate(dateDebut) && !dateDebut.isEqual(LocalDate.now())) {
            throw new ValidationException("La date de début ne peut pas être dans le passé");
        }
        this.dateDebut = dateDebut;
    }
    
    /**
     * Définit la date de début sans validation (pour le chargement depuis la base de données).
     * @param dateDebut la date de début
     */
    public void setDateDebutFromDB(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) throws ValidationException {
        if (dateFin == null) {
            throw new ValidationException("La date de fin ne peut pas être nulle");
        }
        if (dateDebut != null && (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut))) {
            throw new ValidationException("La date de fin doit être après la date de début");
        }
        this.dateFin = dateFin;
    }
    
    /**
     * Définit la date de fin sans validation (pour le chargement depuis la base de données).
     * @param dateFin la date de fin
     */
    public void setDateFinFromDB(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(int nbPersonnes) throws ValidationException {
        ValidationUtil.validateRange(nbPersonnes, 1, 10, "Le nombre de personnes");
        this.nbPersonnes = nbPersonnes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Calcule le nombre de nuits de la réservation.
     *
     * @return le nombre de nuits
     */
    public int getNbNuits() {
        return DateUtil.calculateNights(dateDebut, dateFin);
    }

    /**
     * Vérifie si la réservation est en cours.
     *
     * @return true si la réservation est en cours
     */
    public boolean isEnCours() {
        if (dateDebut == null || dateFin == null) {
            return false;
        }
        LocalDate aujourdhui = LocalDate.now();
        return !aujourdhui.isBefore(dateDebut) && !aujourdhui.isAfter(dateFin);
    }

    /**
     * Vérifie si la réservation peut être annulée.
     *
     * @return true si la réservation peut être annulée
     */
    public boolean peutEtreAnnulee() {
        return statut == Statut.EN_ATTENTE || statut == Statut.CONFIRMEE;
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
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", idClient=" + idClient +
                ", numeroChambre=" + numeroChambre +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut=" + statut +
                ", nbPersonnes=" + nbPersonnes +
                ", notes='" + notes + '\'' +
                '}';
    }
}

