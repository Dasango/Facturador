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
