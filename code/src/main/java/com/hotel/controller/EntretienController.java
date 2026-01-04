package com.hotel.controller;

import com.hotel.model.TacheEntretien;
import com.hotel.service.interfaces.IEntretienService;
import com.hotel.factory.ServiceFactory;
import com.hotel.util.Logger;

import java.util.List;

public class EntretienController {
    private static final Logger logger = Logger.getLogger(EntretienController.class);
    private final IEntretienService entretienService;

    public EntretienController() {
        this.entretienService = ServiceFactory.getEntretienService();
    }

    public TacheEntretien createTache(TacheEntretien tache) throws Exception {
        try {
            return entretienService.createTache(tache);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la tâche", e);
            throw e;
        }
    }

    public List<TacheEntretien> getAllTaches() throws Exception {
        try {
            return entretienService.getAllTaches();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des tâches", e);
            throw e;
        }
    }

    public void updateTache(TacheEntretien tache) throws Exception {
        try {
            entretienService.updateTache(tache);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la tâche", e);
            throw e;
        }
    }

    public void deleteTache(int id) throws Exception {
        try {
            entretienService.deleteTache(id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la tâche", e);
            throw e;
        }
    }
}
