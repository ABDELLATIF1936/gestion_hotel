package com.hotel.controller;

import com.hotel.exception.ServiceException;
import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;
import com.hotel.model.Employe;
import com.hotel.service.interfaces.IUtilisateurService;
import com.hotel.service.impl.UtilisateurServiceImpl;
import com.hotel.dao.interfaces.IEmployeDAO;
import com.hotel.factory.DAOFactory;

import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs.
 */
public class UtilisateurController {
    private final IUtilisateurService utilisateurService;
    private final IEmployeDAO employeDAO;

    public UtilisateurController() {
        this.utilisateurService = new UtilisateurServiceImpl();
        this.employeDAO = DAOFactory.getEmployeDAO();
    }

    /**
     * Crée un nouvel utilisateur.
     */
    public Utilisateur createUtilisateur(String username, String password, int idEmploye, RoleUtilisateur role) throws Exception {
        try {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUsername(username);
            utilisateur.setIdEmploye(idEmploye);
            utilisateur.setRole(role);
            return utilisateurService.createUtilisateur(utilisateur, password);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère tous les utilisateurs.
     */
    public List<Utilisateur> getAllUtilisateurs() throws Exception {
        try {
            return utilisateurService.getAllUtilisateurs();
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère un utilisateur par son ID.
     */
    public Utilisateur getUtilisateurById(int id) throws Exception {
        try {
            return utilisateurService.findUtilisateurById(id);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la récupération de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour le nom d'utilisateur.
     */
    public boolean updateUsername(int idUtilisateur, String newUsername) throws Exception {
        try {
            Utilisateur utilisateur = utilisateurService.findUtilisateurById(idUtilisateur);
            utilisateur.setUsername(newUsername);
            utilisateurService.updateUtilisateur(utilisateur);
            return true;
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la mise à jour du nom d'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour le mot de passe.
     */
    public boolean updatePassword(int idUtilisateur, String newPassword) throws Exception {
        try {
            return utilisateurService.updatePassword(idUtilisateur, newPassword);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la mise à jour du mot de passe: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime un utilisateur.
     */
    public boolean deleteUtilisateur(int idUtilisateur) throws Exception {
        try {
            return utilisateurService.deleteUtilisateur(idUtilisateur);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Active un utilisateur.
     */
    public boolean activateUtilisateur(int idUtilisateur) throws Exception {
        try {
            return utilisateurService.activateUtilisateur(idUtilisateur);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de l'activation de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Désactive un utilisateur.
     */
    public boolean deactivateUtilisateur(int idUtilisateur) throws Exception {
        try {
            return utilisateurService.deactivateUtilisateur(idUtilisateur);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la désactivation de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère tous les employés.
     */
    public List<Employe> getAllEmployes() throws Exception {
        try {
            return employeDAO.findAll();
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des employés: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les employés par rôle.
     */
    public List<Employe> getEmployesByRole(Employe.Role role) throws Exception {
        try {
            return employeDAO.findByRole(role);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la récupération des employés: " + e.getMessage(), e);
        }
    }
}

