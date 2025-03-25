package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Orders;
import lk.ijse.back_end.util.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long> {
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = :status")
    long countByStatus(@Param("status") String status);

    // For more complex queries if needed
    @Query("SELECT o.status, COUNT(o) FROM Orders o GROUP BY o.status")
    List<Object[]> getOrderStatusCounts();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderDate BETWEEN :start AND :end")
    long countOrdersBetweenDates(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);
    long countByStatus(OrderStatus status);

}
