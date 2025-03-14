package lk.ijse.back_end.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String deliveryTime;
    private Long sellerId;
    private Long categoryId;
}