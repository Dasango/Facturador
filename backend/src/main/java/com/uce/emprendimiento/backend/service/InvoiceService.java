package com.uce.emprendimiento.backend.service;

import com.uce.emprendimiento.backend.entity.Invoice;
import java.util.List;

public interface InvoiceService {
    List<Invoice> getInvoicesByUser(String username);
}
