package com.uce.emprendimiento.backend.sriCine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SriMensajeCine {
    private String identificador;
    private String mensaje;
    private String informacionAdicional;
}
