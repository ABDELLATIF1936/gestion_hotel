package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

/**
 * Classe représentant une chambre de l'hôtel.
 */
public class Chambre {
    public enum Statut {
        DISPONIBLE, OCCUPEE, RESERVEE, HORS_SERVICE
    }

    public enum Categorie {
        SIMPLE, DOUBLE, SUITE
    }

    private int numeroChambre;
    private Categorie categorie;
    private Statut statut;
    private double prixNuit;
    private String description;

    // Constructeurs
    public Chambre() {
    }

    public Chambre(int numeroChambre, Categorie categorie, Statut statut, double prixNuit) 
            throws ValidationException {
        setNumeroChambre(numeroChambre);
        setCategorie(categorie);
        setStatut(statut);
        setPrixNuit(prixNuit);
    }

    // Getters et Setters
    public int getNumeroChambre() {
        return numeroChambre;
    }

    public void setNumeroChambre(int numeroChambre) throws ValidationException {
        ValidationUtil.validatePositive(numeroChambre, "Le numéro de chambre");
        this.numeroChambre = numeroChambre;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public double getPrixNuit() {
        return prixNuit;
    }

    public void setPrixNuit(double prixNuit) throws ValidationException {
        ValidationUtil.validatePositive(prixNuit, "Le prix par nuit");
        this.prixNuit = prixNuit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Vérifie si la chambre est disponible pour une réservation.
     *
     * @return true si la chambre est disponible
     */
    public boolean isDisponible() {
        return statut == Statut.DISPONIBLE;
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
     * Convertit la catégorie en String pour la base de données.
     *
     * @return la catégorie en String
     */
    public String getCategorieAsString() {
        return categorie.name();
    }

    /**
     * Crée un Statut à partir d'une String.
     *
     * @param statutString le statut en String
     * @return le Statut correspondant
     */
    public static Statut parseStatut(String statutString) {
        if (statutString == null) {
            return Statut.DISPONIBLE;
        }
        try {
            return Statut.valueOf(statutString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Statut.DISPONIBLE;
        }
    }

    /**
     * Crée une Categorie à partir d'une String.
     *
     * @param categorieString la catégorie en String
     * @return la Categorie correspondante
     */
    public static Categorie parseCategorie(String categorieString) {
        if (categorieString == null) {
            return Categorie.SIMPLE;
        }
        try {
            return Categorie.valueOf(categorieString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Categorie.SIMPLE;
        }
    }

    @Override
    public String toString() {
        return "Chambre{" +
                "numeroChambre=" + numeroChambre +
                ", categorie=" + categorie +
                ", statut=" + statut +
                ", prixNuit=" + prixNuit +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chambre chambre = (Chambre) o;
        return numeroChambre == chambre.numeroChambre;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numeroChambre);
    }
}

