package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.ServiceDTO;

import java.util.List;


public interface ServiceService {
    int saveService(ServiceDTO serviceDTO);
    ServiceDTO getServiceById(Long id);
     List<ServiceDTO> getServicesByCategoryId(Long categoryId);
    List<ServiceDTO> getAllServices();
    int updateService(ServiceDTO serviceDTO);
    int deleteService(Long id);
}