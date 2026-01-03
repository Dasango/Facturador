package com.uce.emprendimiento.backend.sri.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "validarComprobante", namespace = "http://ec.gob.sri.ws.recepcion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidarComprobante {

    @XmlElement(name = "xml")
    private byte[] xml;
}
