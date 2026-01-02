package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.entity.Product;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.ProductRepository;
import com.uce.emprendimiento.backend.repository.UserRepository;
import com.uce.emprendimiento.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<Product> getProductsByUser(String username) {
        // Asumiendo que 'username' es la cédula o el correo.
        // Buscaremos por cédula primero, si no por correo.
        User user = userRepository.findByCedula(username)
                .orElseGet(() -> userRepository.findByCorreo(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username)));

        return productRepository.findByUsuario(user);
    }
}
