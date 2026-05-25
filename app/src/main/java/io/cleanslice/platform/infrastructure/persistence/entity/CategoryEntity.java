package io.cleanslice.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntityWithNumberJpa {
    
    @Column(nullable = false, unique = true)
    public String name;
    
    @Column(length = 500)
    public String description;
    
    @Column(name = "parent_id")
    public Long parentId;
    
    public String slug;
    
    public boolean active = true;
}


