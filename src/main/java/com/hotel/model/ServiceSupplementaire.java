package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

/**
 * Classe représentant un service supplémentaire proposé par l'hôtel.
 */
public class ServiceSupplementaire {
    private int idService;
    private String nom;
    private double prix;
    private String description;
    private boolean actif;

    // Constructeurs
    public ServiceSupplementaire() {
        this.actif = true;
    }

    public ServiceSupplementaire(String nom, double prix) throws ValidationException {
        this();
        setNom(nom);
        setPrix(prix);
    }

    // Getters et Setters
    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) throws ValidationException {
        ValidationUtil.validateNotEmpty(nom, "Le nom du service");
        this.nom = nom.trim();
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) throws ValidationException {
        ValidationUtil.validateNonNegative(prix, "Le prix");
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "ServiceSupplementaire{" +
                "idService=" + idService +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                ", actif=" + actif +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceSupplementaire that = (ServiceSupplementaire) o;
        return idService == that.idService;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idService);
    }
}

