package lk.ijse.back_end.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private Long customerId;
    private Long sellerId;
    private Long serviceId;
    private LocalDateTime orderDate;
    private String status;
}
