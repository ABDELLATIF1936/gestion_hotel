package com.hotel.controller;

import com.hotel.model.Client;
import com.hotel.service.interfaces.IClientService;
import com.hotel.factory.ServiceFactory;
import com.hotel.util.Logger;

import java.util.List;

/**
 * Contrôleur pour la gestion des clients.
 * Fait le lien entre la vue et le service métier.
 */
public class ClientController {
    private static final Logger logger = Logger.getLogger(ClientController.class);
    private final IClientService clientService;

    public ClientController() {
        this.clientService = ServiceFactory.getClientService();
    }

    /**
     * Crée un nouveau client.
     *
     * @param client le client à créer
     * @return le client créé avec son ID
     * @throws Exception si une erreur survient
     */
    public Client createClient(Client client) throws Exception {
        try {
            return clientService.createClient(client);
        } catch (Exception e) {
            logger.error("Erreur dans le contrôleur lors de la création du client", e);
            throw e;
        }
    }

    /**
     * Récupère tous les clients.
     *
     * @return la liste de tous les clients
     * @throws Exception si une erreur survient
     */
    public List<Client> getAllClients() throws Exception {
        try {
            return clientService.getAllClients();
        } catch (Exception e) {
            logger.error("Erreur dans le contrôleur lors de la récupération des clients", e);
            throw e;
        }
    }

    /**
     * Recherche des clients par nom ou prénom.
     *
     * @param searchTerm le terme de recherche
     * @return la liste des clients correspondants
     * @throws Exception si une erreur survient
     */
    public List<Client> searchClients(String searchTerm) throws Exception {
        try {
            return clientService.searchClientsByName(searchTerm);
        } catch (Exception e) {
            logger.error("Erreur dans le contrôleur lors de la recherche de clients", e);
            throw e;
        }
    }

    /**
     * Met à jour un client.
     *
     * @param client le client à mettre à jour
     * @throws Exception si une erreur survient
     */
    public void updateClient(Client client) throws Exception {
        try {
            clientService.updateClient(client);
        } catch (Exception e) {
            logger.error("Erreur dans le contrôleur lors de la mise à jour du client", e);
            throw e;
        }
    }

    /**
     * Supprime un client.
     *
     * @param idClient l'ID du client à supprimer
     * @throws Exception si une erreur survient
     */
    public void deleteClient(int idClient) throws Exception {
        try {
            clientService.deleteClient(idClient);
        } catch (Exception e) {
            logger.error("Erreur dans le contrôleur lors de la suppression du client", e);
            throw e;
        }
    }

    /**
     * Récupère un client par son ID.
     *
     * @param id l'ID du client
     * @return le client trouvé
     * @throws Exception si une erreur survient
     */
    public Client getClientById(int id) throws Exception {
        try {
            return clientService.findClientById(id);
        } catch (Exception e) {
            logger.error("Erreur dans le contrôleur lors de la recherche du client", e);
            throw e;
        }
    }
}

