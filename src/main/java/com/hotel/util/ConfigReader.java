package com.hotel.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe utilitaire pour lire les fichiers de configuration.
 * Utilise le pattern Singleton pour garantir une seule instance.
 */
public class ConfigReader {
    private static ConfigReader instance;
    private Properties properties;

    private ConfigReader() {
        properties = new Properties();
        loadDatabaseConfig();
    }

    /**
     * Obtient l'instance unique de ConfigReader (Singleton).
     *
     * @return l'instance de ConfigReader
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Charge la configuration de la base de données depuis database.properties.
     */
    private void loadDatabaseConfig() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/database.properties")) {
            if (input == null) {
                throw new RuntimeException("Fichier database.properties introuvable dans les resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de la configuration", e);
        }
    }

    /**
     * Récupère une propriété par sa clé.
     *
     * @param key la clé de la propriété
     * @return la valeur de la propriété, ou null si non trouvée
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Récupère une propriété avec une valeur par défaut.
     *
     * @param key la clé de la propriété
     * @param defaultValue la valeur par défaut si la clé n'existe pas
     * @return la valeur de la propriété ou la valeur par défaut
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Récupère une propriété de type int.
     *
     * @param key la clé de la propriété
     * @param defaultValue la valeur par défaut
     * @return la valeur entière de la propriété
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Récupère une propriété de type long.
     *
     * @param key la clé de la propriété
     * @param defaultValue la valeur par défaut
     * @return la valeur long de la propriété
     */
    public long getLongProperty(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

