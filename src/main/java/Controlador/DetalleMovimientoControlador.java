/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

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

    public DetalleMovimientoControlador(Vista.DetalleMovimiento vista, Modelo.DetalleMovimiento modelo, int notaMovimientoId, String tipoMovimiento) {
        this.vista = vista;
        this.modelo = modelo;
        this.notaMovimientoId = notaMovimientoId;
        this.tipoMovimiento = tipoMovimiento;
    }

    public void iniciar() {
        // Asignar y fijar el ID en el ComboBox
        vista.getCmbMovimientoId().removeAllItems();
        vista.getCmbMovimientoId().addItem(String.valueOf(notaMovimientoId));
        vista.getCmbMovimientoId().setEnabled(false); // Bloqueado para mantener coherencia con la nota generada
        vista.getBtnPdf().addActionListener(e -> generarPDF());
        tablaModelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        vista.getBtnRegresar().addActionListener(e -> regresarAlMenu());
        vista.getBtnGuardar().addActionListener(e -> agregarYGuardarDetalle());
        
        
        cargarDetallesEnTabla();

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }
    
    private void regresarAlMenu() {
   
    vista.dispose();

  
    Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
    Controlador.MenuBodegueroControlador menuCtrl = new Controlador.MenuBodegueroControlador(vistaMenu);
    
    menuCtrl.iniciar();
}

    private void agregarYGuardarDetalle() {
        try {
            String loteStr = vista.getTxtLote().getText().trim();
            String productoStr = vista.getTxtProducto().getText().trim();
            String cantidadStr = vista.getTxtCantidad().getText().trim();

            if (loteStr.isEmpty() || cantidadStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Por favor complete los campos de Lote y Cantidad.");
                return;
            }

            int loteId = Integer.parseInt(loteStr);
            int cantidad = Integer.parseInt(cantidadStr);

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a cero.");
                return;
            }

            // Guardar en BDD (detalle_movimiento + actualizar lote)
            boolean exito = modelo.guardarDetalleConSP(notaMovimientoId, loteId, cantidad, tipoMovimiento);

            if (exito) {
                JOptionPane.showMessageDialog(vista, "Detalle registrado e inventario actualizado correctamente.");
                
                // Agregar fila a la JTable visual
                tablaModelo.addRow(new Object[]{notaMovimientoId, loteId, productoStr, cantidad});
                
                vista.getTxtLote().setText("");
                vista.getTxtProducto().setText("");
                vista.getTxtCantidad().setText("");
            } else {
                JOptionPane.showMessageDialog(vista, "Error: No se pudo actualizar el inventario. Verifique el Lote o el stock disponible.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Ingrese números válidos para Lote y Cantidad.");
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