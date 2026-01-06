package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.entity.Invoice;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.InvoiceRepository;
import com.uce.emprendimiento.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByUserId(Long userId) {
        // Llamamos al método optimizado del repositorio
        return invoiceRepository.findByUsuario_Id(userId);
    }

    public Optional<Invoice> getInvoiceByIdAndUserId(Long id, Long userId) {
        return invoiceRepository.findByIdAndUsuarioId(id, userId);
    }

    @Override
    @Transactional
    public Invoice crearFactura(Invoice factura, Long userId, String tipoEmision) {
        // 1. Vincular usuario
        User user = new User();
        user.setId(userId);
        factura.setUsuario(user);

        // 2. Vincular los detalles con la factura (relación bidireccional)
        if (factura.getDetalles() != null) {
            for (var detalle : factura.getDetalles()) {
                detalle.setFactura(factura); // Importante para que JPA guarde la FK
            }
        }

        if (factura.getPagos() != null) {
            for (var pago : factura.getPagos()) {
                pago.setFactura(factura);
            }
        }

        // 3. Lógica según tipo
        if ("ENVIAR".equals(tipoEmision)) {
            // AQUÍ IRÍA LA LÓGICA DEL SRI (Firma, envío, etc.)
            System.out.println(">>> SIMULANDO ENVÍO AL SRI: Autorizando factura...");
            factura.setEstado("AUTORIZADO");
            factura.setClaveAcceso("1234567890123456789012345678901234567890123456789"); // Mock
            factura.setFechaAutorizacion(java.time.LocalDateTime.now());
        } else {
            factura.setEstado("PENDIENTE"); // Borrador
        }

        return invoiceRepository.save(factura);
    }
}