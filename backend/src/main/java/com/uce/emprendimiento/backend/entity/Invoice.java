package com.uce.emprendimiento.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_comprobante")
    private String numeroComprobante;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_identificacion")
    private String clienteIdentificacion;

    private Double total;
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;
}
