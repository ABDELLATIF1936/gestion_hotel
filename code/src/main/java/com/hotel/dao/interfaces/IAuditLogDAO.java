package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface DAO pour la gestion des logs d'audit.
 */
public interface IAuditLogDAO {
    /**
     * Crée un nouveau log d'audit.
     *
     * @param auditLog le log à créer
     * @return l'ID du log créé
     * @throws DAOException en cas d'erreur
     */
    int create(AuditLog auditLog) throws DAOException;

    /**
     * Récupère un log par son ID.
     *
     * @param idLog l'ID du log
     * @return le log ou null si non trouvé
     * @throws DAOException en cas d'erreur
     */
    AuditLog findById(int idLog) throws DAOException;

    /**
     * Récupère tous les logs.
     *
     * @return la liste des logs
     * @throws DAOException en cas d'erreur
     */
    List<AuditLog> findAll() throws DAOException;

    /**
     * Récupère les logs d'un utilisateur.
     *
     * @param idUtilisateur l'ID de l'utilisateur
     * @return la liste des logs
     * @throws DAOException en cas d'erreur
     */
    List<AuditLog> findByUtilisateur(int idUtilisateur) throws DAOException;

    /**
     * Récupère les logs dans une période donnée.
     *
     * @param dateDebut la date de début
     * @param dateFin la date de fin
     * @return la liste des logs
     * @throws DAOException en cas d'erreur
     */
    List<AuditLog> findByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) throws DAOException;

    /**
     * Récupère les logs par action.
     *
     * @param action l'action
     * @return la liste des logs
     * @throws DAOException en cas d'erreur
     */
    List<AuditLog> findByAction(String action) throws DAOException;
}


