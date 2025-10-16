package com.products.backend.service.product;

import java.time.LocalDate;
import java.util.*;

import com.products.backend.classes.metrics.Metric;
import com.products.backend.dto.product.PaginatedProducts;
import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.model.Product;
import com.products.backend.repository.ProductRepository;
import com.products.backend.service.metrics.ProductMetricsService;
import com.products.backend.service.product.filter.IProductFilter;
import com.products.backend.service.product.filter.ProductFilterFactory;
import com.products.backend.service.product.sort.ProductSortStrategy;
import org.springframework.stereotype.Service;

/**
 * Refactored ProductService following Single Responsibility Principle
 * Now focuses only on CRUD operations and delegates filtering/sorting/metrics
 */
@Service
public class ProductService implements IProductService {

    private final ProductRepository repository;
    private final ProductFilterFactory filterFactory;
    private final ProductSortStrategy sortStrategy;
    private final ProductMetricsService metricsService;

    public ProductService(ProductRepository repository,
                          ProductFilterFactory filterFactory,
                          ProductSortStrategy sortStrategy,
                          ProductMetricsService metricsService) {
        this.repository = repository;
        this.filterFactory = filterFactory;
        this.sortStrategy = sortStrategy;
        this.metricsService = metricsService;
    }

    @Override
    public PaginatedProducts getAllProducts(
            String name,
            String category,
            Boolean available,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        IProductFilter filter = filterFactory.createFilter(name, category, available);
        Comparator<Product> comparator = sortStrategy.createComparator(sortBy, direction);

        long totalCount = repository.findAll().size();

        List<ProductResponse> products = repository.findAll().stream()
                .filter(filter.buildPredicate())
                .sorted(comparator)
                .skip((long) page * size)
                .limit(size)
                .map(this::mapToResponse)
                .toList();

        return new PaginatedProducts(products, page, size, (int) totalCount);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        mapRequestToProduct(request, product);
        product.setCreatedAt(LocalDate.now());
        product.setUpdatedAt(LocalDate.now());
        return mapToResponse(repository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        mapRequestToProduct(request, product);
        product.setUpdatedAt(LocalDate.now());
        return mapToResponse(repository.save(product));
    }

    @Override
    public ProductResponse markOutOfStock(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        product.setStock(0);
        product.setUpdatedAt(LocalDate.now());
        return mapToResponse(repository.save(product));
    }

    @Override
    public ProductResponse markInStock(Long id, Integer quantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        product.setStock(quantity);
        product.setUpdatedAt(LocalDate.now());
        return mapToResponse(repository.save(product));
    }

    @Override
    public Optional<ProductResponse> getProductById(Long id) {
        return repository.findById(id).map(this::mapToResponse);
    }

    @Override
    public ProductResponse deleteProductById(Long id) {
        Product deleted = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        repository.delete(id);
        return mapToResponse(deleted);
    }

    @Override
    public List<Metric> getGeneralMetrics() {
        return metricsService.getGeneralMetrics();
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setUnitPrice(product.getUnitPrice());
        response.setExpirationDate(product.getExpirationDate());
        response.setStock(product.getStock());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setUnitPrice(request.getUnitPrice());
        product.setExpirationDate(request.getExpirationDate());
        product.setStock(request.getStock());
    }
}