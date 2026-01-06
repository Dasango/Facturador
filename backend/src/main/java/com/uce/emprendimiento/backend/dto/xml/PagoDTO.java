package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlType(propOrder = {
        "formaPago",
        "total",
        "plazo",
        "unidadTiempo"
})
public class PagoDTO {
    private String formaPago;
    private String total;
    private String plazo;
    private String unidadTiempo;
}
