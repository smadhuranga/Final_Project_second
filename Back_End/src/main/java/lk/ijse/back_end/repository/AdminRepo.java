package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepo extends JpaRepository<Admin,Integer> {
}
