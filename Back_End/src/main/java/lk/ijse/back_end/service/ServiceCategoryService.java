package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.ServiceCategoryDTO;

public interface ServiceCategoryService {
    int saveServiceCategory(ServiceCategoryDTO categoryDTO);
    ServiceCategoryDTO getCategoryById(Long id);
}
