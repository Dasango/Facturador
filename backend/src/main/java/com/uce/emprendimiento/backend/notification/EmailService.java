package com.uce.emprendimiento.backend.notification;

import com.uce.emprendimiento.backend.util.GeneradorFactura;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Async
    public void enviarNotificacionFactura(String destinatario, String mensajeJson) {
        // Validaciones de seguridad básicas
        if (destinatario == null || "No hay correo".equals(destinatario) || !destinatario.contains("@")) {
            System.out.println("Omitiendo envío: Correo inválido o inexistente.");
            return;
        }

        try {
            // 1. Convertir el String mensajeJson a JSONObject (según pide tu PdfController)
            JSONObject data = new JSONObject(mensajeJson);

            // 2. Generar el PDF usando la utilidad que ya usa tu Controller
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);

            // 3. Enviar el correo con el adjunto
            sendActualEmail(destinatario, "Comprobante Electrónico de Facturación", pdfBytes);

        } catch (Exception e) {
            System.err.println("Error procesando notificación: " + e.getMessage());
            System.out.println();
            e.printStackTrace();
        }
    }

    protected void sendActualEmail(String destinatario, String asunto, byte[] pdfBytes) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        // El parámetro 'true' indica que es un mensaje "multipart" (permite adjuntos)
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(
                "Estimado cliente,\n\nAdjunto a este correo encontrará su factura electrónica en formato PDF.\n\nSaludos cordiales.");

        // Adjuntar el PDF generado
        if (pdfBytes != null && pdfBytes.length > 0) {
            helper.addAttachment("Factura.pdf", new ByteArrayResource(pdfBytes));
        }

        mailSender.send(message);
        System.out.println("Correo enviado con éxito a: " + destinatario);
    }
}