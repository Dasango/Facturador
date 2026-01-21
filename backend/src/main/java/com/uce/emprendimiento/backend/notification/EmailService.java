package com.uce.emprendimiento.backend.notification;

import com.uce.emprendimiento.backend.util.GeneradorFactura;
import jakarta.mail.internet.MimeMessage;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // Ya no usamos WebClient, usamos JavaMailSender
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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
            
            // 2. Crear el mensaje preparado para adjuntos (MIME)
            MimeMessage message = mailSender.createMimeMessage();
            // El 'true' indica que el mensaje es multiparte (texto + adjuntos)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // IMPORTANTE: Este correo debe ser el que registraste en Brevo
            helper.setFrom("sdeddxd@gmail.com"); 
            helper.setTo(destinatario);
            helper.setSubject("Comprobante Electrónico de Facturación");
            
            // Cuerpo del correo en HTML
            String contenidoHtml = "<html><body>" +
                                   "<p>Estimado cliente, adjunto encontrará su <strong>factura electrónica</strong> en formato PDF.</p>" +
                                   "</body></html>";
            helper.setText(contenidoHtml, true);

            // 3. Adjuntar el PDF directamente desde los bytes
            helper.addAttachment("Factura.pdf", new ByteArrayResource(pdfBytes));

            // 4. Enviar mediante el SMTP configurado en properties
            mailSender.send(message);
            System.out.println("Éxito: Correo enviado a " + destinatario + " vía SMTP (Puerto 2525)");

        } catch (Exception e) {
            System.err.println("Error en el envío SMTP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
