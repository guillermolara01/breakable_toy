package com.products.backend.integration;

import com.products.backend.dto.category.CategoryRequest;
import com.products.backend.dto.product.ProductRequest;
import com.products.backend.model.Category;
import com.products.backend.repository.CategoryRepository;
import com.products.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Category endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/categories";
    }

    // ========== GET /categories ==========

    @Test
    void getAllCategories_ShouldReturnCategoryList() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("[");
    }

    // ========== POST /categories ==========

    @Test
    void createCategory_WithValidName_ShouldReturnCreatedCategory() {
        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("New Category " + System.currentTimeMillis()); // Unique name

        // Act
        ResponseEntity<Category> response = restTemplate.postForEntity(
                baseUrl,
                request,
                Category.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(request.getName());
    }

    @Test
    void createCategory_WithBlankName_ShouldReturn400() {
        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("");

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                request,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createCategory_WithNullName_ShouldReturn400() {
        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName(null);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                request,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createCategory_WithDuplicateName_ShouldReturn409() {
        // Arrange - Create first category
        String uniqueName = "Duplicate Test " + System.currentTimeMillis();
        CategoryRequest request = new CategoryRequest();
        request.setName(uniqueName);

        restTemplate.postForEntity(baseUrl, request, Category.class);

        // Act - Try to create duplicate
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                request,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("already exists");
    }

    // ========== GET /categories/{id} ==========

    @Test
    void getCategory_WhenExists_ShouldReturnCategory() {
        // Arrange - Create a category first
        CategoryRequest request = new CategoryRequest();
        request.setName("Test Category " + System.currentTimeMillis());

        ResponseEntity<Category> createResponse = restTemplate.postForEntity(
                baseUrl,
                request,
                Category.class
        );
        Long categoryId = createResponse.getBody().getId();

        // Act
        ResponseEntity<Category> response = restTemplate.getForEntity(
                baseUrl + "/" + categoryId,
                Category.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(categoryId);
    }

    @Test
    void getCategory_WhenNotExists_ShouldReturn404() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/99999",
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Category not found");
    }

    // ========== DELETE /categories/{id} ==========

    @Test
    void deleteCategory_WhenExistsAndNotInUse_ShouldReturn204() {
        // Arrange - Create a category
        CategoryRequest request = new CategoryRequest();
        request.setName("Category To Delete " + System.currentTimeMillis());

        ResponseEntity<Category> createResponse = restTemplate.postForEntity(
                baseUrl,
                request,
                Category.class
        );
        Long categoryId = createResponse.getBody().getId();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + categoryId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deletion
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + categoryId,
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteCategory_WhenNotExists_ShouldReturn404() {
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

    @Test
    void deleteCategory_WhenInUse_ShouldReturn409() {
        // Arrange - Create a category
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Category In Use " + System.currentTimeMillis());

        ResponseEntity<Category> categoryResponse = restTemplate.postForEntity(
                baseUrl,
                categoryRequest,
                Category.class
        );
        Category category = categoryResponse.getBody();

        // Create a product using this category
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Product Using Category");
        productRequest.setCategory(category);
        productRequest.setUnitPrice(10.0);
        productRequest.setStock(5);

        restTemplate.postForEntity(
                "http://localhost:" + port + "/products",
                productRequest,
                String.class
        );

        // Act - Try to delete the category
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + category.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("Cannot delete category");
        assertThat(response.getBody()).contains("product(s) are using this category");
    }

    @Test
    void deleteCategory_WithInvalidIdFormat_ShouldReturn400() {
        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/invalid-id",
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Type Mismatch");
    }

    // ========== End-to-End Scenario Test ==========

    @Test
    void endToEndScenario_CreateGetDeleteCategory() {
        // Step 1: Create a category
        CategoryRequest createRequest = new CategoryRequest();
        createRequest.setName("E2E Category " + System.currentTimeMillis());

        ResponseEntity<Category> createResponse = restTemplate.postForEntity(
                baseUrl,
                createRequest,
                Category.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long categoryId = createResponse.getBody().getId();

        // Step 2: Get the category
        ResponseEntity<Category> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + categoryId,
                Category.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo(createRequest.getName());

        // Step 3: Get all categories (should include our new one)
        ResponseEntity<String> getAllResponse = restTemplate.getForEntity(
                baseUrl,
                String.class
        );
        assertThat(getAllResponse.getBody()).contains(createRequest.getName());

        // Step 4: Delete the category
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + categoryId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Step 5: Verify deletion
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                baseUrl + "/" + categoryId,
                String.class
        );
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}