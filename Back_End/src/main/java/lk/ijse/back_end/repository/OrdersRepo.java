package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepo extends JpaRepository<Orders , Long> {
}
