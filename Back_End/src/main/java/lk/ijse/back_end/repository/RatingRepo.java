package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepo  extends JpaRepository<Rating, Long> {
}
