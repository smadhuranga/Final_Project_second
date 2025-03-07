package lk.ijse.back_end.service.impl;



import lk.ijse.back_end.dto.ServiceCategoryDTO;
import lk.ijse.back_end.entity.ServiceCategory;
import lk.ijse.back_end.repository.ServiceCategoryRepo;
import lk.ijse.back_end.service.ServiceCategoryService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    @Autowired
    private ServiceCategoryRepo categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int saveServiceCategory(ServiceCategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            return VarList.Not_Acceptable;
        }
        categoryRepository.save(modelMapper.map(categoryDTO, ServiceCategory.class));
        return VarList.Created;
    }

    @Override
    public ServiceCategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> modelMapper.map(category, ServiceCategoryDTO.class))
                .orElse(null);
    }
}