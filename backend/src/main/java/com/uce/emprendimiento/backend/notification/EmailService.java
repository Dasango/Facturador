package com.uce.emprendimiento.backend.notification;

import com.uce.emprendimiento.backend.util.GeneradorFactura;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Base64;
import java.util.Map;
import java.util.List;

@Service
public class EmailService {

    @Value("${MAIL_PASSWORD}") // Tu API Key de Resend (re_...)
    private String apiKey;

    private final WebClient webClient;

    // Inicializamos WebClient directamente aquí para no necesitar otra clase Config
    public EmailService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }

    @Async
    public void enviarNotificacionFactura(String destinatario, String mensajeJson) {
        // Validaciones básicas
        if (destinatario == null || "No hay correo".equals(destinatario) || !destinatario.contains("@")) {
            System.out.println("Omitiendo envío: Correo inválido.");
            return;
        }

        try {
            // 1. Procesar datos y generar PDF
            JSONObject data = new JSONObject(mensajeJson);
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);
            
            // 2. Convertir PDF a Base64 (Requerido por la API de Resend)
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            // 3. Llamar al método de envío
            enviarViaApi(destinatario, pdfBase64);

        } catch (Exception e) {
            System.err.println("Error procesando notificación vía API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void enviarViaApi(String destinatario, String pdfBase64) {
        // Construcción del cuerpo del JSON para Resend
        Map<String, Object> body = Map.of(
            "from", "onboarding@resend.dev",
            "to", List.of(destinatario),
            "subject", "Comprobante Electrónico de Facturación",
            "html", "<p>Estimado cliente, adjunto encontrará su <strong>factura electrónica</strong> en formato PDF.</p>",
            "attachments", List.of(
                Map.of(
                    "content", pdfBase64,
                    "filename", "Factura.pdf"
                )
            )
        );

        // Petición HTTP POST (Puerto 443 - HTTPS)
        webClient.post()
            .uri("/emails")
            .header("Authorization", "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(
                response -> System.out.println("Éxito API Resend: " + response),
                error -> System.err.println("Fallo total API Resend: " + error.getMessage())
            );
    }
}
