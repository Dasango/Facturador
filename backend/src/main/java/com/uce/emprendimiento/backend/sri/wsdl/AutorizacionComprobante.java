package com.uce.emprendimiento.backend.sri.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "autorizacionComprobante", namespace = "http://ec.gob.sri.ws.autorizacion")
@XmlAccessorType(XmlAccessType.FIELD)
public class AutorizacionComprobante {

    @XmlElement(name = "claveAccesoComprobante")
    private String claveAccesoComprobante;
}
