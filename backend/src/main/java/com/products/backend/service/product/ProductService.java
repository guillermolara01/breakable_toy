package com.products.backend.service.product;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.model.Product;
import com.products.backend.repository.ProductRepository;

public class ProductService implements IProductService{
     private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductResponse> getAllProducts(
            Optional<String> name,
            Optional<String> category,
            Optional<Boolean> available,
            Optional<String> sortBy,
            Optional<String> direction,
            int page,
            int size
    ) {
        Comparator<Product> comparator = getComparator(sortBy.orElse("name"), direction.orElse("asc"));

        return repository.findAll().stream()
                .filter(p -> name.map(n -> p.getName().toLowerCase().contains(n.toLowerCase())).orElse(true))
                .filter(p -> category.map(c -> p.getCategory().equalsIgnoreCase(c)).orElse(true))
                .filter(p -> available.map(a -> a ? p.getQuantityInStock() > 0 : p.getQuantityInStock() == 0).orElse(true))
                .sorted(comparator)
                .skip((long) page * size)
                .limit(size)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        mapRequestToProduct(request, product);
        product.setCreatedAt(LocalDate.now());
        product.setUpdatedAt(LocalDate.now());
        return toResponse(repository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        mapRequestToProduct(request, product);
        product.setUpdatedAt(LocalDate.now());
        return toResponse(repository.save(product));
    }

    @Override
    public ProductResponse markOutOfStock(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        product.setStock(0);
        product.setUpdatedAt(LocalDate.now());
        return toResponse(repository.save(product));
    }

    @Override
    public ProductResponse markInStock(Long id, Integer quantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        product.setStock(quantity);
        product.setUpdatedAt(LocalDate.now());
        return toResponse(repository.save(product));
    }

    @Override
    public Optional<ProductResponse> getProductById(Long id) {
        return repository.findById(id).map(this::toResponse);
    }

    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setUnitPrice(request.getUnitPrice());
        product.setExpirationDate(request.getExpirationDate());
        product.setStock(request.getQuantityInStock());
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setUnitPrice(product.getUnitPrice());
        response.setExpirationDate(product.getExpirationDate());
        response.setQuantityInStock(product.getQuantityInStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    private Comparator<Product> getComparator(String sortBy, String direction) {
        Comparator<Product> comparator = switch (sortBy.toLowerCase()) {
            case "category" -> Comparator.comparing(Product::getCategory, String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparing(Product::getUnitPrice);
            case "stock" -> Comparator.comparing(Product::getQuantityInStock);
            case "expirationdate" -> Comparator.comparing(p -> Optional.ofNullable(p.getExpirationDate()).orElse(LocalDate.MAX));
            default -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}
