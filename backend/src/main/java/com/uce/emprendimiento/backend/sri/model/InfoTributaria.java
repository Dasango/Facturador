package com.uce.emprendimiento.backend.sri.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
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
public class InfoTributaria {

    @XmlElement(required = true)
    private String ambiente;
    @XmlElement(required = true)
    private String tipoEmision;
    @XmlElement(required = true)
    private String razonSocial;
    private String nombreComercial;
    @XmlElement(required = true)
    private String ruc;
    @XmlElement(required = true)
    private String claveAcceso;
    @XmlElement(required = true)
    private String codDoc;
    @XmlElement(required = true)
    private String estab;
    @XmlElement(required = true)
    private String ptoEmi;
    @XmlElement(required = true)
    private String secuencial;
    @XmlElement(required = true)
    private String dirMatriz;
}
