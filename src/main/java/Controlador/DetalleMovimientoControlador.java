/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Lote;
import Vista.DetalleMovimiento;
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
        // 1. Fijar el ID de la Nota de Movimiento
        vista.getCmbMovimientoId().removeAllItems();
        vista.getCmbMovimientoId().addItem(String.valueOf(notaMovimientoId));
        vista.getCmbMovimientoId().setEnabled(false);

        // 2. Cargar lotes iniciales
        cargarLotesEnCombo();

        // 3. Escuchador: Al cambiar Lote en cbxLote, cargar productos en cbxProducto
        vista.getCbxLote().addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                cargarProductosPorLote();
            }
        });

        // 4. Escuchadores de botones
        vista.getBtnGuardar().addActionListener(e -> agregarYGuardarDetalle());
        vista.getBtnPdf().addActionListener(e -> generarPDF());
        vista.getBtnRegresar().addActionListener(e -> regresarAlMenu());

        // 5. Cargar tabla de detalles existentes
        tablaModelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        cargarDetallesEnTabla();

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void cargarLotesEnCombo() {
        vista.getCbxLote().removeAllItems();
        vista.getCbxLote().addItem("Seleccione Lote...");

        // Llamada general al modelo de Lotes
        ArrayList<String> listaLotes = modeloLote.obtenerListaLotes();
        
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
            if (vista.getCbxLote().getSelectedIndex() <= 0 || vista.getCbxProducto().getSelectedItem() == null) {
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

            // Extraemos el id del producto (formato "1 - NombreProducto")
            int productoId = Integer.parseInt(productoSeleccionado.split(" - ")[0].trim());

            // Usamos la variable directa del constructor (notaMovimientoId)
            boolean exito = modelo.registrarDetalleYActualizarStock(notaMovimientoId, loteSeleccionado, productoId, cantidad);

            if (exito) {
                JOptionPane.showMessageDialog(vista, "Detalle guardado y stock actualizado correctamente.");
                cargarDetallesEnTabla();

                // Limpiar entradas
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
        String[] cabecera = modelo.obtenerCabeceraNota(notaMovimientoId);
        ArrayList<String[]> detalles = modelo.obtenerDetallesPorNota(notaMovimientoId);

        if (detalles.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay detalles para generar el reporte.");
            return;
        }

        String ruta = "Reporte_Movimiento_" + notaMovimientoId + ".pdf";
        boolean exito = GeneradorPDF.generarReporteMovimiento(cabecera, detalles, ruta);

        if (exito) {
            JOptionPane.showMessageDialog(vista, "PDF generado con éxito en:\n" + ruta);
        } else {
            JOptionPane.showMessageDialog(vista, "Error al generar el archivo PDF.");
        }
    }
    
   
}