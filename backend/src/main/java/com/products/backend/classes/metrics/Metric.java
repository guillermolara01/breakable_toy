package com.products.backend.classes.metrics;

import com.products.backend.model.Category;

import java.util.List;

public class Metric {
    private Category  category;
    private  int quantity;
    private double value;
    private double averagePrice;

    public Metric(Category category , int quantity, double value, double averagePrice){
        this.category = category;
        this.quantity = quantity;
        this.value = value;
        this.averagePrice = averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public double getValue() {
        return value;
    }

    public Category getCategory(){
        return this.category;
    }

    public int getQuantity(){
        return  this.quantity;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
