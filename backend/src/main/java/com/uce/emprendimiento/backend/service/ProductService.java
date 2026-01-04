package com.uce.emprendimiento.backend.service;

import com.uce.emprendimiento.backend.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getProductsByUserId(Long userId);

    Product getProductByIdAndUser(Long productId, Long userId);

    Product updateProduct(Long productId, Product productDetails, Long userId);

    void deleteProduct(Long productId, Long userId);

    Product createProduct(Product product, Long userId);
}
