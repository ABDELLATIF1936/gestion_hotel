package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IAuditLogDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.AuditLog;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les logs d'audit.
 */
public class AuditLogDAOImpl implements IAuditLogDAO {
    private static final Logger logger = Logger.getLogger(AuditLogDAOImpl.class);

    @Override
    public int create(AuditLog auditLog) throws DAOException {
        String sql = "INSERT INTO audit_log (idUtilisateur, action, table_affectee, id_enregistrement, details, date_action) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, auditLog.getIdUtilisateur());
            statement.setString(2, auditLog.getAction());
            statement.setString(3, auditLog.getTableAffectee());
            if (auditLog.getIdEnregistrement() != null) {
                statement.setInt(4, auditLog.getIdEnregistrement());
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            statement.setString(5, auditLog.getDetails());
            statement.setTimestamp(6, Timestamp.valueOf(auditLog.getDateAction()));
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création du log d'audit");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Log d'audit créé avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création du log d'audit, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion lors de la création du log d'audit", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du log d'audit", e);
            throw new DAOException("Erreur lors de la création du log d'audit: " + e.getMessage(), e);
        }
    }

    @Override
    public AuditLog findById(int idLog) throws DAOException {
        String sql = "SELECT * FROM audit_log WHERE idLog = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idLog);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAuditLog(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du log d'audit par ID: " + idLog, e);
            throw new DAOException("Erreur lors de la recherche du log: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> findAll() throws DAOException {
        String sql = "SELECT * FROM audit_log ORDER BY date_action DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                logs.add(mapResultSetToAuditLog(resultSet));
            }
            
            return logs;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les logs d'audit", e);
            throw new DAOException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> findByUtilisateur(int idUtilisateur) throws DAOException {
        String sql = "SELECT * FROM audit_log WHERE idUtilisateur = ? ORDER BY date_action DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idUtilisateur);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
            
            return logs;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des logs par utilisateur", e);
            throw new DAOException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> findByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) throws DAOException {
        String sql = "SELECT * FROM audit_log WHERE date_action BETWEEN ? AND ? ORDER BY date_action DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(dateDebut));
            statement.setTimestamp(2, Timestamp.valueOf(dateFin));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
            
            return logs;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des logs par période", e);
            throw new DAOException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> findByAction(String action) throws DAOException {
        String sql = "SELECT * FROM audit_log WHERE action = ? ORDER BY date_action DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, action);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
            
            return logs;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des logs par action", e);
            throw new DAOException("Erreur lors de la récupération des logs: " + e.getMessage(), e);
        }
    }

    private AuditLog mapResultSetToAuditLog(ResultSet resultSet) throws SQLException {
        AuditLog auditLog = new AuditLog();
        auditLog.setIdLog(resultSet.getInt("idLog"));
        auditLog.setIdUtilisateur(resultSet.getInt("idUtilisateur"));
        auditLog.setAction(resultSet.getString("action"));
        auditLog.setTableAffectee(resultSet.getString("table_affectee"));
        
        int idEnregistrement = resultSet.getInt("id_enregistrement");
        if (!resultSet.wasNull()) {
            auditLog.setIdEnregistrement(idEnregistrement);
        }
        
        auditLog.setDetails(resultSet.getString("details"));
        
        Timestamp dateAction = resultSet.getTimestamp("date_action");
        if (dateAction != null) {
            auditLog.setDateAction(dateAction.toLocalDateTime());
        }
        
        return auditLog;
    }
}


