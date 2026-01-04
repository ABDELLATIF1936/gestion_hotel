package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.Client;

import java.util.List;

/**
 * Interface du service métier pour la gestion des clients.
 */
public interface IClientService {
    Client createClient(Client client) throws ServiceException;
    Client findClientById(int id) throws ServiceException;
    Client findClientByEmail(String email) throws ServiceException;
    List<Client> getAllClients() throws ServiceException;
    List<Client> searchClientsByName(String searchTerm) throws ServiceException;
    Client updateClient(Client client) throws ServiceException;
    boolean deleteClient(int id) throws ServiceException;
}

