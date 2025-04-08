package lk.ijse.back_end.repository;



import lk.ijse.back_end.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepo extends JpaRepository<Skill, Long> {
    boolean existsByName(String name);
    Optional<Skill> findByName(String name);
    List<Skill> findAllByNameIn(List<String> names);
}