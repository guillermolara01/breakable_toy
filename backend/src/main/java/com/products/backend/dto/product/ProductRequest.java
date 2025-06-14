package com.products.backend.dto.product;
import com.products.backend.model.Category;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class ProductRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    private Category category;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double unitPrice;

    private LocalDate expirationDate;

    @NotNull
    @Min(0)
    private Integer quantityInStock;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
}
