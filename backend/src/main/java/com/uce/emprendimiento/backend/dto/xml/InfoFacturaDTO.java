package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import java.util.List;

@Data
@XmlType(propOrder = {
        "fechaEmision",
        "dirEstablecimiento",
        "obligadoContabilidad",
        "tipoIdentificacionComprador",
        "razonSocialComprador",
        "identificacionComprador",
        "direccionComprador",
        "totalSinImpuestos",
        "totalDescuento",
        "totalConImpuestos",
        "propina",
        "importeTotal",
        "moneda",
        "pagos"
})
public class InfoFacturaDTO {
    private String fechaEmision;
    private String dirEstablecimiento;
    private String obligadoContabilidad;
    private String tipoIdentificacionComprador; // 04, 05, etc
    private String razonSocialComprador;
    private String identificacionComprador;
    private String direccionComprador;
    private String totalSinImpuestos;
    private String totalDescuento;

    @XmlElementWrapper(name = "totalConImpuestos")
    @XmlElement(name = "totalImpuesto")
    private List<TotalImpuestoDTO> totalConImpuestos;

    private String propina;
    private String importeTotal;
    private String moneda;

    @XmlElementWrapper(name = "pagos")
    @XmlElement(name = "pago")
    private List<PagoDTO> pagos;
}
