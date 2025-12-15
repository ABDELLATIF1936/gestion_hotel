package com.hotel.controller;

import com.hotel.model.Chambre;
import com.hotel.service.interfaces.IChambreService;
import com.hotel.factory.ServiceFactory;
import com.hotel.util.Logger;

import java.util.List;

/**
 * Contrôleur pour la gestion des chambres.
 */
public class ChambreController {
    private static final Logger logger = Logger.getLogger(ChambreController.class);
    private final IChambreService chambreService;

    public ChambreController() {
        this.chambreService = ServiceFactory.getChambreService();
    }

    public Chambre createChambre(Chambre chambre) throws Exception {
        try {
            return chambreService.createChambre(chambre);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la chambre", e);
            throw e;
        }
    }

    public List<Chambre> getAllChambres() throws Exception {
        try {
            return chambreService.getAllChambres();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des chambres", e);
            throw e;
        }
    }

    public List<Chambre> getAvailableChambres() throws Exception {
        try {
            return chambreService.getAvailableChambres();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des chambres disponibles", e);
            throw e;
        }
    }

    public void updateChambre(Chambre chambre) throws Exception {
        try {
            chambreService.updateChambre(chambre);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la chambre", e);
            throw e;
        }
    }

    public void deleteChambre(int numero) throws Exception {
        try {
            chambreService.deleteChambre(numero);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la chambre", e);
            throw e;
        }
    }

    public Chambre getChambreByNumero(int numero) throws Exception {
        try {
            return chambreService.findChambreByNumero(numero);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la chambre", e);
            throw e;
        }
    }
}
