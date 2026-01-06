package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampoAdicionalDTO {

    @XmlAttribute(name = "nombre")
    private String nombre;

    @XmlValue
    private String valor;
}
