package com.products.backend.service.product.filter;
import com.products.backend.model.Product;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Composite filter that combines multiple filters with AND logic
 */
public class CompositeProductFilter implements IProductFilter {
    private final java.util.List<IProductFilter> filters;

    public CompositeProductFilter(java.util.List<IProductFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate<Product> buildPredicate() {
        return filters.stream()
                .map(IProductFilter::buildPredicate)
                .reduce(Predicate::and)
                .orElse(p -> true);
    }
}

/**
 * Filter products by name (case-insensitive partial match)
 */
class NameFilter implements IProductFilter {
    private final String name;

    public NameFilter(String name) {
        this.name = name;
    }

    @Override
    public Predicate<Product> buildPredicate() {
        if (name == null || name.isBlank()) {
            return p -> true;
        }
        return product -> product.getName()
                .toLowerCase()
                .contains(name.toLowerCase());
    }
}

/**
 * Filter products by category IDs
 */
class CategoryFilter implements IProductFilter {
    private final Set<Long> categoryIds;

    public CategoryFilter(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    @Override
    public Predicate<Product> buildPredicate() {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return p -> true;
        }
        return product -> categoryIds.contains(product.getCategory().getId());
    }
}

/**
 * Filter products by availability (stock > 0 or stock == 0)
 */
class AvailabilityFilter implements IProductFilter {
    private final Boolean available;

    public AvailabilityFilter(Boolean available) {
        this.available = available;
    }

    @Override
    public Predicate<Product> buildPredicate() {
        if (available == null) {
            return p -> true;
        }
        return product -> available ? product.getStock() > 0 : product.getStock() == 0;
    }
}