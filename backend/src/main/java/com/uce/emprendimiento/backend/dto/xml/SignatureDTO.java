package com.uce.emprendimiento.backend.dto.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.w3c.dom.Element;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class SignatureDTO {
    // This catches the 'Signature' content as raw DOM elements to allow standard
    // JAXB marshalling/unmarshalling preserving the signature structure.
    @XmlAnyElement
    private List<Element> content;
}
