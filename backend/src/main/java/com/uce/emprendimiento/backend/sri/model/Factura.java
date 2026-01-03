package com.uce.emprendimiento.backend.sri.model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "factura")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "infoTributaria",
        "infoFactura",
        "detalles",
        "infoAdicional"
})
public class Factura {

    @XmlAttribute(required = true)
    private String id; // usually "comprobante"
    @XmlAttribute(required = true)
    private String version;

    @XmlElement(required = true)
    private InfoTributaria infoTributaria;

    @XmlElement(required = true)
    private InfoFactura infoFactura;

    @XmlElementWrapper(name = "detalles")
    @XmlElement(name = "detalle")
    private List<Detalle> detalles;

    @XmlElementWrapper(name = "infoAdicional")
    @XmlElement(name = "campoAdicional")
    private List<CampoAdicional> infoAdicional;

    public Factura() {
        this.version = "1.0.0"; // Or 1.1.0 or 2.1.0 depending on XSD
        this.id = "comprobante"; // Standard ID
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CampoAdicional {
        @XmlAttribute(required = true)
        private String nombre;
        @XmlValue
        private String value;
    }
}
