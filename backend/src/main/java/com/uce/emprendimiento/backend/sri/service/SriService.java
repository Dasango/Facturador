package com.uce.emprendimiento.backend.sri.service;

import com.uce.emprendimiento.backend.sri.model.Factura;
import com.uce.emprendimiento.backend.sri.wsdl.RespuestaComprobante;
import com.uce.emprendimiento.backend.sri.wsdl.RespuestaSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SriService {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private SriSoapService sriSoapService;

    public String procesarFactura(Factura factura, String pathP12, String claveP12) {
        try {
            // 1. Generar XML firmado
            byte[] xmlFirmado = facturaService.generarFacturaFirmada(factura, pathP12, claveP12);

            // 2. Enviar a Recepción
            RespuestaSolicitud respuestaRecepcion = sriSoapService.enviarFactura(xmlFirmado);

            if (!"RECIBIDA".equals(respuestaRecepcion.getEstado())) {
                return "Error en Recepción: " + respuestaRecepcion.getEstado();
            }

            // 3. Autorizar (Se usa la clave de acceso)
            String claveAcceso = factura.getInfoTributaria().getClaveAcceso();
            // Nota: En producción, se debe esperar unos segundos entre recepción y
            // autorización
            Thread.sleep(3000);

            RespuestaComprobante respuestaAutorizacion = sriSoapService.autorizarFactura(claveAcceso);

            // Verificar si hay autorizaciones
            if (respuestaAutorizacion.getAutorizaciones() != null
                    && !respuestaAutorizacion.getAutorizaciones().isEmpty()) {
                return "Estado Autorización: " + respuestaAutorizacion.getAutorizaciones().get(0).getEstado();
            }

            return "Respuesta de Autorización recibida pero sin detalles.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
