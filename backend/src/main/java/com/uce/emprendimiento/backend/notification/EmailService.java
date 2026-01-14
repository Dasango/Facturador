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

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.resend.com").build();
    }

    @Async
    public void enviarNotificacionFactura(String destinatario, String mensajeJson) {
        if (destinatario == null || !destinatario.contains("@")) return;

        try {
            JSONObject data = new JSONObject(mensajeJson);
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);
            
            // Convertir PDF a Base64 para enviarlo por HTTP
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            enviarViaApi(destinatario, pdfBase64);

        } catch (Exception e) {
            System.err.println("Error en API de Resend: " + e.getMessage());
        }
    }

    private void enviarViaApi(String destinatario, String pdfBase64) {
        Map<String, Object> body = Map.of(
            "from", "onboarding@resend.dev",
            "to", List.of(destinatario),
            "subject", "Comprobante Electrónico",
            "html", "<strong>Estimado cliente, adjunto encontrará su factura.</strong>",
            "attachments", List.of(
                Map.of(
                    "content", pdfBase64,
                    "filename", "Factura.pdf"
                )
            )
        );

        webClient.post()
            .uri("/emails")
            .header("Authorization", "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(response -> System.out.println("Respuesta API Resend: " + response));
    }
}
