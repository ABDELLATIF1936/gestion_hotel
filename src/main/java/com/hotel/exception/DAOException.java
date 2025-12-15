package com.hotel.exception;

/**
 * Exception personnalisée pour les erreurs dans la couche DAO.
 */
public class DAOException extends Exception {
    
    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}

