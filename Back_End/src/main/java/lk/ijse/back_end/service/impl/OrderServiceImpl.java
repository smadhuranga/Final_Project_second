package lk.ijse.back_end.service.impl;



import lk.ijse.back_end.dto.OrderDTO;
import lk.ijse.back_end.entity.Orders;
import lk.ijse.back_end.entity.Rating;
import lk.ijse.back_end.repository.OrdersRepo;
import lk.ijse.back_end.repository.RatingRepo;
import lk.ijse.back_end.service.OrderService;
import lk.ijse.back_end.util.OrderStatus;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersRepo orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int saveOrder(OrderDTO orderDTO) {
        orderRepository.save(modelMapper.map(orderDTO, Orders.class));
        return VarList.Created;
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orders -> modelMapper.map(orders, OrderDTO.class))
                .orElse(null);
    }

    @Override
    public List<OrderDTO> getOrdersBySellerAndStatus(Long sellerId, OrderStatus status) {
        List<Orders> orders = orderRepository.findBySeller_IdAndStatus(
                sellerId,
                status.toString()
        );
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public int countOrdersBySellerAndStatus(Long sellerId, OrderStatus status) {
        return orderRepository.countBySeller_IdAndStatus(
                sellerId,
                status.toString()
        ).intValue();
    }


}