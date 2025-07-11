package com.products.backend.model;
import java.time.LocalDate;

public class Product {
    private Long id;
    private String name;
    private Category category;
    private Double unitPrice;
    private LocalDate expirationDate;
    private Integer stock;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public  Product(){

    }
    public Product(String name, String category, Double unitPrice, LocalDate date, int stock){
        this.name = name;
        this.category = new Category();
        this.category.setName(category);
        this.category.setId(0);
        this.unitPrice = unitPrice;
        this.expirationDate = date;
        this.stock = stock;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setStock(Integer quantity) {
        this.stock = quantity;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOutOfStock() {
        return stock == 0;
    }

}
