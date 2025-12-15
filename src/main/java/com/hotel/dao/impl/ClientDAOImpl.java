package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IClientDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Client;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les clients.
 */
public class ClientDAOImpl implements IClientDAO {
    private static final Logger logger = Logger.getLogger(ClientDAOImpl.class);

    @Override
    public int create(Client client) throws DAOException {
        String sql = "INSERT INTO client (nom, prenom, telephone, email, typeClient) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, client.getNom());
            statement.setString(2, client.getPrenom());
            statement.setString(3, client.getTelephone());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getTypeClient());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création du client, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Client créé avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création du client, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion lors de la création du client", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du client", e);
            throw new DAOException("Erreur lors de la création du client: " + e.getMessage(), e);
        }
    }

    @Override
    public Client findById(int idClient) throws DAOException {
        String sql = "SELECT * FROM client WHERE idClient = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idClient);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToClient(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du client par ID: " + idClient, e);
            throw new DAOException("Erreur lors de la recherche du client: " + e.getMessage(), e);
        }
    }

    @Override
    public Client findByEmail(String email) throws DAOException {
        String sql = "SELECT * FROM client WHERE email = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToClient(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du client par email: " + email, e);
            throw new DAOException("Erreur lors de la recherche du client par email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findAll() throws DAOException {
        String sql = "SELECT * FROM client ORDER BY nom, prenom";
        List<Client> clients = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                clients.add(mapResultSetToClient(resultSet));
            }
            return clients;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les clients", e);
            throw new DAOException("Erreur lors de la récupération des clients: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> searchByName(String searchTerm) throws DAOException {
        String sql = "SELECT * FROM client WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        List<Client> clients = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clients.add(mapResultSetToClient(resultSet));
                }
                return clients;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de clients par nom: " + searchTerm, e);
            throw new DAOException("Erreur lors de la recherche de clients: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Client client) throws DAOException {
        String sql = "UPDATE client SET nom = ?, prenom = ?, telephone = ?, email = ?, typeClient = ? WHERE idClient = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, client.getNom());
            statement.setString(2, client.getPrenom());
            statement.setString(3, client.getTelephone());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getTypeClient());
            statement.setInt(6, client.getIdClient());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Client mis à jour avec l'ID: " + client.getIdClient());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du client: " + client.getIdClient(), e);
            throw new DAOException("Erreur lors de la mise à jour du client: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int idClient) throws DAOException {
        String sql = "DELETE FROM client WHERE idClient = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idClient);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Client supprimé avec l'ID: " + idClient);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du client: " + idClient, e);
            throw new DAOException("Erreur lors de la suppression du client: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet Client.
     *
     * @param resultSet le ResultSet à mapper
     * @return un objet Client
     * @throws SQLException si une erreur SQL survient
     */
    private Client mapResultSetToClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        try {
            client.setIdClient(resultSet.getInt("idClient"));
            client.setNom(resultSet.getString("nom"));
            client.setPrenom(resultSet.getString("prenom"));
            client.setTelephone(resultSet.getString("telephone"));
            client.setEmail(resultSet.getString("email"));
            client.setTypeClient(resultSet.getString("typeClient"));
        } catch (Exception e) {
            logger.error("Erreur lors du mapping du ResultSet vers Client", e);
            throw new SQLException("Erreur lors du mapping: " + e.getMessage());
        }
        return client;
    }
}

