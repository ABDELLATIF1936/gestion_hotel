package com.hotel.strategy;

import com.hotel.model.Chambre;
import com.hotel.model.Reservation;

/**
 * Interface pour les stratégies de tarification.
 * Implémente le pattern Strategy pour différentes méthodes de calcul de prix.
 */
public interface IPricingStrategy {
    /**
     * Calcule le prix total d'une réservation.
     *
     * @param chambre la chambre réservée
     * @param reservation la réservation
     * @return le prix total calculé
     */
    double calculatePrice(Chambre chambre, Reservation reservation);
}

