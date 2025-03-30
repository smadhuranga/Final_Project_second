package lk.ijse.back_end.entity;



import jakarta.persistence.*;
import lk.ijse.back_end.util.UserType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;

    private String password;
    private String phone;
    private String address;
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", insertable = false, updatable = false)
    private UserType type;



    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

}