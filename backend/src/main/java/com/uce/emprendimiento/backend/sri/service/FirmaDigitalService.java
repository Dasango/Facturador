package com.uce.emprendimiento.backend.sri.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.crypto.dsig.*;
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
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Service
public class FirmaDigitalService {

    public byte[] firmarXml(byte[] xmlBytes, String pathFirma, String claveFirma) throws Exception {
        // 1. Cargar el Almacén de Claves (Keystore) PKCS12
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream is = new FileInputStream(pathFirma)) {
            ks.load(is, claveFirma.toCharArray());
        }

        // 2. Obtener el Alias (asumimos que hay uno solo o tomamos el primero)
        String alias = null;
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            if (ks.isKeyEntry(alias)) {
                break;
            }
        }

        if (alias == null) {
            throw new RuntimeException("No se encontró ningún alias de clave privada en el archivo P12");
        }

        // 3. Obtener Clave Privada y Certificado
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, claveFirma.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

        // 4. Preparar el Documento XML para firmar
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new ByteArrayInputStream(xmlBytes)));

        // 5. Configurar el contexto de firma (XMLDSig)
        // Nota: Para XAdES-BES real se requieren propiedades adicionales
        // (SignedProperties, etc.)
        // Aquí implementamos una firma XMLDSig estándar que es la base.
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference(
                "",
                fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null,
                null);

        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref));

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = List.of(cert); // Java 9+ List.of
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // 6. Firmar
        signature.sign(dsc);

        // 7. Retornar XML frimado como byte[]
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));

        return os.toByteArray();
    }
}
