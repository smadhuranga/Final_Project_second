package lk.ijse.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {
    private Long id;
    private Long customerId;
    private Long sellerId;
    private Long serviceId;
    private LocalDateTime orderDate;
    private String status;
    // Getters and Setters
}