package com.uce.emprendimiento.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <--- Importante
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "detalles_factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    private Double descuento;

    private Double subtotal;

    @Column(name = "valor_impuesto")
    private Double valorImpuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonIgnore
    private Invoice factura;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = true)
    private Product producto;

    // --- Snapshot Data (Para integridad histórica) ---
    @Column(name = "producto_codigo_principal")
    private String codigoPrincipal;

    @Column(name = "producto_codigo_auxiliar")
    private String codigoAuxiliar;

    @Column(name = "producto_nombre")
    private String nombreProducto;
}