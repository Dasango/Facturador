package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.dto.xml.*;
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
                List<Invoice> facturas = invoiceRepository.findByUsuario_Id(userId);
                // Inicializamos colecciones lazy para serialización
                facturas.forEach(invoice -> {
                        if (invoice != null) {
                                invoice.getPagos().size();
                                invoice.getDetalles().forEach(d -> d.getProducto().getNombre());
                                invoice.getInfoAdicional().size();
                        }
                });
                return facturas;
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<Invoice> getInvoiceByIdAndUserId(Long id, Long userId) {
                Optional<Invoice> opt = invoiceRepository.findByIdAndUsuarioId(id, userId);
                // Inicializamos las colecciones Lazy para que no fallen al serializar en el
                // Controller
                opt.ifPresent(invoice -> {
                        invoice.getPagos().size();
                        invoice.getDetalles().forEach(d -> d.getProducto().getNombre()); // Accedemos tb a producto por
                                                                                         // si acaso
                        invoice.getInfoAdicional().size();
                });
                return opt;
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

                if (factura.getInfoAdicional() != null) {
                        for (var info : factura.getInfoAdicional()) {
                                info.setFactura(factura);
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

        @Override
        @Transactional(readOnly = true)
        public List<com.uce.emprendimiento.backend.dto.InvoiceSummaryDTO> getInvoiceSummariesByUserId(Long userId) {
                return invoiceRepository.findInvoiceSummariesByUserId(userId);
        }

        @Override
        @Transactional(readOnly = true)
        public FacturaDTO getFacturaDTO(Long invoiceId, Long userId) {
                Invoice invoice = invoiceRepository.findByIdAndUsuarioId(invoiceId, userId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Factura no encontrada o no pertenece al usuario"));

                // Init lazy para evitar sorpresas durante el mapeo
                invoice.getPagos().size();
                invoice.getDetalles().forEach(d -> {
                        if (d.getProducto() != null)
                                d.getProducto().getNombre();
                });
                invoice.getInfoAdicional().size();

                User emisor = invoice.getUsuario();

                FacturaDTO dto = new FacturaDTO();
                dto.setId("comprobante");
                dto.setVersion("1.1.0");

                // --- Info Tributaria ---
                var infoTrib = new InfoTributariaDTO();
                infoTrib.setAmbiente("1"); // 1: Pruebas, 2: Producción
                infoTrib.setTipoEmision("1"); // Normal
                // Mapeo seguro de campos del emisor
                infoTrib.setRazonSocial(emisor.getRazonSocial() != null ? emisor.getRazonSocial()
                                : emisor.getNombres() + " " + emisor.getApellidos());
                infoTrib.setNombreComercial(
                                emisor.getNombreComercial() != null ? emisor.getNombreComercial()
                                                : "SIN NOMBRE COMERCIAL");
                infoTrib.setRuc(emisor.getRuc());
                infoTrib.setClaveAcceso(invoice.getClaveAcceso());
                infoTrib.setCodDoc("01"); // Factura
                infoTrib.setEstab(
                                emisor.getCodigoEstablecimiento() != null ? emisor.getCodigoEstablecimiento() : "001");
                infoTrib.setPtoEmi(emisor.getCodigoPuntoEmision() != null ? emisor.getCodigoPuntoEmision() : "001");
                infoTrib.setSecuencial(invoice.getNumeroComprobante() != null ? invoice.getNumeroComprobante()
                                : String.format("%09d", invoice.getId()));
                infoTrib.setDirMatriz(
                                emisor.getDireccionMatriz() != null ? emisor.getDireccionMatriz()
                                                : "Direccion Matriz Default");

                dto.setInfoTributaria(infoTrib);

                // --- Info Factura ---
                var infoFact = new InfoFacturaDTO();
                // Formato fecha dd/MM/yyyy
                java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                infoFact.setFechaEmision(invoice.getFechaEmision() != null ? invoice.getFechaEmision().format(dtf)
                                : java.time.LocalDate.now().format(dtf));
                infoFact.setDirEstablecimiento(
                                invoice.getDireccionEstablecimiento() != null ? invoice.getDireccionEstablecimiento()
                                                : "Direccion Establecimiento Default");
                infoFact.setObligadoContabilidad(
                                emisor.getObligadoContabilidad() != null ? emisor.getObligadoContabilidad() : "NO");
                infoFact.setTipoIdentificacionComprador(
                                invoice.getTipoIdentificacionComprador() != null
                                                ? invoice.getTipoIdentificacionComprador()
                                                : "05"); // 05
                                                         // cedula
                infoFact.setRazonSocialComprador(invoice.getClienteNombre());
                infoFact.setIdentificacionComprador(invoice.getClienteIdentificacion());
                infoFact.setDireccionComprador(
                                invoice.getDireccionComprador() != null ? invoice.getDireccionComprador()
                                                : "Sin Direccion");
                infoFact.setTotalSinImpuestos(
                                String.format("%.2f",
                                                invoice.getTotalSinImpuestos() != null ? invoice.getTotalSinImpuestos()
                                                                : 0.00)
                                                .replace(",", "."));
                infoFact.setTotalDescuento(
                                String.format("%.2f",
                                                invoice.getTotalDescuento() != null ? invoice.getTotalDescuento()
                                                                : 0.00)
                                                .replace(",", "."));
                infoFact.setPropina(
                                String.format("%.2f", invoice.getPropina() != null ? invoice.getPropina() : 0.00)
                                                .replace(",", "."));
                infoFact.setImporteTotal(
                                String.format("%.2f", invoice.getTotal() != null ? invoice.getTotal() : 0.00)
                                                .replace(",", "."));
                infoFact.setMoneda(invoice.getMoneda());

                // TotalImpuestos (Agrupado) - Por simplicidad asumo todo IVA 12/15 o 0
                // En una app real hay que agrupar por codigo de impuesto. Aquí crearé uno solo
                // basado en totales.
                var totalImp = new TotalImpuestoDTO();
                totalImp.setCodigo("2"); // IVA
                totalImp.setCodigoPorcentaje("2"); // 12% (ejemplo, debería venir dinámico)
                totalImp.setBaseImponible(infoFact.getTotalSinImpuestos());
                double totalVal = (invoice.getTotal() != null ? invoice.getTotal() : 0)
                                - (invoice.getTotalSinImpuestos() != null ? invoice.getTotalSinImpuestos() : 0);
                totalImp.setValor(String.format("%.2f", Math.max(0, totalVal)).replace(",", "."));

                infoFact.setTotalConImpuestos(java.util.Collections.singletonList(totalImp));

                // Pagos
                if (invoice.getPagos() != null && !invoice.getPagos().isEmpty()) {
                        infoFact.setPagos(invoice.getPagos().stream().map(p -> {
                                var pDto = new PagoDTO();
                                pDto.setFormaPago(p.getFormaPago());
                                pDto.setTotal(String
                                                .format("%.2f", p.getTotal() != null ? p.getTotal()
                                                                : java.math.BigDecimal.ZERO)
                                                .replace(",", "."));
                                pDto.setPlazo(p.getPlazo() != null ? p.getPlazo().toString() : "0");
                                pDto.setUnidadTiempo(p.getUnidadTiempo());
                                return pDto;
                        }).collect(java.util.stream.Collectors.toList()));
                }

                dto.setInfoFactura(infoFact);

                // --- Detalles ---
                if (invoice.getDetalles() != null) {
                        dto.setDetalles(invoice.getDetalles().stream().map(d -> {
                                var dDto = new DetalleDTO();
                                // Null-safe access to Product
                                if (d.getProducto() != null) {
                                        dDto.setCodigoPrincipal(d.getProducto().getCodigoPrincipal());
                                        dDto.setCodigoAuxiliar(d.getProducto().getCodigoAuxiliar());
                                        dDto.setDescripcion(d.getProducto().getNombre());

                                        // Safe Double/Integer handling
                                        Double tarifa = d.getProducto().getTarifa() != null
                                                        ? d.getProducto().getTarifa()
                                                        : 0.0;

                                        // Impuesto Detalle
                                        var impDto = new ImpuestoDTO();
                                        impDto.setCodigo(
                                                        d.getProducto().getCodigoImpuesto() != null
                                                                        ? d.getProducto().getCodigoImpuesto()
                                                                        : "2");
                                        impDto.setCodigoPorcentaje(
                                                        d.getProducto().getCodigoPorcentaje() != null
                                                                        ? d.getProducto().getCodigoPorcentaje()
                                                                        : "0");
                                        impDto.setTarifa(String.format("%.2f", tarifa).replace(",", "."));
                                        impDto.setBaseImponible(
                                                        String.format("%.2f",
                                                                        d.getSubtotal() != null ? d.getSubtotal() : 0.0)
                                                                        .replace(",", "."));
                                        impDto.setValor(String.format("%.2f",
                                                        d.getValorImpuesto() != null ? d.getValorImpuesto() : 0.0)
                                                        .replace(",", "."));

                                        dDto.setImpuestos(java.util.Collections.singletonList(impDto));
                                } else {
                                        dDto.setDescripcion("PRODUCTO ELIMINADO");
                                }

                                dDto.setCantidad(String
                                                .format("%.2f", d.getCantidad() != null ? d.getCantidad().doubleValue()
                                                                : 0.0)
                                                .replace(",", "."));
                                dDto.setPrecioUnitario(String
                                                .format("%.2f", d.getPrecioUnitario() != null ? d.getPrecioUnitario()
                                                                : 0.0)
                                                .replace(",", "."));
                                dDto.setDescuento(
                                                String.format("%.2f", d.getDescuento() != null ? d.getDescuento() : 0.0)
                                                                .replace(",", "."));
                                dDto.setPrecioTotalSinImpuesto(
                                                String.format("%.2f", d.getSubtotal() != null ? d.getSubtotal() : 0.0)
                                                                .replace(",", "."));

                                return dDto;
                        }).collect(java.util.stream.Collectors.toList()));
                }

                // --- Info Adicional ---
                if (invoice.getInfoAdicional() != null) {
                        dto.setInfoAdicional(invoice.getInfoAdicional().stream()
                                        .map(info -> new CampoAdicionalDTO(info.getNombre(),
                                                        info.getValor()))
                                        .collect(java.util.stream.Collectors.toList()));
                }

                return dto;
        }
}