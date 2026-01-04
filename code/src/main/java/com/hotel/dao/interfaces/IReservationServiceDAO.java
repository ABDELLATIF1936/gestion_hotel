package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.Inclure;
import com.hotel.model.ServiceSupplementaire;

import java.util.List;

/**
 * Interface DAO pour la gestion des services associés aux réservations.
 */
public interface IReservationServiceDAO {
    /**
     * Ajoute un service à une réservation.
     *
     * @param inclure la relation réservation-service à créer
     * @return true si l'ajout a réussi
     * @throws DAOException si une erreur survient
     */
    boolean addServiceToReservation(Inclure inclure) throws DAOException;
    
    /**
     * Supprime un service d'une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @param idService l'ID du service
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean removeServiceFromReservation(int idReservation, int idService) throws DAOException;
    
    /**
     * Récupère tous les services d'une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @return la liste des services avec leurs quantités et prix
     * @throws DAOException si une erreur survient
     */
    List<Inclure> getServicesByReservation(int idReservation) throws DAOException;
    
    /**
     * Récupère les détails complets des services d'une réservation (avec les informations du service).
     *
     * @param idReservation l'ID de la réservation
     * @return la liste des services avec leurs détails
     * @throws DAOException si une erreur survient
     */
    List<ServiceSupplementaire> getServiceDetailsByReservation(int idReservation) throws DAOException;
    
    /**
     * Met à jour la quantité d'un service dans une réservation.
     *
     * @param inclure la relation réservation-service à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean updateServiceQuantity(Inclure inclure) throws DAOException;
    
    /**
     * Calcule le montant total des services pour une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @return le montant total des services
     * @throws DAOException si une erreur survient
     */
    double getTotalServicesAmount(int idReservation) throws DAOException;
}


