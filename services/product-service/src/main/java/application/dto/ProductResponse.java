package application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public Integer stock;
    public Long categoryId;
    public LocalDateTime createdAt;
    public String createdBy;
    public LocalDateTime lastModifiedAt;
    public String lastModifiedBy;
    public boolean active;
}
