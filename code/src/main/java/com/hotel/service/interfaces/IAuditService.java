package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface du service métier pour la gestion des logs d'audit.
 */
public interface IAuditService {
    void logAction(int idUtilisateur, String action, String tableAffectee, Integer idEnregistrement, String details) throws ServiceException;
    AuditLog findById(int idLog) throws ServiceException;
    List<AuditLog> getAllLogs() throws ServiceException;
    List<AuditLog> getLogsByUtilisateur(int idUtilisateur) throws ServiceException;
    List<AuditLog> getLogsByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) throws ServiceException;
    List<AuditLog> getLogsByAction(String action) throws ServiceException;
}


