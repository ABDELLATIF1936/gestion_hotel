package com.hotel.observer;

import com.hotel.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Observateur pour les réservations.
 * Notifie les changements de statut des réservations.
 */
public class ReservationObserver implements IObserver {
    private static final Logger logger = Logger.getLogger(ReservationObserver.class);
    private final List<String> notifications = new ArrayList<>();

    @Override
    public void update(String message) {
        notifications.add(message);
        logger.info("Notification de réservation: " + message);
        // Ici, on pourrait envoyer un email, une notification système, etc.
    }

    /**
     * Récupère toutes les notifications reçues.
     *
     * @return la liste des notifications
     */
    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Efface toutes les notifications.
     */
    public void clearNotifications() {
        notifications.clear();
    }
}

