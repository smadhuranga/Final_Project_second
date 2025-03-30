package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.ServiceCategoryDTO;

import java.util.List;

public interface ServiceCategoryService {
    int saveServiceCategory(ServiceCategoryDTO categoryDTO);
    ServiceCategoryDTO getCategoryById(Long id);

    List<ServiceCategoryDTO> getAllCategories();
    int updateCategory(ServiceCategoryDTO categoryDTO);
    int deleteCategory(Long id);
}
