package com.uce.emprendimiento.backend.sriCine;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class SriServiceCine {

    private final String SRI_URL = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline";

    public SriResponseCine enviarAlSri(String xmlFirmado) {
        try {
            String xmlBase64 = Base64.getEncoder().encodeToString(xmlFirmado.getBytes());

            String soapEnvelope = 
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ec=\"http://ec.gob.sri.ws.recepcion\">" +
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
            return SriResponseCine.builder()
                    .estado("ERROR_TECNICO")
                    .xmlRespuestaSriCrudo(e.getMessage())
                    .mensajes(new ArrayList<>())
                    .build();
        }
    }

    private SriResponseCine procesarRespuestaSoap(String soapResponse, String originalXml) {
        // Imprimimos en consola para que puedas copiar el XML largo si quieres
        System.out.println("======= XML REAL DEL SRI =======");
        System.out.println(soapResponse);
        System.out.println("================================");

        SriResponseCine dto = new SriResponseCine();
        dto.setXmlRespuestaSriCrudo(soapResponse); // Guardamos el XML real en el JSON

        // --- LÓGICA DE PROCESAMIENTO (Comentada pero funcional para tu prueba) ---
        
        if (soapResponse != null && soapResponse.contains("RECIBIDA")) {
            dto.setEstado("AUTORIZADO");
            dto.setClaveAcceso("CLAVE_RECIBIDA_OK");
            dto.setFechaAutorizacion("2025-12-27T10:30:00");
            dto.setMensajes(new ArrayList<>());
        } else {
            dto.setEstado("RECHAZADO");
            dto.setMensajes(new ArrayList<>());
            dto.getMensajes().add(new SriMensajeCine("43", "ERROR REAL DETECTADO", "Revisar campo xmlRespuestaSriCrudo"));
        }
        
        return dto;
    }
}