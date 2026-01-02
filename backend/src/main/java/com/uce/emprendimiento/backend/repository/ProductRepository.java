package com.uce.emprendimiento.backend.repository;

import com.uce.emprendimiento.backend.entity.Product;
import com.uce.emprendimiento.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUsuario(User usuario);
}
