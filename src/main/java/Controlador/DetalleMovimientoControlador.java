/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Lote;
import Vista.DetalleMovimiento;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author KEVIN
 */

 

public class DetalleMovimientoControlador {
    private Vista.DetalleMovimiento vista;
    private Modelo.DetalleMovimiento modelo;
    private int notaMovimientoId;
    private String tipoMovimiento;
    private DefaultTableModel tablaModelo;
    private Lote modeloLote;

    public DetalleMovimientoControlador(Vista.DetalleMovimiento vista, Modelo.DetalleMovimiento modelo, int notaMovimientoId, String tipoMovimiento) {
        this.vista = vista;
        this.modelo = modelo;
        this.notaMovimientoId = notaMovimientoId;
        this.tipoMovimiento = tipoMovimiento;
        this.modeloLote = new Modelo.Lote();
    }

    public void iniciar() {
        vista.getCmbMovimientoId().removeAllItems();
        vista.getCmbMovimientoId().addItem(String.valueOf(notaMovimientoId));
        vista.getCmbMovimientoId().setEnabled(false);

        cargarLotesEnCombo();

        vista.getCbxLote().addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                cargarProductosPorLote();
            }
        });

        vista.getBtnGuardar().addActionListener(e -> agregarYGuardarDetalle());
        vista.getBtnPdf().addActionListener(e -> generarPDF());
        vista.getBtnRegresar().addActionListener(e -> regresarAlMenu());

        tablaModelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        cargarDetallesEnTabla();

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    // CORREGIDO: Ahora busca solo los lotes pertencientes a la bodega origen de esta nota
    private void cargarLotesEnCombo() {
        vista.getCbxLote().removeAllItems();
        vista.getCbxLote().addItem("Seleccione Lote...");

        ArrayList<String> listaLotes = modelo.obtenerLotesPorNotaMovimiento(notaMovimientoId);

        if (listaLotes == null || listaLotes.isEmpty()) {
            listaLotes = modeloLote.obtenerListaLotes();
        }

        if (listaLotes != null && !listaLotes.isEmpty()) {
            for (String lote : listaLotes) {
                vista.getCbxLote().addItem(lote);
            }
        }
    }

    private void cargarProductosPorLote() {
        vista.getCbxProducto().removeAllItems();
        vista.getCbxProducto().addItem("Seleccione Producto...");

        Object itemSeleccionado = vista.getCbxLote().getSelectedItem();

        if (itemSeleccionado != null) {
            String textoLote = itemSeleccionado.toString().trim();

            if (!textoLote.isEmpty() && !textoLote.startsWith("Seleccione")) {
                ArrayList<String> productos = modelo.obtenerProductosPorNumeroLote(textoLote);
                
                if (productos != null && !productos.isEmpty()) {
                    for (String prod : productos) {
                        vista.getCbxProducto().addItem(prod);
                    }
                }
            }
        }
    }

    private void agregarYGuardarDetalle() {
        try {
            if (vista.getCbxLote().getSelectedIndex() <= 0 || vista.getCbxProducto().getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(vista, "Seleccione un lote y un producto válidos.");
                return;
            }

            String loteSeleccionado = vista.getCbxLote().getSelectedItem().toString().trim();
            String productoSeleccionado = vista.getCbxProducto().getSelectedItem().toString().trim();
            int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.");
                return;
            }

            // Extraer ID del formato "ID - Nombre"
            String[] partes = productoSeleccionado.split("-");
            int productoId = Integer.parseInt(partes[0].trim());

            boolean exito = modelo.registrarDetalleYActualizarStock(
    notaMovimientoId, 
    loteSeleccionado, 
    productoId, 
    cantidad, 
    this.tipoMovimiento != null ? this.tipoMovimiento : "SALIDA" // Garantiza que no viaje nulo
);

            if (exito) {
                JOptionPane.showMessageDialog(vista, "Detalle guardado y stock actualizado correctamente.");
                cargarDetallesEnTabla();

                if (vista.getCbxLote().getItemCount() > 0) {
                    vista.getCbxLote().setSelectedIndex(0);
                }
                vista.getCbxProducto().removeAllItems();
                vista.getTxtCantidad().setText("");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el detalle o stock insuficiente en bodega.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Por favor, ingrese un número entero válido en la cantidad.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private void cargarDetallesEnTabla() {
        tablaModelo.setRowCount(0);
        ArrayList<String[]> lista = modelo.obtenerDetallesPorNota(notaMovimientoId);
        for (String[] fila : lista) {
            tablaModelo.addRow(fila);
        }
    }

    private void regresarAlMenu() {
        vista.dispose();
        Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
        Controlador.MenuBodegueroControlador menuCtrl = new Controlador.MenuBodegueroControlador(vistaMenu);
        menuCtrl.iniciar();
    }

    private void generarPDF() {
    // 1. Obtener los datos de cabecera usando el ID de la nota actual (notaMovimientoId)
    String[] cabecera = modelo.obtenerCabeceraNota(this.notaMovimientoId);
    ArrayList<String[]> detalles = modelo.obtenerDetallesPorNota(this.notaMovimientoId);

    // Validar que existan registros en la tabla
    if (detalles == null || detalles.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "No hay detalles registrados para generar el reporte.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validar que la cabecera contenga información válida
    if (cabecera == null || cabecera.length == 0 || cabecera[0] == null) {
        JOptionPane.showMessageDialog(vista, "No se encontraron los datos de la nota N° " + this.notaMovimientoId, "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 2. Definir nombre y ruta del archivo PDF
    String ruta = "Reporte_Movimiento_" + this.notaMovimientoId + ".pdf";

    // 3. Invocar al generador de PDF
    boolean exito = GeneradorPDF.generarReporteMovimiento(cabecera, detalles, ruta);

    if (exito) {
        JOptionPane.showMessageDialog(vista, "PDF generado con éxito:\n" + ruta, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(vista, "Error al crear el archivo PDF.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}