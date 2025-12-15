package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.Chambre;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service métier pour la gestion des chambres.
 */
public interface IChambreService {
    Chambre createChambre(Chambre chambre) throws ServiceException;
    Chambre findChambreByNumero(int numero) throws ServiceException;
    List<Chambre> getAllChambres() throws ServiceException;
    List<Chambre> getAvailableChambres() throws ServiceException;
    List<Chambre> getChambresByCategorie(Chambre.Categorie categorie) throws ServiceException;
    List<Chambre> getAvailableChambresForPeriod(LocalDate dateDebut, LocalDate dateFin) throws ServiceException;
    Chambre updateChambre(Chambre chambre) throws ServiceException;
    boolean updateChambreStatut(int numero, Chambre.Statut statut) throws ServiceException;
    /**
     * Calcule et met à jour automatiquement le statut d'une chambre
     * en fonction des entretiens actifs et des réservations.
     *
     * @param numeroChambre le numéro de la chambre
     * @return le nouveau statut calculé
     * @throws ServiceException si une erreur survient
     */
    Chambre.Statut calculateAndUpdateChambreStatut(int numeroChambre) throws ServiceException;
    boolean deleteChambre(int numero) throws ServiceException;
}

