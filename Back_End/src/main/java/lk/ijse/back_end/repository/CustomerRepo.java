package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends JpaRepository<Customer , String> {
}
