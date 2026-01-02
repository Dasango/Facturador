package com.uce.emprendimiento.backend.service;

import com.uce.emprendimiento.backend.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getProductsByUser(String username);
}
