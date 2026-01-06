package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import java.util.List;

@Data
@XmlType(propOrder = {
        "codigoPrincipal",
        "codigoAuxiliar",
        "descripcion",
        "cantidad",
        "precioUnitario",
        "descuento",
        "precioTotalSinImpuesto",
        "impuestos"
})
public class DetalleDTO {
    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String descripcion;
    private String cantidad;
    private String precioUnitario;
    private String descuento;
    private String precioTotalSinImpuesto;

    @XmlElementWrapper(name = "impuestos")
    @XmlElement(name = "impuesto")
    private List<ImpuestoDTO> impuestos;
}
