package com.hotel.service.impl;

import com.hotel.dao.interfaces.IChambreDAO;
import com.hotel.dao.interfaces.IReservationDAO;
import com.hotel.dao.interfaces.IReservationServiceDAO;
import com.hotel.dao.interfaces.IServiceDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.exception.ValidationException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.Chambre;
import com.hotel.model.Inclure;
import com.hotel.model.Reservation;
import com.hotel.model.ServiceSupplementaire;
import com.hotel.service.interfaces.IReservationService;
import com.hotel.util.Logger;

import java.time.LocalDate;
import java.util.List;

/**
 * Implémentation du service métier pour la gestion des réservations.
 */
public class ReservationServiceImpl implements IReservationService {
    private static final Logger logger = Logger.getLogger(ReservationServiceImpl.class);
    private final IReservationDAO reservationDAO;
    private final IChambreDAO chambreDAO;
    private final IReservationServiceDAO reservationServiceDAO;
    private final IServiceDAO serviceDAO;

    public ReservationServiceImpl() {
        this.reservationDAO = DAOFactory.getReservationDAO();
        this.chambreDAO = DAOFactory.getChambreDAO();
        this.reservationServiceDAO = DAOFactory.getReservationServiceDAO();
        this.serviceDAO = DAOFactory.getServiceDAO();
    }

