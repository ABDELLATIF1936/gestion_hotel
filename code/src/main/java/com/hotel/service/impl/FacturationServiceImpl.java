package com.hotel.service.impl;

import com.hotel.dao.interfaces.IFactureDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.Facture;
import com.hotel.service.interfaces.IFacturationService;
import com.hotel.util.Logger;

import java.time.LocalDate;
import java.util.List;

/**
 * Implémentation du service métier pour la gestion de la facturation.
 */
public class FacturationServiceImpl implements IFacturationService {
    private static final Logger logger = Logger.getLogger(FacturationServiceImpl.class);
    private final IFactureDAO factureDAO;

    public FacturationServiceImpl() {
        this.factureDAO = DAOFactory.getFactureDAO();
    }

    @Override
    public Facture createFacture(Facture facture) throws ServiceException {
        try {
            int id = factureDAO.create(facture);
            facture.setIdFacture(id);
            logger.info("Facture créée avec succès: " + id);
            return facture;
        } catch (DAOException e) {
            logger.error("Erreur lors de la création de la facture", e);
            throw new ServiceException("Erreur lors de la création de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture findFactureById(int id) throws ServiceException {
        try {
            Facture facture = factureDAO.findById(id);
            if (facture == null) {
                throw new ServiceException("Facture non trouvée avec l'ID: " + id);
            }
            return facture;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de la facture: " + id, e);
            throw new ServiceException("Erreur lors de la recherche de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture findFactureByReservation(int idReservation) throws ServiceException {
        try {
            return factureDAO.findByReservation(idReservation);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de la facture pour la réservation: " + idReservation, e);
            throw new ServiceException("Erreur lors de la recherche de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> getAllFactures() throws ServiceException {
        try {
            return factureDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de toutes les factures", e);
            throw new ServiceException("Erreur lors de la récupération des factures: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> getFacturesByPeriod(LocalDate dateDebut, LocalDate dateFin) throws ServiceException {
        try {
            return factureDAO.findByPeriod(dateDebut, dateFin);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de factures pour la période", e);
            throw new ServiceException("Erreur lors de la recherche de factures: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> getPendingFactures() throws ServiceException {
        try {
            return factureDAO.findPending();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des factures en attente", e);
            throw new ServiceException("Erreur lors de la récupération des factures en attente: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture updateFacture(Facture facture) throws ServiceException {
        try {
            boolean updated = factureDAO.update(facture);
            if (!updated) {
                throw new ServiceException("Échec de la mise à jour de la facture");
            }
            logger.info("Facture mise à jour avec succès: " + facture.getIdFacture());
            return facture;
        } catch (DAOException e) {
            logger.error("Erreur lors de la mise à jour de la facture", e);
            throw new ServiceException("Erreur lors de la mise à jour de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean markFactureAsPaid(int id) throws ServiceException {
        try {
            Facture facture = factureDAO.findById(id);
            if (facture == null) {
                throw new ServiceException("Facture non trouvée");
            }
            if (!facture.peutEtrePayee()) {
                throw new ServiceException("Cette facture ne peut pas être marquée comme payée");
            }
            facture.setStatut(Facture.Statut.PAYEE);
            return factureDAO.update(facture);
        } catch (DAOException e) {
            logger.error("Erreur lors du marquage de la facture comme payée: " + id, e);
            throw new ServiceException("Erreur lors du marquage de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFacture(int id) throws ServiceException {
        try {
            boolean deleted = factureDAO.delete(id);
            if (deleted) {
                logger.info("Facture supprimée avec succès: " + id);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression de la facture: " + id, e);
            throw new ServiceException("Erreur lors de la suppression de la facture: " + e.getMessage(), e);
        }
    }
}

