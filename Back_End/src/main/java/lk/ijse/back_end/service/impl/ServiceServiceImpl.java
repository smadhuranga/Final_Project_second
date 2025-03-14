package lk.ijse.back_end.service.impl;



import lk.ijse.back_end.dto.ServiceDTO;
import lk.ijse.back_end.repository.ServiceRepo;
import lk.ijse.back_end.service.ServiceService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepo serviceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int saveService(ServiceDTO serviceDTO) {
        if (serviceRepository.existsByTitle(serviceDTO.getTitle())) {
            return VarList.Not_Acceptable;
        }
        serviceRepository.save(modelMapper.map(serviceDTO, Service.class));
        return VarList.Created;
    }

    @Override
    public ServiceDTO getServiceById(Long id) {
        return serviceRepository.findById(id)
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .orElse(null);
    }
}