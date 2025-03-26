package lk.ijse.back_end.service;


import lk.ijse.back_end.dto.OrderDTO;
import lk.ijse.back_end.util.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderService {
    int saveOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getOrdersBySellerAndStatus(Long sellerId, OrderStatus status);
    int countOrdersBySellerAndStatus(Long sellerId, OrderStatus status);

}