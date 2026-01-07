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
        "baseImponible",
        "valor"
})
public class TotalImpuestoDTO {
    private String codigo;
    private String codigoPorcentaje;
    private String baseImponible;
    private String valor;
}
