package lk.ijse.back_end.service.impl;



import lk.ijse.back_end.dto.ServiceDTO;


import lk.ijse.back_end.repository.ServiceRepo;
import lk.ijse.back_end.service.ServiceService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private final ServiceRepo serviceRepository;
    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    public ServiceServiceImpl(ServiceRepo serviceRepository, ModelMapper modelMapper) {
        this.serviceRepository = serviceRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public int saveService(ServiceDTO serviceDTO) {
        try {
            if (serviceRepository.existsByTitle(serviceDTO.getTitle())) {
                return VarList.Not_Acceptable;
            }

            lk.ijse.back_end.entity.Service serviceEntity = modelMapper.map(serviceDTO, lk.ijse.back_end.entity.Service.class);
            serviceRepository.save(serviceEntity);
            return VarList.Created;

        } catch (DataIntegrityViolationException e) {
            return VarList.Bad_Request;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public ServiceDTO getServiceById(Long id) {
        Optional<lk.ijse.back_end.entity.Service> serviceEntity = serviceRepository.findById(id);
        return serviceEntity.map(entity -> modelMapper.map(entity, ServiceDTO.class))
                .orElse(null);
    }
    @Override
    public List<ServiceDTO> getServicesByCategoryId(Long categoryId) {
        // Call via instance, not static class
        List<lk.ijse.back_end.entity.Service> services = serviceRepository.findByCategoryId(categoryId);
        return services.stream()
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDTO> getAllServices() {
        List<lk.ijse.back_end.entity.Service> services = serviceRepository.findAll();
        return services.stream()
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public int updateService(ServiceDTO serviceDTO) {
        try {
            lk.ijse.back_end.entity.Service existingService = serviceRepository.findById(serviceDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            modelMapper.map(serviceDTO, existingService);
            serviceRepository.save(existingService);
            return VarList.OK;

        } catch (Exception e) {
            e.printStackTrace();
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public int deleteService(Long id) {
        try {
            serviceRepository.deleteById(id);
            return VarList.OK;
        } catch (Exception e) {
            e.printStackTrace();
            return VarList.Internal_Server_Error;
        }
    }
}