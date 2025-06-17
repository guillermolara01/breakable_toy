package com.products.backend.controller;

import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
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
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam (required = false)String category,
            @RequestParam (required = false)Boolean available,
            @RequestParam (required = false)String sortBy,
            @RequestParam (required = false)String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<ProductResponse> products = productService.getAllProducts(name, category, available, sortBy, direction, page, size);
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

    @PostMapping("/{id}/outofstock")
    public ResponseEntity<ProductResponse> markOutOfStock(@PathVariable Long id) {

        ProductResponse updated = productService.markOutOfStock(id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/instock")
    public ResponseEntity<ProductResponse> markInStock(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {

        ProductResponse updated = productService.markInStock(id, quantity);
        return ResponseEntity.ok(updated);
    }
}
