package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IServiceDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.ServiceSupplementaire;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les services supplémentaires.
 */
public class ServiceDAOImpl implements IServiceDAO {
    private static final Logger logger = Logger.getLogger(ServiceDAOImpl.class);

    @Override
    public int create(ServiceSupplementaire service) throws DAOException {
        String sql = "INSERT INTO servicesupplementaire (nom, prix, description, actif) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, service.getNom());
            statement.setDouble(2, service.getPrix());
            statement.setString(3, service.getDescription());
            statement.setBoolean(4, service.isActif());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création du service, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Service créé avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création du service, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du service", e);
            throw new DAOException("Erreur lors de la création du service: " + e.getMessage(), e);
        }
    }

    @Override
    public ServiceSupplementaire findById(int idService) throws DAOException {
        String sql = "SELECT * FROM servicesupplementaire WHERE idService = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idService);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToService(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du service: " + idService, e);
            throw new DAOException("Erreur lors de la recherche du service: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ServiceSupplementaire> findAll() throws DAOException {
        String sql = "SELECT * FROM servicesupplementaire ORDER BY nom";
        List<ServiceSupplementaire> services = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                services.add(mapResultSetToService(resultSet));
            }
            return services;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les services", e);
            throw new DAOException("Erreur lors de la récupération des services: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ServiceSupplementaire> findActive() throws DAOException {
        String sql = "SELECT * FROM servicesupplementaire WHERE actif = TRUE ORDER BY nom";
        List<ServiceSupplementaire> services = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                services.add(mapResultSetToService(resultSet));
            }
            return services;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des services actifs", e);
            throw new DAOException("Erreur lors de la récupération des services actifs: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(ServiceSupplementaire service) throws DAOException {
        String sql = "UPDATE servicesupplementaire SET nom = ?, prix = ?, description = ?, actif = ? WHERE idService = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, service.getNom());
            statement.setDouble(2, service.getPrix());
            statement.setString(3, service.getDescription());
            statement.setBoolean(4, service.isActif());
            statement.setInt(5, service.getIdService());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Service mis à jour avec l'ID: " + service.getIdService());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du service: " + service.getIdService(), e);
            throw new DAOException("Erreur lors de la mise à jour du service: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int idService) throws DAOException {
        String sql = "DELETE FROM servicesupplementaire WHERE idService = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idService);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Service supprimé avec l'ID: " + idService);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du service: " + idService, e);
            throw new DAOException("Erreur lors de la suppression du service: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet ServiceSupplementaire.
     *
     * @param resultSet le ResultSet à mapper
     * @return un objet ServiceSupplementaire
     * @throws SQLException si une erreur SQL survient
     */
    private ServiceSupplementaire mapResultSetToService(ResultSet resultSet) throws SQLException {
        ServiceSupplementaire service = new ServiceSupplementaire();
        try {
            service.setIdService(resultSet.getInt("idService"));
            service.setNom(resultSet.getString("nom"));
            service.setPrix(resultSet.getDouble("prix"));
            service.setDescription(resultSet.getString("description"));
            service.setActif(resultSet.getBoolean("actif"));
        } catch (Exception e) {
            logger.error("Erreur lors du mapping du ResultSet vers ServiceSupplementaire", e);
            throw new SQLException("Erreur lors du mapping: " + e.getMessage());
        }
        return service;
    }
}

