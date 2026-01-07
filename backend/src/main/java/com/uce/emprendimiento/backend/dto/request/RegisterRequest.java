package com.uce.emprendimiento.backend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterRequest {
    private String ruc;
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasena;

    // Campos SRI
    private String razonSocial;
    private String nombreComercial;
    private String direccionMatriz;
    private String codigoEstablecimiento;
    private String codigoPuntoEmision;
    private String obligadoContabilidad;
    private String nroContribuyenteEspecial;

    // Archivos (MultipartFile para backend upload)
    private MultipartFile firma;
    private MultipartFile logo;
    private String firmaPassword;
}
