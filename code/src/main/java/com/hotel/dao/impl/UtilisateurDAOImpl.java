package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IUtilisateurDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.exception.ValidationException;
import com.hotel.model.Utilisateur;
import com.hotel.model.RoleUtilisateur;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les utilisateurs.
 */
public class UtilisateurDAOImpl implements IUtilisateurDAO {
    private static final Logger logger = Logger.getLogger(UtilisateurDAOImpl.class);

    @Override
    public int create(Utilisateur utilisateur) throws DAOException {
        String sql = "INSERT INTO utilisateur (username, password, idEmploye, role, actif, dateCreation) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, utilisateur.getUsername());
            statement.setString(2, utilisateur.getPasswordHash());
            statement.setInt(3, utilisateur.getIdEmploye());
            statement.setString(4, utilisateur.getRole().getRoleAsString());
            statement.setBoolean(5, utilisateur.isActif());
            statement.setTimestamp(6, Timestamp.valueOf(utilisateur.getDateCreation()));
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création de l'utilisateur, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Utilisateur créé avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création de l'utilisateur, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion lors de la création de l'utilisateur", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de l'utilisateur", e);
            throw new DAOException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public Utilisateur findById(int idUtilisateur) throws DAOException {
        String sql = "SELECT * FROM utilisateur WHERE idUtilisateur = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idUtilisateur);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUtilisateur(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par ID: " + idUtilisateur, e);
            throw new DAOException("Erreur lors de la recherche de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public Utilisateur findByUsername(String username) throws DAOException {
        String sql = "SELECT * FROM utilisateur WHERE username = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, username.toLowerCase().trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUtilisateur(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par username: " + username, e);
            throw new DAOException("Erreur lors de la recherche de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Utilisateur> findAll() throws DAOException {
        String sql = "SELECT * FROM utilisateur ORDER BY username";
        List<Utilisateur> utilisateurs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(resultSet));
            }
            
            return utilisateurs;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les utilisateurs", e);
            throw new DAOException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Utilisateur> findByRole(RoleUtilisateur role) throws DAOException {
        String sql = "SELECT * FROM utilisateur WHERE role = ? ORDER BY username";
        List<Utilisateur> utilisateurs = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, role.getRoleAsString());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    utilisateurs.add(mapResultSetToUtilisateur(resultSet));
                }
            }
            
            return utilisateurs;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des utilisateurs par rôle", e);
            throw new DAOException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Utilisateur utilisateur) throws DAOException {
        String sql = "UPDATE utilisateur SET username = ?, password = ?, idEmploye = ?, role = ?, actif = ? WHERE idUtilisateur = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, utilisateur.getUsername());
            statement.setString(2, utilisateur.getPasswordHash());
            statement.setInt(3, utilisateur.getIdEmploye());
            statement.setString(4, utilisateur.getRole().getRoleAsString());
            statement.setBoolean(5, utilisateur.isActif());
            statement.setInt(6, utilisateur.getIdUtilisateur());
            
            int rowsAffected = statement.executeUpdate();
            connection.commit();
            
            if (rowsAffected == 0) {
                throw new DAOException("Aucun utilisateur trouvé avec l'ID: " + utilisateur.getIdUtilisateur());
            }
            
            logger.info("Utilisateur mis à jour: " + utilisateur.getIdUtilisateur());
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur", e);
            throw new DAOException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateLastLogin(int idUtilisateur) throws DAOException {
        String sql = "UPDATE utilisateur SET dernierLogin = ? WHERE idUtilisateur = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, idUtilisateur);
            
            statement.executeUpdate();
            connection.commit();
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du dernier login: " + idUtilisateur, e);
            throw new DAOException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int idUtilisateur) throws DAOException {
        String sql = "DELETE FROM utilisateur WHERE idUtilisateur = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idUtilisateur);
            
            int rowsAffected = statement.executeUpdate();
            connection.commit();
            
            if (rowsAffected > 0) {
                logger.info("Utilisateur supprimé: " + idUtilisateur);
                return true;
            }
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'utilisateur: " + idUtilisateur, e);
            throw new DAOException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet Utilisateur.
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet resultSet) throws SQLException, DAOException {
        try {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setIdUtilisateur(resultSet.getInt("idUtilisateur"));
            utilisateur.setUsername(resultSet.getString("username"));
            utilisateur.setPasswordHash(resultSet.getString("password"));
            utilisateur.setIdEmploye(resultSet.getInt("idEmploye"));
            utilisateur.setRole(RoleUtilisateur.parseRole(resultSet.getString("role")));
            utilisateur.setActif(resultSet.getBoolean("actif"));
            
            Timestamp dateCreation = resultSet.getTimestamp("dateCreation");
            if (dateCreation != null) {
                utilisateur.setDateCreation(dateCreation.toLocalDateTime());
            }
            
            Timestamp dernierLogin = resultSet.getTimestamp("dernierLogin");
            if (dernierLogin != null) {
                utilisateur.setDernierLogin(dernierLogin.toLocalDateTime());
            }
            
            return utilisateur;
        } catch (ValidationException e) {
            logger.error("Erreur de validation lors du mapping de l'utilisateur", e);
            throw new DAOException("Erreur de validation des données utilisateur: " + e.getMessage(), e);
        }
    }
}


