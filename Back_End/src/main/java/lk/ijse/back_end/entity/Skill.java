package lk.ijse.back_end.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Skill {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
}
