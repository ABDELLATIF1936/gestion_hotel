package com.hotel.util;

import org.slf4j.LoggerFactory;

/**
 * Wrapper pour le système de logging SLF4J.
 * Fournit une interface simplifiée pour le logging dans l'application.
 */
public class Logger {
    private final org.slf4j.Logger slf4jLogger;

    private Logger(Class<?> clazz) {
        this.slf4jLogger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Crée un logger pour une classe donnée.
     *
     * @param clazz la classe pour laquelle créer le logger
     * @return une instance de Logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    /**
     * Log un message de niveau DEBUG.
     *
     * @param message le message à logger
     */
    public void debug(String message) {
        slf4jLogger.debug(message);
    }

    /**
     * Log un message de niveau INFO.
     *
     * @param message le message à logger
     */
    public void info(String message) {
        slf4jLogger.info(message);
    }

    /**
     * Log un message de niveau WARN.
     *
     * @param message le message à logger
     */
    public void warn(String message) {
        slf4jLogger.warn(message);
    }

    /**
     * Log un message de niveau WARN avec une exception.
     *
     * @param message le message à logger
     * @param throwable l'exception à logger
     */
    public void warn(String message, Throwable throwable) {
        slf4jLogger.warn(message, throwable);
    }

    /**
     * Log un message de niveau ERROR.
     *
     * @param message le message à logger
     */
    public void error(String message) {
        slf4jLogger.error(message);
    }

    /**
     * Log un message de niveau ERROR avec une exception.
     *
     * @param message le message à logger
     * @param throwable l'exception à logger
     */
    public void error(String message, Throwable throwable) {
        slf4jLogger.error(message, throwable);
    }
}

