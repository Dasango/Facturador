package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "factura")
@XmlType(propOrder = {
        "infoTributaria",
        "infoFactura",
        "detalles",
        "infoAdicional",
        "signature"
})
public class FacturaDTO {

    @XmlAttribute(name = "id")
    private String id; // "comprobante"

    @XmlAttribute(name = "version")
    private String version; // "1.1.0"

    private InfoTributariaDTO infoTributaria;
    private InfoFacturaDTO infoFactura;

    @XmlElementWrapper(name = "detalles")
    @XmlElement(name = "detalle")
    private List<DetalleDTO> detalles;

    @XmlElementWrapper(name = "infoAdicional")
    @XmlElement(name = "campoAdicional")
    private List<CampoAdicionalDTO> infoAdicional;

    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    private SignatureDTO signature;
}
