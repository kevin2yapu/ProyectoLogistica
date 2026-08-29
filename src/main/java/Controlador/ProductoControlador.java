/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Producto;
import Vista.ProductoIngreso;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author KEVIN
 */
public class ProductoControlador {
  private Producto pmodelo;
    private ProductoIngreso pvista;
    private int idProductoSeleccionado = -1;

    public ProductoControlador() {}

    public ProductoControlador(Producto pmodelo, ProductoIngreso pvista) {
        this.pmodelo = pmodelo;
        this.pvista = pvista;
    }

   public void iniciar() {
    // Cargar combos iniciales y la tabla con los datos de la BDD
    cargarCombos();
    cargarDatosTabla(); // Asegúrate de tener este método para llenar la JTable

    // 1. Enlazar Listeners de los botones
    pvista.addGuardarListener(e -> agregarProducto());
    pvista.addEditarListener(e -> actualizarProducto());
    pvista.addDeshabilitarListener(e -> deshabilitarProducto());
    pvista.addBuscarListener(e -> buscarProducto());
    pvista.getBtnRegresar().addActionListener(e -> regresarAlMenu());
    // 2. Enlazar el clic en la JTable
    pvista.addTablaListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            seleccionarFila();
        }
    });

    pvista.setLocationRelativeTo(null);
    pvista.setVisible(true);
}
   
   private void regresarAlMenu() {
    pvista.dispose(); 
    
    Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
    Controlador.MenuBodegueroControlador menuCtrl = new Controlador.MenuBodegueroControlador(vistaMenu);
    menuCtrl.iniciar(); 
}

    public void cargarDatosTabla() {
    pvista.getModeloTabla().setRowCount(0);
    ArrayList<String[]> lProds = pmodelo.obtenerProductos();

    if (lProds != null) {
        int num = 1;
        for (String[] p : lProds) {
            Object[] fila = {
                p[0], 
                p[1], 
                p[2], 
                p[3], 
                p[4], 
                p[6], 
                p[7], 
                p[5]
            };
            pvista.getModeloTabla().addRow(fila);
        }
    }
}

   
    public void seleccionarFila() {
    int fila = pvista.getFilaSeleccionada();

    if (fila >= 0) {
       
        this.idProductoSeleccionado = Integer.parseInt(pvista.getModeloTabla().getValueAt(fila, 0).toString());

        // 2. Leemos los campos con sus columnas correctas
        String codigo = pvista.getModeloTabla().getValueAt(fila, 1).toString();
        String nombre = pvista.getModeloTabla().getValueAt(fila, 2).toString();
        String descripcion = pvista.getModeloTabla().getValueAt(fila, 3).toString();
        String stock = pvista.getModeloTabla().getValueAt(fila, 4).toString();

        pvista.setCodigo(codigo);
        pvista.setNombre(nombre);
        pvista.setDescripcion(descripcion);
        pvista.setStock(stock);

        if (pvista.getModeloTabla().getValueAt(fila, 5) != null) {
            pvista.getCbxEstadoProducto().setSelectedItem(pvista.getModeloTabla().getValueAt(fila, 5).toString());
        }

        System.out.println("-> Producto listo para editar. ID Real: " + this.idProductoSeleccionado);
    }
}

    public void agregarProducto() {
        pmodelo.setCodigo(pvista.getCodigo());
        pmodelo.setNombre(pvista.getNombre());
        pmodelo.setDescripcion(pvista.getDescripcion());

        try {
            pmodelo.setStock(Double.parseDouble(pvista.getStock()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ingrese una cantidad numérica válida.");
            return;
        }

        if (pvista.getCbxEstadoProducto().getSelectedItem() != null) {
            pmodelo.setEstadoProducto(pvista.getCbxEstadoProducto().getSelectedItem().toString());
        }

        pmodelo.setIdLote(obtenerIdLoteSeleccionado());

        if (pmodelo.insertarProducto()) {
            JOptionPane.showMessageDialog(null, "Producto guardado con éxito.");
            limpiarCampos();
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(null, "Error al guardar el producto.");
        }
    }

    public void actualizarProducto() {
        System.out.println("-> Ejecutando actualización. ID actual: " + idProductoSeleccionado);

        if (idProductoSeleccionado <= 0) {
            JOptionPane.showMessageDialog(null, "Debe hacer clic sobre un producto de la tabla para seleccionarlo.");
            return;
        }

        pmodelo.setId(idProductoSeleccionado);
        pmodelo.setCodigo(pvista.getCodigo());
        pmodelo.setNombre(pvista.getNombre());
        pmodelo.setDescripcion(pvista.getDescripcion());

        try {
            pmodelo.setStock(Double.parseDouble(pvista.getStock()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ingrese una cantidad numérica válida.");
            return;
        }

        if (pvista.getCbxEstadoProducto().getSelectedItem() != null) {
            pmodelo.setEstadoProducto(pvista.getCbxEstadoProducto().getSelectedItem().toString());
        }

        pmodelo.setIdLote(obtenerIdLoteSeleccionado());

        if (pmodelo.actualizarProducto()) {
            JOptionPane.showMessageDialog(null, "Producto actualizado correctamente.");
            limpiarCampos();
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo actualizar el producto en la base de datos.");
        }
    }

    public void deshabilitarProducto() {
        if (idProductoSeleccionado <= 0) {
            JOptionPane.showMessageDialog(null, "Debe hacer clic sobre un producto de la tabla para deshabilitarlo.");
            return;
        }

        int op = JOptionPane.showConfirmDialog(null, "¿Está seguro de deshabilitar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            if (pmodelo.deshabilitarProductoBD(idProductoSeleccionado)) {
                JOptionPane.showMessageDialog(null, "Producto deshabilitado correctamente.");
                limpiarCampos();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(null, "Error al deshabilitar el producto.");
            }
        }
    }

    public void buscarProducto() {
    String codigo = pvista.getCodigo().trim();
    String nombre = pvista.getNombre().trim();

    String criterio = !codigo.isEmpty() ? codigo : nombre;

    if (criterio.isEmpty()) {
        cargarDatosTabla();
    } else {
        pvista.getModeloTabla().setRowCount(0);
        ArrayList<String[]> lProductos = pmodelo.buscarProductos(criterio);
        
        if (lProductos != null && !lProductos.isEmpty()) {
            for (String[] p : lProductos) {
                // p[0]=ID, p[1]=Codigo, p[2]=Nombre, p[3]=Descripcion, p[4]=Stock, p[5]=Estado, p[6]=EstadoProducto, p[7]=Lote
                Object[] fila = {
                    p[0], p[1], p[2], p[3], p[4], p[6], p[7], p[5]
                };
                pvista.getModeloTabla().addRow(fila);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No se encontraron productos con el criterio: " + criterio);
        }
    }
}

    public void cargarCombos() {
        pvista.getCbxEstadoProducto().removeAllItems();
        pvista.getCbxEstadoProducto().addItem("BUENO ESATDO");
        pvista.getCbxEstadoProducto().addItem("MAL ESATDO");

        pvista.getCbxLote().removeAllItems();
        pvista.getCbxLote().addItem("0 - Sin Lote");

        ArrayList<String> lotes = pmodelo.obtenerComboLotes();
        if (lotes != null) {
            for (String l : lotes) {
                pvista.getCbxLote().addItem(l);
            }
        }
    }

    private Integer obtenerIdLoteSeleccionado() {
        Object item = pvista.getCbxLote().getSelectedItem();
        if (item != null) {
            String seleccion = item.toString().trim();
            if (seleccion.contains(" - ")) {
                try {
                    int id = Integer.parseInt(seleccion.split(" - ")[0].trim());
                    return (id > 0) ? id : null;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private void limpiarCampos() {
        this.idProductoSeleccionado = -1;
        pvista.setCodigo("");
        pvista.setNombre("");
        pvista.setDescripcion("");
        pvista.setStock("");

        if (pvista.getCbxEstadoProducto().getItemCount() > 0) pvista.getCbxEstadoProducto().setSelectedIndex(0);
        if (pvista.getCbxLote().getItemCount() > 0) pvista.getCbxLote().setSelectedIndex(0);
    }
}