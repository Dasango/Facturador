package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.entity.Invoice;
import com.uce.emprendimiento.backend.service.InvoiceService;
import com.uce.emprendimiento.backend.security.CustomUserDetails; // Importante
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getMyInvoices(@AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1. Verificación de seguridad (aunque SecurityConfig ya debería bloquear esto)
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        // 2. Obtenemos el ID real del usuario desde la memoria
        Long userId = userDetails.getUser().getId();

        // 3. Consultamos las facturas de ese ID específico
        List<Invoice> facturas = invoiceService.getInvoicesByUserId(userId);

        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/{id}/ride")
    public ResponseEntity<?> getMyInvoiceRIDE(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 1. Verificación de seguridad básica
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = userDetails.getUser().getId();

        // 2. Buscamos la factura ESPECÍFICA validando que pertenezca al usuario
        // Es crucial buscar por (invoiceId AND userId) para evitar que un usuario vea
        // facturas ajenas.
        Optional<Invoice> invoiceOpt = invoiceService.getInvoiceByIdAndUserId(id, userId);

        if (invoiceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 3. Construimos la respuesta combinada (User + Invoice)
        // Esto facilita al frontend pintar el encabezado del RIDE (datos emisor) y el
        // cuerpo (datos factura)
        Map<String, Object> response = new HashMap<>();

        // Datos de la factura
        response.put("invoice", invoiceOpt.get());

        // Datos del usuario logeado (Emisor) tomados directamente de la sesión segura
        response.put("issuer", userDetails.getUser());

        return ResponseEntity.ok(response);
    }
}
