package lk.ijse.back_end.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ServiceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String description;
}