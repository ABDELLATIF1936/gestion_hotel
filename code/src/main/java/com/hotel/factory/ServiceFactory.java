package com.hotel.factory;

import com.hotel.service.interfaces.*;
import com.hotel.service.impl.*;

/**
 * Factory pour créer les instances de Service.
 * Implémente le pattern Factory pour centraliser la création des services.
 */
public class ServiceFactory {
    
    /**
     * Crée une instance de IClientService.
     *
     * @return une instance de ClientServiceImpl
     */
    public static IClientService getClientService() {
        return new ClientServiceImpl();
    }

    /**
     * Crée une instance de IChambreService.
     *
     * @return une instance de ChambreServiceImpl
     */
    public static IChambreService getChambreService() {
        return new ChambreServiceImpl();
    }

    /**
     * Crée une instance de IReservationService.
     *
     * @return une instance de ReservationServiceImpl
     */
    public static IReservationService getReservationService() {
        return new ReservationServiceImpl();
    }

    /**
     * Crée une instance de IFacturationService.
     *
     * @return une instance de FacturationServiceImpl
     */
    public static IFacturationService getFacturationService() {
        return new FacturationServiceImpl();
    }

    /**
     * Crée une instance de IServiceSupplementaireService.
     *
     * @return une instance de ServiceSupplementaireServiceImpl
     */
    public static IServiceSupplementaireService getServiceSupplementaireService() {
        return new ServiceSupplementaireServiceImpl();
    }

    /**
     * Crée une instance de IEntretienService.
     *
     * @return une instance de EntretienServiceImpl
     */
    public static IEntretienService getEntretienService() {
        return new EntretienServiceImpl();
    }

    /**
     * Crée une instance de IEmployeService.
     *
     * @return une instance de EmployeServiceImpl
     */
    public static IEmployeService getEmployeService() {
        return new EmployeServiceImpl();
    }
}

