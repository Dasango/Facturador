package com.uce.emprendimiento.backend;

import com.uce.emprendimiento.backend.sri.SriService;
import com.uce.emprendimiento.backend.service.InvoiceService;
// Imports para JAXB
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestFlujoSRI {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SriService sriService;

    @Test
    void testDeLaVerdad() throws Exception {
        System.out.println("\n\n============== INICIO TEST MANUAL SRI ==============");

        // 1. DATOS DE PRUEBA
        Long facturaId = 1L;
        Long userId = 1L;

        // 2. OBTENER DTO DESDE LA BASE
        var facturaDTO = invoiceService.getFacturaDTO(facturaId, userId);
        System.out.println("-> DTO recuperado correctamente.");

        // =================================================================================
        // -----> AQUÍ EMPIEZA EL PARCHE AUTOMÁTICO (LO QUE TIENES QUE AGREGAR) <-----
        // =================================================================================

        // A. ARREGLAR SECUENCIAL (Quitar los guiones si existen, ej:
        // "001-001-000000001" -> "000000001")
        // if (facturaDTO.getInfoTributaria().getSecuencial() != null &&
        // facturaDTO.getInfoTributaria().getSecuencial().contains("-")) {
        // String[] partes = facturaDTO.getInfoTributaria().getSecuencial().split("-");
        // facturaDTO.getInfoTributaria().setSecuencial(partes[partes.length - 1]);
        // }

        // B. GENERAR CLAVE ÚNICA PARA EVITAR ERROR DE DUPLICADOS
        // Datos fijos para el test
        String fechaHoy = "11012026";
        String rucPruebas = "1791715772001";
        String ambiente = "1";
        String serie = "001001";
        String secuencial = "000000001"; // Fijo para test

        // Generamos un número aleatorio de 8 dígitos para que el SRI crea que es una
        // factura nueva
        String codigoNumerico = String.format("%08d", new Random().nextInt(99999999));

        String tipoEmision = "1";
        String digitoVerificador = "1"; // Valor dummy, el SRI lo validará pero pasará la estructura

        // Armamos la nueva clave
        String nuevaClave = fechaHoy + "01" + rucPruebas + ambiente + serie + secuencial + codigoNumerico + tipoEmision
                + digitoVerificador;

        // Inyectamos los datos nuevos al DTO
        facturaDTO.getInfoTributaria().setClaveAcceso(nuevaClave);
        // Alineamos la fecha de emisión para que coincida con la clave (Evita error de
        // fechas)
        facturaDTO.getInfoFactura().setFechaEmision("11/01/2026");

        System.out.println("-> [TEST] Se generó clave aleatoria: " + nuevaClave);
        // =================================================================================
        // -----> FIN DEL PARCHE <-----
        // =================================================================================

        // 3. CONVERTIR A XML USANDO JAXB
        JAXBContext context = JAXBContext.newInstance(facturaDTO.getClass());
        Marshaller marshaller = context.createMarshaller();

        // Opcional: Para que el XML se vea bonito en la consola
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(facturaDTO, sw);
        String xmlCrudo = sw.toString();

        System.out.println(
                "-> XML Generado (Fragmento):\n" + xmlCrudo.substring(0, Math.min(xmlCrudo.length(), 200)) + "...");

        // 4. ENVIAR AL SRI
        var respuestaSRI = sriService.enviarAlSri(xmlCrudo);

        // 5. VISUALIZACIÓN ORDENADA
        System.out.println("\n============== REPORTE INTEGRAL SRI ==============");
        System.out.println("ESTADO GENERAL: " + respuestaSRI.getEstado());

        // Imprimir mensajes de error limpios (si hay)
        if (respuestaSRI.getMensajes() != null) {
            System.out.println("MENSAJES DEL SERVIDOR:");
            respuestaSRI.getMensajes().forEach(m -> System.out
                    .println(" - [" + m.getIdentificador() + "] " + m.getMensaje() + ": "
                            + m.getInformacionAdicional()));
        }

        // FORMATEAR EL XML RESPUESTA
        System.out.println("\n--- XML RESPUESTA DEL SRI (FORMATEADO) ---");
        try {
            String xmlSucio = respuestaSRI.getXmlRespuestaSriCrudo();
            if (xmlSucio != null && !xmlSucio.isEmpty()) {
                Source xmlInput = new StreamSource(new StringReader(xmlSucio));
                StringWriter stringWriter = new StringWriter();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(xmlInput, new StreamResult(stringWriter));

                System.out.println(stringWriter.toString());
            } else {
                System.out.println("(El campo xmlRespuestaSriCrudo llegó nulo)");
            }
        } catch (Exception e) {
            System.out.println("No se pudo formatear el XML: " + respuestaSRI.getXmlRespuestaSriCrudo());
        }
        System.out.println("==================================================\n");

        // ESTO SÍ HACE QUE EL TEST FALLE si no es RECIBIDA ni ACEPTADO
        String estado = respuestaSRI.getEstado();
        assertTrue(estado.equals("RECIBIDA") || estado.equals("ACEPTADO"),
                "⛔ ERROR CRÍTICO: El SRI rechazó la factura. Estado: " + estado);

        System.out.println("✅ ÉXITO: Factura aceptada/recibida por el SRI");
    }
}