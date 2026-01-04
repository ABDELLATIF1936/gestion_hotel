package com.hotel.strategy;

import com.hotel.model.Chambre;
import com.hotel.model.Reservation;
import com.hotel.util.DateUtil;

import java.time.Month;

/**
 * Stratégie de tarification saisonnière avec majoration en haute saison.
 */
public class SeasonalPricingStrategy implements IPricingStrategy {
    private static final double HIGH_SEASON_MULTIPLIER = 1.3; // +30% en haute saison
    private static final double LOW_SEASON_MULTIPLIER = 0.9;  // -10% en basse saison
    
    @Override
    public double calculatePrice(Chambre chambre, Reservation reservation) {
        int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
        double basePrice = chambre.getPrixNuit() * nbNuits;
        
        // Haute saison : Juin, Juillet, Août, Décembre
        Month month = reservation.getDateDebut().getMonth();
        if (isHighSeason(month)) {
            return basePrice * HIGH_SEASON_MULTIPLIER;
        } else if (isLowSeason(month)) {
            return basePrice * LOW_SEASON_MULTIPLIER;
        }
        
        return basePrice;
    }
    
    private boolean isHighSeason(Month month) {
        return month == Month.JUNE || month == Month.JULY || 
               month == Month.AUGUST || month == Month.DECEMBER;
    }
    
    private boolean isLowSeason(Month month) {
        return month == Month.JANUARY || month == Month.FEBRUARY || 
               month == Month.NOVEMBER;
    }
}

