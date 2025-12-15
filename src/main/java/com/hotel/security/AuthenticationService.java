package com.hotel.security;

import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;
import com.hotel.util.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service d'authentification pour gérer les sessions utilisateurs.
 */
public class AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationService.class);
    
    private static Utilisateur utilisateurConnecte = null;
    private static Map<String, Integer> tentativesConnexion = new HashMap<>();
    private static Map<String, Long> blocages = new HashMap<>();
    
    private static final int MAX_TENTATIVES = 3;
    private static final long DUREE_BLOCAGE_MS = 5 * 60 * 1000; // 5 minutes
    
    /**
     * Authentifie un utilisateur.
     *
     * @param username le nom d'utilisateur
     * @param password le mot de passe en clair
     * @param utilisateurDAO le DAO pour récupérer l'utilisateur
     * @return true si l'authentification réussit
     */
    public static boolean authentifier(String username, String password, 
                                       com.hotel.dao.interfaces.IUtilisateurDAO utilisateurDAO) {
        if (username == null || password == null || username.trim().isEmpty()) {
            logger.warn("Tentative de connexion avec des identifiants vides");
            return false;
        }
        
        username = username.trim().toLowerCase();
        
        // Vérifier si le compte est bloqué
        if (estBloque(username)) {
            logger.warn("Tentative de connexion pour un compte bloqué: " + username);
            return false;
        }
        
        try {
            // Récupérer l'utilisateur
            Utilisateur utilisateur = utilisateurDAO.findByUsername(username);
            
            if (utilisateur == null) {
                incrementerTentative(username);
                logger.warn("Tentative de connexion avec un nom d'utilisateur inexistant: " + username);
                return false;
            }
            
            // Vérifier si le compte est actif
            if (!utilisateur.isActif()) {
                logger.warn("Tentative de connexion avec un compte inactif: " + username);
                return false;
            }
            
            // Vérifier le mot de passe
            if (!PasswordUtils.verifyPassword(password, utilisateur.getPasswordHash())) {
                incrementerTentative(username);
                logger.warn("Tentative de connexion avec un mot de passe incorrect: " + username);
                return false;
            }
            
            // Authentification réussie
            utilisateurConnecte = utilisateur;
            utilisateur.setDernierLogin(LocalDateTime.now());
            utilisateurDAO.updateLastLogin(utilisateur.getIdUtilisateur());
            
            // Réinitialiser les tentatives
            tentativesConnexion.remove(username);
            blocages.remove(username);
            
            logger.info("Connexion réussie pour l'utilisateur: " + username + " (Rôle: " + utilisateur.getRole() + ")");
            return true;
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification", e);
            return false;
        }
    }
    
    /**
     * Déconnecte l'utilisateur actuel.
     */
    public static void deconnecter() {
        if (utilisateurConnecte != null) {
            logger.info("Déconnexion de l'utilisateur: " + utilisateurConnecte.getUsername());
            utilisateurConnecte = null;
        }
    }
    
    /**
     * Récupère l'utilisateur actuellement connecté.
     *
     * @return l'utilisateur connecté ou null
     */
    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    /**
     * Définit l'utilisateur connecté (utilisé après modification du profil).
     *
     * @param utilisateur l'utilisateur à définir
     */
    public static void setUtilisateurConnecte(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }
    
    /**
     * Vérifie si un utilisateur est connecté.
     *
     * @return true si un utilisateur est connecté
     */
    public static boolean estConnecte() {
        return utilisateurConnecte != null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a une permission.
     *
     * @param permission la permission à vérifier
     * @return true si l'utilisateur a la permission
     */
    public static boolean hasPermission(String permission) {
        if (utilisateurConnecte == null) {
            return false;
        }
        return PermissionManager.hasPermission(utilisateurConnecte.getRole(), permission);
    }
    
    /**
     * Vérifie si l'utilisateur connecté a un rôle spécifique.
     *
     * @param role le rôle à vérifier
     * @return true si l'utilisateur a le rôle
     */
    public static boolean hasRole(RoleUtilisateur role) {
        if (utilisateurConnecte == null) {
            return false;
        }
        return utilisateurConnecte.getRole() == role;
    }
    
    /**
     * Incrémente le nombre de tentatives de connexion.
     */
    private static void incrementerTentative(String username) {
        int tentatives = tentativesConnexion.getOrDefault(username, 0) + 1;
        tentativesConnexion.put(username, tentatives);
        
        if (tentatives >= MAX_TENTATIVES) {
            blocages.put(username, System.currentTimeMillis());
            logger.warn("Compte bloqué après " + MAX_TENTATIVES + " tentatives échouées: " + username);
        }
    }
    
    /**
     * Vérifie si un compte est bloqué.
     */
    private static boolean estBloque(String username) {
        Long blocageTime = blocages.get(username);
        if (blocageTime == null) {
            return false;
        }
        
        long tempsEcoule = System.currentTimeMillis() - blocageTime;
        if (tempsEcoule >= DUREE_BLOCAGE_MS) {
            // Débloquer
            blocages.remove(username);
            tentativesConnexion.remove(username);
            return false;
        }
        
        return true;
    }
    
    /**
     * Réinitialise les tentatives pour un utilisateur (utilisé après déblocage).
     */
    public static void reinitialiserTentatives(String username) {
        tentativesConnexion.remove(username);
        blocages.remove(username);
    }
}


