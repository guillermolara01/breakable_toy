package com.products.backend.service.product;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.products.backend.classes.metrics.Metric;
import com.products.backend.dto.product.MetricsResponse;
import com.products.backend.dto.product.PaginatedProducts;
import com.products.backend.dto.product.ProductRequest;
import com.products.backend.dto.product.ProductResponse;
import com.products.backend.model.Category;
import com.products.backend.model.Product;
import com.products.backend.repository.CategoryRepository;
import com.products.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService{
    // Getting the Repository to store the temporary data
    private final ProductRepository repository;
    private  final CategoryRepository categories;
    public ProductService(ProductRepository repository, CategoryRepository categories) {
        this.repository = repository;
        this.categories = categories;
    }
    
    
    private ProductResponse setResponseProduct(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setUnitPrice(product.getUnitPrice());
        response.setExpirationDate(product.getExpirationDate());
        response.setStock(product.getStock());
        //response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
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
        sortBy = sortBy != null && !sortBy.isEmpty() ? sortBy : "name";
        direction = direction != null && !direction.isEmpty() ? direction : "asc";
        Comparator<Product> comparator = getMultiComparator(sortBy, direction);

        Set<Long> selectedCategories;
        if(category != null && !category.isBlank()){
            String [] categories = category.split("-");
            selectedCategories = Arrays.stream(categories).map(Long::parseLong).collect(Collectors.toSet());
        } else {
            selectedCategories = Set.of();
        }
        Long all = repository.findAll().stream().count();

        List<ProductResponse> filteredProducts =  repository.findAll().stream()
                .filter(product -> {
                        return name == null || name.isBlank()|| product.getName().toLowerCase().contains(name.toLowerCase());
                })
                .filter(
                        product -> {
                            return category == null || category.isBlank() || selectedCategories.contains(product.getCategory().getId());
                        }
                )
                .filter(product -> {
                    if(available == null) return  true; // if not available filter, we return anything.
                    return available ? product.getStock() > 0 : product.getStock() == 0;
                })
                .sorted(comparator)
                .skip((long) page * size)
                .limit(size)
                .map(this::setResponseProduct)
                .toList();
        PaginatedProducts paginated = new PaginatedProducts(filteredProducts, page, size, all.intValue());
        return  paginated;
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

    @Override
    public ProductResponse deleteProductById(Long id){
        Product deleted = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        this.repository.delete(id);
        return  setResponseProduct(deleted);
    }

    @Override
    public List<Metric> getGeneralMetrics(){
        List<Metric> metrics = new ArrayList<Metric>();
        List<Category> cats = this.categories.findAll();
        int overallQuantity = 0;
        double overallValue = 0;
        for(Category category: cats){
            List<Product> categoryProducts = this.repository.findAll().stream().filter(product -> product.getCategory().getId() == category.getId()).toList();
            int quantity = categoryProducts.stream().map(Product::getStock).reduce(0, Integer::sum);
            double value = categoryProducts.stream().map(product -> {return  product.getStock() * product.getUnitPrice();}).reduce(0.0, Double::sum);
            double averagePrice = quantity > 0 ? value / quantity : 0;
            overallQuantity+= quantity;
            overallValue += value;

            Metric categoryMetric = new Metric(category, quantity, value, averagePrice);
            metrics.add(categoryMetric);
        }
        double overallAveragePrice = overallQuantity > 0 ? overallValue / overallQuantity : 0;
        Category overallCategory = new Category();
        overallCategory.setName("Overall");
        Metric overalMetric = new Metric(overallCategory, overallQuantity, overallValue, overallAveragePrice);
        metrics.add(overalMetric);
        return metrics;
    }

    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setUnitPrice(request.getUnitPrice());
        product.setExpirationDate(request.getExpirationDate());
        product.setStock(request.getStock());
    }

    private Comparator<Product> getMultiComparator(String sortByRaw, String directionRaw) {
        String[] sortFields = sortByRaw != null ? sortByRaw.split("-") : new String[]{"name"};
        String[] directions = directionRaw != null ? directionRaw.split("-") : new String[]{"asc"};

        Comparator<Product> combined = getComparator(sortFields[0], directions.length > 0 ? directions[0] : "asc");

        if (sortFields.length > 1) {
            for (int i = 1; i < sortFields.length; i++) {
                String field = sortFields[i];
                String dir = i < directions.length ? directions[i] : "asc";
                combined = combined.thenComparing(getComparator(field, dir));
            }
        }

        return combined;
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
