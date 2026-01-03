package com.uce.emprendimiento.backend.sri.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClaveAccesoHelper {

    /**
     * Genera la Clave de Acceso de 49 dígitos requerida por el SRI.
     *
     * @param fechaEmision    Fecha de emisión de la factura.
     * @param tipoComprobante Tipo de comprobante (ej: "01" para factura).
     * @param ruc             RUC del emisor.
     * @param ambiente        Tipo de ambiente (1: Pruebas, 2: Producción).
     * @param serie           Serie del comprobante (Establecimiento + Punto de
     *                        Emisión).
     * @param secuencial      Número secuencial del comprobante.
     * @param codigoNumerico  Código numérico (8 dígitos).
     * @param tipoEmision     Tipo de emisión (1: Normal).
     * @return Clave de acceso generada de 49 dígitos.
     */
    public static String generarClaveAcceso(Date fechaEmision, String tipoComprobante, String ruc, String ambiente,
            String serie, String secuencial, String codigoNumerico, String tipoEmision) {
        StringBuilder clave = new StringBuilder();

        // 1. Fecha de Emisión (ddMMyyyy)
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        clave.append(sdf.format(fechaEmision));

        // 2. Tipo de Comprobante
        clave.append(tipoComprobante);

        // 3. RUC
        clave.append(ruc);

        // 4. Tipo de Ambiente
        clave.append(ambiente);

        // 5. Serie
        clave.append(serie);

        // 6. Número Secuencial
        clave.append(secuencial);

        // 7. Código Numérico
        clave.append(codigoNumerico);

        // 8. Tipo de Emisión
        clave.append(tipoEmision);

        // 9. Dígito Verificador
        String digitoVerificador = generarDigitoVerificador(clave.toString());
        clave.append(digitoVerificador);

        // Validar longitud
        if (clave.length() != 49) {
            throw new RuntimeException("La clave de acceso generada no tiene 49 dígitos: " + clave.length());
        }

        return clave.toString();
    }

    /**
     * Algoritmo de Módulo 11 para generar el dígito verificador.
     */
    private static String generarDigitoVerificador(String clave) {
        int factor = 2;
        int suma = 0;

        // Recorrer la cadena de derecha a izquierda
        for (int i = clave.length() - 1; i >= 0; i--) {
            int digito = Integer.parseInt(String.valueOf(clave.charAt(i)));
            suma += digito * factor;
            factor++;
            if (factor > 7) {
                factor = 2; // Reiniciar factor
            }
        }

        int residuo = suma % 11;
        int digito = 11 - residuo;

        if (digito == 11) {
            digito = 0;
        } else if (digito == 10) {
            digito = 1;
        }

        return String.valueOf(digito);
    }
}
