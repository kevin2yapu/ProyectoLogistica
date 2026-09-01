/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public class GeneradorPDF {
    
    public static boolean generarReporteMovimiento(String[] cabecera, ArrayList<String[]> detalles, String rutaDestino) {
        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaDestino));
            documento.open();

            // Tipografía
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
            Font valorFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
            Font cabeceraTablaFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            Font contenidoFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

            // Título
            Paragraph titulo = new Paragraph("COMPROBANTE DE MOVIMIENTO DE ALMACÉN", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph(" ")); // Espaciador

            // Extraer Bodegas de la cabecera (posiciones 5 y 6)
            String bodegaOrigen = (cabecera.length > 5 && cabecera[5] != null && !cabecera[5].trim().isEmpty()) ? cabecera[5] : "N/A";
            String bodegaDestino = (cabecera.length > 6 && cabecera[6] != null && !cabecera[6].trim().isEmpty()) ? cabecera[6] : "N/A";

            // Cabecera: Datos de la Nota
            documento.add(new Paragraph("N° Nota Movimiento: " + (cabecera[0] != null ? cabecera[0] : "N/A"), valorFont));
            documento.add(new Paragraph("Fecha: " + (cabecera[1] != null ? cabecera[1] : "N/A"), valorFont));
            documento.add(new Paragraph("Tipo Movimiento: " + (cabecera[2] != null ? cabecera[2] : "N/A"), valorFont));
            
            // --- NUEVOS CAMPOS AÑADIDOS ---
            documento.add(new Paragraph("Bodega Origen: " + bodegaOrigen, valorFont));
            documento.add(new Paragraph("Bodega Destino: " + bodegaDestino, valorFont));
            
            documento.add(new Paragraph("Realizado por: " + (cabecera[4] != null ? cabecera[4] : "N/A"), valorFont));
            documento.add(new Paragraph("Observación: " + (cabecera[3] != null ? cabecera[3] : "Sin observaciones"), valorFont));
            documento.add(new Paragraph(" "));

            // Tabla de Detalle
            PdfPTable tabla = new PdfPTable(4); // 4 Columnas
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 2, 4, 2});

            // Encabezados de Tabla
            String[] titulos = {"N° Movimiento", "Lote", "Producto", "Cantidad"};
            for (String col : titulos) {
                PdfPCell celda = new PdfPCell(new Phrase(col, cabeceraTablaFont));
                celda.setBackgroundColor(BaseColor.DARK_GRAY);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(6);
                tabla.addCell(celda);
            }

            // Filas de Detalle
            for (String[] fila : detalles) {
                for (int i = 0; i < 4; i++) {
                    PdfPCell celda = new PdfPCell(new Phrase(fila[i], contenidoFont));
                    celda.setHorizontalAlignment(i == 3 ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
                    celda.setPadding(5);
                    tabla.addCell(celda);
                }
            }

            documento.add(tabla);
            documento.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            return false;
        }
    }
}