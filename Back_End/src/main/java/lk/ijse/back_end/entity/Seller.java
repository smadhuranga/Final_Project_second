package lk.ijse.back_end.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@DiscriminatorValue("SELLER")
public class Seller extends User {
    private String nic;
    private String bio;

    @ElementCollection
    private List<String> qualifications;

    @ElementCollection
    private List<Long> skillIds;

    @ElementCollection
    private List<Long> serviceIds;

    @ElementCollection
    private List<Long> ratingIds;
}