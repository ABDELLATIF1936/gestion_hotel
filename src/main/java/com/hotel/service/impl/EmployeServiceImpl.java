package com.hotel.service.impl;

import com.hotel.dao.interfaces.IEmployeDAO;
import com.hotel.exception.DAOException;
import com.hotel.exception.ServiceException;
import com.hotel.factory.DAOFactory;
import com.hotel.model.Employe;
import com.hotel.service.interfaces.IEmployeService;
import com.hotel.util.Logger;

import java.util.List;

/**
 * Implémentation du service métier pour la gestion des employés.
 */
public class EmployeServiceImpl implements IEmployeService {
    private static final Logger logger = Logger.getLogger(EmployeServiceImpl.class);
    private final IEmployeDAO employeDAO;

    public EmployeServiceImpl() {
        this.employeDAO = DAOFactory.getEmployeDAO();
    }

    @Override
    public Employe createEmploye(Employe employe) throws ServiceException {
        try {
            int id = employeDAO.create(employe);
            employe.setIdEmploye(id);
            logger.info("Employé créé avec succès: " + id);
            return employe;
        } catch (DAOException e) {
            logger.error("Erreur lors de la création de l'employé", e);
            throw new ServiceException("Erreur lors de la création de l'employé: " + e.getMessage(), e);
        }
    }

    @Override
    public Employe findEmployeById(int id) throws ServiceException {
        try {
            Employe employe = employeDAO.findById(id);
            if (employe == null) {
                throw new ServiceException("Employé non trouvé avec l'ID: " + id);
            }
            return employe;
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche de l'employé: " + id, e);
            throw new ServiceException("Erreur lors de la recherche de l'employé: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employe> getAllEmployes() throws ServiceException {
        try {
            return employeDAO.findAll();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération de tous les employés", e);
            throw new ServiceException("Erreur lors de la récupération des employés: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employe> getActiveEmployes() throws ServiceException {
        try {
            return employeDAO.findActive();
        } catch (DAOException e) {
            logger.error("Erreur lors de la récupération des employés actifs", e);
            throw new ServiceException("Erreur lors de la récupération des employés actifs: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employe> getEmployesByRole(Employe.Role role) throws ServiceException {
        try {
            return employeDAO.findByRole(role);
        } catch (DAOException e) {
            logger.error("Erreur lors de la recherche d'employés par rôle: " + role, e);
            throw new ServiceException("Erreur lors de la recherche d'employés: " + e.getMessage(), e);
        }
    }

    @Override
    public Employe updateEmploye(Employe employe) throws ServiceException {
        try {
            boolean updated = employeDAO.update(employe);
            if (!updated) {
                throw new ServiceException("Échec de la mise à jour de l'employé");
            }
            logger.info("Employé mis à jour avec succès: " + employe.getIdEmploye());
            return employe;
        } catch (DAOException e) {
            logger.error("Erreur lors de la mise à jour de l'employé", e);
            throw new ServiceException("Erreur lors de la mise à jour de l'employé: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteEmploye(int id) throws ServiceException {
        try {
            boolean deleted = employeDAO.delete(id);
            if (deleted) {
                logger.info("Employé supprimé avec succès: " + id);
            }
            return deleted;
        } catch (DAOException e) {
            logger.error("Erreur lors de la suppression de l'employé: " + id, e);
            throw new ServiceException("Erreur lors de la suppression de l'employé: " + e.getMessage(), e);
        }
    }
}

