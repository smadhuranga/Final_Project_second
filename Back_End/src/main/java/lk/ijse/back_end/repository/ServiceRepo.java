package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Long> {
    boolean existsByTitle(String title);
    Optional<Service> findByTitle(String title);
}
