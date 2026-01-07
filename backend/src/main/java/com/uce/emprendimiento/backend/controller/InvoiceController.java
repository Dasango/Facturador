package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.dto.InvoiceSummaryDTO;
import com.uce.emprendimiento.backend.entity.Invoice;
import com.uce.emprendimiento.backend.service.InvoiceService;
import com.uce.emprendimiento.backend.security.CustomUserDetails; // Importante
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final com.uce.emprendimiento.backend.service.SriService sriService;

    @GetMapping
    public ResponseEntity<List<InvoiceSummaryDTO>> getMyInvoices(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1. Verificación de seguridad (aunque SecurityConfig ya debería bloquear esto)
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        // 2. Obtenemos el ID real del usuario desde la memoria
        Long userId = userDetails.getUser().getId();

        // 3. Consultamos las facturas de ese ID específico, pero SOLO los datos
        // necesarios
        List<InvoiceSummaryDTO> facturas = invoiceService.getInvoiceSummariesByUserId(userId);

        return ResponseEntity.ok(facturas);
    }

    @GetMapping(value = "/{id}/xml-data", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getInvoiceXmlData(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            var dto = invoiceService.getFacturaDTO(id, userDetails.getUser().getId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace(); // Log del error en consola
            return ResponseEntity.badRequest()
                    .body("Error generando XML data: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }

    @GetMapping(value = "/{id}/xml-data-injsonformat", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInvoiceJsonData(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            var dto = invoiceService.getFacturaDTO(id, userDetails.getUser().getId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Error generando JSON data: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }

    @GetMapping(value = "/{id}/sri-mock-response", produces = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> getSriMockResponse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            // 1. Generar XML Real
            var dto = invoiceService.getFacturaDTO(id, userDetails.getUser().getId());
            String realXml = sriService.objectToXml(dto);

            // 2. Construir Fake Response
            String soapResponse = """
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                     <soap:Body>
                     <ns2:autorizacionComprobanteResponse
                    xmlns:ns2="http://ec.gob.sri.ws.autorizacion">
                     <RespuestaAutorizacionComprobante>
                     <claveAccesoConsultada>
                     1302201201176001321000120010030000050431234567814
                     </claveAccesoConsultada>
                     <numeroComprobantes>1</numeroComprobantes>
                     <autorizaciones>
                     <autorizacion>
                     <estado>RECHAZADO</estado>
                     <fechaAutorizacion>2012-02-13T16:34:48.997-05:00</fechaAutorizacion>
                     <ambiente>PRUEBAS</ambiente>
                     <comprobante><![CDATA[
                     %s
                     ]]></comprobante>
                     <mensajes>
                     <mensaje>
                     <identificador>46</identificador>
                     <mensaje> RUC no existe </mensaje>
                     <tipo>ERROR</tipo>
                     </mensaje>
                     </mensajes>
                     </autorizacion>
                     </autorizaciones>
                     </RespuestaAutorizacionComprobante>
                     </ns2:autorizacionComprobanteResponse>
                     </soap:Body>
                    </soap:Envelope>
                                """.formatted(realXml);

            // 3. Concatenar (Usuario pidió: "el xml que ya está dando Y ABAJO este dato
            // quemado")
            // Interpretación literal: Archivo con XML + SOAP
            String finalOutput = realXml + "\n\n" + "<!-- RESULTADO SRI -->" + "\n" + soapResponse;

            return ResponseEntity.ok(finalOutput);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Error simulando SRI: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody Invoice invoice,
            @RequestParam(defaultValue = "BORRADOR") String accion, // "BORRADOR" o "ENVIAR"
            @RequestParam(required = false) String claveFirma,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();
        System.out.println("SE CREA FACTURA XXDXD OJALA");
        try {
            // Vinculación explícita de relaciones anidadas para asegurar integridad
            if (invoice.getPagos() != null) {
                invoice.getPagos().forEach(pago -> pago.setFactura(invoice));
            }
            if (invoice.getDetalles() != null) {
                invoice.getDetalles().forEach(detalle -> detalle.setFactura(invoice));
            }
            if (invoice.getInfoAdicional() != null) {
                invoice.getInfoAdicional().forEach(info -> info.setFactura(invoice));
            }

            Invoice nuevaFactura = invoiceService.crearFactura(invoice, userDetails.getUser().getId(), accion,
                    claveFirma);
            return ResponseEntity.ok(nuevaFactura);
        } catch (Exception e) {
            e.printStackTrace(); // Para que veas el error en consola
            return ResponseEntity.badRequest().body("Error creando factura: " + e.getMessage());
        }
    }
}
