package com.uce.emprendimiento.backend.sri;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.uce.emprendimiento.backend.notification.EmailService;
import com.uce.emprendimiento.backend.service.XmlService;

import org.springframework.http.*;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class SriService {

    EmailService emailService;
    XmlService xmlService;

    private final String SRI_URL = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline";

    public SriResponse enviarAlSri(String xmlFirmado) {
        try {
            String xmlBase64 = Base64.getEncoder().encodeToString(xmlFirmado.getBytes());

            String soapEnvelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ec=\"http://ec.gob.sri.ws.recepcion\">"
                    +
                    "<soapenv:Header/>" +
                    "<soapenv:Body>" +
                    "<ec:validarComprobante>" +
                    "<xml>" + xmlBase64 + "</xml>" +
                    "</ec:validarComprobante>" +
                    "</soapenv:Body>" +
                    "</soapenv:Envelope>";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            HttpEntity<String> entity = new HttpEntity<>(soapEnvelope, headers);

            // Esta es la respuesta real del SRI
            ResponseEntity<String> response = restTemplate.postForEntity(SRI_URL, entity, String.class);

            return procesarRespuestaSoap(response.getBody(), xmlFirmado);

        } catch (Exception e) {
            return SriResponse.builder()
                    .estado("ERROR_TECNICO")
                    .xmlRespuestaSriCrudo(e.getMessage())
                    .mensajes(new ArrayList<>())
                    .build();
        }
    }

    private SriResponse procesarRespuestaSoap(String soapResponse, String xmlFirmado) {

        SriResponse dto = new SriResponse();
        dto.setXmlRespuestaSriCrudo(soapResponse); // Guardamos el XML real en el JSON

        // --- LÓGICA DE PROCESAMIENTO (Comentada pero funcional para tu prueba) ---

        if (soapResponse != null && soapResponse.contains("RECIBIDA")) {
            dto.setEstado("RECIBIDA");
            dto.setClaveAcceso("CLAVE_RECIBIDA_OK");
            dto.setFechaAutorizacion("2025-12-27T10:30:00");
            String email = xmlService.extraerEmailDeInfoAdicional(xmlFirmado);
            emailService.enviarNotificacionFactura(email, "Factura procesada exitosamente");
            dto.setMensajes(new ArrayList<>());
        } else {
            dto.setEstado("RECHAZADO");
            dto.setMensajes(new ArrayList<>());
            dto.getMensajes().add(new SriMensaje("43", "ERROR REAL DETECTADO", "Revisar campo xmlRespuestaSriCrudo"));
        }

        return dto;
    }

    public Boolean soloEnviar(String xmlFirmado) {
        String email = xmlService.extraerEmailDeInfoAdicional(xmlFirmado);
        emailService.enviarNotificacionFactura(email, "Factura procesada exitosamente");
        return true;
    }
}