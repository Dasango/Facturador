package com.uce.emprendimiento.backend.repository;

import com.uce.emprendimiento.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCorreo(String correo);

    Optional<User> findByCedula(String cedula);

    boolean existsByCorreo(String correo);

    boolean existsByCedula(String cedula);
}
