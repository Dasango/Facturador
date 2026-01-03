package com.uce.emprendimiento.backend.sri.controller;

import com.uce.emprendimiento.backend.sri.model.Factura;
import com.uce.emprendimiento.backend.sri.service.SriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sri")
public class SriController {

    @Autowired
    private com.uce.emprendimiento.backend.repository.UserRepository userRepository;

    @Autowired
    private SriService sriService;

    @PostMapping("/emitir")
    public ResponseEntity<String> emitirFactura(
            @RequestBody Factura factura,
            @RequestParam Long userId,
            @RequestParam String claveFirma) {

        // 1. Obtener Usuario
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }
        var user = userOpt.get();

        if (user.getFirmaPath() == null || user.getFirmaPath().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("El usuario no tiene una firma electrónica configurada en su perfil.");
        }

        // 2. Usar path almacenado y clave proporcionada (o almacenada si se prefiere)
        // El usuario pide " popup" para firmar, así que asumimos que envia la clave por
        // seguridad
        String pathP12 = user.getFirmaPath();

        // Setup básico de datos del emisor desde el perfil
        if (factura.getInfoTributaria().getRazonSocial() == null
                || factura.getInfoTributaria().getRazonSocial().equals("EMPRESA DE PRUEBA")) {
            factura.getInfoTributaria().setRazonSocial(user.getRazonSocial() != null ? user.getRazonSocial()
                    : user.getNombres() + " " + user.getApellidos());
            factura.getInfoTributaria().setRuc(user.getRuc() != null ? user.getRuc() : "9999999999999");
        }

        String resultado = sriService.procesarFactura(factura, pathP12, claveFirma);
        return ResponseEntity.ok(resultado);
    }
}
