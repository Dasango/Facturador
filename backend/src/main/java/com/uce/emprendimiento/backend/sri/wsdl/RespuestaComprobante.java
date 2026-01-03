package com.uce.emprendimiento.backend.sri.wsdl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "RespuestaComprobante", namespace = "http://ec.gob.sri.ws.autorizacion")
@XmlAccessorType(XmlAccessType.FIELD)
public class RespuestaComprobante {

    private String claveAccesoConsultada;
    private String numeroComprobantes;
    
    @XmlElementWrapper(name = "autorizaciones")
    @XmlElement(name = "autorizacion")
    private List<Autorizacion> autorizaciones;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Autorizacion {
        private String estado;
        private String numeroAutorizacion;
        private String fechaAutorizacion;
        private String ambiente;
        private String comprobante; // The XML itself
        
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
