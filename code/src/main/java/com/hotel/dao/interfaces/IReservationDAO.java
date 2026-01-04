package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface DAO pour la gestion des réservations.
 */
public interface IReservationDAO {
    /**
     * Crée une nouvelle réservation.
     *
     * @param reservation la réservation à créer
     * @return l'ID de la réservation créée
     * @throws DAOException si une erreur survient
     */
    int create(Reservation reservation) throws DAOException;

    /**
     * Récupère une réservation par son ID.
     *
     * @param idReservation l'ID de la réservation
     * @return la réservation trouvée, ou null si non trouvée
     * @throws DAOException si une erreur survient
     */
    Reservation findById(int idReservation) throws DAOException;

    /**
     * Récupère toutes les réservations.
     *
     * @return la liste de toutes les réservations
     * @throws DAOException si une erreur survient
     */
    List<Reservation> findAll() throws DAOException;

    /**
     * Récupère les réservations d'un client.
     *
     * @param idClient l'ID du client
     * @return la liste des réservations du client
     * @throws DAOException si une erreur survient
     */
    List<Reservation> findByClient(int idClient) throws DAOException;

    /**
     * Récupère les réservations d'une chambre.
     *
     * @param numeroChambre le numéro de la chambre
     * @return la liste des réservations de la chambre
     * @throws DAOException si une erreur survient
     */
    List<Reservation> findByChambre(int numeroChambre) throws DAOException;

    /**
     * Récupère les réservations pour une période donnée.
     *
     * @param dateDebut la date de début
     * @param dateFin la date de fin
     * @return la liste des réservations dans cette période
     * @throws DAOException si une erreur survient
     */
    List<Reservation> findByPeriod(LocalDate dateDebut, LocalDate dateFin) throws DAOException;

    /**
     * Récupère les réservations en cours.
     *
     * @return la liste des réservations en cours
     * @throws DAOException si une erreur survient
     */
    List<Reservation> findActive() throws DAOException;

    /**
     * Vérifie si une chambre est disponible pour une période donnée.
     *
     * @param numeroChambre le numéro de la chambre
     * @param dateDebut la date de début
     * @param dateFin la date de fin
     * @param excludeReservationId l'ID d'une réservation à exclure (pour les mises à jour)
     * @return true si la chambre est disponible
     * @throws DAOException si une erreur survient
     */
    boolean isChambreAvailable(int numeroChambre, LocalDate dateDebut, LocalDate dateFin, 
                                Integer excludeReservationId) throws DAOException;

    /**
     * Met à jour une réservation.
     *
     * @param reservation la réservation à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean update(Reservation reservation) throws DAOException;

    /**
     * Supprime une réservation.
     *
     * @param idReservation l'ID de la réservation à supprimer
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean delete(int idReservation) throws DAOException;
}

