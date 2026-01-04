package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IChambreDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Chambre;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.DateUtil;
import com.hotel.util.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les chambres.
 */
public class ChambreDAOImpl implements IChambreDAO {
    private static final Logger logger = Logger.getLogger(ChambreDAOImpl.class);

    @Override
    public boolean create(Chambre chambre) throws DAOException {
        String sql = "INSERT INTO chambre (numeroChambre, categorie, statut, prixNuit, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, chambre.getNumeroChambre());
            statement.setString(2, chambre.getCategorieAsString());
            statement.setString(3, chambre.getStatutAsString());
            statement.setDouble(4, chambre.getPrixNuit());
            statement.setString(5, chambre.getDescription());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Chambre créée avec le numéro: " + chambre.getNumeroChambre());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de la chambre: " + chambre.getNumeroChambre(), e);
            throw new DAOException("Erreur lors de la création de la chambre: " + e.getMessage(), e);
        }
    }

    @Override
    public Chambre findByNumero(int numeroChambre) throws DAOException {
        String sql = "SELECT * FROM chambre WHERE numeroChambre = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, numeroChambre);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToChambre(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de la chambre: " + numeroChambre, e);
            throw new DAOException("Erreur lors de la recherche de la chambre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> findAll() throws DAOException {
        String sql = "SELECT * FROM chambre ORDER BY numeroChambre";
        List<Chambre> chambres = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                chambres.add(mapResultSetToChambre(resultSet));
            }
            return chambres;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de toutes les chambres", e);
            throw new DAOException("Erreur lors de la récupération des chambres: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> findAvailable() throws DAOException {
        String sql = "SELECT * FROM chambre WHERE statut = 'DISPONIBLE' ORDER BY numeroChambre";
        List<Chambre> chambres = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                chambres.add(mapResultSetToChambre(resultSet));
            }
            return chambres;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des chambres disponibles", e);
            throw new DAOException("Erreur lors de la récupération des chambres disponibles: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> findByCategorie(Chambre.Categorie categorie) throws DAOException {
        String sql = "SELECT * FROM chambre WHERE categorie = ? ORDER BY numeroChambre";
        List<Chambre> chambres = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, categorie.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    chambres.add(mapResultSetToChambre(resultSet));
                }
                return chambres;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de chambres par catégorie: " + categorie, e);
            throw new DAOException("Erreur lors de la recherche de chambres: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Chambre> findAvailableForPeriod(LocalDate dateDebut, LocalDate dateFin) throws DAOException {
        String sql = "SELECT DISTINCT c.* FROM chambre c " +
                     "WHERE c.statut = 'DISPONIBLE' " +
                     "AND c.numeroChambre NOT IN (" +
                     "    SELECT r.numeroChambre FROM reservation r " +
                     "    WHERE r.statut IN ('CONFIRMEE', 'EN_COURS') " +
                     "    AND ? < r.dateFin AND ? > r.dateDebut" +
                     ") ORDER BY c.numeroChambre";
        List<Chambre> chambres = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(dateDebut));
            statement.setDate(2, Date.valueOf(dateFin));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    chambres.add(mapResultSetToChambre(resultSet));
                }
                return chambres;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de chambres disponibles pour la période", e);
            throw new DAOException("Erreur lors de la recherche de chambres disponibles: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Chambre chambre) throws DAOException {
        String sql = "UPDATE chambre SET categorie = ?, statut = ?, prixNuit = ?, description = ? WHERE numeroChambre = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, chambre.getCategorieAsString());
            statement.setString(2, chambre.getStatutAsString());
            statement.setDouble(3, chambre.getPrixNuit());
            statement.setString(4, chambre.getDescription());
            statement.setInt(5, chambre.getNumeroChambre());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Chambre mise à jour: " + chambre.getNumeroChambre());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la chambre: " + chambre.getNumeroChambre(), e);
            throw new DAOException("Erreur lors de la mise à jour de la chambre: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatut(int numeroChambre, Chambre.Statut statut) throws DAOException {
        String sql = "UPDATE chambre SET statut = ? WHERE numeroChambre = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, statut.name());
            statement.setInt(2, numeroChambre);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Statut de la chambre " + numeroChambre + " mis à jour: " + statut);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du statut de la chambre: " + numeroChambre, e);
            throw new DAOException("Erreur lors de la mise à jour du statut: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int numeroChambre) throws DAOException {
        String sql = "DELETE FROM chambre WHERE numeroChambre = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, numeroChambre);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Chambre supprimée: " + numeroChambre);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la chambre: " + numeroChambre, e);
            throw new DAOException("Erreur lors de la suppression de la chambre: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet Chambre.
     *
     * @param resultSet le ResultSet à mapper
     * @return un objet Chambre
     * @throws SQLException si une erreur SQL survient
     */
    private Chambre mapResultSetToChambre(ResultSet resultSet) throws SQLException {
        Chambre chambre = new Chambre();
        try {
            chambre.setNumeroChambre(resultSet.getInt("numeroChambre"));
            chambre.setCategorie(Chambre.parseCategorie(resultSet.getString("categorie")));
            chambre.setStatut(Chambre.parseStatut(resultSet.getString("statut")));
            chambre.setPrixNuit(resultSet.getDouble("prixNuit"));
            chambre.setDescription(resultSet.getString("description"));
        } catch (Exception e) {
            logger.error("Erreur lors du mapping du ResultSet vers Chambre", e);
            throw new SQLException("Erreur lors du mapping: " + e.getMessage());
        }
        return chambre;
    }
}

