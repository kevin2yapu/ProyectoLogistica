/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Producto;
import Vista.ProductoIngreso;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author KEVIN
 */
public class ProductoControlador {
    private Producto modelo;
    private ProductoIngreso vista;

    // INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();

    public ProductoControlador() {
    }

    public ProductoControlador(Producto modelo, ProductoIngreso vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    // MÉTODO QUE EJECUTA EL SP EN LA BASE DE DATOS
    public boolean ingresarProducto(String codigo, String nombre, String descripcion, double stock) throws SQLException {
        boolean guardado = false;
        String sql = "{call sp_ingresar_producto(?, ?, ?, ?)}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement cs = conectado.prepareCall(sql);

            cs.setString(1, codigo);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setDouble(4, stock);

            int filasAfectadas = cs.executeUpdate();
            if (filasAfectadas > 0) {
                guardado = true;
            }

            conectado.close();

        } catch (SQLException e) {
            System.out.println("Error en el SP de ingresar producto: " + e.getMessage());
        }

        return guardado;
    }

    // MÉTODO QUE RECOGE LOS DATOS DE LA VISTA Y VALIDA
    public void registrarProducto() throws SQLException {
        String codigo = vista.getCodigo();
        String nombre = vista.getNombre();
        String descripcion = vista.getDescripcion();
        String stockText = vista.getStock();

        if (!codigo.isEmpty() && !nombre.isEmpty() && !stockText.isEmpty()) {

            try {
                double stock = Double.parseDouble(stockText);

                if (stock >= 0) {
                    boolean exito = ingresarProducto(codigo, nombre, descripcion, stock);

                    if (exito) {
                        JOptionPane.showMessageDialog(vista, "Producto guardado correctamente.");
                        limpiarCampos();
                    } else {
                        JOptionPane.showMessageDialog(vista, "Error al guardar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(vista, "La cantidad no puede ser negativa.", "Error", JOptionPane.WARNING_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(vista, "Ingrese un valor numérico en la cantidad.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(vista, "Por favor complete los campos obligatorios.");
        }
    }

    private void limpiarCampos() {
        vista.setCodigo("");
        vista.setNombre("");
        vista.setDescripcion("");
        vista.setStock("");
    }

    // INICIAR
    public void iniciar() {
        vista.addGuardarListener(mi -> {
            try {
                registrarProducto();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(vista, "Error de base de datos: " + e.getMessage());
            }
        });
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }
}
