package com.hotel.service.impl;

import com.hotel.dao.interfaces.IServiceDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.ServiceSupplementaire;
import com.hotel.service.interfaces.IServiceSupplementaireService;
import com.hotel.util.Logger;

import java.util.List;

/**
 * Implémentation du service métier pour la gestion des services supplémentaires.
 */
public class ServiceSupplementaireServiceImpl implements IServiceSupplementaireService {
    private static final Logger logger = Logger.getLogger(ServiceSupplementaireServiceImpl.class);
    private final IServiceDAO serviceDAO;

    public ServiceSupplementaireServiceImpl() {
        this.serviceDAO = DAOFactory.getServiceDAO();
    }

    @Override
    public ServiceSupplementaire createService(ServiceSupplementaire service) throws ServiceException {
        try {
            int id = serviceDAO.create(service);
            service.setIdService(id);
            logger.info("Service créé avec succès: " + id);
            return service;
        } catch (DAOException e) {
            logger.error("Erreur lors de la création du service", e);
            throw new ServiceException("Erreur lors de la création du service: " + e.getMessage(), e);
        }
    }

    @Override
    public ServiceSupplementaire findServiceById(int id) throws ServiceException {
        try {
            ServiceSupplementaire service = serviceDAO.findById(id);
            if (service == null) {
                throw new ServiceException("Service non trouvé avec l'ID: " + id);
            }
            return service;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche du service: " + id, e);
            throw new ServiceException("Erreur lors de la recherche du service: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ServiceSupplementaire> getAllServices() throws ServiceException {
        try {
            return serviceDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de tous les services", e);
            throw new ServiceException("Erreur lors de la récupération des services: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ServiceSupplementaire> getActiveServices() throws ServiceException {
        try {
            return serviceDAO.findActive();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des services actifs", e);
            throw new ServiceException("Erreur lors de la récupération des services actifs: " + e.getMessage(), e);
        }
    }

    @Override
    public ServiceSupplementaire updateService(ServiceSupplementaire service) throws ServiceException {
        try {
            boolean updated = serviceDAO.update(service);
            if (!updated) {
                throw new ServiceException("Échec de la mise à jour du service");
            }
            logger.info("Service mis à jour avec succès: " + service.getIdService());
            return service;
        } catch (DAOException e) {
            logger.error("Erreur lors de la mise à jour du service", e);
            throw new ServiceException("Erreur lors de la mise à jour du service: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteService(int id) throws ServiceException {
        try {
            boolean deleted = serviceDAO.delete(id);
            if (deleted) {
                logger.info("Service supprimé avec succès: " + id);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression du service: " + id, e);
            throw new ServiceException("Erreur lors de la suppression du service: " + e.getMessage(), e);
        }
    }
}

