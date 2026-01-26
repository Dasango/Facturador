package com.uce.emprendimiento.backend.notification;

import com.uce.emprendimiento.backend.util.GeneradorFactura;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class EmailService {

    private final RestTemplate restTemplate;
    private final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

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
            // 1. Preparar datos y PDF
            JSONObject data = new JSONObject(mensajeJson);
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            // 2. Construir el cuerpo de la petición (JSON)
            JSONObject emailRequest = new JSONObject();
            
            // Sender: Usando la variable configurada
            emailRequest.put("sender", new JSONObject()
                    .put("email", senderEmail)
                    .put("name", "Facto Facturación"));
            
            // To: Lista de destinatarios
            JSONArray to = new JSONArray();
            to.put(new JSONObject().put("email", destinatario));
            emailRequest.put("to", to);
            
            emailRequest.put("subject", "Comprobante Electrónico de Facturación");
            emailRequest.put("htmlContent", "<html><body><p>Estimado cliente, adjunto encontrará su factura.</p></body></html>");

            // Attachment
            JSONArray attachments = new JSONArray();
            JSONObject file = new JSONObject();
            file.put("content", pdfBase64);
            file.put("name", "Factura.pdf");
            attachments.put(file);
            emailRequest.put("attachments", attachments);

            // 3. Configurar Headers (Autenticación por API Key)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey.trim());

            HttpEntity<String> entity = new HttpEntity<>(emailRequest.toString(), headers);

            // 4. Ejecutar la llamada HTTP POST
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Éxito Railway: Correo enviado vía API a " + destinatario);
            } else {
                System.err.println("Error API Brevo: " + response.getStatusCode() + " - " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Error crítico en EmailService: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
