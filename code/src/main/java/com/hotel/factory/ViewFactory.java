package com.hotel.factory;

import com.hotel.view.*;

/**
 * Factory pour créer les instances de View.
 * Implémente le pattern Factory pour centraliser la création des vues JavaFX.
 */
public class ViewFactory {
    
    /**
     * Crée une instance de MainView.
     *
     * @return une instance de MainView
     */
    public static MainView createMainView() {
        return new MainView();
    }

    /**
     * Crée une instance de ClientView.
     *
     * @return une instance de ClientView
     */
    public static ClientView createClientView() {
        return new ClientView();
    }

    /**
     * Crée une instance de ReservationView.
     *
     * @return une instance de ReservationView
     */
    public static ReservationView createReservationView() {
        return new ReservationView();
    }

    /**
     * Crée une instance de ChambreView.
     *
     * @return une instance de ChambreView
     */
    public static ChambreView createChambreView() {
        return new ChambreView();
    }

    /**
     * Crée une instance de FacturationView.
     *
     * @return une instance de FacturationView
     */
    public static FacturationView createFacturationView() {
        return new FacturationView();
    }

    /**
     * Crée une instance de ServiceView.
     *
     * @return une instance de ServiceView
     */
    public static ServiceView createServiceView() {
        return new ServiceView();
    }

    /**
     * Crée une instance de EntretienView.
     *
     * @return une instance de EntretienView
     */
    public static EntretienView createEntretienView() {
        return new EntretienView();
    }

    /**
     * Crée une instance de DashboardView.
     *
     * @return une instance de DashboardView
     */
    public static DashboardView createDashboardView() {
        return new DashboardView();
    }
}

