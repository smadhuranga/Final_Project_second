package lk.ijse.back_end.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.List;


@Entity
public class Coordinator extends User {
    private String nic;
    @ElementCollection
    private List<String> qualifications;

    @ManyToMany
    @JoinTable(name = "coordinator_skills")
    private List<Skill> skills;
}