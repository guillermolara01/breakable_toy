package com.products.backend;

import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.model.Category;
import com.products.backend.repository.CategoryRepository;
import com.products.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Product endpoints
 * Tests the full application stack with TestRestTemplate
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String baseUrl;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/products";

        // Clear existing products
        productRepository.findAll().forEach(p -> productRepository.delete(p.getId()));

        // Get or create test category
        testCategory = categoryRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> categoryRepository.save("Test Category"));
    }

    // ========== GET /products ==========

    @Test
    void getAllProducts_ShouldReturnPaginatedResponse() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "?page=0&size=10",
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("products");
        assertThat(response.getBody()).contains("currentPage");
        assertThat(response.getBody()).contains("pageSize");
        assertThat(response.getBody()).contains("totalItems");
    }

    // ========== POST /products ==========

    @Test
    void createProduct_WithValidData_ShouldReturnCreatedProduct() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Integration Test Product");
        request.setCategory(testCategory);
        request.setUnitPrice(49.99);
        request.setStock(100);
        request.setExpirationDate(LocalDate.now().plusMonths(6));

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Integration Test Product");
        assertThat(response.getBody().getUnitPrice()).isEqualTo(49.99);
        assertThat(response.getBody().getStock()).isEqualTo(100);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturn400() {
        // Arrange - Missing required fields
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName(""); // Invalid: blank name

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                invalidRequest,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation Failed");
    }

    // ========== GET /products/{id} ==========

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        // Arrange - Create a product first
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setCategory(testCategory);
        request.setUnitPrice(29.99);
        request.setStock(50);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class
        );
        Long productId = createResponse.getBody().getId();

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
                baseUrl + "/" + productId,
                ProductResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(productId);
        assertThat(response.getBody().getName()).isEqualTo("Test Product");
    }

    @Test
    void getProductById_WhenNotExists_ShouldReturn404() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/99999",
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ========== PUT /products/{id} ==========

    @Test
    void updateProduct_WhenExists_ShouldReturnUpdatedProduct() {
        // Arrange - Create a product first
        ProductRequest createRequest = new ProductRequest();
        createRequest.setName("Original Name");
        createRequest.setCategory(testCategory);
        createRequest.setUnitPrice(19.99);
        createRequest.setStock(25);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                baseUrl,
                createRequest,
                ProductResponse.class
        );
        Long productId = createResponse.getBody().getId();

        // Update request
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setCategory(testCategory);
        updateRequest.setUnitPrice(24.99);
        updateRequest.setStock(30);

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                baseUrl + "/" + productId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                ProductResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Name");
        assertThat(response.getBody().getUnitPrice()).isEqualTo(24.99);
        assertThat(response.getBody().getStock()).isEqualTo(30);
    }

    @Test
    void updateProduct_WhenNotExists_ShouldReturn404() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Non-existent Product");
        request.setCategory(testCategory);
        request.setUnitPrice(10.0);
        request.setStock(5);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/99999",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ========== PUT /products/{id}/outofstock ==========

    @Test
    void markOutOfStock_WhenExists_ShouldSetStockToZero() {
        // Arrange - Create a product with stock
        ProductRequest request = new ProductRequest();
        request.setName("Product In Stock");
        request.setCategory(testCategory);
        request.setUnitPrice(15.99);
        request.setStock(50);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class
        );
        Long productId = createResponse.getBody().getId();

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                baseUrl + "/" + productId + "/outofstock",
                HttpMethod.PUT,
                null,
                ProductResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStock()).isEqualTo(0);
    }

    // ========== PUT /products/{id}/instock ==========

    @Test
    void markInStock_WithQuantity_ShouldUpdateStock() {
        // Arrange - Create a product
        ProductRequest request = new ProductRequest();
        request.setName("Out of Stock Product");
        request.setCategory(testCategory);
        request.setUnitPrice(12.99);
        request.setStock(0);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class
        );
        Long productId = createResponse.getBody().getId();

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                baseUrl + "/" + productId + "/instock?quantity=25",
                HttpMethod.PUT,
                null,
                ProductResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStock()).isEqualTo(25);
    }

    @Test
    void markInStock_WithNegativeQuantity_ShouldReturn400() {
        // Arrange - Create a product
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setCategory(testCategory);
        request.setUnitPrice(10.0);
        request.setStock(10);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class
        );
        Long productId = createResponse.getBody().getId();

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + productId + "/instock?quantity=-5",
                HttpMethod.PUT,
                null,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Stock quantity cannot be negative");
    }

    // ========== DELETE /products/{id} ==========

    @Test
    void deleteProduct_WhenExists_ShouldReturnDeletedProduct() {
        // Arrange - Create a product
        ProductRequest request = new ProductRequest();
        request.setName("Product To Delete");
        request.setCategory(testCategory);
        request.setUnitPrice(5.99);
        request.setStock(10);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class
        );
        Long productId = createResponse.getBody().getId();

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                baseUrl + "/" + productId,
                HttpMethod.DELETE,
                null,
                ProductResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Product To Delete");

        // Verify product is deleted
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + productId,
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteProduct_WhenNotExists_ShouldReturn404() {
        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/99999",
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ========== GET /products/metrics ==========

    @Test
    void getMetrics_ShouldReturnMetricsArray() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/metrics",
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Overall");
    }

    // ========== End-to-End Scenario Test ==========

    @Test
    void endToEndScenario_CreateUpdateDeleteProduct() {
        // Step 1: Create a product
        ProductRequest createRequest = new ProductRequest();
        createRequest.setName("E2E Test Product");
        createRequest.setCategory(testCategory);
        createRequest.setUnitPrice(99.99);
        createRequest.setStock(100);

        ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                baseUrl,
                createRequest,
                ProductResponse.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long productId = createResponse.getBody().getId();

        // Step 2: Get the product
        ResponseEntity<ProductResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + productId,
                ProductResponse.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("E2E Test Product");

        // Step 3: Update the product
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("E2E Updated Product");
        updateRequest.setCategory(testCategory);
        updateRequest.setUnitPrice(89.99);
        updateRequest.setStock(80);

        ResponseEntity<ProductResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/" + productId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                ProductResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getName()).isEqualTo("E2E Updated Product");

        // Step 4: Mark out of stock
        ResponseEntity<ProductResponse> outOfStockResponse = restTemplate.exchange(
                baseUrl + "/" + productId + "/outofstock",
                HttpMethod.PUT,
                null,
                ProductResponse.class
        );
        assertThat(outOfStockResponse.getBody().getStock()).isEqualTo(0);

        // Step 5: Mark in stock
        ResponseEntity<ProductResponse> inStockResponse = restTemplate.exchange(
                baseUrl + "/" + productId + "/instock?quantity=50",
                HttpMethod.PUT,
                null,
                ProductResponse.class
        );
        assertThat(inStockResponse.getBody().getStock()).isEqualTo(50);

        // Step 6: Delete the product
        ResponseEntity<ProductResponse> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + productId,
                HttpMethod.DELETE,
                null,
                ProductResponse.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Step 7: Verify deletion
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                baseUrl + "/" + productId,
                String.class
        );
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}