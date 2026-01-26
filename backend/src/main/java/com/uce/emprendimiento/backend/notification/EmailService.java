package com.uce.emprendimiento.backend.notification;

import com.uce.emprendimiento.backend.util.GeneradorFactura;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class EmailService {

    private final RestTemplate restTemplate;
    
    // Tu API Key de Brevo (V3)
    private final String BREVO_API_KEY = "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"; // Reemplaza por la completa
    private final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public EmailService() {
        this.restTemplate = new RestTemplate();
    }

    @Async
    public void enviarNotificacionFactura(String destinatario, String mensajeJson) {
        if (destinatario == null || "No hay correo".equals(destinatario) || !destinatario.contains("@")) {
            System.out.println("Omitiendo envío: Correo inválido.");
            return;
        }

        try {
            // 1. Generar el PDF
            JSONObject data = new JSONObject(mensajeJson);
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            // 2. Construir el JSON para la API de Brevo
            JSONObject emailRequest = new JSONObject();
            
            // Emisor
            emailRequest.put("sender", new JSONObject().put("email", "sdeddxd@gmail.com").put("name", "Facto Facturación"));
            
            // Destinatario (es una lista)
            JSONArray to = new JSONArray();
            to.put(new JSONObject().put("email", destinatario));
            emailRequest.put("to", to);
            
            emailRequest.put("subject", "Comprobante Electrónico de Facturación");
            emailRequest.put("htmlContent", "<html><body><p>Estimado cliente, adjunto encontrará su <strong>factura electrónica</strong> en formato PDF.</p></body></html>");

            // Adjunto (Base64)
            JSONArray attachments = new JSONArray();
            JSONObject file = new JSONObject();
            file.put("content", pdfBase64);
            file.put("name", "Factura.pdf");
            attachments.put(file);
            emailRequest.put("attachments", attachments);

            // 3. Configurar Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", BREVO_API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(emailRequest.toString(), headers);

            // 4. Enviar vía POST
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Éxito: Correo enviado vía API HTTP a " + destinatario);
            } else {
                System.err.println("Error API Brevo: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Error enviando correo vía API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
