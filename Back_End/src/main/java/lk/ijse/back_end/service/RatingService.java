package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.RatingDTO;
import lk.ijse.back_end.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingService  {
    int saveRating(RatingDTO ratingDTO);
    RatingDTO getRatingById(Long id);
    double getRatingValue(Long ratingId);
}