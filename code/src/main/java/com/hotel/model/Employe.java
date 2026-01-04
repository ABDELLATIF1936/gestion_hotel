package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

/**
 * Classe représentant un employé de l'hôtel.
 */
public class Employe {
    public enum Role {
        RECEPTIONNISTE, ENTRETIEN, MANAGER, ADMINISTRATEUR
    }

    private int idEmploye;
    private String nom;
    private String prenom;
    private Role role;
    private String telephone;
    private String email;
    private boolean actif;

    // Constructeurs
    public Employe() {
        this.actif = true;
    }

    public Employe(String nom, String prenom, Role role) throws ValidationException {
        this();
        setNom(nom);
        setPrenom(prenom);
        setRole(role);
    }

    // Getters et Setters
    public int getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) throws ValidationException {
        if (telephone != null && !telephone.trim().isEmpty()) {
            ValidationUtil.validatePhone(telephone);
            this.telephone = telephone.replaceAll("[\\s-()]", "");
        } else {
            this.telephone = null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidationException {
        if (email != null && !email.trim().isEmpty()) {
            ValidationUtil.validateEmail(email);
            this.email = email.toLowerCase().trim();
        } else {
            this.email = null;
        }
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    /**
     * Retourne le nom complet de l'employé.
     *
     * @return nom + prénom
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    /**
     * Convertit le rôle en String pour la base de données.
     *
     * @return le rôle en String
     */
    public String getRoleAsString() {
        return role.name();
    }

    /**
     * Crée un Role à partir d'une String.
     *
     * @param roleString le rôle en String
     * @return le Role correspondant
     */
    public static Role parseRole(String roleString) {
        if (roleString == null) {
            return Role.ENTRETIEN;
        }
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.ENTRETIEN;
        }
    }

    @Override
    public String toString() {
        return "Employe{" +
                "idEmploye=" + idEmploye +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", role=" + role +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", actif=" + actif +
                '}';
    }
}

