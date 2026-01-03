package com.uce.emprendimiento.backend.sri.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "fechaEmision",
        "dirEstablecimiento",
        "contribuyenteEspecial",
        "obligadoContabilidad",
        "tipoIdentificacionComprador",
        "guiaRemision",
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
public class InfoFactura {

    @XmlElement(required = true)
    private String fechaEmision;
    @XmlElement(required = true)
    private String dirEstablecimiento;
    private String contribuyenteEspecial;
    private String obligadoContabilidad;
    @XmlElement(required = true)
    private String tipoIdentificacionComprador;
    private String guiaRemision;
    @XmlElement(required = true)
    private String razonSocialComprador;
    @XmlElement(required = true)
    private String identificacionComprador;
    private String direccionComprador;
    @XmlElement(required = true)
    private BigDecimal totalSinImpuestos;
    @XmlElement(required = true)
    private BigDecimal totalDescuento;

    @XmlElementWrapper(name = "totalConImpuestos")
    @XmlElement(name = "totalImpuesto")
    private List<TotalImpuesto> totalConImpuestos;

    @XmlElement(required = true)
    private BigDecimal propina;
    @XmlElement(required = true)
    private BigDecimal importeTotal;
    private String moneda;

    @XmlElementWrapper(name = "pagos")
    @XmlElement(name = "pago")
    private List<Pago> pagos;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalImpuesto {
        @XmlElement(required = true)
        private String codigo;
        @XmlElement(required = true)
        private String codigoPorcentaje;
        @XmlElement(required = true)
        private BigDecimal baseImponible;
        @XmlElement(required = true)
        private BigDecimal valor; // valor del impuesto
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Pago {
        @XmlElement(required = true)
        private String formaPago;
        @XmlElement(required = true)
        private BigDecimal total;
        private BigDecimal plazo;
        private String unidadTiempo;
    }
}
