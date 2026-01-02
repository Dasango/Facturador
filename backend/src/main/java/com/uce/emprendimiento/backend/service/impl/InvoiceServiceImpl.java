package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.entity.Invoice;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.InvoiceRepository;
import com.uce.emprendimiento.backend.repository.UserRepository;
import com.uce.emprendimiento.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    public List<Invoice> getInvoicesByUser(String username) {
        User user = userRepository.findByCedula(username)
                .orElseGet(() -> userRepository.findByCorreo(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username)));

        return invoiceRepository.findByUsuario(user);
    }
}
