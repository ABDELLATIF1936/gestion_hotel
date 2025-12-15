package com.hotel.strategy;

import com.hotel.model.Chambre;
import com.hotel.model.Reservation;
import com.hotel.util.DateUtil;

/**
 * Stratégie de tarification standard : prix par nuit × nombre de nuits.
 */
public class StandardPricingStrategy implements IPricingStrategy {
    
    @Override
    public double calculatePrice(Chambre chambre, Reservation reservation) {
        int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
        return chambre.getPrixNuit() * nbNuits;
    }
}

