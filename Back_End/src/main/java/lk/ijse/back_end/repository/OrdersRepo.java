package lk.ijse.back_end.repository;

import lk.ijse.back_end.dto.OrderDTO;
import lk.ijse.back_end.entity.Orders;
import lk.ijse.back_end.util.OrderStatus;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long> {


    List<Orders> findBySeller_IdAndStatus(Long sellerId, String status);
    Long countBySeller_IdAndStatus(Long sellerId, String status);
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.seller.id = :sellerId AND o.status = :status")
    int countBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Orders o WHERE o.seller.id = :sellerId AND o.status = :status")
    List<Orders> findBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") OrderStatus status);
}
