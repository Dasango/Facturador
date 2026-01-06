package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.XmlAnyElement;
import lombok.Data;
import org.w3c.dom.Element;
import java.util.List;

@Data
public class SignatureDTO {
    // This catches the 'Signature' content as raw DOM elements to allow standard
    // JAXB marshalling/unmarshalling preserving the signature structure.
    @XmlAnyElement
    private List<Element> content;
}
