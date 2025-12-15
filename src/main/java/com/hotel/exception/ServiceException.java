package com.hotel.exception;

/**
 * Exception personnalisée pour les erreurs dans la couche Service.
 */
public class ServiceException extends Exception {
    
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

