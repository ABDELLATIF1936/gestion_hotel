package com.hotel.controller;

import com.hotel.model.ServiceSupplementaire;
import com.hotel.service.interfaces.IServiceSupplementaireService;
import com.hotel.factory.ServiceFactory;
import com.hotel.util.Logger;

import java.util.List;

public class ServiceController {
    private static final Logger logger = Logger.getLogger(ServiceController.class);
    private final IServiceSupplementaireService serviceService;

    public ServiceController() {
        this.serviceService = ServiceFactory.getServiceSupplementaireService();
    }

    public ServiceSupplementaire createService(ServiceSupplementaire service) throws Exception {
        try {
            return serviceService.createService(service);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du service", e);
            throw e;
        }
    }

    public List<ServiceSupplementaire> getAllServices() throws Exception {
        try {
            return serviceService.getAllServices();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des services", e);
            throw e;
        }
    }

    public void updateService(ServiceSupplementaire service) throws Exception {
        try {
            serviceService.updateService(service);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du service", e);
            throw e;
        }
    }

    public void deleteService(int id) throws Exception {
        try {
            serviceService.deleteService(id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du service", e);
            throw e;
        }
    }
}
