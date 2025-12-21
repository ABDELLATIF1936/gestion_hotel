package com.hotel.service.impl;

import com.hotel.dao.interfaces.IChambreDAO;
import com.hotel.dao.interfaces.IReservationDAO;
import com.hotel.dao.interfaces.ITacheEntretienDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.exception.ValidationException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.Chambre;
import com.hotel.model.Reservation;
import com.hotel.model.TacheEntretien;
import com.hotel.service.interfaces.IChambreService;
import com.hotel.util.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier pour la gestion des chambres.
 */
public class ChambreServiceImpl implements IChambreService {
    private static final Logger logger = Logger.getLogger(ChambreServiceImpl.class);
    
    /**
     * Mémorise le statut précédent d'une chambre avant de la passer en HORS_SERVICE
     * à cause d'un entretien actif. Cela permet de restaurer ce statut lorsque
     * tous les entretiens de la chambre sont terminés ou supprimés.
     *
     * Clé   : numéro de chambre
     * Valeur: dernier statut avant HORS_SERVICE
     */
    private static final Map<Integer, Chambre.Statut> previousStatusMap = new ConcurrentHashMap<>();
    private final IChambreDAO chambreDAO;
    private final ITacheEntretienDAO tacheEntretienDAO;
    private final IReservationDAO reservationDAO;

    public ChambreServiceImpl() {
        this.chambreDAO = DAOFactory.getChambreDAO();
        this.tacheEntretienDAO = DAOFactory.getTacheEntretienDAO();
        this.reservationDAO = DAOFactory.getReservationDAO();
    }

    @Override
    public Chambre createChambre(Chambre chambre) throws ServiceException {
        try {
            validateChambre(chambre);
            
            // Vérifier si la chambre existe déjà
            Chambre existing = chambreDAO.findByNumero(chambre.getNumeroChambre());
            if (existing != null) {
                throw new ServiceException("Une chambre avec ce numéro existe déjà");
            }
            
            boolean created = chambreDAO.create(chambre);
            if (!created) {
                throw new ServiceException("Échec de la création de la chambre");
            }
            
            logger.info("Chambre créée avec succès: " + chambre.getNumeroChambre());
            return chambre;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la création de la chambre", e);
            throw new ServiceException("Erreur lors de la création de la chambre: " + e.getMessage(), e);
        }
    }

