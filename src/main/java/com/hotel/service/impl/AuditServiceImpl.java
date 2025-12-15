package com.hotel.service.impl;

import com.hotel.dao.interfaces.IAuditLogDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.AuditLog;
import com.hotel.service.interfaces.IAuditService;
import com.hotel.util.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du service métier pour la gestion des logs d'audit.
 */
public class AuditServiceImpl implements IAuditService {
    private static final Logger logger = Logger.getLogger(AuditServiceImpl.class);
    private final IAuditLogDAO auditLogDAO;

    public AuditServiceImpl() {
        this.auditLogDAO = DAOFactory.getAuditLogDAO();
    }

    @Override
    public void logAction(int idUtilisateur, String action, String tableAffectee, Integer idEnregistrement, String details) throws ServiceException {
        try {
            AuditLog auditLog = new AuditLog(idUtilisateur, action, tableAffectee);
            auditLog.setIdEnregistrement(idEnregistrement);
            auditLog.setDetails(details);
            
            auditLogDAO.create(auditLog);
            logger.debug("Action auditée: " + action + " sur " + tableAffectee + " par utilisateur " + idUtilisateur);
        } catch (DAOException e) {
            logger.error("Erreur lors de l'enregistrement du log d'audit", e);
            throw new ServiceException("Erreur lors de l'enregistrement du log: " + e.getMessage(), e);
        }
    }

    @Override
    public AuditLog findById(int idLog) throws ServiceException {
        try {
            AuditLog auditLog = auditLogDAO.findById(idLog);
            if (auditLog == null) {
                throw new ServiceException("Log d'audit non trouvé avec l'ID: " + idLog);
            }
            return auditLog;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche du log d'audit: " + idLog, e);
            throw new ServiceException("Erreur lors de la recherche du log: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> getAllLogs() throws ServiceException {
        try {
            return auditLogDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de tous les logs d'audit", e);
            throw new ServiceException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> getLogsByUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            return auditLogDAO.findByUtilisateur(idUtilisateur);
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des logs par utilisateur", e);
            throw new ServiceException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> getLogsByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) throws ServiceException {
        try {
            return auditLogDAO.findByDateRange(dateDebut, dateFin);
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des logs par période", e);
            throw new ServiceException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> getLogsByAction(String action) throws ServiceException {
        try {
            return auditLogDAO.findByAction(action);
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des logs par action", e);
            throw new ServiceException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }
}


