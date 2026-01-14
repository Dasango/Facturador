package com.uce.emprendimiento.backend.notification;

import com.uce.emprendimiento.backend.util.GeneradorFactura;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Mantenemos la lectura de la variable, pero en el modo gratuito de Resend 
    // es obligatorio usar "onboarding@resend.dev" en el setFrom.
    @Value("${spring.mail.username}")
    private String remitenteConfigurado;

    @Async
    public void enviarNotificacionFactura(String destinatario, String mensajeJson) {
        // Validaciones de seguridad básicas (Mantenidas)
        if (destinatario == null || "No hay correo".equals(destinatario) || !destinatario.contains("@")) {
            System.out.println("Omitiendo envío: Correo inválido o inexistente.");
            return;
        }

        try {
            // 1. Convertir el String mensajeJson a JSONObject (Mantenido)
            JSONObject data = new JSONObject(mensajeJson);

            // 2. Generar el PDF usando la utilidad (Mantenido)
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);

            // 3. Enviar el correo con el adjunto
            sendActualEmail(destinatario, "Comprobante Electrónico de Facturación", pdfBytes);

        } catch (Exception e) {
            // Log de error mejorado para depuración en Railway
            System.err.println("Error procesando notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void sendActualEmail(String destinatario, String asunto, byte[] pdfBytes) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        // El parámetro 'true' indica que es un mensaje "multipart" (permite adjuntos)
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // CAMBIO CRÍTICO: Para Resend gratuito, el remitente DEBE ser onboarding@resend.dev
        // Si ya tienes dominio propio verificado, puedes volver a usar 'remitenteConfigurado'
        String remitenteFinal = "onboarding@resend.dev"; 
        
        helper.setFrom(remitenteFinal, "Facturación Electrónica");
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(
                "Estimado cliente,\n\nAdjunto a este correo encontrará su factura electrónica en formato PDF.\n\nSaludos cordiales.");

        // Adjuntar el PDF generado (Mantenido)
        if (pdfBytes != null && pdfBytes.length > 0) {
            helper.addAttachment("Factura.pdf", new ByteArrayResource(pdfBytes));
        }

        // El envío se ejecuta. Si falla por Timeout, el @Async evita que bloquee la DB.
        mailSender.send(message);
        System.out.println("Correo enviado con éxito a través de Resend a: " + destinatario);
    }
}
