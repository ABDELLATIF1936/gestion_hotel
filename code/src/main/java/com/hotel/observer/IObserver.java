package com.hotel.observer;

/**
 * Interface pour les observateurs dans le pattern Observer.
 */
public interface IObserver {
    /**
     * Méthode appelée lorsque le sujet notifie un changement.
     *
     * @param message le message de notification
     */
    void update(String message);
}

