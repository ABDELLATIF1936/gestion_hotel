package com.hotel.dao.impl;

import com.hotel.dao.interfaces.IReservationDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.DatabaseException;
import com.hotel.model.Reservation;
import com.hotel.util.DatabaseConnection;
import com.hotel.util.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du DAO pour les réservations.
 */
public class ReservationDAOImpl implements IReservationDAO {
    private static final Logger logger = Logger.getLogger(ReservationDAOImpl.class);

    @Override
    public int create(Reservation reservation) throws DAOException {
        String sql = "INSERT INTO reservation (idClient, numeroChambre, dateDebut, dateFin, statut, nbPersonnes, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, reservation.getIdClient());
            statement.setInt(2, reservation.getNumeroChambre());
            statement.setDate(3, Date.valueOf(reservation.getDateDebut()));
            statement.setDate(4, Date.valueOf(reservation.getDateFin()));
            statement.setString(5, reservation.getStatutAsString());
            statement.setInt(6, reservation.getNbPersonnes());
            statement.setString(7, reservation.getNotes());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Échec de la création de la réservation, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    connection.commit();
                    logger.info("Réservation créée avec l'ID: " + id);
                    return id;
                } else {
                    connection.rollback();
                    throw new DAOException("Échec de la création de la réservation, aucun ID généré");
                }
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion lors de la création de la réservation", e);
            throw new DAOException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de la réservation", e);
            throw new DAOException("Erreur lors de la création de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public Reservation findById(int idReservation) throws DAOException {
        String sql = "SELECT * FROM reservation WHERE idReservation = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idReservation);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToReservation(resultSet);
                }
                return null;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de la réservation: " + idReservation, e);
            throw new DAOException("Erreur lors de la recherche de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> findAll() throws DAOException {
        String sql = "SELECT * FROM reservation ORDER BY dateDebut DESC";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
            return reservations;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de toutes les réservations", e);
            throw new DAOException("Erreur lors de la récupération des réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> findByClient(int idClient) throws DAOException {
        String sql = "SELECT * FROM reservation WHERE idClient = ? ORDER BY dateDebut DESC";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idClient);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToReservation(resultSet));
                }
                return reservations;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de réservations pour le client: " + idClient, e);
            throw new DAOException("Erreur lors de la recherche de réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> findByChambre(int numeroChambre) throws DAOException {
        String sql = "SELECT * FROM reservation WHERE numeroChambre = ? ORDER BY dateDebut DESC";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, numeroChambre);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToReservation(resultSet));
                }
                return reservations;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de réservations pour la chambre: " + numeroChambre, e);
            throw new DAOException("Erreur lors de la recherche de réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> findByPeriod(LocalDate dateDebut, LocalDate dateFin) throws DAOException {
        String sql = "SELECT * FROM reservation WHERE (dateDebut <= ? AND dateFin >= ?) ORDER BY dateDebut";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(dateFin));
            statement.setDate(2, Date.valueOf(dateDebut));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToReservation(resultSet));
                }
                return reservations;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de réservations pour la période", e);
            throw new DAOException("Erreur lors de la recherche de réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> findActive() throws DAOException {
        String sql = "SELECT * FROM reservation WHERE statut IN ('CONFIRMEE', 'EN_COURS') " +
                     "AND dateFin >= CURDATE() ORDER BY dateDebut";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
            return reservations;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des réservations actives", e);
            throw new DAOException("Erreur lors de la récupération des réservations actives: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isChambreAvailable(int numeroChambre, LocalDate dateDebut, LocalDate dateFin, 
                                      Integer excludeReservationId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM reservation " +
                     "WHERE numeroChambre = ? " +
                     "AND statut IN ('CONFIRMEE', 'EN_COURS') " +
                     "AND ? < dateFin AND ? > dateDebut";
        
        if (excludeReservationId != null) {
            sql += " AND idReservation != ?";
        }
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, numeroChambre);
            statement.setDate(2, Date.valueOf(dateFin));
            statement.setDate(3, Date.valueOf(dateDebut));
            
            if (excludeReservationId != null) {
                statement.setInt(4, excludeReservationId);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
                return true;
            }
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la vérification de disponibilité de la chambre: " + numeroChambre, e);
            throw new DAOException("Erreur lors de la vérification de disponibilité: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Reservation reservation) throws DAOException {
        String sql = "UPDATE reservation SET idClient = ?, numeroChambre = ?, dateDebut = ?, dateFin = ?, " +
                     "statut = ?, nbPersonnes = ?, notes = ? WHERE idReservation = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, reservation.getIdClient());
            statement.setInt(2, reservation.getNumeroChambre());
            statement.setDate(3, Date.valueOf(reservation.getDateDebut()));
            statement.setDate(4, Date.valueOf(reservation.getDateFin()));
            statement.setString(5, reservation.getStatutAsString());
            statement.setInt(6, reservation.getNbPersonnes());
            statement.setString(7, reservation.getNotes());
            statement.setInt(8, reservation.getIdReservation());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Réservation mise à jour avec l'ID: " + reservation.getIdReservation());
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la réservation: " + reservation.getIdReservation(), e);
            throw new DAOException("Erreur lors de la mise à jour de la réservation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int idReservation) throws DAOException {
        String sql = "DELETE FROM reservation WHERE idReservation = ?";
        
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idReservation);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                logger.info("Réservation supprimée avec l'ID: " + idReservation);
                return true;
            }
            connection.rollback();
            return false;
        } catch (DatabaseException e) {
            logger.error("Erreur de connexion", e);
            throw new DAOException("Erreur de connexion � la base de donn�es: " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la réservation: " + idReservation, e);
            throw new DAOException("Erreur lors de la suppression de la réservation: " + e.getMessage(), e);
        }
    }

    /**
     * Mappe un ResultSet vers un objet Reservation.
     *
     * @param resultSet le ResultSet à mapper
     * @return un objet Reservation
     * @throws SQLException si une erreur SQL survient
     */
    private Reservation mapResultSetToReservation(ResultSet resultSet) throws SQLException {
        Reservation reservation = new Reservation();
        try {
            reservation.setIdReservation(resultSet.getInt("idReservation"));
            reservation.setIdClient(resultSet.getInt("idClient"));
            reservation.setNumeroChambre(resultSet.getInt("numeroChambre"));
            
            Date dateDebut = resultSet.getDate("dateDebut");
            if (dateDebut != null) {
                // Utiliser la méthode spéciale pour charger depuis la DB (sans validation stricte)
                reservation.setDateDebutFromDB(dateDebut.toLocalDate());
            }
            
            Date dateFin = resultSet.getDate("dateFin");
            if (dateFin != null) {
                // Utiliser la méthode spéciale pour charger depuis la DB (sans validation stricte)
                reservation.setDateFinFromDB(dateFin.toLocalDate());
            }
            
            reservation.setStatut(Reservation.parseStatut(resultSet.getString("statut")));
            reservation.setNbPersonnes(resultSet.getInt("nbPersonnes"));
            
            String notes = resultSet.getString("notes");
            reservation.setNotes(notes != null ? notes : "");
        } catch (Exception e) {
            logger.error("Erreur lors du mapping du ResultSet vers Reservation", e);
            throw new SQLException("Erreur lors du mapping: " + e.getMessage(), e);
        }
        return reservation;
    }
}

