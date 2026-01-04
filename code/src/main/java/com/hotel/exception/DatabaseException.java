package com.hotel.exception;

/**
 * Exception personnalisée pour les erreurs liées à la base de données.
 */
public class DatabaseException extends Exception {
    
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

