package com.products.backend;

import com.products.backend.dto.product.PaginatedProducts;
import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.model.Category;
import com.products.backend.model.Product;
import com.products.backend.repository.CategoryRepository;
import com.products.backend.repository.ProductRepository;
import com.products.backend.service.metrics.ProductMetricsService;
import com.products.backend.service.product.ProductService;
import com.products.backend.service.product.filter.ProductFilterFactory;
import com.products.backend.service.product.sort.ProductSortStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProductFilterFactory filterFactory;
    private ProductSortStrategy sortStrategy;
    private ProductMetricsService metricsService;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);

        // Use real implementations for filter and sort strategies
        filterFactory = new ProductFilterFactory();
        sortStrategy = new ProductSortStrategy();

        // Mock the metrics service since it's not the focus of these tests
        metricsService = mock(ProductMetricsService.class);

        productService = new ProductService(
                productRepository,
                filterFactory,
                sortStrategy,
                metricsService
        );
    }

    @Test
    void shouldReturnOnlyAvailableProducts() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category");

        Product p1 = new Product("A", "Category", 10.0, LocalDate.now().plusDays(10), 5);
        p1.setCategory(category);

        Product p2 = new Product("B", "Category", 15.0, LocalDate.now().plusDays(5), 0);
        p2.setCategory(category);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        PaginatedProducts result = productService.getAllProducts(null, null, true, null, null, 0, 10);

        assertEquals(1, result.getProducts().size());
        assertEquals("A", result.getProducts().get(0).getName());
    }

    @Test
    void shouldFilterByName() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category");

        Product p1 = new Product("Apple", "Category", 10.0, LocalDate.now(), 5);
        p1.setCategory(category);

        Product p2 = new Product("Banana", "Category", 15.0, LocalDate.now(), 5);
        p2.setCategory(category);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        PaginatedProducts result = productService.getAllProducts("App", null, null, null, null, 0, 10);

        assertEquals(1, result.getProducts().size());
        assertEquals("Apple", result.getProducts().get(0).getName());
    }

    @Test
    void shouldFilterByCategory() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Fruits");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Hygiene");

        Product p1 = new Product("Apple", "Fruits", 10.0, LocalDate.now(), 5);
        p1.setCategory(cat1);

        Product p2 = new Product("Soap", "Hygiene", 2.0, LocalDate.now(), 5);
        p2.setCategory(cat2);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        PaginatedProducts result = productService.getAllProducts(null, "1", null, null, null, 0, 10);

        assertEquals(1, result.getProducts().size());
        assertEquals("Apple", result.getProducts().get(0).getName());
    }

    @Test
    void shouldCreateProductSuccessfully() {
        ProductRequest request = new ProductRequest();
        request.setName("Milk");
        request.setUnitPrice(3.5);
        request.setStock(10);
        request.setExpirationDate(LocalDate.now().plusDays(5));
        Category cat = new Category();
        cat.setId(1L);
        request.setCategory(cat);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.createProduct(request);

        assertEquals("Milk", response.getName());
        assertEquals(10, response.getStock());
        assertEquals(3.5, response.getUnitPrice());
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Category cat = new Category();
        cat.setId(1L);

        Product existing = new Product("OldName", "Category", 1.0, LocalDate.now(), 1);
        existing.setId(1L);
        existing.setCategory(cat);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductRequest request = new ProductRequest();
        request.setName("Updated");
        request.setUnitPrice(10.0);
        request.setStock(15);
        request.setExpirationDate(LocalDate.now().plusDays(10));
        request.setCategory(cat);

        ProductResponse updated = productService.updateProduct(1L, request);

        assertEquals("Updated", updated.getName());
        assertEquals(10.0, updated.getUnitPrice());
    }

    @Test
    void shouldMarkProductOutOfStock() {
        Category cat = new Category();
        cat.setId(1L);

        Product product = new Product("Bread", "Food", 1.0, LocalDate.now(), 5);
        product.setId(1L);
        product.setCategory(cat);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.markOutOfStock(1L);

        assertEquals(0, response.getStock());
    }

    @Test
    void shouldMarkProductInStock() {
        Category cat = new Category();
        cat.setId(1L);

        Product product = new Product("Water", "Drinks", 2.0, LocalDate.now(), 0);
        product.setId(1L);
        product.setCategory(cat);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.markInStock(1L, 20);

        assertEquals(20, response.getStock());
    }

    @Test
    void shouldDeleteProduct() {
        Category cat = new Category();
        cat.setId(1L);

        Product product = new Product("Juice", "Drinks", 3.0, LocalDate.now(), 10);
        product.setId(1L);
        product.setCategory(cat);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse deleted = productService.deleteProductById(1L);

        verify(productRepository, times(1)).delete(1L);
        assertEquals("Juice", deleted.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            productService.updateProduct(999L, new ProductRequest());
        });
    }

    @Test
    void shouldThrowExceptionWhenMarkingInvalidStockQuantity() {
        Category cat = new Category();
        cat.setId(1L);

        Product product = new Product("Water", "Drinks", 2.0, LocalDate.now(), 0);
        product.setId(1L);
        product.setCategory(cat);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> {
            productService.markInStock(1L, -5);
        });
    }

    @Test
    void shouldSortProductsByName() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Category");

        Product p1 = new Product("Banana", "Category", 10.0, LocalDate.now(), 5);
        p1.setCategory(cat);

        Product p2 = new Product("Apple", "Category", 15.0, LocalDate.now(), 5);
        p2.setCategory(cat);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        PaginatedProducts result = productService.getAllProducts(null, null, null, "name", "asc", 0, 10);

        assertEquals(2, result.getProducts().size());
        assertEquals("Apple", result.getProducts().get(0).getName());
        assertEquals("Banana", result.getProducts().get(1).getName());
    }

    @Test
    void shouldSortProductsByPriceDescending() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Category");

        Product p1 = new Product("A", "Category", 5.0, LocalDate.now(), 5);
        p1.setCategory(cat);

        Product p2 = new Product("B", "Category", 15.0, LocalDate.now(), 5);
        p2.setCategory(cat);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        PaginatedProducts result = productService.getAllProducts(null, null, null, "price", "desc", 0, 10);

        assertEquals(2, result.getProducts().size());
        assertEquals(15.0, result.getProducts().get(0).getUnitPrice());
        assertEquals(5.0, result.getProducts().get(1).getUnitPrice());
    }
}