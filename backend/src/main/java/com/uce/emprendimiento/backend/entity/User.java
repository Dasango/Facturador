package com.uce.emprendimiento.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false)
    @JsonIgnore
    private String contrasena;

    // --- Campos SRI ---
    @Column(length = 13, unique = true)
    private String ruc;

    @Column(length = 255)
    private String razonSocial;

    @JsonIgnore
    @Column(name = "firma_path")
    private String firmaPath; // Ruta absoluta al archivo .p12

    @JsonIgnore
    @Column(name = "firma_password")
    private String firmaPassword; // Contraseña de la firma (si el usuario la guarda)

    @JsonIgnore
    @Column(name = "logo_path")
    private String logoPath; // Ruta absoluta al archivo del logo

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
