package lk.ijse.back_end.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Data;

import java.util.List;

@Entity
@Data
@DiscriminatorValue("COORDINATOR")
public class Coordinator extends User {
    private String nic;

    @ElementCollection
    private List<String> qualifications;

    @ElementCollection
    private List<Long> skillIds;
}