    @Override
    public Reservation createReservation(Reservation reservation) throws ServiceException {
        try {
            validateReservation(reservation);
            
            // Vérifier que la chambre existe
            Chambre chambre = chambreDAO.findByNumero(reservation.getNumeroChambre());
            if (chambre == null) {
                throw new ServiceException("La chambre n'existe pas");
            }
            
            // Vérifier que la chambre est disponible
            if (!chambre.isDisponible()) {
                throw new ServiceException("La chambre n'est pas disponible");
            }
            
            // Vérifier la disponibilité pour la période (réservations)
            if (!reservationDAO.isChambreAvailable(reservation.getNumeroChambre(), 
                    reservation.getDateDebut(), reservation.getDateFin(), null)) {
                throw new ServiceException("La chambre n'est pas disponible pour cette période");
            }
            
            // Vérifier qu'il n'y a pas d'entretien actif pendant cette période
            com.hotel.dao.interfaces.ITacheEntretienDAO tacheDAO = com.hotel.factory.DAOFactory.getTacheEntretienDAO();
            List<com.hotel.model.TacheEntretien> taches = tacheDAO.findByChambre(reservation.getNumeroChambre());
            boolean hasActiveEntretien = taches.stream()
                    .anyMatch(t -> t.isActiveForPeriod(reservation.getDateDebut(), reservation.getDateFin()));
            if (hasActiveEntretien) {
                throw new ServiceException("La chambre n'est pas disponible : un entretien est prévu pendant cette période");
            }
            
            int id = reservationDAO.create(reservation);
            reservation.setIdReservation(id);
            
            // Réévaluer automatiquement le statut de la chambre
            com.hotel.service.interfaces.IChambreService chambreService = 
                com.hotel.factory.ServiceFactory.getChambreService();
            chambreService.calculateAndUpdateChambreStatut(reservation.getNumeroChambre());
            
            logger.info("Réservation créée avec succès: " + id);
            return reservation;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la création de la réservation", e);
            throw new ServiceException("Erreur lors de la création de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public Reservation findReservationById(int id) throws ServiceException {
        try {
            Reservation reservation = reservationDAO.findById(id);
            if (reservation == null) {
                throw new ServiceException("Réservation non trouvée avec l'ID: " + id);
            }
            return reservation;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de la réservation: " + id, e);
            throw new ServiceException("Erreur lors de la recherche de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> getAllReservations() throws ServiceException {
        try {
            return reservationDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de toutes les réservations", e);
            throw new ServiceException("Erreur lors de la récupération des réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> getReservationsByClient(int idClient) throws ServiceException {
        try {
            return reservationDAO.findByClient(idClient);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de réservations pour le client: " + idClient, e);
            throw new ServiceException("Erreur lors de la recherche de réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> getReservationsByChambre(int numeroChambre) throws ServiceException {
        try {
            return reservationDAO.findByChambre(numeroChambre);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de réservations pour la chambre: " + numeroChambre, e);
            throw new ServiceException("Erreur lors de la recherche de réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> getActiveReservations() throws ServiceException {
        try {
            return reservationDAO.findActive();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des réservations actives", e);
            throw new ServiceException("Erreur lors de la récupération des réservations actives: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isChambreAvailable(int numeroChambre, LocalDate dateDebut, LocalDate dateFin, 
                                     Integer excludeReservationId) throws ServiceException {
        try {
            return reservationDAO.isChambreAvailable(numeroChambre, dateDebut, dateFin, excludeReservationId);
        } catch (DAOException e) {
            logger.error("Erreur lors de la vérification de disponibilité", e);
            throw new ServiceException("Erreur lors de la vérification de disponibilité: " + e.getMessage(), e);
        }
    }

    @Override
    public Reservation updateReservation(Reservation reservation) throws ServiceException {
        try {
            validateReservation(reservation);
            
            // Vérifier la disponibilité si la chambre ou les dates changent
            Reservation existing = reservationDAO.findById(reservation.getIdReservation());
            if (existing == null) {
                throw new ServiceException("Réservation non trouvée");
            }
            
            if (reservation.getNumeroChambre() != existing.getNumeroChambre() ||
                !reservation.getDateDebut().equals(existing.getDateDebut()) ||
                !reservation.getDateFin().equals(existing.getDateFin())) {
                
                if (!reservationDAO.isChambreAvailable(reservation.getNumeroChambre(), 
                        reservation.getDateDebut(), reservation.getDateFin(), reservation.getIdReservation())) {
                    throw new ServiceException("La chambre n'est pas disponible pour cette période");
                }
                
                // Vérifier qu'il n'y a pas d'entretien actif pendant cette période
                com.hotel.dao.interfaces.ITacheEntretienDAO tacheDAO = com.hotel.factory.DAOFactory.getTacheEntretienDAO();
                List<com.hotel.model.TacheEntretien> taches = tacheDAO.findByChambre(reservation.getNumeroChambre());
                boolean hasActiveEntretien = taches.stream()
                        .anyMatch(t -> t.isActiveForPeriod(reservation.getDateDebut(), reservation.getDateFin()));
                if (hasActiveEntretien) {
                    throw new ServiceException("La chambre n'est pas disponible : un entretien est prévu pendant cette période");
                }
            }
            
            boolean updated = reservationDAO.update(reservation);
            if (!updated) {
                throw new ServiceException("Échec de la mise à jour de la réservation");
            }
            
            // Réévaluer automatiquement le statut de la chambre
            com.hotel.service.interfaces.IChambreService chambreService = 
                com.hotel.factory.ServiceFactory.getChambreService();
            chambreService.calculateAndUpdateChambreStatut(reservation.getNumeroChambre());
            
            logger.info("Réservation mise à jour avec succès: " + reservation.getIdReservation());
            return reservation;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la mise à jour de la réservation", e);
            throw new ServiceException("Erreur lors de la mise à jour de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean cancelReservation(int id) throws ServiceException {
        try {
            Reservation reservation = reservationDAO.findById(id);
            if (reservation == null) {
                throw new ServiceException("Réservation non trouvée");
            }
            
            if (!reservation.peutEtreAnnulee()) {
                throw new ServiceException("Cette réservation ne peut pas être annulée");
            }
            
            reservation.setStatut(Reservation.Statut.ANNULEE);
            int numeroChambre = reservation.getNumeroChambre();
            boolean updated = reservationDAO.update(reservation);
            
            if (updated) {
                // Réévaluer automatiquement le statut de la chambre
                com.hotel.service.interfaces.IChambreService chambreService = 
                    com.hotel.factory.ServiceFactory.getChambreService();
                chambreService.calculateAndUpdateChambreStatut(numeroChambre);
                logger.info("Réservation annulée avec succès: " + id);
            }
            
            return updated;
        } catch (DAOException e) {
            logger.error("Erreur lors de l'annulation de la réservation: " + id, e);
            throw new ServiceException("Erreur lors de l'annulation de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteReservation(int id) throws ServiceException {
        try {
            // Récupérer la réservation avant suppression pour connaître la chambre
            Reservation reservation = reservationDAO.findById(id);
            if (reservation == null) {
                throw new ServiceException("Réservation non trouvée");
            }
            int numeroChambre = reservation.getNumeroChambre();
            
            boolean deleted = reservationDAO.delete(id);
            if (deleted) {
                // Réévaluer automatiquement le statut de la chambre
                com.hotel.service.interfaces.IChambreService chambreService = 
                    com.hotel.factory.ServiceFactory.getChambreService();
                chambreService.calculateAndUpdateChambreStatut(numeroChambre);
                logger.info("Réservation supprimée avec succès: " + id);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression de la réservation: " + id, e);
            throw new ServiceException("Erreur lors de la suppression de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean addServiceToReservation(int idReservation, int idService, int quantite) throws ServiceException {
        try {
            // Vérifier que la réservation existe
            Reservation reservation = reservationDAO.findById(idReservation);
            if (reservation == null) {
                throw new ServiceException("Réservation non trouvée");
            }
            
            // Vérifier que le service existe et est actif
            ServiceSupplementaire service = serviceDAO.findById(idService);
            if (service == null) {
                throw new ServiceException("Service non trouvé");
            }
            if (!service.isActif()) {
                throw new ServiceException("Le service n'est pas actif");
            }
            
            // Créer la relation
            Inclure inclure = new Inclure(idReservation, idService, quantite, service.getPrix());
            boolean added = reservationServiceDAO.addServiceToReservation(inclure);
            
            if (added) {
                logger.info("Service " + idService + " ajouté à la réservation " + idReservation);
            }
            
            return added;
        } catch (DAOException e) {
            logger.error("Erreur lors de l'ajout du service à la réservation", e);
            throw new ServiceException("Erreur lors de l'ajout du service: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeServiceFromReservation(int idReservation, int idService) throws ServiceException {
        try {
            boolean removed = reservationServiceDAO.removeServiceFromReservation(idReservation, idService);
            if (removed) {
                logger.info("Service " + idService + " supprimé de la réservation " + idReservation);
            }
            return removed;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression du service de la réservation", e);
            throw new ServiceException("Erreur lors de la suppression du service: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Inclure> getServicesByReservation(int idReservation) throws ServiceException {
        try {
            return reservationServiceDAO.getServicesByReservation(idReservation);
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des services de la réservation: " + idReservation, e);
            throw new ServiceException("Erreur lors de la récupération des services: " + e.getMessage(), e);
        }
    }

    @Override
    public double getTotalServicesAmount(int idReservation) throws ServiceException {
        try {
            return reservationServiceDAO.getTotalServicesAmount(idReservation);
        } catch (DAOException e) {
            logger.error("Erreur lors du calcul du montant total des services: " + idReservation, e);
            throw new ServiceException("Erreur lors du calcul: " + e.getMessage(), e);
        }
    }

    /**
     * Valide une réservation avant traitement.
     *
     * @param reservation la réservation à valider
     * @throws ValidationException si la validation échoue
     */
    private void validateReservation(Reservation reservation) throws ValidationException {
        if (reservation == null) {
            throw new ValidationException("La réservation ne peut pas être null");
        }
        // Les validations sont déjà faites dans les setters du modèle
    }
}

