package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.entity.Product;
import com.uce.emprendimiento.backend.security.CustomUserDetails;
import com.uce.emprendimiento.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getMyProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();
        return ResponseEntity.ok(productService.getProductsByUserId(userDetails.getUser().getId()));
    }

    // 1. Obtener uno solo
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        try {
            Product product = productService.getProductByIdAndUser(id, userDetails.getUser().getId());
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 2. Editar (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
            @RequestBody Product product,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        try {
            Product updated = productService.updateProduct(id, product, userDetails.getUser().getId());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Manejo básico de error (podría ser 403 Forbidden o 404 Not Found)
            return ResponseEntity.badRequest().build();
        }
    }

    // 3. Eliminar (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        try {
            productService.deleteProduct(id, userDetails.getUser().getId());
            return ResponseEntity.noContent().build(); // 204 No Content es el estándar para delete
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        Product newProduct = productService.createProduct(product, userDetails.getUser().getId());
        return ResponseEntity.ok(newProduct);
    }
}