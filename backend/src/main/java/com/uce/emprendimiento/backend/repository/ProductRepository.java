package com.uce.emprendimiento.backend.repository;

import com.uce.emprendimiento.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Busca productos donde el campo 'usuario' tenga el 'id' especificado
    List<Product> findByUsuario_Id(Long usuarioId);
}