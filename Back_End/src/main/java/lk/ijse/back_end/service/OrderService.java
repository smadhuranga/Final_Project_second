package lk.ijse.back_end.service;


import lk.ijse.back_end.dto.OrderDTO;

public interface OrderService {
    int saveOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
}