package com.uce.emprendimiento.backend.notification;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailService {

    @Async
    public void enviarNotificacionFactura(String destinatario, String mensaje) {
        if (destinatario == null || "No hay correo".equals(destinatario)) {
            System.out.println("Omitiendo envio: No hay correo registrado.");
            return;
        }

        // Validacion simple de email (contiene @ y .)
        if (!destinatario.contains("@") || !destinatario.contains(".")) {
            System.out.println("Omitiendo envio: Correo invalido -> " + destinatario);
            return;
        }

        sendActualEmail(destinatario, mensaje);
    }

    protected void sendActualEmail(String destinatario, String mensaje) {
        // En un futuro aqui se inyectara JavaMailSender
        System.out.println("SIMULACION: Enviando correo a " + destinatario + " con mensaje: " + mensaje);
    }
}