package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.Facture;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service métier pour la gestion de la facturation.
 */
public interface IFacturationService {
    Facture createFacture(Facture facture) throws ServiceException;
    Facture findFactureById(int id) throws ServiceException;
    Facture findFactureByReservation(int idReservation) throws ServiceException;
    List<Facture> getAllFactures() throws ServiceException;
    List<Facture> getFacturesByPeriod(LocalDate dateDebut, LocalDate dateFin) throws ServiceException;
    List<Facture> getPendingFactures() throws ServiceException;
    Facture updateFacture(Facture facture) throws ServiceException;
    boolean markFactureAsPaid(int id) throws ServiceException;
    boolean deleteFacture(int id) throws ServiceException;
}

