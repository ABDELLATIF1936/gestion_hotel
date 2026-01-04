package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IReservationServiceDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.exception.ValidationException;
import com.hotel.model.Inclure;
import com.hotel.model.ServiceSupplementaire;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour la gestion des services associés aux réservations.
 */
public class ReservationServiceDAOImpl implements IReservationServiceDAO {
    private static final Logger logger = Logger.getLogger(ReservationServiceDAOImpl.class);

    @Override
    public boolean addServiceToReservation(Inclure inclure) throws DAOException {
        String sql = "INSERT INTO inclure (idReservation, idService, quantite, prixUnitaire) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantite = quantite + ?, prixUnitaire = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, inclure.getIdReservation());
            statement.setInt(2, inclure.getIdService());
            statement.setInt(3, inclure.getQuantite());
            statement.setDouble(4, inclure.getPrixUnitaire());
            statement.setInt(5, inclure.getQuantite()); // Pour ON DUPLICATE KEY UPDATE
            statement.setDouble(6, inclure.getPrixUnitaire()); // Pour ON DUPLICATE KEY UPDATE
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Service " + inclure.getIdService() + " ajouté à la réservation " + inclure.getIdReservation());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de l'ajout du service à la réservation", e);
            throw new DAOException("Erreur lors de l'ajout du service: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeServiceFromReservation(int idReservation, int idService) throws DAOException {
        String sql = "DELETE FROM inclure WHERE idReservation = ? AND idService = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idReservation);
            statement.setInt(2, idService);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Service " + idService + " supprimé de la réservation " + idReservation);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du service de la réservation", e);
            throw new DAOException("Erreur lors de la suppression du service: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Inclure> getServicesByReservation(int idReservation) throws DAOException {
        String sql = "SELECT * FROM inclure WHERE idReservation = ?";
        List<Inclure> services = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idReservation);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Inclure inclure = new Inclure();
                    inclure.setIdReservation(resultSet.getInt("idReservation"));
                    inclure.setIdService(resultSet.getInt("idService"));
                    inclure.setQuantite(resultSet.getInt("quantite"));
                    inclure.setPrixUnitaire(resultSet.getDouble("prixUnitaire"));
                    services.add(inclure);
                }
                return services;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des services de la réservation: " + idReservation, e);
            throw new DAOException("Erreur lors de la récupération des services: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ServiceSupplementaire> getServiceDetailsByReservation(int idReservation) throws DAOException {
        String sql = "SELECT s.*, i.quantite, i.prixUnitaire " +
                     "FROM servicesupplementaire s " +
                     "INNER JOIN inclure i ON s.idService = i.idService " +
                     "WHERE i.idReservation = ?";
        List<ServiceSupplementaire> services = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idReservation);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    try {
                        ServiceSupplementaire service = new ServiceSupplementaire();
                        service.setIdService(resultSet.getInt("idService"));
                        service.setNom(resultSet.getString("nom"));
                        service.setPrix(resultSet.getDouble("prixUnitaire")); // Utiliser le prix unitaire de la réservation
                        service.setDescription(resultSet.getString("description"));
                        service.setActif(resultSet.getBoolean("actif"));
                        services.add(service);
                    } catch (ValidationException e) {
                        logger.error("Erreur de validation lors du mapping du service", e);
                        throw new DAOException("Erreur de validation des données service: " + e.getMessage(), e);
                    }
                }
                return services;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des détails des services: " + idReservation, e);
            throw new DAOException("Erreur lors de la récupération des services: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateServiceQuantity(Inclure inclure) throws DAOException {
        String sql = "UPDATE inclure SET quantite = ?, prixUnitaire = ? WHERE idReservation = ? AND idService = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, inclure.getQuantite());
            statement.setDouble(2, inclure.getPrixUnitaire());
            statement.setInt(3, inclure.getIdReservation());
            statement.setInt(4, inclure.getIdService());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Quantité du service mise à jour pour la réservation " + inclure.getIdReservation());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la quantité", e);
            throw new DAOException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }

    @Override
    public double getTotalServicesAmount(int idReservation) throws DAOException {
        String sql = "SELECT SUM(quantite * prixUnitaire) as total FROM inclure WHERE idReservation = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idReservation);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double total = resultSet.getDouble("total");
                    return resultSet.wasNull() ? 0.0 : total;
                }
                return 0.0;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors du calcul du montant total des services: " + idReservation, e);
            throw new DAOException("Erreur lors du calcul: " + e.getMessage(), e);
        }
    }
}

