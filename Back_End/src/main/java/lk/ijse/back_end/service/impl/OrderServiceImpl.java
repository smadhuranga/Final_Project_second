package lk.ijse.back_end.service.impl;



import lk.ijse.back_end.dto.OrderDTO;
import lk.ijse.back_end.entity.Order;
import lk.ijse.back_end.repository.OrdersRepo;
import lk.ijse.back_end.service.OrderService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersRepo orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int saveOrder(OrderDTO orderDTO) {
        orderRepository.save(modelMapper.map(orderDTO, Order.class));
        return VarList.Created;
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .orElse(null);
    }
}