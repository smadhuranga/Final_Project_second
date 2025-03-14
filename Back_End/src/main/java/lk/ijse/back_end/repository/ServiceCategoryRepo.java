package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCategoryRepo extends JpaRepository<ServiceCategory ,Long> {
    boolean existsByName(String name);  // Add this method
    Optional<ServiceCategory> findByName(String name);
}
