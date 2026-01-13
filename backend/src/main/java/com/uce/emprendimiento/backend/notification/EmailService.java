package com.uce.emprendimiento.backend.notification;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailService {

    @Async
    public void enviarNotificacionFactura(String destinatario, String mensaje) {
        if (destinatario == null || destinatario.isEmpty()) {
            System.out.println("No hay email para enviar.");
            return;
        }

        // Lógica real de envío de correo aquí
        System.out.println("Enviando correo a: " + destinatario);
        System.out.println("Contenido: " + mensaje);
    }
}