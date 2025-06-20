package com.products.backend.controller;

import com.products.backend.dto.product.PaginatedProducts;
import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.model.Category;
import com.products.backend.service.product.IProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PaginatedProducts> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam (required = false)String category,
            @RequestParam (required = false)Boolean available,
            @RequestParam (required = false)String sortBy,
            @RequestParam (required = false)String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginatedProducts products = productService.getAllProducts(name, category, available, sortBy, direction, page, size);


        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {


        ProductResponse updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/outofstock")
    public ResponseEntity<ProductResponse> markOutOfStock(@PathVariable Long id) {

        ProductResponse updated = productService.markOutOfStock(id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/instock")
    public ResponseEntity<ProductResponse> markInStock(
            @PathVariable Long id,
            @RequestParam (defaultValue = "10") Integer quantity
    ) {

        ProductResponse updated = productService.markInStock(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Long id){
        ProductResponse deleted = productService.deleteProductById(id);
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<Category>> getMetrics(){
        List<Category> cats = this.productService.getGeneralMetrics();
        return  ResponseEntity.ok(cats);
    }
}
