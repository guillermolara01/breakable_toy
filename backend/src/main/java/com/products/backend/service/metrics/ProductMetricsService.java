package com.products.backend.service.metrics;

import com.products.backend.classes.metrics.Metric;
import com.products.backend.model.Category;
import com.products.backend.model.Product;
import com.products.backend.repository.CategoryRepository;
import com.products.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for calculating product metrics
 * Separated from ProductService to follow Single Responsibility Principle
 */
@Service
public class ProductMetricsService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductMetricsService(ProductRepository productRepository,
                                 CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Metric> getGeneralMetrics() {
        List<Metric> metrics = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();

        int overallQuantity = 0;
        double overallValue = 0.0;

        for (Category category : categories) {
            List<Product> categoryProducts = productRepository.findAll().stream()
                    .filter(product -> product.getCategory().getId() == category.getId())
                    .toList();

            int quantity = categoryProducts.stream()
                    .mapToInt(Product::getStock)
                    .sum();

            double value = categoryProducts.stream()
                    .mapToDouble(product -> product.getStock() * product.getUnitPrice())
                    .sum();

            double averagePrice = quantity > 0 ? value / quantity : 0.0;

            overallQuantity += quantity;
            overallValue += value;

            metrics.add(new Metric(category, quantity, value, averagePrice));
        }

        double overallAveragePrice = overallQuantity > 0 ? overallValue / overallQuantity : 0.0;
        Category overallCategory = new Category();
        overallCategory.setName("Overall");
        metrics.add(new Metric(overallCategory, overallQuantity, overallValue, overallAveragePrice));

        return metrics;
    }
}