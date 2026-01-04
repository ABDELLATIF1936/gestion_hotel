package com.hotel.service.interfaces;

import com.hotel.exception.ServiceException;
import com.hotel.model.ServiceSupplementaire;

import java.util.List;

/**
 * Interface du service métier pour la gestion des services supplémentaires.
 */
public interface IServiceSupplementaireService {
    ServiceSupplementaire createService(ServiceSupplementaire service) throws ServiceException;
    ServiceSupplementaire findServiceById(int id) throws ServiceException;
    List<ServiceSupplementaire> getAllServices() throws ServiceException;
    List<ServiceSupplementaire> getActiveServices() throws ServiceException;
    ServiceSupplementaire updateService(ServiceSupplementaire service) throws ServiceException;
    boolean deleteService(int id) throws ServiceException;
}

