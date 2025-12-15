package com.hotel.service.impl;

import com.hotel.dao.interfaces.IClientDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.exception.ValidationException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.Client;
import com.hotel.service.interfaces.IClientService;
import com.hotel.util.Logger;

import java.util.List;

/**
 * Implémentation du service métier pour la gestion des clients.
 */
public class ClientServiceImpl implements IClientService {
    private static final Logger logger = Logger.getLogger(ClientServiceImpl.class);
    private final IClientDAO clientDAO;

    public ClientServiceImpl() {
        this.clientDAO = DAOFactory.getClientDAO();
    }

    @Override
    public Client createClient(Client client) throws ServiceException {
        try {
            // Validation
            validateClient(client);
            
            // Vérifier si l'email existe déjà
            Client existing = clientDAO.findByEmail(client.getEmail());
            if (existing != null) {
                throw new ServiceException("Un client avec cet email existe déjà");
            }
            
            int id = clientDAO.create(client);
            client.setIdClient(id);
            logger.info("Client créé avec succès: " + client.getEmail());
            return client;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la création du client", e);
            throw new ServiceException("Erreur lors de la création du client: " + e.getMessage(), e);
        }
    }

    @Override
    public Client findClientById(int id) throws ServiceException {
        try {
            Client client = clientDAO.findById(id);
            if (client == null) {
                throw new ServiceException("Client non trouvé avec l'ID: " + id);
            }
            return client;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche du client: " + id, e);
            throw new ServiceException("Erreur lors de la recherche du client: " + e.getMessage(), e);
        }
    }

    @Override
    public Client findClientByEmail(String email) throws ServiceException {
        try {
            return clientDAO.findByEmail(email);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche du client par email: " + email, e);
            throw new ServiceException("Erreur lors de la recherche du client: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> getAllClients() throws ServiceException {
        try {
            return clientDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de tous les clients", e);
            throw new ServiceException("Erreur lors de la récupération des clients: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> searchClientsByName(String searchTerm) throws ServiceException {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllClients();
            }
            return clientDAO.searchByName(searchTerm);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de clients: " + searchTerm, e);
            throw new ServiceException("Erreur lors de la recherche de clients: " + e.getMessage(), e);
        }
    }

    @Override
    public Client updateClient(Client client) throws ServiceException {
        try {
            validateClient(client);
            
            if (client.getIdClient() <= 0) {
                throw new ServiceException("ID de client invalide");
            }
            
            boolean updated = clientDAO.update(client);
            if (!updated) {
                throw new ServiceException("Échec de la mise à jour du client");
            }
            
            logger.info("Client mis à jour avec succès: " + client.getIdClient());
            return client;
        } catch (DAOException | ValidationException e) {
            logger.error("Erreur lors de la mise à jour du client", e);
            throw new ServiceException("Erreur lors de la mise à jour du client: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteClient(int id) throws ServiceException {
        try {
            boolean deleted = clientDAO.delete(id);
            if (deleted) {
                logger.info("Client supprimé avec succès: " + id);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression du client: " + id, e);
            throw new ServiceException("Erreur lors de la suppression du client: " + e.getMessage(), e);
        }
    }

    /**
     * Valide un client avant traitement.
     *
     * @param client le client à valider
     * @throws ValidationException si la validation échoue
     */
    private void validateClient(Client client) throws ValidationException {
        if (client == null) {
            throw new ValidationException("Le client ne peut pas être null");
        }
        // Les validations sont déjà faites dans les setters du modèle
    }
}

