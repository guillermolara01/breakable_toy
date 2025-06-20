package com.products.backend.dto.category;

public class CategoryResponse {
    private long id;
    private String name;

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
    public  String getName(){
        return  this.name;
    }

    public void setName(String name){
        this.name = name;
    }

}
