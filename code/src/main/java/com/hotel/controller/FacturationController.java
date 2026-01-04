package com.hotel.controller;

import com.hotel.model.Facture;
import com.hotel.service.interfaces.IFacturationService;
import com.hotel.factory.ServiceFactory;
import com.hotel.util.Logger;

import java.util.List;

public class FacturationController {
    private static final Logger logger = Logger.getLogger(FacturationController.class);
    private final IFacturationService facturationService;

    public FacturationController() {
        this.facturationService = ServiceFactory.getFacturationService();
    }

    public Facture createFacture(Facture facture) throws Exception {
        try {
            return facturationService.createFacture(facture);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la facture", e);
            throw e;
        }
    }

    public List<Facture> getAllFactures() throws Exception {
        try {
            return facturationService.getAllFactures();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des factures", e);
            throw e;
        }
    }

    public void updateFacture(Facture facture) throws Exception {
        try {
            facturationService.updateFacture(facture);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la facture", e);
            throw e;
        }
    }

    public void deleteFacture(int id) throws Exception {
        try {
            facturationService.deleteFacture(id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la facture", e);
            throw e;
        }
    }

    public void generatePdf(com.hotel.model.Facture facture, String destPath) throws Exception {
        com.hotel.service.PdfService pdfService = new com.hotel.service.PdfService();
        pdfService.generateInvoicePdf(facture, destPath);
    }
}
