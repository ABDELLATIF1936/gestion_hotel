package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.Facture;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface DAO pour la gestion des factures.
 */
public interface IFactureDAO {
    /**
     * Crée une nouvelle facture.
     *
     * @param facture la facture à créer
     * @return l'ID de la facture créée
     * @throws DAOException si une erreur survient
     */
    int create(Facture facture) throws DAOException;

    /**
     * Récupère une facture par son ID.
     *
     * @param idFacture l'ID de la facture
     * @return la facture trouvée, ou null si non trouvée
     * @throws DAOException si une erreur survient
     */
    Facture findById(int idFacture) throws DAOException;

    /**
     * Récupère la facture d'une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @return la facture trouvée, ou null si non trouvée
     * @throws DAOException si une erreur survient
     */
    Facture findByReservation(int idReservation) throws DAOException;

    /**
     * Récupère toutes les factures.
     *
     * @return la liste de toutes les factures
     * @throws DAOException si une erreur survient
     */
    List<Facture> findAll() throws DAOException;

    /**
     * Récupère les factures pour une période donnée.
     *
     * @param dateDebut la date de début
     * @param dateFin la date de fin
     * @return la liste des factures dans cette période
     * @throws DAOException si une erreur survient
     */
    List<Facture> findByPeriod(LocalDate dateDebut, LocalDate dateFin) throws DAOException;

    /**
     * Récupère les factures en attente de paiement.
     *
     * @return la liste des factures en attente
     * @throws DAOException si une erreur survient
     */
    List<Facture> findPending() throws DAOException;

    /**
     * Met à jour une facture.
     *
     * @param facture la facture à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean update(Facture facture) throws DAOException;

    /**
     * Supprime une facture.
     *
     * @param idFacture l'ID de la facture à supprimer
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean delete(int idFacture) throws DAOException;
}

