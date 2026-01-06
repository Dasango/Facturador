package com.uce.emprendimiento.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Impuestos SRI
    @Column(name = "codigo_impuesto", nullable = false)
    private String codigoImpuesto; // 2 (IVA), 3 (ICE), etc.

    @Column(name = "codigo_porcentaje", nullable = false)
    private String codigoPorcentaje; // 0, 2, 3, etc. (Tarifa 0%, 12%, 14%)

    @Column(nullable = false)
    private Double tarifa; // 0.0, 12.0, 15.0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private User usuario;
}
