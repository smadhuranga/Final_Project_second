package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment , Long> {
}
