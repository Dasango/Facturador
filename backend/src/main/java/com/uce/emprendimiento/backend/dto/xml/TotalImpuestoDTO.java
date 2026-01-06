package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
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
