package com.products.backend.service.product;
import com.products.backend.dto.product.*;
import com.products.backend.model.Category;

import java.util.List;
import java.util.Optional;

public interface IProductService {
 PaginatedProducts getAllProducts(
            String name,
            String category,
            Boolean available,
            String sortBy,
            String direction,
            int page,
            int size
    );

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    ProductResponse markOutOfStock(Long id);

    ProductResponse markInStock(Long id, Integer quantity);
      ProductResponse deleteProductById(Long id);
    Optional<ProductResponse> getProductById(Long id);

    List<Category> getGeneralMetrics();
}
