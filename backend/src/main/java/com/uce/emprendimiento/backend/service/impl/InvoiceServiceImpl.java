package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.dto.xml.*;
import com.uce.emprendimiento.backend.entity.Invoice;
import com.uce.emprendimiento.backend.entity.Product;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.InvoiceRepository;
import com.uce.emprendimiento.backend.repository.ProductRepository;
import com.uce.emprendimiento.backend.repository.UserRepository;
import com.uce.emprendimiento.backend.service.InvoiceService;
import com.uce.emprendimiento.backend.sri.SriService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

        private final InvoiceRepository invoiceRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final XmlServiceImpl xmlService;
        private final SriService sriServiceCine;

        @Override
        @Transactional(readOnly = true)
        public List<Invoice> getInvoicesByUserId(Long userId) {
                // Llamamos al método optimizado del repositorio
                List<Invoice> facturas = invoiceRepository.findByUsuario_Id(userId);
                // Inicializamos colecciones lazy para serialización
                facturas.forEach(invoice -> {
                        if (invoice != null) {
                                invoice.getPagos().size();
                                invoice.getDetalles().forEach(d -> {
                                        if (d.getProducto() != null)
                                                d.getProducto().getNombre();
                                });
                                invoice.getInfoAdicional().size();
                        }
                });
                return facturas;
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<Invoice> getInvoiceByIdAndUserId(Long id, Long userId) {
                Optional<Invoice> opt = invoiceRepository.findByIdAndUsuarioId(id, userId);
                opt.ifPresent(invoice -> {
                        invoice.getPagos().size();
                        if (invoice.getDetalles() != null) {
                                invoice.getDetalles().forEach(d -> {
                                        if (d.getProducto() != null)
                                                d.getProducto().getNombre();
                                });
                        }
                        invoice.getInfoAdicional().size();
                });
                return opt;
        }

        @Override
        @Transactional
        public Invoice crearFactura(Invoice factura, Long userId, String accion) {
                return crearFactura(factura, userId, accion, null);
        }

        @Transactional
        public Invoice crearFactura(Invoice factura, Long userId, String accion, String claveFirma) {

                User user = new User();
                user.setId(userId);
                factura.setUsuario(user);

                if (factura.getDetalles() != null) {
                        for (var detalle : factura.getDetalles()) {
                                detalle.setFactura(factura);

                                Product inputProd = detalle.getProducto();
                                if (inputProd != null) {
                                        Optional<Product> existingProd = productRepository
                                                        .findByCodigoPrincipalAndUsuarioId(
                                                                        inputProd.getCodigoPrincipal(), userId);

                                        if (existingProd.isPresent()) {
                                                detalle.setProducto(existingProd.get());
                                        } else {
                                                // Crear nuevo producto automáticamente
                                                inputProd.setUsuario(user);
                                                if (inputProd.getCodigoImpuesto() == null)
                                                        inputProd.setCodigoImpuesto("2"); // IVA Default
                                                if (inputProd.getCodigoPorcentaje() == null)
                                                        inputProd.setCodigoPorcentaje("2"); // 12% Default (adjustable)
                                                if (inputProd.getTarifa() == null)
                                                        inputProd.setTarifa(12.0); // 12%
                                                inputProd = productRepository.save(inputProd);
                                                detalle.setProducto(inputProd);
                                        }
                                }
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

                factura.setEstado("PENDIENTE");
                factura = invoiceRepository.save(factura);

                if ("ENVIAR".equals(accion)) {
                        if (claveFirma == null || claveFirma.isEmpty()) {
                                throw new RuntimeException("Se requiere clave de firma para enviar al SRI");
                        }

                        try {
                                // Reload EXACT User from DB to ensure we have firmaPath
                                User fullUser = userRepository.findById(userId)
                                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                                FacturaDTO dto = getFacturaDTO(factura.getId(), userId);
                                String xmlContent = xmlService.objectToXml(dto);

                                if (fullUser.getFirmaPath() == null)
                                        throw new RuntimeException(
                                                        "El usuario no tiene configurada firma electrónica (.p12)");
                                String signedXml = xmlService.signXml(xmlContent, fullUser.getFirmaPath(), claveFirma);

                                var sriResponse = sriServiceCine.enviarAlSri(signedXml);

                                factura.setXmlContent(signedXml);
                                factura.setEstado(sriResponse.getEstado());
                                factura.setMensajeSri(xmlService.toString());
                                if (sriResponse.getMensajes() != null && !sriResponse.getMensajes().isEmpty()) {
                                        factura.setMensajeSri(sriResponse.getMensajes().toString());
                                }

                                if ("AUTORIZADO".equals(sriResponse.getEstado())) {
                                        factura.setFechaAutorizacion(java.time.LocalDateTime.now());
                                        if (sriResponse.getClaveAcceso() != null)
                                                factura.setClaveAcceso(sriResponse.getClaveAcceso());
                                }

                                factura = invoiceRepository.save(factura);

                        } catch (Exception e) {
                                e.printStackTrace();
                                factura.setMensajeSri("Error envío: " + e.getMessage());
                                invoiceRepository.save(factura);
                                throw new RuntimeException("Error en proceso SRI: " + e.getMessage(), e);
                        }
                }

                return factura;
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

                // cosas paa el secuential
                // 1. Obtenemos el valor crudo
                String rawSecuencial = invoice.getNumeroComprobante();
                String secuencialLimpio;

                if (rawSecuencial != null) {
                        if (rawSecuencial.contains("-")) {
                                // CASO A: Viene completo "001-001-000000001" -> Tomamos el último pedazo
                                String[] partes = rawSecuencial.split("-");
                                secuencialLimpio = partes[partes.length - 1];
                        } else {
                                // CASO B: Viene solo el número "1" o "123"
                                secuencialLimpio = rawSecuencial;
                        }
                } else {
                        // CASO C: Es nulo, usamos el ID como respaldo
                        secuencialLimpio = String.valueOf(invoice.getId());
                }

                // 2. IMPORTANTE: Formatear a 9 dígitos (Rellenar con ceros a la izquierda)
                // El SRI rechaza "1", exige "000000001"
                try {
                        long numero = Long.parseLong(secuencialLimpio);
                        infoTrib.setSecuencial(String.format("%09d", numero));
                } catch (NumberFormatException e) {
                        // Por si acaso venga basura que no sea número
                        infoTrib.setSecuencial("000000001");
                }
                // fin secuential

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