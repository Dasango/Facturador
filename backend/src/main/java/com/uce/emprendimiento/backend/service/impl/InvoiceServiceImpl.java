package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.entity.Invoice;
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
}