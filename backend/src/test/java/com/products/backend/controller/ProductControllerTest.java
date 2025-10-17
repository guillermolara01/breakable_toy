package com.products.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.products.backend.dto.product.PaginatedProducts;
import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.exception.GlobalExceptionHandler;
import com.products.backend.exception.InvalidOperationException;
import com.products.backend.exception.ResourceNotFoundException;
import com.products.backend.model.Category;
import com.products.backend.service.product.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for ProductController using MockMvc
 * Tests REST endpoints with mocked service layer
 */
@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = {ProductController.class, GlobalExceptionHandler.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IProductService productService;

    private ProductResponse sampleProduct;
    private ProductRequest sampleRequest;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Electronics");

        sampleProduct = new ProductResponse();
        sampleProduct.setId(1L);
        sampleProduct.setName("Laptop");
        sampleProduct.setCategory(sampleCategory);
        sampleProduct.setUnitPrice(999.99);
        sampleProduct.setStock(10);
        sampleProduct.setExpirationDate(LocalDate.now().plusYears(1));
        sampleProduct.setUpdatedAt(LocalDate.now());

        sampleRequest = new ProductRequest();
        sampleRequest.setName("Laptop");
        sampleRequest.setCategory(sampleCategory);
        sampleRequest.setUnitPrice(999.99);
        sampleRequest.setStock(10);
        sampleRequest.setExpirationDate(LocalDate.now().plusYears(1));
    }

    // ========== GET /products ==========

    @Test
    void getAllProducts_ShouldReturnPaginatedProducts() throws Exception {
        PaginatedProducts paginatedProducts = new PaginatedProducts(
                List.of(sampleProduct),
                0,
                10,
                1
        );

        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)
        )).thenReturn(paginatedProducts);

        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("Laptop"))
                .andExpect(jsonPath("$.products[0].unitPrice").value(999.99))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(productService, times(1)).getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)
        );
    }

    @Test
    void getAllProducts_WithFilters_ShouldReturnFilteredProducts() throws Exception {
        PaginatedProducts paginatedProducts = new PaginatedProducts(
                List.of(sampleProduct),
                0,
                10,
                1
        );

        when(productService.getAllProducts(
                eq("Laptop"), eq("1"), eq(true), eq("name"), eq("asc"), eq(0), eq(10)
        )).thenReturn(paginatedProducts);

        mockMvc.perform(get("/products")
                        .param("name", "Laptop")
                        .param("category", "1")
                        .param("available", "true")
                        .param("sortBy", "name")
                        .param("direction", "asc")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("Laptop"));
    }

    // ========== GET /products/{id} ==========

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.unitPrice").value(999.99));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void getProductById_WhenNotExists_ShouldReturn404() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999L);
    }

    // ========== POST /products ==========

    @Test
    void createProduct_WithValidData_ShouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(sampleProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.unitPrice").value(999.99));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturn400() throws Exception {
        ProductRequest invalidRequest = new ProductRequest();
        // Missing required fields

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }

    // ========== PUT /products/{id} ==========

    @Test
    void updateProduct_WhenExists_ShouldReturnUpdatedProduct() throws Exception {
        ProductResponse updatedProduct = new ProductResponse();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setCategory(sampleCategory);
        updatedProduct.setUnitPrice(1099.99);
        updatedProduct.setStock(5);
        updatedProduct.setUpdatedAt(LocalDate.now());

        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.unitPrice").value(1099.99));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    void updateProduct_WhenNotExists_ShouldReturn404() throws Exception {
        when(productService.updateProduct(eq(999L), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product", 999L));

        mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    // ========== PUT /products/{id}/outofstock ==========

    @Test
    void markOutOfStock_WhenExists_ShouldReturnUpdatedProduct() throws Exception {
        ProductResponse outOfStockProduct = new ProductResponse();
        outOfStockProduct.setId(1L);
        outOfStockProduct.setName("Laptop");
        outOfStockProduct.setCategory(sampleCategory);
        outOfStockProduct.setUnitPrice(999.99);
        outOfStockProduct.setStock(0);
        outOfStockProduct.setUpdatedAt(LocalDate.now());

        when(productService.markOutOfStock(1L)).thenReturn(outOfStockProduct);

        mockMvc.perform(put("/products/1/outofstock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(0));

        verify(productService, times(1)).markOutOfStock(1L);
    }

    @Test
    void markOutOfStock_WhenNotExists_ShouldReturn404() throws Exception {
        when(productService.markOutOfStock(999L))
                .thenThrow(new ResourceNotFoundException("Product", 999L));

        mockMvc.perform(put("/products/999/outofstock"))
                .andExpect(status().isNotFound());
    }

    // ========== PUT /products/{id}/instock ==========

    @Test
    void markInStock_WithValidQuantity_ShouldReturnUpdatedProduct() throws Exception {
        ProductResponse inStockProduct = new ProductResponse();
        inStockProduct.setId(1L);
        inStockProduct.setName("Laptop");
        inStockProduct.setCategory(sampleCategory);
        inStockProduct.setUnitPrice(999.99);
        inStockProduct.setStock(20);
        inStockProduct.setUpdatedAt(LocalDate.now());

        when(productService.markInStock(1L, 20)).thenReturn(inStockProduct);

        mockMvc.perform(put("/products/1/instock")
                        .param("quantity", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(20));

        verify(productService, times(1)).markInStock(1L, 20);
    }

    @Test
    void markInStock_WithNegativeQuantity_ShouldReturn400() throws Exception {
        when(productService.markInStock(1L, -5))
                .thenThrow(new InvalidOperationException("Stock quantity cannot be negative"));

        mockMvc.perform(put("/products/1/instock")
                        .param("quantity", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Operation"));
    }

    @Test
    void markInStock_WithDefaultQuantity_ShouldUseDefault() throws Exception {
        ProductResponse inStockProduct = new ProductResponse();
        inStockProduct.setId(1L);
        inStockProduct.setStock(10);

        when(productService.markInStock(1L, 10)).thenReturn(inStockProduct);

        mockMvc.perform(put("/products/1/instock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(10));

        verify(productService, times(1)).markInStock(1L, 10);
    }

    // ========== DELETE /products/{id} ==========

    @Test
    void deleteProduct_WhenExists_ShouldReturnDeletedProduct() throws Exception {
        when(productService.deleteProductById(1L)).thenReturn(sampleProduct);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).deleteProductById(1L);
    }

    @Test
    void deleteProduct_WhenNotExists_ShouldReturn404() throws Exception {
        when(productService.deleteProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product", 999L));

        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    // ========== GET /products/metrics ==========

    @Test
    void getMetrics_ShouldReturnMetricsList() throws Exception {
        when(productService.getGeneralMetrics()).thenReturn(List.of());

        mockMvc.perform(get("/products/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(productService, times(1)).getGeneralMetrics();
    }
}