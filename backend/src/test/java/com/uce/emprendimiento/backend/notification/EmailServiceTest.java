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

    String invoice = "{\r\n" + //
            "  \"id\": \"comprobante\",\r\n" + //
            "  \"version\": \"1.1.0\",\r\n" + //
            "  \"infoTributaria\": {\r\n" + //
            "    \"ambiente\": \"1\",\r\n" + //
            "    \"tipoEmision\": \"1\",\r\n" + //
            "    \"razonSocial\": \"JUAN PEREZ SA\",\r\n" + //
            "    \"nombreComercial\": \"COMERCIAL PEREZ\",\r\n" + //
            "    \"ruc\": \"1791715772001\",\r\n" + //
            "    \"claveAcceso\": null,\r\n" + //
            "    \"codDoc\": \"01\",\r\n" + //
            "    \"estab\": \"001\",\r\n" + //
            "    \"ptoEmi\": \"001\",\r\n" + //
            "    \"secuencial\": \"000000002\",\r\n" + //
            "    \"dirMatriz\": \"Av. Amazonas y Naciones Unidas\"\r\n" + //
            "  },\r\n" + //
            "  \"infoFactura\": {\r\n" + //
            "    \"fechaEmision\": \"05/12/2025\",\r\n" + //
            "    \"dirEstablecimiento\": \"Av. Amazonas\",\r\n" + //
            "    \"obligadoContabilidad\": \"SI\",\r\n" + //
            "    \"tipoIdentificacionComprador\": \"05\",\r\n" + //
            "    \"razonSocialComprador\": \"Cliente B\",\r\n" + //
            "    \"identificacionComprador\": \"1700000002\",\r\n" + //
            "    \"direccionComprador\": \"Quito Sur\",\r\n" + //
            "    \"totalSinImpuestos\": \"25.00\",\r\n" + //
            "    \"totalDescuento\": \"0.00\",\r\n" + //
            "    \"totalConImpuestos\": [\r\n" + //
            "      {\r\n" + //
            "        \"codigo\": \"2\",\r\n" + //
            "        \"codigoPorcentaje\": \"2\",\r\n" + //
            "        \"baseImponible\": \"25.00\",\r\n" + //
            "        \"valor\": \"3.00\"\r\n" + //
            "      }\r\n" + //
            "    ],\r\n" + //
            "    \"propina\": \"0.00\",\r\n" + //
            "    \"importeTotal\": \"28.00\",\r\n" + //
            "    \"moneda\": \"DOLAR\",\r\n" + //
            "    \"pagos\": [\r\n" + //
            "      {\r\n" + //
            "        \"formaPago\": \"01\",\r\n" + //
            "        \"total\": \"28.00\",\r\n" + //
            "        \"plazo\": \"0.00\",\r\n" + //
            "        \"unidadTiempo\": \"dias\"\r\n" + //
            "      }\r\n" + //
            "    ]\r\n" + //
            "  },\r\n" + //
            "  \"detalles\": [\r\n" + //
            "    {\r\n" + //
            "      \"codigoPrincipal\": \"P1-002\",\r\n" + //
            "      \"codigoAuxiliar\": null,\r\n" + //
            "      \"descripcion\": \"Mouse Logitech\",\r\n" + //
            "      \"cantidad\": \"1.00\",\r\n" + //
            "      \"precioUnitario\": \"25.00\",\r\n" + //
            "      \"descuento\": \"0.00\",\r\n" + //
            "      \"precioTotalSinImpuesto\": \"25.00\",\r\n" + //
            "      \"impuestos\": [\r\n" + //
            "        {\r\n" + //
            "          \"codigo\": \"2\",\r\n" + //
            "          \"codigoPorcentaje\": \"2\",\r\n" + //
            "          \"tarifa\": \"12.00\",\r\n" + //
            "          \"baseImponible\": \"25.00\",\r\n" + //
            "          \"valor\": \"3.00\"\r\n" + //
            "        }\r\n" + //
            "      ]\r\n" + //
            "    }\r\n" + //
            "  ],\r\n" + //
            "  \"infoAdicional\": [],\r\n" + //
            "  \"signature\": null\r\n" + //
            "}";

    @Test
    void testNoHayCorreo() {
        // Caso: Destinatario es "No hay correo"

        emailService.enviarNotificacionFactura("No hay correo", invoice);
        // Verificamos que el metodo protegido de envio NO sea invocado
        verify(emailService, never()).sendActualEmail(anyString(), anyString());
    }

    @Test
    void testCorreoInvalido() {
        // Caso: Destinatario es basura "dsandklasda"
        // Resultado esperado: Debe detectar invalidez y no enviar.
        emailService.enviarNotificacionFactura("dsandklasda", invoice);

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
