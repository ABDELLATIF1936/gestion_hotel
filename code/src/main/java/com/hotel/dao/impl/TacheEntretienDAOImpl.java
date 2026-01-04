package com.hotel.dao.impl;

import com.hotel.dao.interfaces.ITacheEntretienDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.TacheEntretien;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les tâches d'entretien.
 */
public class TacheEntretienDAOImpl implements ITacheEntretienDAO {
    private static final Logger logger = Logger.getLogger(TacheEntretienDAOImpl.class);

    @Override
    public int create(TacheEntretien tache) throws DAOException {
        String sql = "INSERT INTO tacheentretien (idEmploye, numeroChambre, type, date, statut, description, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (tache.getIdEmploye() != null) {
                statement.setInt(1, tache.getIdEmploye());
            } else {
                statement.setNull(1, Types.INTEGER);
            }
            statement.setInt(2, tache.getNumeroChambre());
            statement.setString(3, tache.getTypeAsString());
            statement.setDate(4, Date.valueOf(tache.getDate()));
            statement.setString(5, tache.getStatutAsString());
            statement.setString(6, tache.getDescription());
            statement.setString(7, tache.getNotes());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création de la tâche, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Tâche créée avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création de la tâche, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de la tâche", e);
            throw new DAOException("Erreur lors de la création de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public TacheEntretien findById(int idTache) throws DAOException {
        String sql = "SELECT * FROM tacheentretien WHERE idTache = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idTache);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToTache(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de la tâche: " + idTache, e);
            throw new DAOException("Erreur lors de la recherche de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> findAll() throws DAOException {
        String sql = "SELECT * FROM tacheentretien ORDER BY date DESC, statut";
        List<TacheEntretien> taches = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                taches.add(mapResultSetToTache(resultSet));
            }
            return taches;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de toutes les tâches", e);
            throw new DAOException("Erreur lors de la récupération des tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> findByEmploye(int idEmploye) throws DAOException {
        String sql = "SELECT * FROM tacheentretien WHERE idEmploye = ? ORDER BY date DESC";
        List<TacheEntretien> taches = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idEmploye);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    taches.add(mapResultSetToTache(resultSet));
                }
                return taches;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de tâches pour l'employé: " + idEmploye, e);
            throw new DAOException("Erreur lors de la recherche de tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> findByChambre(int numeroChambre) throws DAOException {
        String sql = "SELECT * FROM tacheentretien WHERE numeroChambre = ? ORDER BY date DESC";
        List<TacheEntretien> taches = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, numeroChambre);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    taches.add(mapResultSetToTache(resultSet));
                }
                return taches;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de tâches pour la chambre: " + numeroChambre, e);
            throw new DAOException("Erreur lors de la recherche de tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> findByDate(LocalDate date) throws DAOException {
        String sql = "SELECT * FROM tacheentretien WHERE date = ? ORDER BY statut";
        List<TacheEntretien> taches = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(date));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    taches.add(mapResultSetToTache(resultSet));
                }
                return taches;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de tâches pour la date: " + date, e);
            throw new DAOException("Erreur lors de la recherche de tâches: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TacheEntretien> findPending() throws DAOException {
        String sql = "SELECT * FROM tacheentretien WHERE statut = 'EN_ATTENTE' ORDER BY date";
        List<TacheEntretien> taches = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                taches.add(mapResultSetToTache(resultSet));
            }
            return taches;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des tâches en attente", e);
            throw new DAOException("Erreur lors de la récupération des tâches en attente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(TacheEntretien tache) throws DAOException {
        String sql = "UPDATE tacheentretien SET idEmploye = ?, numeroChambre = ?, type = ?, date = ?, " +
                     "statut = ?, description = ?, notes = ? WHERE idTache = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            if (tache.getIdEmploye() != null) {
                statement.setInt(1, tache.getIdEmploye());
            } else {
                statement.setNull(1, Types.INTEGER);
            }
            statement.setInt(2, tache.getNumeroChambre());
            statement.setString(3, tache.getTypeAsString());
            statement.setDate(4, Date.valueOf(tache.getDate()));
            statement.setString(5, tache.getStatutAsString());
            statement.setString(6, tache.getDescription());
            statement.setString(7, tache.getNotes());
            statement.setInt(8, tache.getIdTache());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Tâche mise à jour avec l'ID: " + tache.getIdTache());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la tâche: " + tache.getIdTache(), e);
            throw new DAOException("Erreur lors de la mise à jour de la tâche: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int idTache) throws DAOException {
        String sql = "DELETE FROM tacheentretien WHERE idTache = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idTache);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Tâche supprimée avec l'ID: " + idTache);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la tâche: " + idTache, e);
            throw new DAOException("Erreur lors de la suppression de la tâche: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet TacheEntretien.
     *
     * @param resultSet le ResultSet à mapper
     * @return un objet TacheEntretien
     * @throws SQLException si une erreur SQL survient
     */
    private TacheEntretien mapResultSetToTache(ResultSet resultSet) throws SQLException {
        TacheEntretien tache = new TacheEntretien();
        try {
            tache.setIdTache(resultSet.getInt("idTache"));
            
            int idEmploye = resultSet.getInt("idEmploye");
            if (!resultSet.wasNull()) {
                tache.setIdEmploye(idEmploye);
            }
            
            tache.setNumeroChambre(resultSet.getInt("numeroChambre"));
            tache.setType(TacheEntretien.parseType(resultSet.getString("type")));
            
            Date date = resultSet.getDate("date");
            if (date != null) {
                tache.setDate(date.toLocalDate());
            }
            
            tache.setStatut(TacheEntretien.parseStatut(resultSet.getString("statut")));
            tache.setDescription(resultSet.getString("description"));
            tache.setNotes(resultSet.getString("notes"));
        } catch (Exception e) {
            logger.error("Erreur lors du mapping du ResultSet vers TacheEntretien", e);
            throw new SQLException("Erreur lors du mapping: " + e.getMessage());
        }
        return tache;
    }
}

