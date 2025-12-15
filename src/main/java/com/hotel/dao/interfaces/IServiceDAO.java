package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.ServiceSupplementaire;

import java.util.List;

/**
 * Interface DAO pour la gestion des services supplémentaires.
 */
public interface IServiceDAO {
    /**
     * Crée un nouveau service.
     *
     * @param service le service à créer
     * @return l'ID du service créé
     * @throws DAOException si une erreur survient
     */
    int create(ServiceSupplementaire service) throws DAOException;

    /**
     * Récupère un service par son ID.
     *
     * @param idService l'ID du service
     * @return le service trouvé, ou null si non trouvé
     * @throws DAOException si une erreur survient
     */
    ServiceSupplementaire findById(int idService) throws DAOException;

    /**
     * Récupère tous les services.
     *
     * @return la liste de tous les services
     * @throws DAOException si une erreur survient
     */
    List<ServiceSupplementaire> findAll() throws DAOException;

    /**
     * Récupère les services actifs.
     *
     * @return la liste des services actifs
     * @throws DAOException si une erreur survient
     */
    List<ServiceSupplementaire> findActive() throws DAOException;

    /**
     * Met à jour un service.
     *
     * @param service le service à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean update(ServiceSupplementaire service) throws DAOException;

    /**
     * Supprime un service.
     *
     * @param idService l'ID du service à supprimer
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean delete(int idService) throws DAOException;
}

