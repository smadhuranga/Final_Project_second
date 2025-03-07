package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinatorRepo extends JpaRepository<Coordinator , String> {
}
