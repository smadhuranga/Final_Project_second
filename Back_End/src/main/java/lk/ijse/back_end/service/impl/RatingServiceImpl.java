package lk.ijse.back_end.service.impl;


import lk.ijse.back_end.dto.RatingDTO;
import lk.ijse.back_end.entity.Rating;
import lk.ijse.back_end.repository.RatingRepo;
import lk.ijse.back_end.service.RatingService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepo ratingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int saveRating(RatingDTO ratingDTO) {
        ratingRepository.save(modelMapper.map(ratingDTO, Rating.class));
        return VarList.Created;
    }

    @Override
    public RatingDTO getRatingById(Long id) {
        return ratingRepository.findById(id)
                .map(rating -> modelMapper.map(rating, RatingDTO.class))
                .orElse(null);
    }
}