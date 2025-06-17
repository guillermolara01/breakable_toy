package com.products.backend.service.product;
import com.products.backend.dto.product.*;

import java.util.List;
import java.util.Optional;

public interface IProductService {
 List<ProductResponse> getAllProducts(
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

    Optional<ProductResponse> getProductById(Long id);
}
