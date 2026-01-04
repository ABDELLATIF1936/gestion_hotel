package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IEmployeDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Employe;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les employés.
 */
public class EmployeDAOImpl implements IEmployeDAO {
    private static final Logger logger = Logger.getLogger(EmployeDAOImpl.class);

    @Override
    public int create(Employe employe) throws DAOException {
        String sql = "INSERT INTO employer (nom, prenom, role, telephone, email, actif) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, employe.getNom());
            statement.setString(2, employe.getPrenom());
            statement.setString(3, employe.getRoleAsString());
            statement.setString(4, employe.getTelephone());
            statement.setString(5, employe.getEmail());
            statement.setBoolean(6, employe.isActif());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création de l'employé, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Employé créé avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création de l'employé, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de l'employé", e);
            throw new DAOException("Erreur lors de la création de l'employé: " + e.getMessage(), e);
        }
    }

    @Override
    public Employe findById(int idEmploye) throws DAOException {
        String sql = "SELECT * FROM employer WHERE idEmploye = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idEmploye);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToEmploye(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de l'employé: " + idEmploye, e);
            throw new DAOException("Erreur lors de la recherche de l'employé: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employe> findAll() throws DAOException {
        String sql = "SELECT * FROM employer ORDER BY nom, prenom";
        List<Employe> employes = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                employes.add(mapResultSetToEmploye(resultSet));
            }
            return employes;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les employés", e);
            throw new DAOException("Erreur lors de la récupération des employés: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employe> findActive() throws DAOException {
        String sql = "SELECT * FROM employer WHERE actif = TRUE ORDER BY nom, prenom";
        List<Employe> employes = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                employes.add(mapResultSetToEmploye(resultSet));
            }
            return employes;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des employés actifs", e);
            throw new DAOException("Erreur lors de la récupération des employés actifs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employe> findByRole(Employe.Role role) throws DAOException {
        String sql = "SELECT * FROM employer WHERE role = ? AND actif = TRUE ORDER BY nom, prenom";
        List<Employe> employes = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, role.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employes.add(mapResultSetToEmploye(resultSet));
                }
                return employes;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche d'employés par rôle: " + role, e);
            throw new DAOException("Erreur lors de la recherche d'employés: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Employe employe) throws DAOException {
        String sql = "UPDATE employer SET nom = ?, prenom = ?, role = ?, telephone = ?, email = ?, actif = ? " +
                     "WHERE idEmploye = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, employe.getNom());
            statement.setString(2, employe.getPrenom());
            statement.setString(3, employe.getRoleAsString());
            statement.setString(4, employe.getTelephone());
            statement.setString(5, employe.getEmail());
            statement.setBoolean(6, employe.isActif());
            statement.setInt(7, employe.getIdEmploye());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Employé mis à jour avec l'ID: " + employe.getIdEmploye());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'employé: " + employe.getIdEmploye(), e);
            throw new DAOException("Erreur lors de la mise à jour de l'employé: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int idEmploye) throws DAOException {
        // Soft delete : on met actif à false au lieu de supprimer
        String sql = "UPDATE employer SET actif = FALSE WHERE idEmploye = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idEmploye);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Employé désactivé avec l'ID: " + idEmploye);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'employé: " + idEmploye, e);
            throw new DAOException("Erreur lors de la suppression de l'employé: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet Employe.
     *
     * @param resultSet le ResultSet à mapper
     * @return un objet Employe
     * @throws SQLException si une erreur SQL survient
     */
    private Employe mapResultSetToEmploye(ResultSet resultSet) throws SQLException {
        Employe employe = new Employe();
        try {
            employe.setIdEmploye(resultSet.getInt("idEmploye"));
            employe.setNom(resultSet.getString("nom"));
            employe.setPrenom(resultSet.getString("prenom"));
            employe.setRole(Employe.parseRole(resultSet.getString("role")));
            employe.setTelephone(resultSet.getString("telephone"));
            employe.setEmail(resultSet.getString("email"));
            employe.setActif(resultSet.getBoolean("actif"));
        } catch (Exception e) {
            logger.error("Erreur lors du mapping du ResultSet vers Employe", e);
            throw new SQLException("Erreur lors du mapping: " + e.getMessage());
        }
        return employe;
    }
}

