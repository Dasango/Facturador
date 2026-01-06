package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.dto.xml.FacturaDTO;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.security.CustomUserDetails;
import com.uce.emprendimiento.backend.service.InvoiceService;
import com.uce.emprendimiento.backend.service.SriService;
import com.uce.emprendimiento.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/xml")
@RequiredArgsConstructor
public class InvoiceXmlController {

    private final InvoiceService invoiceService;
    private final SriService sriService;
    private final UserService userService;

    // 1. Generar XML (Sin firmar) a partir de un DTO
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> generateXml(@RequestBody FacturaDTO facturaDTO) {
        try {
            String xml = sriService.objectToXml(facturaDTO);
            return ResponseEntity.ok(xml);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("<error>" + e.getMessage() + "</error>");
        }
    }

    // 2. Firmar XML existente
    @PostMapping(value = "/sign", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> signXml(@RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        String xml = request.get("xml");
        String password = request.get("password"); // Contraseña del certificado .p12

        try {
            // Obtener path de la firma del usuario
            User user = userService.getUserById(userDetails.getUser().getId());
            if (user.getFirmaPath() == null) {
                return ResponseEntity.badRequest().body("<error>Usuario no tiene firma configurada</error>");
            }

            String signedXml = sriService.signXml(xml, user.getFirmaPath(), password);
            return ResponseEntity.ok(signedXml);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("<error>Error firmando: " + e.getMessage() + "</error>");
        }
    }

    // 3. Crear XML + Firmar (Método completo)
    @PostMapping(value = "/create-signed", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> createSignedInvoice(@RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        try {
            // a. Obtener datos. Puede venir el ID de la factura O el objeto factura
            // completo.
            // Por simplicidad, asumiremos que viene el ID de la factura ya creada en
            // borrador.
            Long invoiceId = Long.valueOf(request.get("invoiceId").toString());
            String password = request.get("password").toString();

            // b. Convertir a DTO
            FacturaDTO facturaDTO = invoiceService.getFacturaDTO(invoiceId, userDetails.getUser().getId());

            // c. Generar XML
            String xml = sriService.objectToXml(facturaDTO);

            // d. Obtener Path Firma
            User user = userService.getUserById(userDetails.getUser().getId());
            if (user.getFirmaPath() == null) {
                return ResponseEntity.badRequest().body("<error>Usuario no tiene firma configurada</error>");
            }

            // e. Firmar
            String signedXml = sriService.signXml(xml, user.getFirmaPath(), password);

            return ResponseEntity.ok(signedXml);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("<error>Error generando factura firmada: " + e.getMessage() + "</error>");
        }
    }

    // Endpoint Mock original (para referencia o tests simples)
    @GetMapping(value = "/factura", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getFacturaXml() {

        // Este XML quemado simula una factura REAL autorizada por el SRI
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <factura id="comprobante" version="1.1.0">
                    <infoTributaria>
                        <ambiente>1</ambiente>
                        <tipoEmision>1</tipoEmision>
                        <razonSocial>SANGO PILLALAZA EDGAR ABEL</razonSocial>
                        <nombreComercial>FACTO SYSTEM</nombreComercial>
                        <ruc>1799999999001</ruc>
                        <claveAcceso>2712202501179999999900120010010000000341234567811</claveAcceso>
                        <codDoc>01</codDoc>
                        <estab>001</estab>
                        <ptoEmi>100</ptoEmi>
                        <secuencial>000000034</secuencial>
                        <dirMatriz>Av. Amazonas N24-196 y Av. Naciones Unidas, Quito</dirMatriz>
                    </infoTributaria>
                    <infoFactura>
                        <fechaEmision>27/12/2025</fechaEmision>
                        <dirEstablecimiento>Av. Amazonas N24-196 y Av. Naciones Unidas, Quito</dirEstablecimiento>
                        <obligadoContabilidad>NO</obligadoContabilidad>
                        <tipoIdentificacionComprador>05</tipoIdentificacionComprador>
                        <razonSocialComprador>CLIENTE DE PRUEBAS</razonSocialComprador>
                        <identificacionComprador>9999999999</identificacionComprador>
                        <direccionComprador>Ciudad de Quito, Ecuador</direccionComprador>
                        <totalSinImpuestos>950.00</totalSinImpuestos>
                        <totalDescuento>0.00</totalDescuento>
                        <totalConImpuestos>
                            <totalImpuesto>
                                <codigo>2</codigo>
                                <codigoPorcentaje>0</codigoPorcentaje>
                                <baseImponible>950.00</baseImponible>
                                <valor>0.00</valor>
                            </totalImpuesto>
                        </totalConImpuestos>
                        <propina>0.00</propina>
                        <importeTotal>950.00</importeTotal>
                        <moneda>DOLAR</moneda>
                        <pagos>
                            <pago>
                                <formaPago>20</formaPago>
                                <total>950.00</total>
                            </pago>
                        </pagos>
                    </infoFactura>
                    <detalles>
                        <detalle>
                            <codigoPrincipal>SERV-001</codigoPrincipal>
                            <codigoAuxiliar>TRANSP-01</codigoAuxiliar>
                            <descripcion>SERVICIO DE TRANSPORTE PRIVADO</descripcion>
                            <cantidad>1.00</cantidad>
                            <precioUnitario>950.00</precioUnitario>
                            <descuento>0.00</descuento>
                            <precioTotalSinImpuesto>950.00</precioTotalSinImpuesto>
                            <impuestos>
                                <impuesto>
                                    <codigo>2</codigo>
                                    <codigoPorcentaje>0</codigoPorcentaje>
                                    <tarifa>0.00</tarifa>
                                    <baseImponible>950.00</baseImponible>
                                    <valor>0.00</valor>
                                </impuesto>
                            </impuestos>
                        </detalle>
                    </detalles>
                    <infoAdicional>
                        <campoAdicional nombre="Telefono">0999999999</campoAdicional>
                        <campoAdicional nombre="Email">pruebas@ejemplo.com</campoAdicional>
                    </infoAdicional>
                    <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" Id="xmldsig-firma-prueba">
                        <ds:SignedInfo>
                            <ds:CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
                            <ds:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
                            <ds:Reference URI="#comprobante">
                                <ds:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
                                <ds:DigestValue>HASH_SIMULADO==</ds:DigestValue>
                            </ds:Reference>
                        </ds:SignedInfo>
                        <ds:SignatureValue>FIRMA_SIMULADA_BASE64...</ds:SignatureValue>
                    </ds:Signature>
                </factura>
                """;

        return ResponseEntity.ok(xmlContent);
    }
}