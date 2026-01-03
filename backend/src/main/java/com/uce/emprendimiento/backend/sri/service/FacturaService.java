package com.uce.emprendimiento.backend.sri.service;

import com.uce.emprendimiento.backend.sri.model.Factura;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Service
public class FacturaService {

    @Autowired
    private FirmaDigitalService firmaDigitalService;

    /**
     * Genera el XML de la factura y lo firma digitalmente.
     *
     * @param factura  Objeto Factura poblado.
     * @param pathP12  Ruta al archivo .p12.
     * @param claveP12 Contraseña del .p12.
     * @return Arreglo de bytes del XML firmado.
     */
    public byte[] generarFacturaFirmada(Factura factura, String pathP12, String claveP12) {
        try {
            // 0. Completar Datos (Clave Acceso)
            completarDatosFactura(factura);

            // 1. Generar XML (Marshalling)
            byte[] xmlRaw = generarXml(factura);

            // 2. Firmar XML
            return firmaDigitalService.firmarXml(xmlRaw, pathP12, claveP12);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar/firmar la factura: " + e.getMessage(), e);
        }
    }

    private void completarDatosFactura(Factura factura) {
        var infoTrib = factura.getInfoTributaria();
        var infoFact = factura.getInfoFactura();

        if (infoTrib.getClaveAcceso() == null || infoTrib.getClaveAcceso().isEmpty()) {
            try {
                // Parsear fecha dd/MM/yyyy (formato visual) o yyyy-MM-dd (formato ISO)
                // Asumimos que viene en formato dd/MM/yyyy que es el estándar SRI XML
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.util.Date fecha = sdf.parse(infoFact.getFechaEmision());

                String clave = com.uce.emprendimiento.backend.sri.util.ClaveAccesoHelper.generarClaveAcceso(
                        fecha,
                        "01", // Tipo Factura
                        infoTrib.getRuc(),
                        infoTrib.getAmbiente(),
                        infoTrib.getEstab(),
                        infoTrib.getPtoEmi(),
                        infoTrib.getSecuencial(),
                        "1" // Tipo Emisión Normal
                );

                infoTrib.setClaveAcceso(clave);
            } catch (Exception e) {
                // Si falla, loguear o dejar pasar para que falle validación XML si es necesario
                System.err.println("No se pudo generar clave acceso automática: " + e.getMessage());
            }
        }
    }

    public byte[] generarXml(Factura factura) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Factura.class);
        Marshaller marshaller = context.createMarshaller();

        // Formatear salida (pretty print)
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Codificación
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshaller.marshal(factura, os);

        return os.toByteArray();
    }
}
