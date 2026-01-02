package com.uce.emprendimiento.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_principal", nullable = false)
    private String codigoPrincipal;

    @Column(name = "codigo_auxiliar")
    private String codigoAuxiliar;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "valor_unitario", nullable = false)
    private Double valorUnitario;

    private String iva;
    private String ice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;
}
