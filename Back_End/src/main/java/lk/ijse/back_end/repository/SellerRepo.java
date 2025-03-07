package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepo extends JpaRepository<Seller , String> {
}
