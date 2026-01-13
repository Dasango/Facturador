package com.uce.emprendimiento.backend.service;

import com.uce.emprendimiento.backend.dto.xml.FacturaDTO;

public interface XmlService {
    public String objectToXml(FacturaDTO facturaDTO) throws Exception;

    public String signXml(String xmlContent, String p12Path, String password) throws Exception;

    public String extraerEmailDeInfoAdicional(String xmlContent);

}
