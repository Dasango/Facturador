package com.uce.emprendimiento.backend.sri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sri-prueba")
public class SriController {

    @Autowired
    private SriService sriService;

    @PostMapping(value = "/enviar", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<SriResponse> procesarFactura(@RequestBody String xmlFirmado) {
        SriResponse resultado = sriService.enviarAlSri(xmlFirmado);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping(value = "/solo-enviar", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<Boolean> procesarFacturaSoloEnviar(@RequestBody String xmlFirmado) {
        Boolean resultado = sriService.soloEnviar(xmlFirmado);
        return ResponseEntity.ok(resultado);
    }
}
