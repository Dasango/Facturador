package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.entity.Product;
import com.uce.emprendimiento.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getMyProducts() {
        // En un entorno real con seguridad:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // String username = auth.getName();

        // Para este caso, usaremos 'root' por defecto si no hay auth o para simplificar
        // la demo
        // ya que el usuario mencionó "root user".
        String username = "root";

        // Si hay autenticación real, usamos el nombre del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            username = auth.getName();
        }

        return ResponseEntity.ok(productService.getProductsByUser(username));
    }
}
