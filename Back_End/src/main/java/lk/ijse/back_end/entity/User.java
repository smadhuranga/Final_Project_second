package lk.ijse.back_end.entity;

import jakarta.persistence.*;
import lk.ijse.back_end.util.UserType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private UserType type; // CUSTOMER, SELLER, COORDINATOR, ADMIN

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Getters and setters
}
