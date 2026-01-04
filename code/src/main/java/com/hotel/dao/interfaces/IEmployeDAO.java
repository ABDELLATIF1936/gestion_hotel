package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.Employe;

import java.util.List;

/**
 * Interface DAO pour la gestion des employés.
 */
public interface IEmployeDAO {
    /**
     * Crée un nouvel employé.
     *
     * @param employe l'employé à créer
     * @return l'ID de l'employé créé
     * @throws DAOException si une erreur survient
     */
    int create(Employe employe) throws DAOException;

    /**
     * Récupère un employé par son ID.
     *
     * @param idEmploye l'ID de l'employé
     * @return l'employé trouvé, ou null si non trouvé
     * @throws DAOException si une erreur survient
     */
    Employe findById(int idEmploye) throws DAOException;

    /**
     * Récupère tous les employés.
     *
     * @return la liste de tous les employés
     * @throws DAOException si une erreur survient
     */
    List<Employe> findAll() throws DAOException;

    /**
     * Récupère les employés actifs.
     *
     * @return la liste des employés actifs
     * @throws DAOException si une erreur survient
     */
    List<Employe> findActive() throws DAOException;

    /**
     * Récupère les employés par rôle.
     *
     * @param role le rôle recherché
     * @return la liste des employés avec ce rôle
     * @throws DAOException si une erreur survient
     */
    List<Employe> findByRole(Employe.Role role) throws DAOException;

    /**
     * Met à jour un employé.
     *
     * @param employe l'employé à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean update(Employe employe) throws DAOException;

    /**
     * Supprime un employé (soft delete en mettant actif à false).
     *
     * @param idEmploye l'ID de l'employé à supprimer
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean delete(int idEmploye) throws DAOException;
}

