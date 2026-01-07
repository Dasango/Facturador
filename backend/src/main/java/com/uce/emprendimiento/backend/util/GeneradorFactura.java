package com.uce.emprendimiento.backend.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;
import java.io.FileOutputStream; // NOTE: We might want to switch to ByteArrayOutputStream if we want to return bytes directly without file I/O
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class GeneradorFactura {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // Overloaded method to generate to a specific file path
    public static void generarPdf(JSONObject data, String rutaDestino) throws DocumentException, IOException {
        try (FileOutputStream fos = new FileOutputStream(rutaDestino)) {
            generarPdfToStream(data, fos);
        }
    }

    // New method that returns byte array
    public static byte[] generarPdfBytes(JSONObject data) throws DocumentException, IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            generarPdfToStream(data, baos);
            return baos.toByteArray();
        }
    }

    private static void generarPdfToStream(JSONObject data, java.io.OutputStream outputStream)
            throws DocumentException, IOException {
        JSONObject infoTributaria = data.optJSONObject("infoTributaria");
        if (infoTributaria == null)
            infoTributaria = new JSONObject();

        JSONObject infoFactura = data.optJSONObject("infoFactura");
        if (infoFactura == null)
            infoFactura = new JSONObject();

        JSONArray detalles = data.optJSONArray("detalles");
        if (detalles == null)
            detalles = new JSONArray();

        // 1. Configuración del Documento
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Estilos
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 86, 179));
        Font negritaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font headerTablaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 7);

        // --- ENCABEZADO ---
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] { 50, 50 });

        // 1.1 Datos Emisor (Izquierda)
        PdfPTable emisorTable = new PdfPTable(1);
        emisorTable.setWidthPercentage(100);

        // Logo omitted for now

        addTextCell(emisorTable, infoTributaria.optString("razonSocial", "RAZÓN SOCIAL"), tituloFont,
                Rectangle.NO_BORDER);
        addTextCell(emisorTable, "Nombre Comercial: " + infoTributaria.optString("nombreComercial", ""), normalFont,
                Rectangle.NO_BORDER);
        addTextCell(emisorTable, "RUC: " + infoTributaria.optString("ruc", ""), normalFont, Rectangle.NO_BORDER);
        addTextCell(emisorTable, "Dir. Matriz: " + infoTributaria.optString("dirMatriz", ""), normalFont,
                Rectangle.NO_BORDER);
        addTextCell(emisorTable, "Dir. Sucursal: " + infoFactura.optString("dirEstablecimiento", ""), normalFont,
                Rectangle.NO_BORDER);
        addTextCell(emisorTable,
                "Obligado a llevar contabilidad: " + infoFactura.optString("obligadoContabilidad", "NO"), normalFont,
                Rectangle.NO_BORDER);
        if (infoFactura.has("contribuyenteEspecial")) {
            addTextCell(emisorTable, "Contribuyente Especial Nro: " + infoFactura.optString("contribuyenteEspecial"),
                    normalFont, Rectangle.NO_BORDER);
        }

        PdfPCell cellIzq = new PdfPCell(emisorTable);
        cellIzq.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(cellIzq);

        // 1.2 Datos Factura (Derecha)
        PdfPTable facturaTable = new PdfPTable(1);
        facturaTable.setWidthPercentage(100);

        String sec = infoTributaria.optString("estab", "000") + "-" +
                infoTributaria.optString("ptoEmi", "000") + "-" +
                infoTributaria.optString("secuencial", "000000000");

        addTextCell(facturaTable, "R.U.C.: " + infoTributaria.optString("ruc", ""), normalFont, Rectangle.NO_BORDER);
        addTextCell(facturaTable, "FACTURA", tituloFont, Rectangle.NO_BORDER);
        addTextCell(facturaTable, "No. " + sec, normalFont, Rectangle.NO_BORDER);

        addTextCell(facturaTable, "CLAVE DE ACCESO", negritaFont, Rectangle.NO_BORDER);
        String claveAcceso = infoTributaria.isNull("claveAcceso") ? "PENDIENTE"
                : infoTributaria.optString("claveAcceso");
        Font monoFont = FontFactory.getFont(FontFactory.COURIER, 8);
        addTextCell(facturaTable, claveAcceso, monoFont, Rectangle.NO_BORDER);

        // Ambiente y Tipo Emisión
        String ambiente = "1".equals(infoTributaria.optString("ambiente")) ? "PRUEBAS" : "PRODUCCIÓN";
        String tipoEmision = "1".equals(infoTributaria.optString("tipoEmision")) ? "NORMAL" : "INDISPONIBILIDAD";
        addTextCell(facturaTable, "AMBIENTE: " + ambiente, normalFont, Rectangle.NO_BORDER);
        addTextCell(facturaTable, "EMISIÓN: " + tipoEmision, normalFont, Rectangle.NO_BORDER);

        PdfPCell cellDer = new PdfPCell(facturaTable);
        cellDer.setBorderColor(new Color(0, 86, 179));
        cellDer.setBorderWidth(1);
        cellDer.setPadding(10);
        headerTable.addCell(cellDer);

        document.add(headerTable);
        document.add(new Paragraph(" "));

        // --- CLIENTE ---
        PdfPTable clienteTable = new PdfPTable(2);
        clienteTable.setWidthPercentage(100);
        clienteTable.setWidths(new float[] { 65, 35 });

        addTextCell(clienteTable, "Razón Social / Nombres: " + infoFactura.optString("razonSocialComprador", ""),
                normalFont, Rectangle.BOTTOM);
        addTextCell(clienteTable, "Identificación: " + infoFactura.optString("identificacionComprador", ""), normalFont,
                Rectangle.BOTTOM);
        addTextCell(clienteTable, "Fecha Emisión: " + infoFactura.optString("fechaEmision", ""), normalFont,
                Rectangle.NO_BORDER);

        // Guia Remision if present
        if (infoFactura.has("guiaRemision")) {
            addTextCell(clienteTable, "Guía Remisión: " + infoFactura.optString("guiaRemision"), normalFont,
                    Rectangle.NO_BORDER);
        } else {
            addTextCell(clienteTable, "", normalFont, Rectangle.NO_BORDER);
        }

        addTextCell(clienteTable, "Dirección: " + infoFactura.optString("direccionComprador", ""), normalFont,
                Rectangle.NO_BORDER);
        if (infoFactura.has("tipoIdentificacionComprador")) {
            addTextCell(clienteTable, "Tipo ID: " + infoFactura.optString("tipoIdentificacionComprador"), smallFont,
                    Rectangle.NO_BORDER);
        }

        document.add(clienteTable);
        document.add(new Paragraph(" "));

        // --- DETALLES ---
        // Columns: Cod. Princ., Cod. Aux., Cant., Descripcion, P. Unit, Desc., P. Total
        PdfPTable itemsTable = new PdfPTable(7);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[] { 10, 10, 8, 35, 12, 10, 15 });

        String[] headers = { "Cod. Princ.", "Cod. Aux.", "Cant.", "Descripción", "P. Unit", "Desc.", "Precio Total" };
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerTablaFont));
            cell.setBackgroundColor(new Color(0, 86, 179));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            itemsTable.addCell(cell);
        }

        for (int i = 0; i < detalles.length(); i++) {
            JSONObject item = detalles.getJSONObject(i);

            String cPrinc = item.optString("codigoPrincipal", "");
            String cAux = item.isNull("codigoAuxiliar") ? "" : item.optString("codigoAuxiliar", "");
            String cant = item.optString("cantidad", "0");
            String descrip = item.optString("descripcion", "");
            String pUnit = item.optString("precioUnitario", "0.00");
            String desc = item.optString("descuento", "0.00");
            String pTotal = item.optString("precioTotalSinImpuesto", "0.00");

            addCell(itemsTable, cPrinc, normalFont, Element.ALIGN_LEFT);
            addCell(itemsTable, cAux, normalFont, Element.ALIGN_LEFT);
            addCell(itemsTable, cant, normalFont, Element.ALIGN_CENTER);
            addCell(itemsTable, descrip, normalFont, Element.ALIGN_LEFT);
            addCell(itemsTable, pUnit, normalFont, Element.ALIGN_RIGHT);
            addCell(itemsTable, desc, normalFont, Element.ALIGN_RIGHT);
            addCell(itemsTable, pTotal, normalFont, Element.ALIGN_RIGHT);
        }
        document.add(itemsTable);
        document.add(new Paragraph(" "));

        // --- INFO ADICIONAL y PAGOS (Izquierda) vs TOTALES (Derecha) ---
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[] { 60, 40 });

        PdfPTable leftInfoTable = new PdfPTable(1);

        // Info Adicional
        Paragraph infoAdTitle = new Paragraph("Información Adicional", negritaFont);
        PdfPCell cellTitle = new PdfPCell(infoAdTitle);
        cellTitle.setBorder(Rectangle.NO_BORDER);
        leftInfoTable.addCell(cellTitle);

        try {
            JSONArray infoAdicional = data.optJSONArray("infoAdicional");
            if (infoAdicional != null) {
                for (int i = 0; i < infoAdicional.length(); i++) {
                    JSONObject info = infoAdicional.getJSONObject(i);
                    String nombre = info.optString("nombre", "");
                    String valor = info.optString("__text", info.optString("valor", ""));
                    if (valor.isEmpty() && info.length() > 0 && !info.keys().hasNext())
                        valor = info.toString();
                    if (!nombre.isEmpty()) {
                        addTextCell(leftInfoTable, nombre + ": " + valor, normalFont, Rectangle.NO_BORDER);
                    }
                }
            }
        } catch (Exception e) {
            // Ignore parse errors here
        }

        addTextCell(leftInfoTable, " ", normalFont, Rectangle.NO_BORDER);

        // Pagos
        Paragraph pagosTitle = new Paragraph("Formas de Pago", negritaFont);
        PdfPCell cellPagosTitle = new PdfPCell(pagosTitle);
        cellPagosTitle.setBorder(Rectangle.NO_BORDER);
        leftInfoTable.addCell(cellPagosTitle);

        if (infoFactura.has("pagos")) {
            JSONArray pagos = infoFactura.getJSONArray("pagos");
            for (int i = 0; i < pagos.length(); i++) {
                JSONObject p = pagos.getJSONObject(i);
                String forma = p.optString("formaPago", "01");
                String total = p.optString("total", "0.00");
                String plazo = p.optString("plazo", "0");
                String unidad = p.optString("unidadTiempo", "dias");

                String pagoStr = mapFormaPago(forma) + " - " + total;
                // Only show plazo if it's meaningful
                try {
                    double dPlazo = Double.parseDouble(plazo);
                    if (dPlazo > 0) {
                        pagoStr += " (" + plazo + " " + unidad + ")";
                    }
                } catch (Exception e) {
                }

                addTextCell(leftInfoTable, pagoStr, normalFont, Rectangle.NO_BORDER);
            }
        }

        PdfPCell cellLeft = new PdfPCell(leftInfoTable);
        cellLeft.setBorder(Rectangle.NO_BORDER);
        footerTable.addCell(cellLeft);

        // Totals (Derecha)
        PdfPTable tablaTotales = new PdfPTable(2);
        tablaTotales.setWidthPercentage(100);
        tablaTotales.setWidths(new float[] { 60, 40 });

        addTotalRow(tablaTotales, "SUBTOTAL 12%", calcularSubtotalPorImpuesto(infoFactura, "2"), normalFont);
        addTotalRow(tablaTotales, "SUBTOTAL 15%", calcularSubtotalPorImpuesto(infoFactura, "4"), normalFont); // 15%
                                                                                                              // code
                                                                                                              // might
                                                                                                              // be 4 or
                                                                                                              // 2
                                                                                                              // depending
                                                                                                              // on era,
                                                                                                              // placeholder
        addTotalRow(tablaTotales, "SUBTOTAL 0%", calcularSubtotalPorImpuesto(infoFactura, "0"), normalFont);
        addTotalRow(tablaTotales, "SUBTOTAL NO OBJETO IVA", calcularSubtotalPorImpuesto(infoFactura, "6"), normalFont);
        addTotalRow(tablaTotales, "SUBTOTAL EXENTO IVA", calcularSubtotalPorImpuesto(infoFactura, "7"), normalFont);
        addTotalRow(tablaTotales, "SUBTOTAL SIN IMPUESTOS", infoFactura.optString("totalSinImpuestos", "0.00"),
                normalFont);
        addTotalRow(tablaTotales, "TOTAL DESCUENTO", infoFactura.optString("totalDescuento", "0.00"), normalFont);
        addTotalRow(tablaTotales, "ICE", "0.00", normalFont);

        // IVA details
        JSONArray impuestos = infoFactura.optJSONArray("totalConImpuestos");
        double totalIva = 0;
        if (impuestos != null) {
            for (int i = 0; i < impuestos.length(); i++) {
                JSONObject imp = impuestos.getJSONObject(i);
                // Sum all that are considered IVA
                String code = imp.optString("codigo"); // 2 is IVA
                if ("2".equals(code)) {
                    totalIva += imp.optDouble("valor", 0.0);
                }
            }
        }
        addTotalRow(tablaTotales, "IVA", df.format(totalIva), normalFont);

        addTotalRow(tablaTotales, "PROPINA", infoFactura.optString("propina", "0.00"), normalFont);

        // Add Moneda
        addTotalRow(tablaTotales, "MONEDA", infoFactura.optString("moneda", "DOLAR"), normalFont);

        PdfPCell lblTotal = new PdfPCell(new Phrase("VALOR TOTAL", negritaFont));
        lblTotal.setBorder(Rectangle.TOP);
        tablaTotales.addCell(lblTotal);

        PdfPCell valTotal = new PdfPCell(new Phrase(infoFactura.optString("importeTotal", "0.00"), negritaFont));
        valTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valTotal.setBorder(Rectangle.TOP);
        tablaTotales.addCell(valTotal);

        PdfPCell cellTotalesContainer = new PdfPCell(tablaTotales);
        cellTotalesContainer.setBorder(Rectangle.NO_BORDER);
        footerTable.addCell(cellTotalesContainer);

        document.add(footerTable);
        document.close();
    }

    private static String calcularSubtotalPorImpuesto(JSONObject infoFactura, String codigoPorcentajeBusqueda) {
        JSONArray totalConImpuestos = infoFactura.optJSONArray("totalConImpuestos");
        double base = 0.0;
        if (totalConImpuestos != null) {
            for (int i = 0; i < totalConImpuestos.length(); i++) {
                JSONObject tax = totalConImpuestos.getJSONObject(i);
                // "codigo": "2" (IVA)
                // "codigoPorcentaje": "2" (12%), "0" (0%), "4" (15%), etc
                if ("2".equals(tax.optString("codigo"))
                        && codigoPorcentajeBusqueda.equals(tax.optString("codigoPorcentaje"))) {
                    base += tax.optDouble("baseImponible", 0.0);
                } else if ("0".equals(codigoPorcentajeBusqueda) && "2".equals(tax.optString("codigo"))
                        && "0".equals(tax.optString("codigoPorcentaje"))) {
                    // specific for 0%
                    base += tax.optDouble("baseImponible", 0.0);
                }
            }
        }
        return df.format(base);
    }

    private static String mapFormaPago(String codigo) {
        switch (codigo) {
            case "01":
                return "SIN UTILIZACION DEL SISTEMA FINANCIERO";
            case "15":
                return "COMPENSACION DE DEUDAS";
            case "16":
                return "TARJETA DE DEBITO";
            case "17":
                return "DINERO ELECTRONICO";
            case "18":
                return "TARJETA PREPAGO";
            case "19":
                return "TARJETA DE CREDITO";
            case "20":
                return "OTROS CON UTILIZACION DEL SISTEMA FINANCIERO";
            case "21":
                return "ENDOSO DE TITULOS";
            default:
                return "OTROS";
        }
    }

    private static void addTextCell(PdfPTable table, String text, Font font, int border) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(border);
        cell.setPadding(2);
        table.addCell(cell);
    }

    private static void addCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(3);
        table.addCell(cell);
    }

    private static void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, font));
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Phrase(value, font));
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setBorder(Rectangle.NO_BORDER);
        table.addCell(c2);
    }
}
