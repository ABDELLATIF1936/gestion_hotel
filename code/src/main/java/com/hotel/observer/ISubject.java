package com.hotel.observer;

/**
 * Interface pour les sujets observables dans le pattern Observer.
 */
public interface ISubject {
    /**
     * Ajoute un observateur.
     *
     * @param observer l'observateur à ajouter
     */
    void addObserver(IObserver observer);

    /**
     * Retire un observateur.
     *
     * @param observer l'observateur à retirer
     */
    void removeObserver(IObserver observer);

    /**
     * Notifie tous les observateurs d'un changement.
     *
     * @param message le message de notification
     */
    void notifyObservers(String message);
}

