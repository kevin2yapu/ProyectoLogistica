/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private double stock;
    private String estado;
    
    private ConexionBDD conectar = new ConexionBDD();
    
   
   
    public Producto(){
        
    }

    public Producto(int id, String codigo, String nombre, String descripcion, double stock, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
 public ArrayList<String[]> obtenerProductos() {
        ArrayList<String[]> lregistros = new ArrayList<>();
        String sentenciaSQL = "{call sp_listar_productos()}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement cs = conectado.prepareCall(sentenciaSQL);
            ResultSet res = cs.executeQuery();

            while (res.next()) {
                String[] listaProductos = new String[6];
                listaProductos[0] = res.getInt("id") + "";
                listaProductos[1] = res.getString("codigo");
                listaProductos[2] = res.getString("nombre");
                listaProductos[3] = res.getString("descripcion");
                listaProductos[4] = res.getDouble("stock") + "";
                listaProductos[5] = res.getString("estado");
                lregistros.add(listaProductos);
            }

            res.close();
            cs.close();
            conectado.close();

        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
        return lregistros;
    }

    // INSERTAR PRODUCTO
    public boolean insertarProducto() {
        boolean guardado = false;
        String sentenciaSQL = "{call sp_ingresar_producto(?, ?, ?, ?)}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL);
            
            ejecutar.setString(1, this.codigo);
            ejecutar.setString(2, this.nombre);
            ejecutar.setString(3, this.descripcion);
            ejecutar.setDouble(4, this.stock);

            int filas = ejecutar.executeUpdate();
            if (filas > 0) {
                guardado = true;
                System.out.println("Producto creado en la BDD");
            }
            
            ejecutar.close();
            conectado.close();

        } catch (SQLException e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
        }

        return guardado;
    }
    
    public boolean editarProducto() {
    boolean editado = false;
    String sentenciaSQL = "{call sp_editar_producto(?, ?, ?, ?)}";

    try {
        Connection conectado = conectar.conectar();
        CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL);
        
        ejecutar.setString(1, this.codigo);
        ejecutar.setString(2, this.nombre);
        ejecutar.setString(3, this.descripcion);
        ejecutar.setDouble(4, this.stock);

        int filas = ejecutar.executeUpdate();
        if (filas > 0) {
            editado = true;
            System.out.println("Producto actualizado en la BDD");
        }
        
        ejecutar.close();
        conectado.close();

    } catch (SQLException e) {
        System.out.println("Error al editar producto: " + e.getMessage());
    }

    return editado;
}
    
    public boolean deshabilitarProducto() {
    boolean deshabilitado = false;
    String sentenciaSQL = "{call sp_deshabilitar_producto(?)}";

    try {
        Connection conectado = conectar.conectar();
        CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL);
        
        ejecutar.setString(1, this.codigo);

        int filas = ejecutar.executeUpdate();
        if (filas > 0) {
            deshabilitado = true;
            System.out.println("Producto deshabilitado en la BDD");
        }
        
        ejecutar.close();
        conectado.close();

    } catch (SQLException e) {
        System.out.println("Error al deshabilitar producto: " + e.getMessage());
    }

    return deshabilitado;
}
    
   public ArrayList<String[]> buscarProductos(String criterio) {
    ArrayList<String[]> lregistros = new ArrayList<>();
    String sentenciaSQL = "{call sp_buscar_producto(?)}";

    try {
        Connection conectado = conectar.conectar();
        CallableStatement cs = conectado.prepareCall(sentenciaSQL);
        cs.setString(1, criterio);
        ResultSet res = cs.executeQuery();

        while (res.next()) {
            String[] lista = new String[6];
            lista[0] = res.getInt("id") + "";
            lista[1] = res.getString("codigo");
            lista[2] = res.getString("nombre");
            lista[3] = res.getString("descripcion");
            lista[4] = res.getDouble("stock") + "";
            lista[5] = res.getString("estado");
            lregistros.add(lista);
        }

        res.close();
        cs.close();
        conectado.close();
    } catch (SQLException e) {
        System.out.println("Error al buscar productos: " + e.getMessage());
    }
    return lregistros;
}
}
