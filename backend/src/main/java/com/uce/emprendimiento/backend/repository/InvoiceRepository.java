package com.uce.emprendimiento.backend.repository;

import com.uce.emprendimiento.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Spring entiende esto automágicamente:
    // "Busca en la entidad Invoice, el campo 'usuario', y filtra por su 'id'"
    List<Invoice> findByUsuario_Id(Long usuarioId);

    Optional<Invoice> findByIdAndUsuarioId(Long id, Long usuarioId);
}