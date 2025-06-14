package com.products.backend.service.product;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.model.Product;
import com.products.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService{
    // Getting the Repository to store the temporary data
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    
    private ProductResponse setResponseProduct(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setUnitPrice(product.getUnitPrice());
        response.setExpirationDate(product.getExpirationDate());
        response.setQuantityInStock(product.getStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
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
                .filter(p -> category.map(c -> p.getCategory().getName().equalsIgnoreCase(c)).orElse(true))
                .filter(p -> available.map(a -> a ? p.getStock() > 0 : p.getStock() == 0).orElse(true))
                .sorted(comparator)
                .skip((long) page * size)
                .limit(size)
                .map(this::setResponseProduct)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        mapRequestToProduct(request, product);
        product.setCreatedAt(LocalDate.now());
        product.setUpdatedAt(LocalDate.now());
        return setResponseProduct(repository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        mapRequestToProduct(request, product);
        product.setUpdatedAt(LocalDate.now());
        return setResponseProduct(repository.save(product));
    }

    @Override
    public ProductResponse markOutOfStock(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        product.setStock(0);
        product.setUpdatedAt(LocalDate.now());
        return setResponseProduct(repository.save(product));
    }

    @Override
    public ProductResponse markInStock(Long id, Integer quantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        product.setStock(quantity);
        product.setUpdatedAt(LocalDate.now());
        return setResponseProduct(repository.save(product));
    }

    @Override
    public Optional<ProductResponse> getProductById(Long id) {
        return repository.findById(id).map(this::setResponseProduct);
    }

    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setUnitPrice(request.getUnitPrice());
        product.setExpirationDate(request.getExpirationDate());
        product.setStock(request.getQuantityInStock());
    }

    private Comparator<Product> getComparator(String sortBy, String direction) {
        Comparator<Product> comparator = switch (sortBy.toLowerCase()) {
            case "category" -> Comparator.comparing((product) -> product.getCategory().getName(), String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparing(Product::getUnitPrice);
            case "stock" -> Comparator.comparing(Product::getStock);
            case "expirationdate" -> Comparator.comparing(product -> Optional.ofNullable(product.getExpirationDate()).orElse(LocalDate.MAX));
            default -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}
