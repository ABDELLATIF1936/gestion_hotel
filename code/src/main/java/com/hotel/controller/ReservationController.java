package com.hotel.controller;

import com.hotel.model.Inclure;
import com.hotel.model.Reservation;
import com.hotel.service.interfaces.IReservationService;
import com.hotel.factory.ServiceFactory;
import com.hotel.util.Logger;

import java.util.List;

/**
 * Contrôleur pour la gestion des réservations.
 */
public class ReservationController {
    private static final Logger logger = Logger.getLogger(ReservationController.class);
    private final IReservationService reservationService;

    public ReservationController() {
        this.reservationService = ServiceFactory.getReservationService();
    }

    public Reservation createReservation(Reservation reservation) throws Exception {
        try {
            return reservationService.createReservation(reservation);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la réservation", e);
            throw e;
        }
    }

    public List<Reservation> getAllReservations() throws Exception {
        try {
            return reservationService.getAllReservations();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des réservations", e);
            throw e;
        }
    }

    public List<Reservation> getActiveReservations() throws Exception {
        try {
            return reservationService.getActiveReservations();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des réservations actives", e);
            throw e;
        }
    }

    public void updateReservation(Reservation reservation) throws Exception {
        try {
            reservationService.updateReservation(reservation);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la réservation", e);
            throw e;
        }
    }

    public void cancelReservation(int id) throws Exception {
        try {
            reservationService.cancelReservation(id);
        } catch (Exception e) {
            logger.error("Erreur lors de l'annulation de la réservation", e);
            throw e;
        }
    }

    public void deleteReservation(int id) throws Exception {
        try {
            reservationService.deleteReservation(id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la réservation", e);
            throw e;
        }
    }

    public Reservation getReservationById(int id) throws Exception {
        try {
            return reservationService.findReservationById(id);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la réservation", e);
            throw e;
        }
    }

    public boolean addServiceToReservation(int idReservation, int idService, int quantite) throws Exception {
        try {
            return reservationService.addServiceToReservation(idReservation, idService, quantite);
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du service à la réservation", e);
            throw e;
        }
    }

    public boolean removeServiceFromReservation(int idReservation, int idService) throws Exception {
        try {
            return reservationService.removeServiceFromReservation(idReservation, idService);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du service de la réservation", e);
            throw e;
        }
    }

    public List<Inclure> getServicesByReservation(int idReservation) throws Exception {
        try {
            return reservationService.getServicesByReservation(idReservation);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des services de la réservation", e);
            throw e;
        }
    }

    public double getTotalServicesAmount(int idReservation) throws Exception {
        try {
            return reservationService.getTotalServicesAmount(idReservation);
        } catch (Exception e) {
            logger.error("Erreur lors du calcul du montant total des services", e);
            throw e;
        }
    }
}
