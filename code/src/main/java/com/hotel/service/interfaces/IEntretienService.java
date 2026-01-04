package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.TacheEntretien;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service métier pour la gestion de l'entretien.
 */
public interface IEntretienService {
    TacheEntretien createTache(TacheEntretien tache) throws ServiceException;
    TacheEntretien findTacheById(int id) throws ServiceException;
    List<TacheEntretien> getAllTaches() throws ServiceException;
    List<TacheEntretien> getTachesByEmploye(int idEmploye) throws ServiceException;
    List<TacheEntretien> getTachesByChambre(int numeroChambre) throws ServiceException;
    List<TacheEntretien> getTachesByDate(LocalDate date) throws ServiceException;
    List<TacheEntretien> getPendingTaches() throws ServiceException;
    TacheEntretien updateTache(TacheEntretien tache) throws ServiceException;
    boolean assignTacheToEmploye(int idTache, int idEmploye) throws ServiceException;
    boolean markTacheAsCompleted(int id) throws ServiceException;
    boolean deleteTache(int id) throws ServiceException;
}

