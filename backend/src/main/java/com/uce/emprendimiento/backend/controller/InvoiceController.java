package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.dto.InvoiceSummaryDTO;
import com.uce.emprendimiento.backend.entity.Invoice;
import com.uce.emprendimiento.backend.service.InvoiceService;
import com.uce.emprendimiento.backend.service.XmlService;
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
    private final XmlService xmlService;

    @GetMapping
    public ResponseEntity<List<InvoiceSummaryDTO>> getMyInvoices(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = userDetails.getUser().getId();

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

    @GetMapping(value = "/{id}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody Invoice invoice,
            @RequestParam(defaultValue = "BORRADOR") String accion, // "BORRADOR" o "ENVIAR"
            @RequestParam(required = false) String claveFirma,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

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
