package lk.ijse.back_end.repository;

import lk.ijse.back_end.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.type = :userType")
    Optional<User> findByIdAndType(@Param("userId") String userId,
                                   @Param("userType") String userType);
}
