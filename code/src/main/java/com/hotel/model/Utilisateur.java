package com.hotel.model;

import com.hotel.exception.ValidationException;
import com.hotel.util.ValidationUtil;

import java.time.LocalDateTime;

/**
 * Classe représentant un utilisateur du système avec authentification.
 */
public class Utilisateur {
    private int idUtilisateur;
    private String username;
    private String passwordHash;
    private int idEmploye;
    private Employe employe; // Relation optionnelle
    private RoleUtilisateur role;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dernierLogin;

    // Constructeurs
    public Utilisateur() {
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
    }

    public Utilisateur(String username, String passwordHash, int idEmploye, RoleUtilisateur role) 
            throws ValidationException {
        this();
        setUsername(username);
        setPasswordHash(passwordHash);
        setIdEmploye(idEmploye);
        setRole(role);
    }

    // Getters et Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws ValidationException {
        ValidationUtil.validateNotEmpty(username, "Le nom d'utilisateur");
        if (username.length() < 3 || username.length() > 50) {
            throw new ValidationException("Le nom d'utilisateur doit contenir entre 3 et 50 caractères");
        }
        this.username = username.trim().toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) throws ValidationException {
        ValidationUtil.validateNotEmpty(passwordHash, "Le mot de passe");
        this.passwordHash = passwordHash;
    }

    public int getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(int idEmploye) throws ValidationException {
        ValidationUtil.validatePositive(idEmploye, "L'ID de l'employé");
        this.idEmploye = idEmploye;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
        if (role == null) {
            throw new IllegalArgumentException("Le rôle ne peut pas être null");
        }
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDernierLogin() {
        return dernierLogin;
    }

    public void setDernierLogin(LocalDateTime dernierLogin) {
        this.dernierLogin = dernierLogin;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", username='" + username + '\'' +
                ", idEmploye=" + idEmploye +
                ", role=" + role +
                ", actif=" + actif +
                '}';
    }
}


