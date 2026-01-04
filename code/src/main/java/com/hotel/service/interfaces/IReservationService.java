package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service métier pour la gestion des réservations.
 */
public interface IReservationService {
    Reservation createReservation(Reservation reservation) throws ServiceException;
    Reservation findReservationById(int id) throws ServiceException;
    List<Reservation> getAllReservations() throws ServiceException;
    List<Reservation> getReservationsByClient(int idClient) throws ServiceException;
    List<Reservation> getReservationsByChambre(int numeroChambre) throws ServiceException;
    List<Reservation> getActiveReservations() throws ServiceException;
    boolean isChambreAvailable(int numeroChambre, LocalDate dateDebut, LocalDate dateFin, Integer excludeReservationId) throws ServiceException;
    Reservation updateReservation(Reservation reservation) throws ServiceException;
    boolean cancelReservation(int id) throws ServiceException;
    boolean deleteReservation(int id) throws ServiceException;
    
    /**
     * Ajoute un service à une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @param idService l'ID du service
     * @param quantite la quantité du service
     * @return true si l'ajout a réussi
     * @throws ServiceException si une erreur survient
     */
    boolean addServiceToReservation(int idReservation, int idService, int quantite) throws ServiceException;
    
    /**
     * Supprime un service d'une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @param idService l'ID du service
     * @return true si la suppression a réussi
     * @throws ServiceException si une erreur survient
     */
    boolean removeServiceFromReservation(int idReservation, int idService) throws ServiceException;
    
    /**
     * Récupère tous les services d'une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @return la liste des services avec leurs quantités
     * @throws ServiceException si une erreur survient
     */
    List<com.hotel.model.Inclure> getServicesByReservation(int idReservation) throws ServiceException;
    
    /**
     * Calcule le montant total des services pour une réservation.
     *
     * @param idReservation l'ID de la réservation
     * @return le montant total des services
     * @throws ServiceException si une erreur survient
     */
    double getTotalServicesAmount(int idReservation) throws ServiceException;
}

