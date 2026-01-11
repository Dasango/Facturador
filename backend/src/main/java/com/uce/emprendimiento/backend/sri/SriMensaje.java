package com.uce.emprendimiento.backend.sri;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SriMensaje {
    private String identificador;
    private String mensaje;
    private String informacionAdicional;
}
