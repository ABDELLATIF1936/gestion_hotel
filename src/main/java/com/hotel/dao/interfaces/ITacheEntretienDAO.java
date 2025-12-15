package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.TacheEntretien;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface DAO pour la gestion des tâches d'entretien.
 */
public interface ITacheEntretienDAO {
    /**
     * Crée une nouvelle tâche d'entretien.
     *
     * @param tache la tâche à créer
     * @return l'ID de la tâche créée
     * @throws DAOException si une erreur survient
     */
    int create(TacheEntretien tache) throws DAOException;

    /**
     * Récupère une tâche par son ID.
     *
     * @param idTache l'ID de la tâche
     * @return la tâche trouvée, ou null si non trouvée
     * @throws DAOException si une erreur survient
     */
    TacheEntretien findById(int idTache) throws DAOException;

    /**
     * Récupère toutes les tâches.
     *
     * @return la liste de toutes les tâches
     * @throws DAOException si une erreur survient
     */
    List<TacheEntretien> findAll() throws DAOException;

    /**
     * Récupère les tâches d'un employé.
     *
     * @param idEmploye l'ID de l'employé
     * @return la liste des tâches de l'employé
     * @throws DAOException si une erreur survient
     */
    List<TacheEntretien> findByEmploye(int idEmploye) throws DAOException;

    /**
     * Récupère les tâches d'une chambre.
     *
     * @param numeroChambre le numéro de la chambre
     * @return la liste des tâches de la chambre
     * @throws DAOException si une erreur survient
     */
    List<TacheEntretien> findByChambre(int numeroChambre) throws DAOException;

    /**
     * Récupère les tâches pour une date donnée.
     *
     * @param date la date recherchée
     * @return la liste des tâches pour cette date
     * @throws DAOException si une erreur survient
     */
    List<TacheEntretien> findByDate(LocalDate date) throws DAOException;

    /**
     * Récupère les tâches en attente.
     *
     * @return la liste des tâches en attente
     * @throws DAOException si une erreur survient
     */
    List<TacheEntretien> findPending() throws DAOException;

    /**
     * Met à jour une tâche.
     *
     * @param tache la tâche à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean update(TacheEntretien tache) throws DAOException;

    /**
     * Supprime une tâche.
     *
     * @param idTache l'ID de la tâche à supprimer
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean delete(int idTache) throws DAOException;
}

