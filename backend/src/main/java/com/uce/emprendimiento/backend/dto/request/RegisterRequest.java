package com.uce.emprendimiento.backend.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String cedula;
    private String correo;
    private String contrasena;
}
