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
        
        vista.getCbxLote().addActionListener(e -> cargarProductosPorLote());

    
cargarLotesSegunTipo();
        // Si el tipo de movimiento viene nulo o vacío, lo recuperamos desde la base de datos
        if (this.tipoMovimiento == null || this.tipoMovimiento.trim().isEmpty()) {
            String[] cabecera = modelo.obtenerCabeceraNota(this.notaMovimientoId);
            if (cabecera != null && cabecera.length > 2 && cabecera[2] != null) {
                this.tipoMovimiento = cabecera[2].trim(); 
            }
        }

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

    // 1. Validar que la selección no sea vacía o la opción por defecto
    if (vista.getCbxLote().getSelectedIndex() <= 0) {
        return;
    }

    Object itemLote = vista.getCbxLote().getSelectedItem();
    if (itemLote == null) return;

    String loteSeleccionado = itemLote.toString().trim();

    // 2. Si el ítem es el marcador de posición, cancelar búsqueda
    if (loteSeleccionado.equalsIgnoreCase("Seleccione Lote...")) {
        return;
    }

    // 3. Cargar catálogo o productos filtrados según el tipo de movimiento
    if ("ENTRADA".equalsIgnoreCase(this.tipoMovimiento)) {
        ArrayList<String> productos = modelo.obtenerTodosLosProductos();
        for (String p : productos) {
            vista.getCbxProducto().addItem(p);
        }
    } else {
        int bodegaOrigenId = SesionUsuario.getIdBodega();
        ArrayList<String> productosFiltrados = modelo.obtenerProductosPorLoteYBodega(bodegaOrigenId, loteSeleccionado);

        if (productosFiltrados.isEmpty()) {
            System.out.println("No se encontraron productos con stock para el lote " + loteSeleccionado + " en la bodega " + bodegaOrigenId);
        } else {
            for (String p : productosFiltrados) {
                vista.getCbxProducto().addItem(p);
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
        
        if (vista.getTxtCantidad().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese una cantidad válida.");
            return;
        }

        int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.");
            return;
        }

        // 1. Manejo seguro del Lote (Pasa el texto del lote si es String o extrae ID si viene formateado)
        String numeroLote = loteSeleccionado.contains("-") 
                            ? loteSeleccionado.split("-")[1].trim() 
                            : loteSeleccionado.trim();

        // 2. Extraer ID del Producto del formato "15 - foot" -> 15
        String[] partesProducto = productoSeleccionado.split("-");
        int productoId = Integer.parseInt(partesProducto[0].trim());

        // 3. Tipo de movimiento
        String tipoMovAccion = (this.tipoMovimiento != null && !this.tipoMovimiento.trim().isEmpty()) 
                                ? this.tipoMovimiento.trim() 
                                : "TRANSFERENCIA";

        // 4. Invocar al Modelo pasando el numeroLote en formato String para que la BD busque el ID real por su columna numero_lote
        boolean exito = modelo.registrarDetalleYActualizarStock(
            notaMovimientoId, 
            numeroLote, 
            productoId, 
            cantidad, 
            tipoMovAccion
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
            JOptionPane.showMessageDialog(vista, "Error al guardar el detalle o no se encontró stock suficiente en la bodega de origen.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(vista, "La cantidad ingresada debe ser un número entero válido.");
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
        String[] cabecera = modelo.obtenerCabeceraNota(this.notaMovimientoId);
        ArrayList<String[]> detalles = modelo.obtenerDetallesPorNota(this.notaMovimientoId);

        if (detalles == null || detalles.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay detalles registrados para generar el reporte.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cabecera == null || cabecera.length == 0 || cabecera[0] == null) {
            JOptionPane.showMessageDialog(vista, "No se encontraron los datos de la nota N° " + this.notaMovimientoId, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ruta = "Reporte_Movimiento_" + this.notaMovimientoId + ".pdf";
        boolean exito = GeneradorPDF.generarReporteMovimiento(cabecera, detalles, ruta);

        if (exito) {
            JOptionPane.showMessageDialog(vista, "PDF generado con éxito:\n" + ruta, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "Error al crear el archivo PDF.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarLotesSegunTipo() {
    vista.getCbxLote().removeAllItems();
    vista.getCbxLote().addItem("Seleccione Lote...");

    if ("ENTRADA".equalsIgnoreCase(this.tipoMovimiento)) {
        // En ENTRADA se carga todo el catálogo general
        ArrayList<String> lotes = modelo.obtenerTodosLosLotes(); 
        for (String l : lotes) {
            vista.getCbxLote().addItem(l);
        }
    } else {
        // En TRANSFERENCIA se filtra estricto por la Bodega de Origen (Ej: Bodega 2)
        int bodegaOrigenId = SesionUsuario.getIdBodega(); 
        ArrayList<String> lotesFiltrados = modelo.obtenerLotesPorBodega(bodegaOrigenId);

        for (String l : lotesFiltrados) {
            vista.getCbxLote().addItem(l);
        }
    }
}


}