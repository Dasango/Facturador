package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.dto.xml.FacturaDTO;
import com.uce.emprendimiento.backend.service.XmlService;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.xml.crypto.dsig.*;
import java.util.Collections;
import java.util.List;

@Service
public class XmlServiceImpl implements XmlService {

    public String objectToXml(FacturaDTO facturaDTO) throws Exception {
        JAXBContext context = JAXBContext.newInstance(FacturaDTO.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // UTF-8
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        marshaller.marshal(facturaDTO, writer);
        return writer.toString();
    }

    // Firma lógica básica manual (Simulación de estructura XAdES-BES válida para
    // SRI)
    // NOTA: Implementar XAdES estricto desde cero es muy complejo y propenso a
    // errores.
    // Aquí implementaremos la inyección de la firma calculada usando Bouncy Castle
    // si fuera necesario,
    // o Java Security estándar para firmar el digest.
    public String signXml(String xmlContent, String p12Path, String password) throws Exception {

        // ---------------------------------------------------------
        // 1. CARGAR EL KEYSTORE Y CERTIFICADO (REAL)
        // ---------------------------------------------------------
        // Nota: Si quieres usar la "memoria" como hablamos antes, cambia
        // FileInputStream
        // por un ByteArrayInputStream con los bytes que le pases al método.
        KeyStore ks = KeyStore.getInstance("PKCS12");

        InputStream sourceStream = null;
        if (p12Path.startsWith("http")) {
            sourceStream = new java.net.URL(p12Path).openStream();
        } else {
            sourceStream = new FileInputStream(p12Path);
        }

        try (InputStream is = sourceStream) {
            ks.load(is, password.toCharArray());
        } catch (java.io.FileNotFoundException e) {
            throw new Exception(
                    "El archivo de firma electrónica no existe en la ruta configurada (verifique su perfil).");
        } catch (java.io.IOException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("password")) {
                throw new Exception("La contraseña de la firma es incorrecta.");
            }
            throw new Exception("Error cargando firma electrónica: " + e.getMessage());
        }

        // Obtener el alias (usualmente hay uno solo)
        String alias = ks.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

        // ---------------------------------------------------------
        // 2. PREPARAR EL XML PARA FIRMAR
        // ---------------------------------------------------------
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true); // CRÍTICO para firmas XML
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xmlContent)));

        // ---------------------------------------------------------
        // 3. CONFIGURAR LA FIRMA (XMLDSig)
        // ---------------------------------------------------------
        // Crear factory para firmas XML
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // a. Crear la REFERENCIA (Qué vamos a firmar: Todo el documento)
        // El URI="" significa "la raíz del documento".
        // Transform: ENVELOPED (La firma estará DENTRO del XML, no afuera)
        Reference ref = fac.newReference(
                "",
                fac.newDigestMethod(DigestMethod.SHA1, null), // SRI suele usar SHA1, aunque acepta SHA256
                Collections.singletonList(
                        fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null,
                null);

        // b. Crear el SIGNEDINFO (Cómo vamos a firmar)
        // Canonicalization: INCLUSIVE (Para estandarizar el XML antes del hash)
        // SignatureMethod: RSA_SHA1
        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref));

        // c. Crear el KEYINFO (Datos públicos para que el SRI verifique)
        // Incluimos el Certificado X509
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = new ArrayList<>();
        x509Content.add(cert);
        X509Data x509Data = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509Data));

        // ---------------------------------------------------------
        // 4. EJECUTAR LA FIRMA CRIPTOGRÁFICA
        // ---------------------------------------------------------
        // El contexto de firma define DÓNDE se pondrá la firma y con qué llave privada
        DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());

        // Crear el objeto XMLSignature y firmar
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // ¡AQUÍ OCURRE LA MAGIA REAL! (Nada de mocks)
        // Calcula el hash, cifra con RSA y modifica el DOM insertando el nodo
        // <Signature>
        signature.sign(dsc);

        // ---------------------------------------------------------
        // 5. CONVERTIR DOM FIRMADO A STRING
        // ---------------------------------------------------------
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        StringWriter writer = new StringWriter();
        trans.transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    public String extraerEmailDeInfoAdicionalXML(String xmlContent) {
        System.out.println("ENTRAAAAAemail");
        try {
            if (xmlContent == null || xmlContent.isEmpty()) {
                return "No hay correo";
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlContent)));

            org.w3c.dom.NodeList infoAdicionalList = doc.getElementsByTagName("infoAdicional");
            if (infoAdicionalList.getLength() > 0) {
                Element infoAdicional = (Element) infoAdicionalList.item(0);
                org.w3c.dom.NodeList campos = infoAdicional.getElementsByTagName("campoAdicional");

                for (int i = 0; i < campos.getLength(); i++) {
                    Element campo = (Element) campos.item(i);
                    String nombre = campo.getAttribute("nombre");
                    if ("Email".equalsIgnoreCase(nombre)) {
                        String email = campo.getTextContent();
                        return (email != null && !email.trim().isEmpty()) ? email : "No hay correo";
                    }
                }
            }
            System.out.println("SALeeeeeeeeemail");
            return "No hay correo";

        } catch (Exception e) {
            System.out.println("SALeeeeeeeeemailpero mal");
            e.printStackTrace();
            return "No hay correo";
        }
    }

    public String extraerEmailDeInfoAdicionalJSON(String jsonContent) {
        try {
            if (jsonContent == null || jsonContent.isEmpty()) {
                return "No hay correo";
            }
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonContent);
            if (jsonObject.has("infoAdicional")) {
                org.json.JSONArray infoAdicional = jsonObject.getJSONArray("infoAdicional");
                for (int i = 0; i < infoAdicional.length(); i++) {
                    org.json.JSONObject item = infoAdicional.getJSONObject(i);
                    if (item.has("nombre") && "Email".equalsIgnoreCase(item.getString("nombre"))) {
                        String email = item.optString("valor");
                        return (email != null && !email.trim().isEmpty()) ? email : "No hay correo";
                    }
                }
            }
            return "No hay correo";
        } catch (Exception e) {
            e.printStackTrace();
            return "No hay correo";
        }
    }
}
