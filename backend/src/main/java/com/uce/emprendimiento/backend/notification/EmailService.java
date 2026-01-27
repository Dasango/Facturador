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
            // 1. Parsear el JSON recibido para generar el PDF
            JSONObject data = new JSONObject(mensajeJson);
            
            // Generar los bytes del PDF usando tu utilidad
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);
            
            // Convertir a Base64 (Brevo requiere esto para los adjuntos)
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            // 2. Construir el objeto principal de la petición para Brevo
            JSONObject emailRequest = new JSONObject();
            
            // Emisor
            emailRequest.put("sender", new JSONObject()
                    .put("email", senderEmail)
                    .put("name", "Facto Facturación"));
            
            // Destinatario
            JSONArray to = new JSONArray();
            to.put(new JSONObject().put("email", destinatario));
            emailRequest.put("to", to);
            
            // Asunto y Contenido
            emailRequest.put("subject", "Comprobante Electrónico de Facturación");
            emailRequest.put("htmlContent", "<html><body>" +
                    "<h3>Su factura electrónica está lista</h3>" +
                    "<p>Estimado cliente, adjunto a este correo encontrará su comprobante en formato PDF.</p>" +
                    "<p>Gracias por usar nuestro servicio.</p>" +
                    "</body></html>");

            // 3. SECCIÓN DE ADJUNTOS (Asegúrate de que esta estructura sea exacta)
            JSONArray attachments = new JSONArray();
            JSONObject attachment = new JSONObject();
            attachment.put("content", pdfBase64);    // El contenido en Base64
            attachment.put("name", "Factura.pdf");   // Nombre del archivo
            attachments.put(attachment);
            
            emailRequest.put("attachment", attachments); // Ojo: Brevo usa "attachment" (singular) en su JSON API

            // 4. Configurar Headers de autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey.trim());

            HttpEntity<String> entity = new HttpEntity<>(emailRequest.toString(), headers);

            // 5. Enviar la petición POST
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Éxito en Railway: Correo con PDF enviado a " + destinatario);
            } else {
                System.err.println("Error API Brevo: " + response.getStatusCode() + " - " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Error al procesar o enviar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
