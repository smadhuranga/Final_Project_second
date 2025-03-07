package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.RatingDTO;

public interface RatingService {
    int saveRating(RatingDTO ratingDTO);
    RatingDTO getRatingById(Long id);
}