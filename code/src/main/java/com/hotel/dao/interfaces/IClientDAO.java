package com.hotel.dao.interfaces;

import com.hotel.exception.DAOException;
import com.hotel.model.Client;

import java.util.List;

/**
 * Interface DAO pour la gestion des clients.
 */
public interface IClientDAO {
    /**
     * Crée un nouveau client.
     *
     * @param client le client à créer
     * @return l'ID du client créé
     * @throws DAOException si une erreur survient
     */
    int create(Client client) throws DAOException;

    /**
     * Récupère un client par son ID.
     *
     * @param idClient l'ID du client
     * @return le client trouvé, ou null si non trouvé
     * @throws DAOException si une erreur survient
     */
    Client findById(int idClient) throws DAOException;

    /**
     * Récupère un client par son email.
     *
     * @param email l'email du client
     * @return le client trouvé, ou null si non trouvé
     * @throws DAOException si une erreur survient
     */
    Client findByEmail(String email) throws DAOException;

    /**
     * Récupère tous les clients.
     *
     * @return la liste de tous les clients
     * @throws DAOException si une erreur survient
     */
    List<Client> findAll() throws DAOException;

    /**
     * Recherche des clients par nom ou prénom.
     *
     * @param searchTerm le terme de recherche
     * @return la liste des clients correspondants
     * @throws DAOException si une erreur survient
     */
    List<Client> searchByName(String searchTerm) throws DAOException;

    /**
     * Met à jour un client.
     *
     * @param client le client à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws DAOException si une erreur survient
     */
    boolean update(Client client) throws DAOException;

    /**
     * Supprime un client.
     *
     * @param idClient l'ID du client à supprimer
     * @return true si la suppression a réussi
     * @throws DAOException si une erreur survient
     */
    boolean delete(int idClient) throws DAOException;
}

