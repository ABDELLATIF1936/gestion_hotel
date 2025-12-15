package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;

import java.util.List;

/**
 * Interface DAO pour la gestion des utilisateurs.
 */
public interface IUtilisateurDAO {
    /**
     * Crée un nouvel utilisateur.
     *
     * @param utilisateur l'utilisateur à créer
     * @return l'ID de l'utilisateur créé
     * @throws DAOException en cas d'erreur
     */
    int create(Utilisateur utilisateur) throws DAOException;

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param idUtilisateur l'ID de l'utilisateur
     * @return l'utilisateur ou null si non trouvé
     * @throws DAOException en cas d'erreur
     */
    Utilisateur findById(int idUtilisateur) throws DAOException;

    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return l'utilisateur ou null si non trouvé
     * @throws DAOException en cas d'erreur
     */
    Utilisateur findByUsername(String username) throws DAOException;

    /**
     * Récupère tous les utilisateurs.
     *
     * @return la liste des utilisateurs
     * @throws DAOException en cas d'erreur
     */
    List<Utilisateur> findAll() throws DAOException;

    /**
     * Récupère les utilisateurs par rôle.
     *
     * @param role le rôle
     * @return la liste des utilisateurs
     * @throws DAOException en cas d'erreur
     */
    List<Utilisateur> findByRole(RoleUtilisateur role) throws DAOException;

    /**
     * Met à jour un utilisateur.
     *
     * @param utilisateur l'utilisateur à mettre à jour
     * @throws DAOException en cas d'erreur
     */
    void update(Utilisateur utilisateur) throws DAOException;

    /**
     * Met à jour le dernier login d'un utilisateur.
     *
     * @param idUtilisateur l'ID de l'utilisateur
     * @throws DAOException en cas d'erreur
     */
    void updateLastLogin(int idUtilisateur) throws DAOException;

    /**
     * Supprime un utilisateur.
     *
     * @param idUtilisateur l'ID de l'utilisateur
     * @return true si supprimé avec succès
     * @throws DAOException en cas d'erreur
     */
    boolean delete(int idUtilisateur) throws DAOException;
}


