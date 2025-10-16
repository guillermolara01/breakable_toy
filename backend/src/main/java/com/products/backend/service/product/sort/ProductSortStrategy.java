package com.products.backend.service.product.sort;

import com.products.backend.model.Product;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

/**
 * Strategy for creating product comparators based on sort criteria
 */
@Component
public class ProductSortStrategy {

    public Comparator<Product> createComparator(String sortBy, String direction) {
        String effectiveSortBy = sortBy != null && !sortBy.isEmpty() ? sortBy : "name";
        String effectiveDirection = direction != null && !direction.isEmpty() ? direction : "asc";

        String[] sortFields = effectiveSortBy.split("-");
        String[] directions = effectiveDirection.split("-");

        Comparator<Product> combined = buildComparator(sortFields[0],
                directions.length > 0 ? directions[0] : "asc");

        for (int i = 1; i < sortFields.length; i++) {
            String field = sortFields[i];
            String dir = i < directions.length ? directions[i] : "asc";
            combined = combined.thenComparing(buildComparator(field, dir));
        }

        return combined;
    }

    private Comparator<Product> buildComparator(String sortBy, String direction) {
        Comparator<Product> comparator = switch (sortBy.toLowerCase()) {
            case "category" -> Comparator.comparing(
                    product -> product.getCategory().getName(),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "price" -> Comparator.comparing(Product::getUnitPrice);
            case "stock" -> Comparator.comparing(Product::getStock);
            case "expirationdate" -> Comparator.comparing(
                    product -> Optional.ofNullable(product.getExpirationDate())
                            .orElse(LocalDate.MAX)
            );
            default -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}