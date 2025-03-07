package lk.ijse.back_end.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Service {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String deliveryTime;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    // Getters and setters
}