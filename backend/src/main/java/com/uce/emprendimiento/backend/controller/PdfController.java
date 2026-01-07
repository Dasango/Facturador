package com.uce.emprendimiento.backend.controller;

import com.uce.emprendimiento.backend.util.GeneradorFactura;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody String jsonContent) {
        try {
            System.out.println("PdfController: Generating PDF...");
            JSONObject data = new JSONObject(jsonContent);
            byte[] pdfBytes = GeneradorFactura.generarPdfBytes(data);
            System.out.println("PdfController: PDF Generated. Size: " + pdfBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "factura.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("PdfController Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
