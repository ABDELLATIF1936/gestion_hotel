package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.Chambre;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface DAO pour la gestion des chambres.
 */
public interface IChambreDAO {
    /**
     * Crée une nouvelle chambre.
     *
     * @param chambre la chambre à créer
     * @return true si la création a réussi
     * @throws DAOException si une erreur survient
     */
    boolean create(Chambre chambre) throws DAOException;

    /**
     * Récupère une chambre par son numéro.
     *
     * @param numeroChambre le numéro de la chambre
     * @return la chambre trouvée, ou null si non trouvée
     * @throws DAOException si une erreur survient
     */
    Chambre findByNumero(int numeroChambre) throws DAOException;

    /**
     * Récupère toutes les chambres.
     *
     * @return la liste de toutes les chambres
     * @throws DAOException si une erreur survient
     */
    List<Chambre> findAll() throws DAOException;

    /**
     * Récupère les chambres disponibles.
     *
     * @return la liste des chambres disponibles
     * @throws DAOException si une erreur survient
     */
    List<Chambre> findAvailable() throws DAOException;

    /**
     * Récupère les chambres par catégorie.
     *
     * @param categorie la catégorie recherchée
     * @return la liste des chambres de cette catégorie
     * @throws DAOException si une erreur survient
     */
    List<Chambre> findByCategorie(Chambre.Categorie categorie) throws DAOException;

    /**
     * Récupère les chambres disponibles pour une période donnée.
     *
     * @param dateDebut la date de début
     * @param dateFin la date de fin
     * @return la liste des chambres disponibles
     * @throws DAOException si une erreur survient
     */
    List<Chambre> findAvailableForPeriod(LocalDate dateDebut, LocalDate dateFin) throws DAOException;

    /**
     * Met à jour une chambre.
     *
     * @param chambre la chambre à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean update(Chambre chambre) throws DAOException;

    /**
     * Met à jour le statut d'une chambre.
     *
     * @param numeroChambre le numéro de la chambre
     * @param statut le nouveau statut
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean updateStatut(int numeroChambre, Chambre.Statut statut) throws DAOException;

    /**
     * Supprime une chambre.
     *
     * @param numeroChambre le numéro de la chambre à supprimer
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean delete(int numeroChambre) throws DAOException;
}

