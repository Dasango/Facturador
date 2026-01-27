package com.uce.emprendimiento.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
@Getter
@Setter
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
    private String estado; // RECIBIDA O NO

    // --- Campos SRI ---
    @Column(name = "clave_acceso", length = 49, unique = true)
    private String claveAcceso;

    @Column(name = "xml_content", columnDefinition = "TEXT")
    private String xmlContent;

    @Column(name = "mensaje_sri", columnDefinition = "TEXT")
    private String mensajeSri; // Para guardar errores o confirmaciones

    @Column(name = "fecha_autorizacion")
    private java.time.LocalDateTime fechaAutorizacion;

    // --- Campos Adicionales SRI ---
    @Column(name = "dir_establecimiento")
    private String direccionEstablecimiento; // Si es sucursal

    @Column(name = "tipo_identificacion_comprador", length = 2)
    private String tipoIdentificacionComprador; // Tabla 6

    @Column(name = "direccion_comprador")
    private String direccionComprador;

    // Totales y Desgloses
    @Column(name = "total_sin_impuestos")
    private Double totalSinImpuestos;

    @Column(name = "total_descuento")
    private Double totalDescuento;

    @Column(name = "propina")
    private Double propina;

    @Column(name = "moneda", length = 15)
    private String moneda = "DOLAR"; // Default

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<InvoicePayment> pagos = new java.util.ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private User usuario;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<InvoiceDetail> detalles = new java.util.ArrayList<>();

    // Info Adicional (Email, Telefono, etc.)
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<InvoiceAdditionalInfo> infoAdicional = new java.util.ArrayList<>();
}
