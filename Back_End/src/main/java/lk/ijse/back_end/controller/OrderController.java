package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.OrderDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.service.impl.OrderServiceImpl;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin
public class OrderController {

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            int result = orderService.saveOrder(orderDTO);
            if(result == VarList.Created) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.Created, "Order Created", orderDTO), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Bad_Request, "Invalid Request", null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}