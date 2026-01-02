package com.uce.emprendimiento.backend.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String cedula;
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasena;
}
