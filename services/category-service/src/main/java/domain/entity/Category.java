package domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    public String name;
    
    @Column(length = 500)
    public String description;
    
    @Column(name = "parent_id")
    public Long parentId;
    
    public String slug;
    
    public boolean active = true;
}
