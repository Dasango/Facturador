package com.uce.emprendimiento.backend.sri.service;

import com.uce.emprendimiento.backend.sri.wsdl.AutorizacionComprobante;
import com.uce.emprendimiento.backend.sri.wsdl.RespuestaComprobante;
import com.uce.emprendimiento.backend.sri.wsdl.RespuestaSolicitud;
import com.uce.emprendimiento.backend.sri.wsdl.ValidarComprobante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.net.URI;

@Service
public class SriSoapService {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    // URLs for "Pruebas" environment by default
    private static final String URL_RECEPCION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline";
    private static final String URL_AUTORIZACION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline";

    public RespuestaSolicitud enviarFactura(byte[] xmlFirmado) {
        ValidarComprobante request = new ValidarComprobante();
        request.setXml(xmlFirmado);

        return (RespuestaSolicitud) webServiceTemplate.marshalSendAndReceive(URL_RECEPCION, request);
    }

    public RespuestaComprobante autorizarFactura(String claveAcceso) {
        AutorizacionComprobante request = new AutorizacionComprobante();
        request.setClaveAccesoComprobante(claveAcceso);

        return (RespuestaComprobante) webServiceTemplate.marshalSendAndReceive(URL_AUTORIZACION, request);
    }
}
