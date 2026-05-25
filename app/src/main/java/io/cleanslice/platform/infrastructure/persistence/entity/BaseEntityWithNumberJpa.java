package io.cleanslice.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntityWithNumberJpa extends BaseEntityJpa {

    @Id
    @Column(name = "Number", unique = true, nullable = false)
    public String Number;
}

