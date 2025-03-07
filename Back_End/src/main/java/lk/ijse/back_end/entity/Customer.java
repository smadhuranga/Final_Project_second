package lk.ijse.back_end.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Customer extends User {
    // Additional customer-specific fields
    @OneToMany(mappedBy = "customer")
    private List<Orders> orders;
}