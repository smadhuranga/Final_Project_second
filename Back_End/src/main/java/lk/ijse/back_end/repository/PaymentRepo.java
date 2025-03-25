package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment , Long> {
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalCompletedPayments();

    @Query("SELECT MONTH(p.paymentDate), SUM(p.amount) FROM Payment p WHERE YEAR(p.paymentDate) = YEAR(CURRENT_DATE) GROUP BY MONTH(p.paymentDate)")
    List<Object[]> getMonthlyEarnings();

    @Query("SELECT p FROM Payment p WHERE p.status = :status")
    List<Payment> findByStatus(@Param("status") String status);
}
