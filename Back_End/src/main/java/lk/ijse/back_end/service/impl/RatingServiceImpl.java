package lk.ijse.back_end.service.impl;


import lk.ijse.back_end.dto.RatingDTO;
import lk.ijse.back_end.entity.Rating;
import lk.ijse.back_end.repository.RatingRepo;
import lk.ijse.back_end.service.RatingService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    private final RatingRepo ratingRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public RatingServiceImpl(RatingRepo ratingRepo, ModelMapper modelMapper) {
        this.ratingRepo = ratingRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public int saveRating(RatingDTO ratingDTO) {
        try {
            Rating rating = modelMapper.map(ratingDTO, Rating.class);
            ratingRepo.save(rating);
            return 1; // Use your VarList constants here
        } catch (Exception e) {
            return -1; // Use error code from VarList
        }
    }

    @Override
    public RatingDTO getRatingById(Long id) {
        Optional<Rating> rating = ratingRepo.findById(id);
        return rating.map(value -> modelMapper.map(value, RatingDTO.class))
                .orElse(null);
    }


    @Override
    public double getRatingValue(Long ratingId) {
        return ratingRepo.findById(ratingId)
                .map(Rating::getValue)
                .orElse(0.0);
    }
}