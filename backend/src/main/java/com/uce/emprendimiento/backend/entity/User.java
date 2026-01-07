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
    @Column(nullable = false, length = 13, unique = true)
    private String ruc;

    @Column(length = 300)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 300)
    private String nombreComercial;

    @Column(name = "dir_matriz", length = 300)
    private String direccionMatriz;

    @Column(name = "codigo_establecimiento", length = 3)
    private String codigoEstablecimiento; // e.g. "001"

    @Column(name = "codigo_punto_emision", length = 3)
    private String codigoPuntoEmision; // e.g. "002"

    @Column(name = "obligado_contabilidad", length = 2)
    private String obligadoContabilidad; // "SI" / "NO"

    @Column(name = "nro_contribuyente_especial", length = 13)
    private String nroContribuyenteEspecial;

    @JsonIgnore
    @Column(name = "firma_path")
    private String firmaPath; // Ruta absoluta al archivo .p12

    @JsonIgnore
    @Column(name = "firma_password")
    private String firmaPassword; // Contraseña de la firma (si el usuario la guarda)

    @Column(name = "logo_path")
    private String logoPath; // Ruta absoluta al archivo del logo

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    public boolean getTieneFirma() {
        return firmaPath != null && !firmaPath.isEmpty();
    }
}
