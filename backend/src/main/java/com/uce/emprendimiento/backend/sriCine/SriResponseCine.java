package com.uce.emprendimiento.backend.sriCine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SriResponseCine {
    private String estado;
    private String claveAcceso;
    private String fechaAutorizacion;
    private List<SriMensajeCine> mensajes;
    // Nuevo campo para que veas el XML real en Thunder Client
    private String xmlRespuestaSriCrudo; 
}