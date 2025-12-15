package com.hotel.observer;

import com.hotel.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Observateur pour les chambres.
 * Notifie les changements de statut des chambres.
 */
public class ChambreObserver implements IObserver {
    private static final Logger logger = Logger.getLogger(ChambreObserver.class);
    private final List<String> notifications = new ArrayList<>();

    @Override
    public void update(String message) {
        notifications.add(message);
        logger.info("Notification de chambre: " + message);
        // Ici, on pourrait mettre à jour l'interface, envoyer une alerte, etc.
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

