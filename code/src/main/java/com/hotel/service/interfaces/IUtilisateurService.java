package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;

import java.util.List;

/**
 * Interface du service métier pour la gestion des utilisateurs.
 */
public interface IUtilisateurService {
    Utilisateur createUtilisateur(Utilisateur utilisateur, String password) throws ServiceException;
    Utilisateur findUtilisateurById(int id) throws ServiceException;
    Utilisateur findUtilisateurByUsername(String username) throws ServiceException;
    List<Utilisateur> getAllUtilisateurs() throws ServiceException;
    List<Utilisateur> getUtilisateursByRole(RoleUtilisateur role) throws ServiceException;
    Utilisateur updateUtilisateur(Utilisateur utilisateur) throws ServiceException;
    boolean updatePassword(int idUtilisateur, String newPassword) throws ServiceException;
    boolean activateUtilisateur(int idUtilisateur) throws ServiceException;
    boolean deactivateUtilisateur(int idUtilisateur) throws ServiceException;
    boolean deleteUtilisateur(int idUtilisateur) throws ServiceException;
}


