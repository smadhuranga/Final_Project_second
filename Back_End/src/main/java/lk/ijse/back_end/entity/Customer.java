package lk.ijse.back_end.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Data;

import java.util.List;

@Entity
@Data
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    @ElementCollection
    private List<Long> orderIds;
}