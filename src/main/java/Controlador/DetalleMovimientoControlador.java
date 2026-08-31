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
        // 1. Asignar y fijar el ID de Nota de Movimiento
        vista.getCmbMovimientoId().removeAllItems();
        vista.getCmbMovimientoId().addItem(String.valueOf(notaMovimientoId));
        vista.getCmbMovimientoId().setEnabled(false);

        // 2. Cargar lista de lotes en el ComboBox
        cargarLotesEnCombo();

        // 3. Evento al cambiar de Lote: Carga automática del Producto
        vista.getCmbLote().addActionListener(e -> actualizarProductoSegunLote());

        // 4. Asignación de Listeners
        vista.getBtnPdf().addActionListener(e -> generarPDF());
        vista.getBtnRegresar().addActionListener(e -> regresarAlMenu());
        vista.getBtnGuardar().addActionListener(e -> agregarYGuardarDetalle());

        tablaModelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        cargarDetallesEnTabla();

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void cargarLotesEnCombo() {
        vista.getCmbLote().removeAllItems();
        vista.getCmbLote().addItem("Seleccione...");

        ArrayList<String> listaLotes = modeloLote.obtenerListaLotes();
        if (listaLotes != null) {
            for (String lote : listaLotes) {
                vista.getCmbLote().addItem(lote);
            }
        }
    }

    private void actualizarProductoSegunLote() {
        String loteSeleccionado = (String) vista.getCmbLote().getSelectedItem();

        if (loteSeleccionado != null && !loteSeleccionado.equals("Seleccione...")) {
            String nombreProducto = modeloLote.obtenerProductoPorLote(loteSeleccionado);
            vista.getTxtProducto().setText(nombreProducto);
        } else {
            vista.getTxtProducto().setText("");
        }
    }

    private void regresarAlMenu() {
        vista.dispose();
        Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
        Controlador.MenuBodegueroControlador menuCtrl = new Controlador.MenuBodegueroControlador(vistaMenu);
        menuCtrl.iniciar();
    }

  private void agregarYGuardarDetalle() {
    String loteStr = (String) vista.getCmbLote().getSelectedItem();
    String productoStr = vista.getTxtProducto().getText().trim();
    String cantidadStr = vista.getTxtCantidad().getText().trim();

    // Validar selección y campo vacío
    if (loteStr == null || loteStr.equals("Seleccione...") || cantidadStr.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Por favor seleccione un Lote e ingrese la Cantidad.");
        return;
    }

    int cantidad;
    try {
        cantidad = Integer.parseInt(cantidadStr);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(vista, "Ingrese un número entero válido para la Cantidad.");
        return;
    }

    if (cantidad <= 0) {
        JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a cero.");
        return;
    }

    // Obtener el ID numérico del lote seleccionado
    int loteId = modeloLote.obtenerIdPorNumeroLote(loteStr);
    
    if (loteId == -1) {
        JOptionPane.showMessageDialog(vista, "Error: No se encontró el ID del lote seleccionado.");
        return;
    }

    // Ahora pasamos loteId (int) tal como requiere guardarDetalleConSP
    boolean exito = modelo.guardarDetalleConSP(notaMovimientoId, loteId, cantidad, tipoMovimiento);

    if (exito) {
        JOptionPane.showMessageDialog(vista, "Detalle registrado e inventario actualizado correctamente.");

        // Refrescar la tabla visual usando el código de lote y limpiar los campos
        tablaModelo.addRow(new Object[]{notaMovimientoId, loteStr, productoStr, cantidad});
        
        vista.getCmbLote().setSelectedIndex(0);
        vista.getTxtProducto().setText("");
        vista.getTxtCantidad().setText("");
    } else {
        JOptionPane.showMessageDialog(vista, "Error: No se pudo actualizar el inventario. Verifique el stock disponible.");
    }
}
    private void cargarDetallesEnTabla() {
        tablaModelo.setRowCount(0);
        ArrayList<String[]> lista = modelo.obtenerDetallesPorNota(notaMovimientoId);
        for (String[] fila : lista) {
            tablaModelo.addRow(fila);
        }
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