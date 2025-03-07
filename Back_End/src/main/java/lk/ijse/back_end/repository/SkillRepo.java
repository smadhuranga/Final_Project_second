package lk.ijse.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepo extends JpaRepository<SkillRepo, String> {
}
