package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

import java.time.LocalDate;

/**
 * Classe représentant une tâche d'entretien.
 */
public class TacheEntretien {
    public enum Type {
        NETTOYAGE, REPARATION, INSPECTION, AUTRE
    }

    public enum Statut {
        EN_ATTENTE, EN_COURS, TERMINEE, ANNULEE
    }

    private int idTache;
    private Integer idEmploye; // Peut être null si non assignée
    private int numeroChambre;
    private Type type;
    private LocalDate date;
    private Statut statut;
    private String description;
    private String notes;

    // Constructeurs
    public TacheEntretien() {
        this.statut = Statut.EN_ATTENTE;
        this.date = LocalDate.now();
    }

    public TacheEntretien(int numeroChambre, Type type, LocalDate date) throws ValidationException {
        this();
        setNumeroChambre(numeroChambre);
        setType(type);
        setDate(date);
    }

    // Getters et Setters
    public int getIdTache() {
        return idTache;
    }

    public void setIdTache(int idTache) {
        this.idTache = idTache;
    }

    public Integer getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(Integer idEmploye) {
        this.idEmploye = idEmploye;
    }

    public int getNumeroChambre() {
        return numeroChambre;
    }

    public void setNumeroChambre(int numeroChambre) throws ValidationException {
        ValidationUtil.validatePositive(numeroChambre, "Le numéro de chambre");
        this.numeroChambre = numeroChambre;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date != null ? date : LocalDate.now();
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Vérifie si la tâche est assignée à un employé.
     *
     * @return true si la tâche est assignée
     */
    public boolean isAssignee() {
        return idEmploye != null;
    }

    /**
     * Vérifie si la tâche est terminée.
     *
     * @return true si la tâche est terminée
     */
    public boolean isTerminee() {
        return statut == Statut.TERMINEE;
    }
    
    /**
     * Vérifie si la tâche est active (en cours ou planifiée).
     * Une tâche est active si :
     * - Son statut est EN_COURS ou EN_ATTENTE
     * - Elle n'est pas annulée
     * - Sa date est aujourd'hui ou dans le futur
     *
     * @return true si la tâche est active
     */
    public boolean isActive() {
        if (statut == Statut.ANNULEE || statut == Statut.TERMINEE) {
            return false;
        }
        // Une tâche est active si elle est en attente ou en cours
        // et si sa date est aujourd'hui ou dans le futur (>= aujourd'hui)
        if (date == null) {
            return false;
        }
        LocalDate aujourdhui = LocalDate.now();
        return (statut == Statut.EN_ATTENTE || statut == Statut.EN_COURS) 
                && (date.isEqual(aujourdhui) || date.isAfter(aujourdhui));
    }
    
    /**
     * Vérifie si la tâche est active pour une période donnée.
     * Une tâche est active pour une période si elle chevauche cette période.
     * Pour simplifier, on considère qu'une tâche d'entretien dure 1 jour.
     *
     * @param dateDebut début de la période
     * @param dateFin fin de la période
     * @return true si la tâche est active pour cette période
     */
    public boolean isActiveForPeriod(LocalDate dateDebut, LocalDate dateFin) {
        if (statut == Statut.ANNULEE || statut == Statut.TERMINEE) {
            return false;
        }
        // Une tâche est active pour une période si :
        // - Elle est en attente ou en cours
        // - Sa date chevauche la période (date >= dateDebut && date <= dateFin)
        if (date == null) {
            return false;
        }
        return (statut == Statut.EN_ATTENTE || statut == Statut.EN_COURS)
                && !date.isBefore(dateDebut) 
                && !date.isAfter(dateFin);
    }

    /**
     * Convertit le type en String pour la base de données.
     *
     * @return le type en String
     */
    public String getTypeAsString() {
        return type.name();
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
     * Crée un Type à partir d'une String.
     *
     * @param typeString le type en String
     * @return le Type correspondant
     */
    public static Type parseType(String typeString) {
        if (typeString == null) {
            return Type.AUTRE;
        }
        try {
            return Type.valueOf(typeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Type.AUTRE;
        }
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
        return "TacheEntretien{" +
                "idTache=" + idTache +
                ", idEmploye=" + idEmploye +
                ", numeroChambre=" + numeroChambre +
                ", type=" + type +
                ", date=" + date +
                ", statut=" + statut +
                ", description='" + description + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}

