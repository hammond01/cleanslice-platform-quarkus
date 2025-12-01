package domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    
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
