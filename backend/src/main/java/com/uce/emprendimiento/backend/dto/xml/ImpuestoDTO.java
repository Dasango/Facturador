package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "codigo",
        "codigoPorcentaje",
        "tarifa", // Sólo en Detalle
        "baseImponible",
        "valor"
})
public class ImpuestoDTO {
    private String codigo; // 2 -> IVA
    private String codigoPorcentaje; // 0, 2, 3
    private String tarifa; // Opcional en TotalImpuesto
    private String baseImponible;
    private String valor;
}
