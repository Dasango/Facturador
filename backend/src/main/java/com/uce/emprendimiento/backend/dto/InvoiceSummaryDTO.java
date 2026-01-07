package com.uce.emprendimiento.backend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSummaryDTO {
    private Long id;
    private String numeroComprobante;
    private LocalDate fechaEmision;
    private String clienteNombre;
    private String clienteIdentificacion;
    private Double total;
    private String estado;
}
