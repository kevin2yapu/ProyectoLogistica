/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public class Bodega {
    private int id;
    private String nombre;
    private String ubicacion;
    private String estado;

    public Bodega(int id, String nombre, String ubicacion, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }
    
    private ConexionBDD conectar = new ConexionBDD();

    public Bodega() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
 // LISTA BODEGAS
    public ArrayList<String[]> obtenerBodegas() {
        ArrayList<String[]> lregistros = new ArrayList<>();
        String sentenciaSQL = "{call sp_listar_bodegas()}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement cs = conectado.prepareCall(sentenciaSQL);
            ResultSet res = cs.executeQuery();

            while (res.next()) {
                String[] lista = new String[4];
                lista[0] = res.getInt("id") + "";
                lista[1] = res.getString("nombre");
                lista[2] = res.getString("ubicacion");
                lista[3] = res.getString("estado");
                lregistros.add(lista);
            }

            res.close();
            cs.close();
            conectado.close();
        } catch (SQLException e) {
            System.out.println("Error al listar bodegas: " + e.getMessage());
        }
        return lregistros;
    }

    // INSERTAR BODEGA
    public boolean insertarBodega() {
        boolean guardado = false;
        String sentenciaSQL = "{call sp_ingresar_bodega(?, ?)}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL);

            ejecutar.setString(1, this.nombre);
            ejecutar.setString(2, this.ubicacion);

            int filas = ejecutar.executeUpdate();
            if (filas > 0) {
                guardado = true;
            }

            ejecutar.close();
            conectado.close();
        } catch (SQLException e) {
            System.out.println("Error al ingresar bodega: " + e.getMessage());
        }

        return guardado;
    }

    // EDITAR BODEGA
    public boolean editarBodega() {
        boolean editado = false;
        String sentenciaSQL = "{call sp_editar_bodega(?, ?, ?)}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL);

            ejecutar.setInt(1, this.id);
            ejecutar.setString(2, this.nombre);
            ejecutar.setString(3, this.ubicacion);

            int filas = ejecutar.executeUpdate();
            if (filas > 0) {
                editado = true;
            }

            ejecutar.close();
            conectado.close();
        } catch (SQLException e) {
            System.out.println("Error al editar bodega: " + e.getMessage());
        }

        return editado;
    }

    // DESHABILITAR BODEGA
    public boolean deshabilitarBodega() {
        boolean deshabilitado = false;
        String sentenciaSQL = "{call sp_deshabilitar_bodega(?)}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL);

            ejecutar.setInt(1, this.id);

            int filas = ejecutar.executeUpdate();
            if (filas > 0) {
                deshabilitado = true;
            }

            ejecutar.close();
            conectado.close();
        } catch (SQLException e) {
            System.out.println("Error al deshabilitar bodega: " + e.getMessage());
        }

        return deshabilitado;
    }

    // BUSCAR BODEGA
    public ArrayList<String[]> buscarBodegas(String criterio) {
        ArrayList<String[]> lregistros = new ArrayList<>();
        String sentenciaSQL = "{call sp_buscar_bodega(?)}";

        try {
            Connection conectado = conectar.conectar();
            CallableStatement cs = conectado.prepareCall(sentenciaSQL);
            cs.setString(1, criterio);
            ResultSet res = cs.executeQuery();

            while (res.next()) {
                String[] lista = new String[4];
                lista[0] = res.getInt("id") + "";
                lista[1] = res.getString("nombre");
                lista[2] = res.getString("ubicacion");
                lista[3] = res.getString("estado");
                lregistros.add(lista);
            }

            res.close();
            cs.close();
            conectado.close();
        } catch (SQLException e) {
            System.out.println("Error al buscar bodegas: " + e.getMessage());
        }
        return lregistros;
    }
}
