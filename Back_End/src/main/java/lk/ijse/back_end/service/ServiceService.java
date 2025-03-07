package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.ServiceDTO;



public interface ServiceService {
    int saveService(ServiceDTO serviceDTO);
    ServiceDTO getServiceById(Long id);
}