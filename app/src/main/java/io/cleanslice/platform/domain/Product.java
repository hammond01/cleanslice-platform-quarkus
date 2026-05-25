package io.cleanslice.platform.domain;


import java.math.BigDecimal;

public class Product extends BaseEntityWithNumber {

    // Getters and Setters
    public String name;
    public String description;
    public BigDecimal price;
    public Integer stock;
    public Long categoryId;

    public boolean active = true;

}


