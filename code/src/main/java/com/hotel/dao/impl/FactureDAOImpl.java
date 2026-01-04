package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IFactureDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Facture;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les factures.
 */
public class FactureDAOImpl implements IFactureDAO {
    private static final Logger logger = Logger.getLogger(FactureDAOImpl.class);

    @Override
    public int create(Facture facture) throws DAOException {
        String sql = "INSERT INTO facture (idReservation, dateEmission, montantTotal, statut, notes) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, facture.getIdReservation());
            statement.setDate(2, Date.valueOf(facture.getDateEmission()));
            statement.setDouble(3, facture.getMontantTotal());
            statement.setString(4, facture.getStatutAsString());
            statement.setString(5, facture.getNotes());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création de la facture, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Facture créée avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création de la facture, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de la facture", e);
            throw new DAOException("Erreur lors de la création de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture findById(int idFacture) throws DAOException {
        String sql = "SELECT * FROM facture WHERE idFacture = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idFacture);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToFacture(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de la facture: " + idFacture, e);
            throw new DAOException("Erreur lors de la recherche de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture findByReservation(int idReservation) throws DAOException {
        String sql = "SELECT * FROM facture WHERE idReservation = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idReservation);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToFacture(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de la facture pour la réservation: " + idReservation, e);
            throw new DAOException("Erreur lors de la recherche de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findAll() throws DAOException {
        String sql = "SELECT * FROM facture ORDER BY dateEmission DESC";
        List<Facture> factures = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                factures.add(mapResultSetToFacture(resultSet));
            }
            return factures;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de toutes les factures", e);
            throw new DAOException("Erreur lors de la récupération des factures: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findByPeriod(LocalDate dateDebut, LocalDate dateFin) throws DAOException {
        String sql = "SELECT * FROM facture WHERE dateEmission BETWEEN ? AND ? ORDER BY dateEmission DESC";
        List<Facture> factures = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(dateDebut));
            statement.setDate(2, Date.valueOf(dateFin));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    factures.add(mapResultSetToFacture(resultSet));
                }
                return factures;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de factures pour la période", e);
            throw new DAOException("Erreur lors de la recherche de factures: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findPending() throws DAOException {
        String sql = "SELECT * FROM facture WHERE statut = 'EN_ATTENTE' ORDER BY dateEmission";
        List<Facture> factures = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                factures.add(mapResultSetToFacture(resultSet));
            }
            return factures;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des factures en attente", e);
            throw new DAOException("Erreur lors de la récupération des factures en attente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Facture facture) throws DAOException {
        String sql = "UPDATE facture SET idReservation = ?, dateEmission = ?, montantTotal = ?, statut = ?, notes = ? " +
                     "WHERE idFacture = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, facture.getIdReservation());
            statement.setDate(2, Date.valueOf(facture.getDateEmission()));
            statement.setDouble(3, facture.getMontantTotal());
            statement.setString(4, facture.getStatutAsString());
            statement.setString(5, facture.getNotes());
            statement.setInt(6, facture.getIdFacture());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Facture mise à jour avec l'ID: " + facture.getIdFacture());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la facture: " + facture.getIdFacture(), e);
            throw new DAOException("Erreur lors de la mise à jour de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int idFacture) throws DAOException {
        String sql = "DELETE FROM facture WHERE idFacture = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idFacture);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Facture supprimée avec l'ID: " + idFacture);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la facture: " + idFacture, e);
            throw new DAOException("Erreur lors de la suppression de la facture: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet Facture.
     *
     * @param resultSet le ResultSet à mapper
     * @return un objet Facture
     * @throws SQLException si une erreur SQL survient
     */
    private Facture mapResultSetToFacture(ResultSet resultSet) throws SQLException {
        Facture facture = new Facture();
        try {
            facture.setIdFacture(resultSet.getInt("idFacture"));
            facture.setIdReservation(resultSet.getInt("idReservation"));
            
            Date dateEmission = resultSet.getDate("dateEmission");
            if (dateEmission != null) {
                facture.setDateEmission(dateEmission.toLocalDate());
            }
            
            facture.setMontantTotal(resultSet.getDouble("montantTotal"));
            facture.setStatut(Facture.parseStatut(resultSet.getString("statut")));
            facture.setNotes(resultSet.getString("notes"));
        } catch (Exception e) {
            logger.error("Erreur lors du mapping du ResultSet vers Facture", e);
            throw new SQLException("Erreur lors du mapping: " + e.getMessage());
        }
        return facture;
    }
}

