package com.uce.emprendimiento.backend.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest // 1. Cambiamos a SpringBootTest para cargar el JavaMailSender
class EmailServiceTest {

    @Autowired
    @SpyBean // 2. Usamos SpyBean para poder verificar llamadas a métodos internos
    private EmailService emailService;

    @Test
    void testNoHayCorreo() {
        // Caso: Destinatario es "No hay correo"
        emailService.enviarNotificacionFactura("No hay correo", "{}");

        // Verificamos que NO se intente enviar el email
        try {
            verify(emailService, never()).sendActualEmail(anyString(), anyString(), any(byte[].class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCorreoInvalido() {
        // Caso: Destinatario no tiene formato de correo
        emailService.enviarNotificacionFactura("correo_sin_arroba", "{}");

        try {
            verify(emailService, never()).sendActualEmail(anyString(), anyString(), any(byte[].class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCorreoReal() {
        // 3. CAMBIO IMPORTANTE: Debes usar un JSON válido y tu correo real
        String destinatario = "sdeddxd@gmail.com"; 
        
        // El mensaje DEBE ser un JSON para que JSONObject no de error
        String mensajeJson = "{"
                + "\"cliente\": \"Juan Perez\","
                + "\"total\": 12.50,"
                + "\"detalles\": \"Prueba de factura\""
                + "}";

        emailService.enviarNotificacionFactura(destinatario, mensajeJson);

        // Verificamos que se llame al método de envío real con los 3 parámetros
        try {
            verify(emailService, times(1)).sendActualEmail(eq(destinatario), anyString(), any(byte[].class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}