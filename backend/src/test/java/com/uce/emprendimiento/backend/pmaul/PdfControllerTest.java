package com.uce.emprendimiento.backend.pmaul;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uce.emprendimiento.backend.controller.PdfController;
import org.springframework.http.ResponseEntity;
import java.io.FileOutputStream;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class PdfControllerTest {
    @Spy
    PdfController pdfController;

    @Test
    void testPdfController() {

        ResponseEntity<byte[]> response = pdfController.generatePdf("null");
        try (FileOutputStream fos = new FileOutputStream("factura1.pdf")) {
            if (response.getBody() != null) {
                fos.write(response.getBody());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testPdfController2() {

        ResponseEntity<byte[]> response = pdfController.generatePdf(null);
        try (FileOutputStream fos = new FileOutputStream("factura5.pdf")) {
            if (response.getBody() != null) {
                fos.write(response.getBody());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testPdfController3() {

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

        ResponseEntity<byte[]> response = pdfController.generatePdf(invoice);
        try (FileOutputStream fos = new FileOutputStream("factura3.pdf")) {
            if (response.getBody() != null) {
                fos.write(response.getBody());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testPdfController4() {
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
                "  \"signature\": null\r\n" + //
                "}";

        ResponseEntity<byte[]> response = pdfController.generatePdf(invoice);
        try (FileOutputStream fos = new FileOutputStream("factura4.pdf")) {
            if (response.getBody() != null) {
                fos.write(response.getBody());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
