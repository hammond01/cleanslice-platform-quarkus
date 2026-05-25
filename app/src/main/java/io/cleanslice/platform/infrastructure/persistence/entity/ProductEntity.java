package io.cleanslice.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntityWithNumberJpa {

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


