package lk.ijse.back_end.entity;



import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String deliveryTime;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "category_id")
    private Long categoryId;
}