package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.Employe;

import java.util.List;

/**
 * Interface du service métier pour la gestion des employés.
 */
public interface IEmployeService {
    Employe createEmploye(Employe employe) throws ServiceException;
    Employe findEmployeById(int id) throws ServiceException;
    List<Employe> getAllEmployes() throws ServiceException;
    List<Employe> getActiveEmployes() throws ServiceException;
    List<Employe> getEmployesByRole(Employe.Role role) throws ServiceException;
    Employe updateEmploye(Employe employe) throws ServiceException;
    boolean deleteEmploye(int id) throws ServiceException;
}

