package com.products.backend.dto.product;
import java.util.List;

public class PaginatedProducts {
    private List<ProductResponse> products;
    private  int totalElements;
    private  int totalPages;
    private int page;
    private int size;


    public PaginatedProducts(List<ProductResponse> content, int page, int size, int total) {
        this.products = !content.isEmpty() ? content : null;
        this.totalElements = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public int getTotalElements(){
        return  this.totalElements;
    }

    public void setTotalElements(int totalElements){
        this.totalElements = totalElements;
    }

    public int getTotalPages(){
        return  this.totalPages;
    }

    public void setTotalPages(int totalPages){
        this.totalPages = totalPages;
    }

    public List<ProductResponse>  getProducts(){
        return this.products;
    }

    public void setProducts(List<ProductResponse> products){
        this.products = products;
    }

    public int getPage(){
        return  this.page;
    }

    public  int getSize(){
        return  this.size;
    }
}
