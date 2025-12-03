package application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreateProduct {
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    public String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    public String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    public BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be non-negative")
    public Integer stock;

    @NotNull(message = "Category ID is required")
    public Long categoryId;
}
