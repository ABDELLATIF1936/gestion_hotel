package com.hotel.factory;

import com.hotel.dao.interfaces.*;
import com.hotel.dao.impl.*;

/**
 * Factory pour créer les instances de DAO.
 * Implémente le pattern Factory pour centraliser la création des DAO.
 */
public class DAOFactory {
    
    /**
     * Crée une instance de IClientDAO.
     *
     * @return une instance de ClientDAOImpl
     */
    public static IClientDAO getClientDAO() {
        return new ClientDAOImpl();
    }

    /**
     * Crée une instance de IChambreDAO.
     *
     * @return une instance de ChambreDAOImpl
     */
    public static IChambreDAO getChambreDAO() {
        return new ChambreDAOImpl();
    }

    /**
     * Crée une instance de IReservationDAO.
     *
     * @return une instance de ReservationDAOImpl
     */
    public static IReservationDAO getReservationDAO() {
        return new ReservationDAOImpl();
    }

    /**
     * Crée une instance de IFactureDAO.
     *
     * @return une instance de FactureDAOImpl
     */
    public static IFactureDAO getFactureDAO() {
        return new FactureDAOImpl();
    }

    /**
     * Crée une instance de IServiceDAO.
     *
     * @return une instance de ServiceDAOImpl
     */
    public static IServiceDAO getServiceDAO() {
        return new ServiceDAOImpl();
    }

    /**
     * Crée une instance de ITacheEntretienDAO.
     *
     * @return une instance de TacheEntretienDAOImpl
     */
    public static ITacheEntretienDAO getTacheEntretienDAO() {
        return new TacheEntretienDAOImpl();
    }

    /**
     * Crée une instance de IEmployeDAO.
     *
     * @return une instance de EmployeDAOImpl
     */
    public static IEmployeDAO getEmployeDAO() {
        return new EmployeDAOImpl();
    }

    /**
     * Crée une instance de IUtilisateurDAO.
     *
     * @return une instance de UtilisateurDAOImpl
     */
    public static IUtilisateurDAO getUtilisateurDAO() {
        return new UtilisateurDAOImpl();
    }

    /**
     * Crée une instance de IAuditLogDAO.
     *
     * @return une instance de AuditLogDAOImpl
     */
    public static IAuditLogDAO getAuditLogDAO() {
        return new AuditLogDAOImpl();
    }

    /**
     * Crée une instance de IReservationServiceDAO.
     *
     * @return une instance de ReservationServiceDAOImpl
     */
    public static IReservationServiceDAO getReservationServiceDAO() {
        return new ReservationServiceDAOImpl();
    }
}

