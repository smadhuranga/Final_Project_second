package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepo extends JpaRepository<Order, Long> {
}
