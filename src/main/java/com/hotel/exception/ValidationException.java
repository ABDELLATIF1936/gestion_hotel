package com.hotel.exception;

/**
 * Exception personnalisée pour les erreurs de validation.
 */
public class ValidationException extends Exception {
    
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

