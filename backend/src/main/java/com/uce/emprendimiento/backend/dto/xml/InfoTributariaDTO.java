package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlType(propOrder = {
        "ambiente",
        "tipoEmision",
        "razonSocial",
        "nombreComercial",
        "ruc",
        "claveAcceso",
        "codDoc",
        "estab",
        "ptoEmi",
        "secuencial",
        "dirMatriz"
})
public class InfoTributariaDTO {
    private String ambiente;
    private String tipoEmision;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String claveAcceso;
    private String codDoc;
    private String estab;
    private String ptoEmi;
    private String secuencial;
    private String dirMatriz;
}
