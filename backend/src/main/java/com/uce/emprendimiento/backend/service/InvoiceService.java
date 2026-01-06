package com.uce.emprendimiento.backend.service;

import com.uce.emprendimiento.backend.entity.Invoice;
import java.util.List;
import java.util.Optional;
import com.uce.emprendimiento.backend.dto.xml.FacturaDTO;

public interface InvoiceService {
    // Cambiamos String username -> Long userId
    List<Invoice> getInvoicesByUserId(Long userId);

    Optional<Invoice> getInvoiceByIdAndUserId(Long id, Long userId);

    Invoice crearFactura(Invoice factura, Long userId, String tipoEmision); // tipoEmision: "BORRADOR" o "ENVIAR"

    FacturaDTO getFacturaDTO(Long invoiceId, Long userId);
}