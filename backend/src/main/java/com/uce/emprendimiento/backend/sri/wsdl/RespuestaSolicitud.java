package com.uce.emprendimiento.backend.sri.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "RespuestaSolicitud", namespace = "http://ec.gob.sri.ws.recepcion")
@XmlAccessorType(XmlAccessType.FIELD)
public class RespuestaSolicitud {

    private String estado;

    @XmlElementWrapper(name = "comprobantes")
    @XmlElement(name = "comprobante")
    private List<Comprobante> comprobantes;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Comprobante {
        private String claveAcceso;
        
        @XmlElementWrapper(name = "mensajes")
        @XmlElement(name = "mensaje")
        private List<Mensaje> mensajes;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Mensaje {
        private String identificador;
        private String mensaje;
        private String informacionAdicional;
        private String tipo;
    }
}
