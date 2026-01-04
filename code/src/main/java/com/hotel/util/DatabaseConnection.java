package com.hotel.util;

import com.hotel.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion à la base de données utilisant HikariCP.
 * Implémente le pattern Singleton pour garantir une seule instance du pool.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    private DatabaseConnection() {
        initializeDataSource();
    }

    /**
     * Obtient l'instance unique de DatabaseConnection (Singleton).
     *
     * @return l'instance de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Initialise le pool de connexions HikariCP avec la configuration.
     */
    private void initializeDataSource() {
        ConfigReader config = ConfigReader.getInstance();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getProperty("db.url"));
        hikariConfig.setUsername(config.getProperty("db.username"));
        hikariConfig.setPassword(config.getProperty("db.password"));
        hikariConfig.setDriverClassName(config.getProperty("db.driver"));

        // Configuration du pool
        hikariConfig.setMinimumIdle(config.getIntProperty("db.pool.minSize", 5));
        hikariConfig.setMaximumPoolSize(config.getIntProperty("db.pool.maxSize", 20));
        hikariConfig.setConnectionTimeout(config.getLongProperty("db.pool.connectionTimeout", 30000));
        hikariConfig.setIdleTimeout(config.getLongProperty("db.pool.idleTimeout", 600000));
        hikariConfig.setMaxLifetime(config.getLongProperty("db.pool.maxLifetime", 1800000));

        // Optimisations
        hikariConfig.setAutoCommit(false);
        hikariConfig.setLeakDetectionThreshold(60000);

        dataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Obtient une connexion depuis le pool.
     *
     * @return une connexion à la base de données
     * @throws DatabaseException si une erreur survient lors de l'obtention de la connexion
     */
    public Connection getConnection() throws DatabaseException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de l'obtention de la connexion à la base de données", e);
        }
    }

    /**
     * Ferme le pool de connexions.
     * À appeler lors de l'arrêt de l'application.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Vérifie si le pool est actif.
     *
     * @return true si le pool est actif, false sinon
     */
    public boolean isActive() {
        return dataSource != null && !dataSource.isClosed();
    }
}

