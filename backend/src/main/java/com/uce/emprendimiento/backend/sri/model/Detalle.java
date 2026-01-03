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
        "codigoPrincipal",
        "codigoAuxiliar",
        "descripcion",
        "cantidad",
        "precioUnitario",
        "descuento",
        "precioTotalSinImpuesto",
        "impuestos"
})
public class Detalle {

    @XmlElement(required = true)
    private String codigoPrincipal;
    private String codigoAuxiliar;
    @XmlElement(required = true)
    private String descripcion;
    @XmlElement(required = true)
    private BigDecimal cantidad;
    @XmlElement(required = true)
    private BigDecimal precioUnitario;
    @XmlElement(required = true)
    private BigDecimal descuento;
    @XmlElement(required = true)
    private BigDecimal precioTotalSinImpuesto;

    @XmlElementWrapper(name = "impuestos")
    @XmlElement(name = "impuesto")
    private List<Impuesto> impuestos;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Impuesto {
        @XmlElement(required = true)
        private String codigo;
        @XmlElement(required = true)
        private String codigoPorcentaje;
        @XmlElement(required = true)
        private BigDecimal tarifa;
        @XmlElement(required = true)
        private BigDecimal baseImponible;
        @XmlElement(required = true)
        private BigDecimal valor;
    }
}