    @Override
    public Chambre findChambreByNumero(int numero) throws ServiceException {
        try {
            Chambre chambre = chambreDAO.findByNumero(numero);
            if (chambre == null) {
                throw new ServiceException("Chambre non trouvée avec le numéro: " + numero);
            }
            return chambre;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de la chambre: " + numero, e);
            throw new ServiceException("Erreur lors de la recherche de la chambre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> getAllChambres() throws ServiceException {
        try {
            return chambreDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de toutes les chambres", e);
            throw new ServiceException("Erreur lors de la récupération des chambres: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> getAvailableChambres() throws ServiceException {
        try {
            return chambreDAO.findAvailable();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des chambres disponibles", e);
            throw new ServiceException("Erreur lors de la récupération des chambres disponibles: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> getChambresByCategorie(Chambre.Categorie categorie) throws ServiceException {
        try {
            return chambreDAO.findByCategorie(categorie);
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des chambres par catégorie", e);
            throw new ServiceException("Erreur lors de la récupération des chambres: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> getAvailableChambresForPeriod(LocalDate dateDebut, LocalDate dateFin) throws ServiceException {
        try {
            return chambreDAO.findAvailableForPeriod(dateDebut, dateFin);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de chambres disponibles pour la période", e);
            throw new ServiceException("Erreur lors de la recherche de chambres: " + e.getMessage(), e);
        }
    }

    @Override
    public Chambre updateChambre(Chambre chambre) throws ServiceException {
        try {
            validateChambre(chambre);
            
            boolean updated = chambreDAO.update(chambre);
            if (!updated) {
                throw new ServiceException("Échec de la mise à jour de la chambre");
            }
            
            logger.info("Chambre mise à jour avec succès: " + chambre.getNumeroChambre());
            return chambre;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la mise à jour de la chambre", e);
            throw new ServiceException("Erreur lors de la mise à jour de la chambre: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateChambreStatut(int numero, Chambre.Statut statut) throws ServiceException {
        try {
            boolean updated = chambreDAO.updateStatut(numero, statut);
            if (updated) {
                logger.info("Statut de la chambre " + numero + " mis à jour: " + statut);
            }
            return updated;
        } catch (DAOException e) {
            logger.error("Erreur lors de la mise à jour du statut de la chambre: " + numero, e);
            throw new ServiceException("Erreur lors de la mise à jour du statut: " + e.getMessage(), e);
        }
    }

    @Override
    public Chambre.Statut calculateAndUpdateChambreStatut(int numeroChambre) throws ServiceException {
        try {
            logger.info("Calcul du statut pour la chambre " + numeroChambre);
            
            // Récupérer toutes les tâches d'entretien pour cette chambre
            List<TacheEntretien> taches = tacheEntretienDAO.findByChambre(numeroChambre);
            logger.info("Nombre de tâches d'entretien trouvées: " + taches.size());
            
            // Filtrer les tâches actives (en cours ou planifiées)
            List<TacheEntretien> tachesActives = taches.stream()
                    .filter(t -> {
                        boolean active = t.isActive();
                        logger.info("Tâche " + t.getIdTache() + " - Date: " + t.getDate() + 
                                    ", Statut: " + t.getStatut() + ", Active: " + active);
                        return active;
                    })
                    .collect(Collectors.toList());
            
            logger.info("Nombre de tâches actives: " + tachesActives.size());
            
            // Si au moins un entretien est actif, la chambre est HORS_SERVICE
            if (!tachesActives.isEmpty()) {
                // Récupérer le statut actuel de la chambre pour pouvoir le restaurer plus tard
                try {
                    Chambre chambreActuelle = chambreDAO.findByNumero(numeroChambre);
                    if (chambreActuelle != null && chambreActuelle.getStatut() != Chambre.Statut.HORS_SERVICE) {
                        // On mémorise uniquement si on n'est pas déjà en HORS_SERVICE
                        previousStatusMap.put(numeroChambre, chambreActuelle.getStatut());
                        logger.info("Statut précédent de la chambre " + numeroChambre + " mémorisé: " + chambreActuelle.getStatut());
                    }
                } catch (DAOException e) {
                    logger.warn("Impossible de récupérer le statut actuel de la chambre " + numeroChambre + " avant de la passer en HORS_SERVICE", e);
                }

                updateChambreStatut(numeroChambre, Chambre.Statut.HORS_SERVICE);
                logger.info("Chambre " + numeroChambre + " mise HORS_SERVICE (" + tachesActives.size() + " entretien(s) actif(s))");
                return Chambre.Statut.HORS_SERVICE;
            }
            
            // S'il n'y a plus d'entretien actif, vérifier d'abord si un statut précédent a été mémorisé.
            // Cela permet de revenir à l'état exact d'avant l'entretien (ex: OCCUPEE, RESERVEE, etc.).
            if (previousStatusMap.containsKey(numeroChambre)) {
                Chambre.Statut statutPrecedent = previousStatusMap.remove(numeroChambre);
                updateChambreStatut(numeroChambre, statutPrecedent);
                logger.info("Chambre " + numeroChambre + " restaurée à son statut précédent: " + statutPrecedent);
                return statutPrecedent;
            }

            // Sinon, vérifier les réservations pour recalculer le statut
            LocalDate aujourdhui = LocalDate.now();
            List<Reservation> reservations = reservationDAO.findByChambre(numeroChambre);
            
            // Vérifier s'il y a une réservation en cours (occupée)
            boolean estOccupee = reservations.stream()
                    .anyMatch(r -> {
                        Reservation.Statut statut = r.getStatut();
                        return (statut == Reservation.Statut.CONFIRMEE || statut == Reservation.Statut.EN_COURS)
                                && !r.getDateDebut().isAfter(aujourdhui)
                                && !r.getDateFin().isBefore(aujourdhui);
                    });
            
            if (estOccupee) {
                updateChambreStatut(numeroChambre, Chambre.Statut.OCCUPEE);
                logger.info("Chambre " + numeroChambre + " mise OCCUPEE");
                return Chambre.Statut.OCCUPEE;
            }
            
            // Vérifier s'il y a une réservation future (réservée)
            boolean estReservee = reservations.stream()
                    .anyMatch(r -> {
                        Reservation.Statut statut = r.getStatut();
                        return (statut == Reservation.Statut.CONFIRMEE || statut == Reservation.Statut.EN_ATTENTE)
                                && r.getDateDebut().isAfter(aujourdhui);
                    });
            
            if (estReservee) {
                updateChambreStatut(numeroChambre, Chambre.Statut.RESERVEE);
                logger.info("Chambre " + numeroChambre + " mise RESERVEE");
                return Chambre.Statut.RESERVEE;
            }
            
            // Sinon, la chambre est disponible
            updateChambreStatut(numeroChambre, Chambre.Statut.DISPONIBLE);
            logger.info("Chambre " + numeroChambre + " mise DISPONIBLE");
            return Chambre.Statut.DISPONIBLE;
            
        } catch (DAOException e) {
            logger.error("Erreur lors du calcul du statut de la chambre: " + numeroChambre, e);
            throw new ServiceException("Erreur lors du calcul du statut: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteChambre(int numero) throws ServiceException {
        try {
            boolean deleted = chambreDAO.delete(numero);
            if (deleted) {
                logger.info("Chambre supprimée avec succès: " + numero);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression de la chambre: " + numero, e);
            throw new ServiceException("Erreur lors de la suppression de la chambre: " + e.getMessage(), e);
        }
    }

    /**
     * Valide une chambre avant traitement.
     *
     * @param chambre la chambre à valider
     * @throws ValidationException si la validation échoue
     */
    private void validateChambre(Chambre chambre) throws ValidationException {
        if (chambre == null) {
            throw new ValidationException("La chambre ne peut pas être null");
        }
        // Les validations sont déjà faites dans les setters du modèle
    }
}

