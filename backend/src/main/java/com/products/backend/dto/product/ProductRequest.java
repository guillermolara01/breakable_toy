package com.products.backend.dto.product;
import com.products.backend.model.Category;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class ProductRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    private Category category;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double unitPrice;

    private LocalDate expirationDate;

    @NotNull
    @Min(0)
    private Integer stock;

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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
