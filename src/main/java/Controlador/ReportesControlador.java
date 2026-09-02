package Controlador;

import Modelo.ModeloReportes;
import Vista.VistaReporte;

// Imports de Java AWT / Swing
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ReportesControlador {

    private VistaReporte vista;
    private ModeloReportes modelo;
    private JFreeChart graficoActual;

    public ReportesControlador(VistaReporte vista, ModeloReportes modelo) {
        this.vista = vista;
        this.modelo = modelo;

        inicializarFiltros();

        // Asignación de eventos a los botones de la Vista
        this.vista.getBtnGenerar().addActionListener(e -> generarReporte());
        this.vista.getBtnPDF().addActionListener(e -> exportarPDF());
    }

//    private void inicializarFiltros() {
//        if (vista.getCbxSolicitud() != null) {
//            vista.getCbxSolicitud().removeAllItems();
//            vista.getCbxSolicitud().addItem("TODOS");
//            vista.getCbxSolicitud().addItem("ENTRADA");
//            vista.getCbxSolicitud().addItem("SALIDA");
//            vista.getCbxSolicitud().addItem("TRANSFERENCIA");
//        }
//    }
    
    private void inicializarFiltros() {
    if (vista.getCbxSolicitud() != null) {
        vista.getCbxSolicitud().removeAllItems();
        vista.getCbxSolicitud().addItem("TODOS");
        vista.getCbxSolicitud().addItem("ENTRADA");
        vista.getCbxSolicitud().addItem("SALIDA");
    }
}

    private void generarReporte() {
    String fInicio = vista.getTxtFechaInicio().getText().trim();
    String fFin = vista.getTxtFechaFin().getText().trim();
    String actor = vista.getTxtActor().getText().trim();
    String tipoMov = vista.getCbxSolicitud().getSelectedItem().toString();

    // 1. VALIDACIÓN DE FECHAS
    if (fInicio.isEmpty() || fFin.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Por favor, ingrese ambas fechas (Inicio y Fin).", "Fechas Incompletas", JOptionPane.WARNING_MESSAGE);
        return;
    }

    LocalDate fechaInicio;
    LocalDate fechaFin;

    try {
        fechaInicio = LocalDate.parse(fInicio); // Formato esperado: YYYY-MM-DD
        fechaFin = LocalDate.parse(fFin);
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(vista, "El formato de fecha no es válido. Use el formato: YYYY-MM-DD (Ejemplo: 2026-08-31)", "Formato Incorrecto", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (fechaInicio.isAfter(fechaFin)) {
        JOptionPane.showMessageDialog(vista, "La fecha de inicio no puede ser posterior a la fecha fin.", "Rango Inválido", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. PROCESAMIENTO SI LAS FECHAS SON VÁLIDAS
    if (actor.isEmpty()) {
        actor = "TODOS";
    }

    // Obtener datos desde la base de datos
  ArrayList<String[]> datos = modelo.obtenerDatosReporte(fInicio, fFin, actor, tipoMov);

    // Llenar la JTable
    DefaultTableModel model = (DefaultTableModel) vista.getJblDetalle().getModel();
    model.setRowCount(0);

    Map<String, Double> acumuladoProducto = new HashMap<>();

    for (String[] fila : datos) {
        if (!tipoMov.equalsIgnoreCase("TODOS") && !fila[4].equalsIgnoreCase(tipoMov)) {
            continue;
        }

        model.addRow(fila);

        String prod = fila[2];
        double cant = Double.parseDouble(fila[3]);
        acumuladoProducto.put(prod, acumuladoProducto.getOrDefault(prod, 0.0) + cant);
    }

    // Construir la gráfica con JFreeChart
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map.Entry<String, Double> entry : acumuladoProducto.entrySet()) {
        dataset.addValue(entry.getValue(), "Cantidad Movida", entry.getKey());
    }

    graficoActual = ChartFactory.createBarChart(
            "Consumo y Movimiento por Producto",
            "Producto",
            "Unidades",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false
    );

    // Renderizar la Gráfica en el JPanel
    ChartPanel chartPanel = new ChartPanel(graficoActual);
    vista.getJplGrafica().setLayout(new BorderLayout());
    vista.getJplGrafica().removeAll();
    vista.getJplGrafica().add(chartPanel, BorderLayout.CENTER);
    vista.getJplGrafica().revalidate();
    vista.getJplGrafica().repaint();
}

    private void exportarPDF() {
        if (graficoActual == null) {
            JOptionPane.showMessageDialog(vista, "Primero debe hacer clic en GENERAR para previsualizar el reporte.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ruta = "Reporte_Estadistico.pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(ruta));
            document.open();

            // 1. ENCABEZADO Y FILTROS
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("REPORTE ESTADÍSTICO PARA TOMA DE DECISIONES", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(15);
            document.add(titulo);

            Font fuenteFiltros = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            document.add(new Paragraph("Rango: " + vista.getTxtFechaInicio().getText() + " a " + vista.getTxtFechaFin().getText(), fuenteFiltros));
            document.add(new Paragraph("Actor: " + (vista.getTxtActor().getText().isEmpty() ? "TODOS" : vista.getTxtActor().getText()), fuenteFiltros));
            document.add(new Paragraph("Tipo de Movimiento: " + vista.getCbxSolicitud().getSelectedItem().toString(), fuenteFiltros));
            
            Paragraph espacio = new Paragraph(" ");
            espacio.setSpacingAfter(10);
            document.add(espacio);

            // 2. CONSTRUCCIÓN DE LA TABLA A PARTIR DEL JTABLE
            javax.swing.JTable tabla = vista.getJblDetalle();
            PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Encabezados de la Tabla
            Font fuenteHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(tabla.getColumnName(i), fuenteHeader));
                cell.setBackgroundColor(new BaseColor(30, 41, 59)); // Gris oscuro azulado
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                pdfTable.addCell(cell);
            }

            // Filas de Datos de la JTable
            Font fuenteCelda = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
            for (int rows = 0; rows < tabla.getRowCount(); rows++) {
                for (int cols = 0; cols < tabla.getColumnCount(); cols++) {
                    Object valor = tabla.getValueAt(rows, cols);
                    PdfPCell cell = new PdfPCell(new Phrase(valor != null ? valor.toString() : "", fuenteCelda));
                    cell.setPadding(5);

                    // Alineación por columna
                    if (cols == 3) { // Cantidad
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    } else if (cols == 0 || cols == 4) { // Fecha / Tipo Movimiento
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }

                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);
            document.add(espacio);

            // 3. CONVERTIR Y AGREGAR LA GRÁFICA JFREECHART AL PDF
            BufferedImage bufferedImage = graficoActual.createBufferedImage(500, 260);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);

            com.itextpdf.text.Image imagenGrafico = com.itextpdf.text.Image.getInstance(baos.toByteArray());
            imagenGrafico.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
            document.add(imagenGrafico);

            document.close();
            JOptionPane.showMessageDialog(vista, "PDF Exportado Exitosamente:\n" + ruta, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    
