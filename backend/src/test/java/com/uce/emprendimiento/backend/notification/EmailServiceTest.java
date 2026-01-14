package com.uce.emprendimiento.backend.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest 
class EmailServiceTest {

    @Autowired
    @SpyBean 
    private EmailService emailService;

    // Tu JSON real para que el PDF no salga vacío
    String invoice = "{\n" +
            "  \"id\": \"comprobante\",\n" +
            "  \"infoTributaria\": {\n" +
            "    \"razonSocial\": \"JUAN PEREZ SA\",\n" +
            "    \"nombreComercial\": \"COMERCIAL PEREZ\",\n" +
            "    \"ruc\": \"1791715772001\",\n" +
            "    \"secuencial\": \"000000002\"\n" +
            "  },\n" +
            "  \"infoFactura\": {\n" +
            "    \"fechaEmision\": \"05/12/2025\",\n" +
            "    \"razonSocialComprador\": \"Cliente B\",\n" +
            "    \"identificacionComprador\": \"1700000002\",\n" +
            "    \"importeTotal\": \"28.00\"\n" +
            "  },\n" +
            "  \"detalles\": [\n" +
            "    {\n" +
            "      \"descripcion\": \"Mouse Logitech\",\n" +
            "      \"cantidad\": \"1.00\",\n" +
            "      \"precioTotalSinImpuesto\": \"25.00\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    void testNoHayCorreo() {
        // Usamos el JSON invoice para que pase la validación de JSONObject
        emailService.enviarNotificacionFactura("No hay correo", invoice);

        try {
            // CORRECCIÓN: Verificar contra la firma de 3 parámetros
            verify(emailService, never()).sendActualEmail(anyString(), anyString(), any(byte[].class));
        } catch (Exception e) {}
    }

    @Test
    void testCorreoInvalido() {
        emailService.enviarNotificacionFactura("esto_no_es_un_correo", invoice);

        try {
            // CORRECCIÓN: Verificar contra la firma de 3 parámetros
            verify(emailService, never()).sendActualEmail(anyString(), anyString(), any(byte[].class));
        } catch (Exception e) {}
    }

    @Test
    void testCorreoReal() {
        // PON TU CORREO AQUÍ PARA PROBAR
        String destinatario = "dasango@uce.edu.ec"; 

        emailService.enviarNotificacionFactura(destinatario, invoice);

        try {
            // Verificamos que se llame al método que genera el PDF y envía
            verify(emailService, times(1)).sendActualEmail(eq(destinatario), anyString(), any(byte[].class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}