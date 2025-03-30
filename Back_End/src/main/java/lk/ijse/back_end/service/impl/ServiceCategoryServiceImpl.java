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

import java.util.List;
import java.util.stream.Collectors;

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
    @Override
    public List<ServiceCategoryDTO> getAllCategories() {
        List<ServiceCategory> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> modelMapper.map(category, ServiceCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public int updateCategory(ServiceCategoryDTO categoryDTO) {
        try {
            ServiceCategory existingCategory = categoryRepository.findById(categoryDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            modelMapper.map(categoryDTO, existingCategory);
            categoryRepository.save(existingCategory);
            return VarList.OK;

        } catch (Exception e) {
            e.printStackTrace();
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public int deleteCategory(Long id) {
        try {
            categoryRepository.deleteById(id);
            return VarList.OK;
        } catch (Exception e) {
            e.printStackTrace();
            return VarList.Internal_Server_Error;
        }
    }
}