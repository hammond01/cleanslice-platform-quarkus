package domain.entity;

import domain.entity.base.BaseEntityWithNumber;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product extends BaseEntityWithNumber {

    // Getters and Setters
    @Column(nullable = false)
    public String name;

    @Column(length = 1000)
    public String description;

    @Column(nullable = false)
    public BigDecimal price;

    @Column(nullable = false)
    public Integer stock;

    @Column(name = "category_id")
    public Long categoryId;

    public boolean active = true;

}
