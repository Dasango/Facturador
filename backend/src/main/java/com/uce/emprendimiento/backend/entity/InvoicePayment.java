package com.uce.emprendimiento.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pagos_factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "forma_pago", nullable = false, length = 2)
    private String formaPago; // Código tabla 24 (ej. "01")

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private BigDecimal plazo;

    @Column(name = "unidad_tiempo", length = 10, nullable = false)
    private String unidadTiempo; // "dias", "meses", etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonIgnore
    private Invoice factura;
}
