package lk.ijse.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatingDTO {
    private Long id;
    private Long customerId;
    private Long sellerId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    // Getters and Setters
}