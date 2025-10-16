package com.products.backend.service.product.filter;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory for creating product filters based on query parameters
 */
@Component
public class ProductFilterFactory {

    public IProductFilter createFilter(String name, String category, Boolean available) {
        List<IProductFilter> filters = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            filters.add(new NameFilter(name));
        }

        if (category != null && !category.isBlank()) {
            Set<Long> categoryIds = parseCategoryIds(category);
            if (!categoryIds.isEmpty()) {
                filters.add(new CategoryFilter(categoryIds));
            }
        }

        if (available != null) {
            filters.add(new AvailabilityFilter(available));
        }

        return new CompositeProductFilter(filters);
    }

    private Set<Long> parseCategoryIds(String category) {
        try {
            return Arrays.stream(category.split("-"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        } catch (NumberFormatException e) {
            return Set.of();
        }
    }
}