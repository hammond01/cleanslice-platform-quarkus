package application.dto;

import java.math.BigDecimal;

public class ProductRequest {
    public String name;
    public String description;
    public BigDecimal price;
    public Integer stock;
    public Long categoryId;
}
