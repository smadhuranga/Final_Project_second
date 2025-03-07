package lk.ijse.back_end.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Seller extends User {
    private String nic;
    private String bio;

    @ElementCollection
    private List<String> qualifications;

    @ManyToMany
    @JoinTable(name = "seller_skills")
    private List<Skill> skills;

    @OneToMany(mappedBy = "seller")
    private List<Service> services;

    @OneToMany(mappedBy = "seller")
    private List<Rating> ratings;
}