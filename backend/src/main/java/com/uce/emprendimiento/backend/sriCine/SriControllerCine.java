package com.uce.emprendimiento.backend.sriCine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sri-prueba")
public class SriControllerCine {

    @Autowired
    private SriServiceCine sriService;

    @PostMapping(value = "/enviar", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<SriResponseCine> procesarFactura(@RequestBody String xmlFirmado) {
        SriResponseCine resultado = sriService.enviarAlSri(xmlFirmado);
        return ResponseEntity.ok(resultado);
    }
}
