package com.hotel.controller;

import com.hotel.exception.ServiceException;
import com.hotel.model.Employe;
import com.hotel.service.interfaces.IEmployeService;
import com.hotel.service.impl.EmployeServiceImpl;

import java.util.List;

/**
 * Contrôleur pour la gestion des employés.
 */
public class EmployeController {
    private final IEmployeService employeService;

    public EmployeController() {
        this.employeService = new EmployeServiceImpl();
    }

    public Employe createEmploye(Employe employe) throws Exception {
        try {
            return employeService.createEmploye(employe);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la création de l'employé: " + e.getMessage(), e);
        }
    }

    public List<Employe> getAllEmployes() throws Exception {
        try {
            return employeService.getAllEmployes();
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la récupération des employés: " + e.getMessage(), e);
        }
    }

    public Employe getEmployeById(int id) throws Exception {
        try {
            return employeService.findEmployeById(id);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la récupération de l'employé: " + e.getMessage(), e);
        }
    }

    public Employe updateEmploye(Employe employe) throws Exception {
        try {
            return employeService.updateEmploye(employe);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la mise à jour de l'employé: " + e.getMessage(), e);
        }
    }

    public boolean deleteEmploye(int id) throws Exception {
        try {
            return employeService.deleteEmploye(id);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la suppression de l'employé: " + e.getMessage(), e);
        }
    }

    public List<Employe> getEmployesByRole(Employe.Role role) throws Exception {
        try {
            return employeService.getEmployesByRole(role);
        } catch (ServiceException e) {
            throw new Exception("Erreur lors de la récupération des employés: " + e.getMessage(), e);
        }
    }
}

