package lk.ijse.back_end.dto;

import lk.ijse.back_end.util.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long customerId;
    private Long sellerId;
    private Long serviceId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private double amount;


}
