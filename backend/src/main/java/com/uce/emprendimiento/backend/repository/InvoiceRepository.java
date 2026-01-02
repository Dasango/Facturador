package com.uce.emprendimiento.backend.repository;

import com.uce.emprendimiento.backend.entity.Invoice;
import com.uce.emprendimiento.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUsuario(User usuario);
}
