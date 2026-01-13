package com.uce.emprendimiento.backend.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Spy
    EmailService emailService;

    @Test
    void testNoHayCorreo() {
        // Caso: Destinatario es "No hay correo"
        // Resultado esperado: No debe intentar enviar nada.
        emailService.enviarNotificacionFactura("No hay correo", "Mensaje de prueba");

        // Verificamos que el metodo protegido de envio NO sea invocado
        verify(emailService, never()).sendActualEmail(anyString(), anyString());
    }

    @Test
    void testCorreoInvalido() {
        // Caso: Destinatario es basura "dsandklasda"
        // Resultado esperado: Debe detectar invalidez y no enviar.
        emailService.enviarNotificacionFactura("dsandklasda", "Mensaje de prueba");

        verify(emailService, never()).sendActualEmail(anyString(), anyString());
    }

    @Test
    void testCorreoReal() {
        // Caso: Correo valido "correoreal@gmail.com"
        // Resultado esperado: Debe procesar el envio.
        String destinatario = "correoreal@gmail.com";
        String mensaje = "Su factura ha sido emitida";

        emailService.enviarNotificacionFactura(destinatario, mensaje);

        // Verificamos que SI se llame al metodo de envio con los argumentos correctos
        verify(emailService, times(1)).sendActualEmail(destinatario, mensaje);
    }
}
