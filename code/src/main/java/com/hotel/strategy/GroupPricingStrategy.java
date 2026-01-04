package com.hotel.strategy;

import com.hotel.model.Chambre;
import com.hotel.model.Reservation;
import com.hotel.util.DateUtil;

/**
 * Stratégie de tarification pour les groupes avec réduction.
 */
public class GroupPricingStrategy implements IPricingStrategy {
    private static final int GROUP_THRESHOLD = 5; // Seuil pour considérer un groupe
    private static final double GROUP_DISCOUNT = 0.15; // 15% de réduction pour les groupes
    
    @Override
    public double calculatePrice(Chambre chambre, Reservation reservation) {
        int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
        double basePrice = chambre.getPrixNuit() * nbNuits;
        
        // Application de la réduction groupe si le nombre de personnes >= seuil
        if (reservation.getNbPersonnes() >= GROUP_THRESHOLD) {
            return basePrice * (1 - GROUP_DISCOUNT);
        }
        
        return basePrice;
    }
}

