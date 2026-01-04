package com.hotel.service.impl;

import com.hotel.dao.interfaces.IUtilisateurDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.exception.ValidationException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;
import com.hotel.security.PasswordUtils;
import com.hotel.service.interfaces.IUtilisateurService;
import com.hotel.util.Logger;

import java.util.List;

/**
 * Implémentation du service métier pour la gestion des utilisateurs.
 */
public class UtilisateurServiceImpl implements IUtilisateurService {
    private static final Logger logger = Logger.getLogger(UtilisateurServiceImpl.class);
    private final IUtilisateurDAO utilisateurDAO;

    public UtilisateurServiceImpl() {
        this.utilisateurDAO = DAOFactory.getUtilisateurDAO();
    }

    @Override
    public Utilisateur createUtilisateur(Utilisateur utilisateur, String password) throws ServiceException {
        try {
            if (password == null || password.isEmpty()) {
                throw new ValidationException("Le mot de passe ne peut pas être vide");
            }
            
            if (!PasswordUtils.isValidPassword(password)) {
                throw new ValidationException("Le mot de passe doit contenir au moins 8 caractères avec majuscule, minuscule et chiffre");
            }
            
            Utilisateur existing = utilisateurDAO.findByUsername(utilisateur.getUsername());
            if (existing != null) {
                throw new ServiceException("Un utilisateur avec ce nom d'utilisateur existe déjà");
            }
            
            utilisateur.setPasswordHash(PasswordUtils.hashPassword(password));
            
            int id = utilisateurDAO.create(utilisateur);
            utilisateur.setIdUtilisateur(id);
            logger.info("Utilisateur créé avec succès: " + utilisateur.getUsername());
            return utilisateur;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la création de l'utilisateur", e);
            throw new ServiceException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public Utilisateur findUtilisateurById(int id) throws ServiceException {
        try {
            Utilisateur utilisateur = utilisateurDAO.findById(id);
            if (utilisateur == null) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + id);
            }
            return utilisateur;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de l'utilisateur: " + id, e);
            throw new ServiceException("Erreur lors de la recherche de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public Utilisateur findUtilisateurByUsername(String username) throws ServiceException {
        try {
            return utilisateurDAO.findByUsername(username);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par username: " + username, e);
            throw new ServiceException("Erreur lors de la recherche de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Utilisateur> getAllUtilisateurs() throws ServiceException {
        try {
            return utilisateurDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de tous les utilisateurs", e);
            throw new ServiceException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Utilisateur> getUtilisateursByRole(RoleUtilisateur role) throws ServiceException {
        try {
            return utilisateurDAO.findByRole(role);
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des utilisateurs par rôle", e);
            throw new ServiceException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }

    @Override
    public Utilisateur updateUtilisateur(Utilisateur utilisateur) throws ServiceException {
        try {
            Utilisateur existing = utilisateurDAO.findById(utilisateur.getIdUtilisateur());
            if (existing == null) {
                throw new ServiceException("Utilisateur non trouvé");
            }
            
            if (!existing.getUsername().equals(utilisateur.getUsername())) {
                Utilisateur userWithSameUsername = utilisateurDAO.findByUsername(utilisateur.getUsername());
                if (userWithSameUsername != null && userWithSameUsername.getIdUtilisateur() != utilisateur.getIdUtilisateur()) {
                    throw new ServiceException("Un utilisateur avec ce nom d'utilisateur existe déjà");
                }
            }
            
            if (utilisateur.getPasswordHash() == null || utilisateur.getPasswordHash().isEmpty()) {
                utilisateur.setPasswordHash(existing.getPasswordHash());
            }
            
            utilisateurDAO.update(utilisateur);
            logger.info("Utilisateur mis à jour: " + utilisateur.getIdUtilisateur());
            return utilisateur;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur", e);
            throw new ServiceException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePassword(int idUtilisateur, String newPassword) throws ServiceException {
        try {
            if (newPassword == null || newPassword.isEmpty()) {
                throw new ValidationException("Le mot de passe ne peut pas être vide");
            }
            
            if (!PasswordUtils.isValidPassword(newPassword)) {
                throw new ValidationException("Le mot de passe doit contenir au moins 8 caractères avec majuscule, minuscule et chiffre");
            }
            
            Utilisateur utilisateur = utilisateurDAO.findById(idUtilisateur);
            if (utilisateur == null) {
                throw new ServiceException("Utilisateur non trouvé");
            }
            
            utilisateur.setPasswordHash(PasswordUtils.hashPassword(newPassword));
            utilisateurDAO.update(utilisateur);
            logger.info("Mot de passe mis à jour pour l'utilisateur: " + idUtilisateur);
            return true;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la mise à jour du mot de passe", e);
            throw new ServiceException("Erreur lors de la mise à jour du mot de passe: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activateUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            Utilisateur utilisateur = utilisateurDAO.findById(idUtilisateur);
            if (utilisateur == null) {
                throw new ServiceException("Utilisateur non trouvé");
            }
            
            utilisateur.setActif(true);
            utilisateurDAO.update(utilisateur);
            logger.info("Utilisateur activé: " + idUtilisateur);
            return true;
        } catch (DAOException e) {
            logger.error("Erreur lors de l'activation de l'utilisateur", e);
            throw new ServiceException("Erreur lors de l'activation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deactivateUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            Utilisateur utilisateur = utilisateurDAO.findById(idUtilisateur);
            if (utilisateur == null) {
                throw new ServiceException("Utilisateur non trouvé");
            }
            
            utilisateur.setActif(false);
            utilisateurDAO.update(utilisateur);
            logger.info("Utilisateur désactivé: " + idUtilisateur);
            return true;
        } catch (DAOException e) {
            logger.error("Erreur lors de la désactivation de l'utilisateur", e);
            throw new ServiceException("Erreur lors de la désactivation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            boolean deleted = utilisateurDAO.delete(idUtilisateur);
            if (deleted) {
                logger.info("Utilisateur supprimé: " + idUtilisateur);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression de l'utilisateur", e);
            throw new ServiceException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }
}


