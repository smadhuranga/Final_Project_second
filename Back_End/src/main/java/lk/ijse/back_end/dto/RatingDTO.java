package lk.ijse.back_end.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingDTO {
    private Long id;
    private Long customerId;
    private Long sellerId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}