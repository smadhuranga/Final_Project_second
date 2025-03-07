package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepo extends JpaRepository<ServiceCategory ,Long> {
}
