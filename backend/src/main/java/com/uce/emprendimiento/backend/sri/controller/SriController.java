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
    private SriService sriService;

    @PostMapping("/emitir")
    public ResponseEntity<String> emitirFactura(
            @RequestBody Factura factura,
            @RequestParam String pathP12,
            @RequestParam String claveP12) {

        String resultado = sriService.procesarFactura(factura, pathP12, claveP12);
        return ResponseEntity.ok(resultado);
    }
}
