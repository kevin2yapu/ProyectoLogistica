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

    public ProductoControlador() {}

    public ProductoControlador(Producto pmodelo, ProductoIngreso pvista) {
        this.pmodelo = pmodelo;
        this.pvista = pvista;
    }

    public void cargarDatosTabla() {
        pvista.getModeloTabla().setRowCount(0); 
        ArrayList<String[]> lProductos = pmodelo.obtenerProductos();

        for (String[] p : lProductos) {
            Object[] fila = {p[0], p[1], p[2], p[3], p[4], p[5]}; 
            pvista.getModeloTabla().addRow(fila);
        }
    }

    // Pasa los valores de la fila seleccionada a los campos de texto
    public void seleccionarFila() {
        int fila = pvista.getFilaSeleccionada();

        if (fila >= 0) {
            // Mapeo según el orden de columnas visuales de la JTable:
            // Columna 1 = Código, Columna 2 = Nombre, Columna 3 = Descripción, Columna 4 = Cantidad
            String codigo = pvista.getModeloTabla().getValueAt(fila, 1).toString();
            String nombre = pvista.getModeloTabla().getValueAt(fila, 2).toString();
            String descripcion = pvista.getModeloTabla().getValueAt(fila, 3).toString();
            String stock = pvista.getModeloTabla().getValueAt(fila, 4).toString();

            pvista.setCodigo(codigo);
            pvista.setNombre(nombre);
            pvista.setDescripcion(descripcion);
            pvista.setStock(stock);
        }
    }

    public void editarProducto() {
        String codigo = pvista.getCodigo();
        String nombre = pvista.getNombre();
        String descripcion = pvista.getDescripcion();
        String stockStr = pvista.getStock();

        if (!codigo.isEmpty() && !nombre.isEmpty() && !stockStr.isEmpty()) {
            double stock = Double.parseDouble(stockStr);

            pmodelo.setCodigo(codigo);
            pmodelo.setNombre(nombre);
            pmodelo.setDescripcion(descripcion);
            pmodelo.setStock(stock);

            if (pmodelo.editarProducto()) {
                limpiarCampos();
                cargarDatosTabla(); 
            }
        }
    }

    public void agregarProducto() {
        String codigo = pvista.getCodigo();
        String nombre = pvista.getNombre();
        String descripcion = pvista.getDescripcion();
        String stockStr = pvista.getStock();

        if (!codigo.isEmpty() && !nombre.isEmpty() && !stockStr.isEmpty()) {
            double stock = Double.parseDouble(stockStr);

            pmodelo.setCodigo(codigo);
            pmodelo.setNombre(nombre);
            pmodelo.setDescripcion(descripcion);
            pmodelo.setStock(stock);

            if (pmodelo.insertarProducto()) {
                limpiarCampos();
                cargarDatosTabla(); 
            }
        }
    }
    
    public void deshabilitarProducto() {
    String codigo = pvista.getCodigo();

    if (!codigo.isEmpty()) {
        pmodelo.setCodigo(codigo);

        if (pmodelo.deshabilitarProducto()) {
            limpiarCampos();
            cargarDatosTabla(); 
        }
    }
}
    
    public void buscarProducto() {
    String codigo = pvista.getCodigo().trim();
    String nombre = pvista.getNombre().trim();

  
    String criterio = "";
    if (!codigo.isEmpty()) {
        criterio = codigo;
    } else if (!nombre.isEmpty()) {
        criterio = nombre;
    }

    if (criterio.isEmpty()) {
        cargarDatosTabla(); 
    } 
  
    else {
        pvista.getModeloTabla().setRowCount(0); // Limpia la tabla
        ArrayList<String[]> lProductos = pmodelo.buscarProductos(criterio);

        for (String[] p : lProductos) {
            Object[] fila = {p[0], p[1], p[2], p[3], p[4], p[5]};
            pvista.getModeloTabla().addRow(fila);
        }
    }
}

    private void limpiarCampos() {
        pvista.setCodigo("");
        pvista.setNombre("");
        pvista.setDescripcion("");
        pvista.setStock("");
    }

   public void iniciar() {
        // Enlaces de los botones
        pvista.addGuardarListener(e -> agregarProducto());
        pvista.addEditarListener(e -> editarProducto());
        pvista.addDeshabilitarListener(e -> deshabilitarProducto());
        pvista.addBuscarListener(e -> buscarProducto());
        
        pvista.addTablaListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        pvista.setLocationRelativeTo(null);
        this.cargarDatosTabla(); 
        pvista.setVisible(true);
    }
}