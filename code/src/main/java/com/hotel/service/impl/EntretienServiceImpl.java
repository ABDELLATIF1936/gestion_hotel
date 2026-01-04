package com.hotel.service.impl;

import com.hotel.dao.interfaces.ITacheEntretienDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.TacheEntretien;
import com.hotel.service.interfaces.IEntretienService;
import com.hotel.service.interfaces.IChambreService;
import com.hotel.util.Logger;

import java.time.LocalDate;
import java.util.List;

/**
 * Implémentation du service métier pour la gestion de l'entretien.
 */
public class EntretienServiceImpl implements IEntretienService {
    private static final Logger logger = Logger.getLogger(EntretienServiceImpl.class);
    private final ITacheEntretienDAO tacheDAO;
    private final IChambreService chambreService;

    public EntretienServiceImpl() {
        this.tacheDAO = DAOFactory.getTacheEntretienDAO();
        this.chambreService = com.hotel.factory.ServiceFactory.getChambreService();
    }

    @Override
    public TacheEntretien createTache(TacheEntretien tache) throws ServiceException {
        try {
            int id = tacheDAO.create(tache);
            tache.setIdTache(id);
            logger.info("Tâche créée avec succès: " + id);
            
            // Toujours réévaluer le statut de la chambre après création d'une tâche
            // La méthode calculateAndUpdateChambreStatut vérifiera si la tâche est active
            chambreService.calculateAndUpdateChambreStatut(tache.getNumeroChambre());
            logger.info("Statut de la chambre " + tache.getNumeroChambre() + " réévalué après création de tâche");
            
            return tache;
        } catch (DAOException e) {
            logger.error("Erreur lors de la création de la tâche", e);
            throw new ServiceException("Erreur lors de la création de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public TacheEntretien findTacheById(int id) throws ServiceException {
        try {
            TacheEntretien tache = tacheDAO.findById(id);
            if (tache == null) {
                throw new ServiceException("Tâche non trouvée avec l'ID: " + id);
            }
            return tache;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de la tâche: " + id, e);
            throw new ServiceException("Erreur lors de la recherche de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> getAllTaches() throws ServiceException {
        try {
            return tacheDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de toutes les tâches", e);
            throw new ServiceException("Erreur lors de la récupération des tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> getTachesByEmploye(int idEmploye) throws ServiceException {
        try {
            return tacheDAO.findByEmploye(idEmploye);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de tâches pour l'employé: " + idEmploye, e);
            throw new ServiceException("Erreur lors de la recherche de tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> getTachesByChambre(int numeroChambre) throws ServiceException {
        try {
            return tacheDAO.findByChambre(numeroChambre);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de tâches pour la chambre: " + numeroChambre, e);
            throw new ServiceException("Erreur lors de la recherche de tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> getTachesByDate(LocalDate date) throws ServiceException {
        try {
            return tacheDAO.findByDate(date);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de tâches pour la date: " + date, e);
            throw new ServiceException("Erreur lors de la recherche de tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> getPendingTaches() throws ServiceException {
        try {
            return tacheDAO.findPending();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des tâches en attente", e);
            throw new ServiceException("Erreur lors de la récupération des tâches en attente: " + e.getMessage(), e);
        }
    }

    @Override
    public TacheEntretien updateTache(TacheEntretien tache) throws ServiceException {
        try {
            boolean updated = tacheDAO.update(tache);
            if (!updated) {
                throw new ServiceException("Échec de la mise à jour de la tâche");
            }
            logger.info("Tâche mise à jour avec succès: " + tache.getIdTache());
            
            // Réévaluer le statut de la chambre après mise à jour d'une tâche
            chambreService.calculateAndUpdateChambreStatut(tache.getNumeroChambre());
            
            return tache;
        } catch (DAOException e) {
            logger.error("Erreur lors de la mise à jour de la tâche", e);
            throw new ServiceException("Erreur lors de la mise à jour de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean assignTacheToEmploye(int idTache, int idEmploye) throws ServiceException {
        try {
            TacheEntretien tache = tacheDAO.findById(idTache);
            if (tache == null) {
                throw new ServiceException("Tâche non trouvée");
            }
            tache.setIdEmploye(idEmploye);
            tache.setStatut(TacheEntretien.Statut.EN_COURS);
            return tacheDAO.update(tache);
        } catch (DAOException e) {
            logger.error("Erreur lors de l'assignation de la tâche: " + idTache, e);
            throw new ServiceException("Erreur lors de l'assignation de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean markTacheAsCompleted(int id) throws ServiceException {
        try {
            TacheEntretien tache = tacheDAO.findById(id);
            if (tache == null) {
                throw new ServiceException("Tâche non trouvée");
            }
            int numeroChambre = tache.getNumeroChambre();
            tache.setStatut(TacheEntretien.Statut.TERMINEE);
            boolean updated = tacheDAO.update(tache);
            
            if (updated) {
                // Réévaluer automatiquement le statut de la chambre quand un entretien se termine
                chambreService.calculateAndUpdateChambreStatut(numeroChambre);
                logger.info("Statut de la chambre " + numeroChambre + " réévalué après fin d'entretien");
            }
            
            return updated;
        } catch (DAOException e) {
            logger.error("Erreur lors du marquage de la tâche comme terminée: " + id, e);
            throw new ServiceException("Erreur lors du marquage de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteTache(int id) throws ServiceException {
        try {
            // Récupérer la tâche avant suppression pour connaître la chambre
            TacheEntretien tache = tacheDAO.findById(id);
            if (tache == null) {
                throw new ServiceException("Tâche non trouvée");
            }
            int numeroChambre = tache.getNumeroChambre();
            
            boolean deleted = tacheDAO.delete(id);
            if (deleted) {
                logger.info("Tâche supprimée avec succès: " + id);
                // Réévaluer le statut de la chambre après suppression
                chambreService.calculateAndUpdateChambreStatut(numeroChambre);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression de la tâche: " + id, e);
            throw new ServiceException("Erreur lors de la suppression de la tâche: " + e.getMessage(), e);
        }
    }
}

