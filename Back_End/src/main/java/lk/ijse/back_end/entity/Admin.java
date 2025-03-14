package lk.ijse.back_end.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    // No additional fields
}