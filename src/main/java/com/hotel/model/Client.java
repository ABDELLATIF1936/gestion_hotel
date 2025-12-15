package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

/**
 * Classe représentant un client de l'hôtel.
 */
public class Client {
    private int idClient;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String typeClient; // REGULIER, VIP, ENTREPRISE, etc.

    // Constructeurs
    public Client() {
    }

    public Client(String nom, String prenom, String telephone, String email, String typeClient) 
            throws ValidationException {
        setNom(nom);
        setPrenom(prenom);
        setTelephone(telephone);
        setEmail(email);
        setTypeClient(typeClient);
    }

    // Getters et Setters
    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) throws ValidationException {
        ValidationUtil.validateNotEmpty(nom, "Le nom");
        this.nom = nom.trim();
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) throws ValidationException {
        ValidationUtil.validateNotEmpty(prenom, "Le prénom");
        this.prenom = prenom.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) throws ValidationException {
        ValidationUtil.validatePhone(telephone);
        this.telephone = telephone.replaceAll("[\\s-()]", "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidationException {
        ValidationUtil.validateEmail(email);
        this.email = email.toLowerCase().trim();
    }

    public String getTypeClient() {
        return typeClient;
    }

    public void setTypeClient(String typeClient) throws ValidationException {
        ValidationUtil.validateNotEmpty(typeClient, "Le type de client");
        this.typeClient = typeClient.toUpperCase().trim();
    }

    /**
     * Retourne le nom complet du client.
     *
     * @return nom + prénom
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", typeClient='" + typeClient + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return idClient == client.idClient;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idClient);
    }
}

