package domain.entity.base;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntityWithNumber extends BaseEntity {

    @Id
    @Column(name = "Number", unique = true, nullable = false)
    public String Number;
}
