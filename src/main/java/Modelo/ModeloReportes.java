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
public class ModeloReportes {
    public ArrayList<String[]> obtenerDatosReporte(String fInicio, String fFin, String actor, String tipoMov) {
    ArrayList<String[]> lista = new ArrayList<>();
    String sql = "{CALL sp_obtener_reportes(?, ?, ?, ?)}";

    // Se usa tu clase real 'ConexionBDD'
    ConexionBDD conexionBD = new ConexionBDD();

    try (Connection con = conexionBD.conectar();
         CallableStatement cs = con.prepareCall(sql)) {

        // Asignación de parámetros al Stored Procedure
        cs.setString(1, fInicio != null ? fInicio : "");
        cs.setString(2, fFin != null ? fFin : "");
        cs.setString(3, actor != null ? actor : "TODOS");
        cs.setString(4, tipoMov != null ? tipoMov : "TODOS");

        try (ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                String[] fila = new String[5];
                fila[0] = rs.getString("fecha");
                fila[1] = rs.getString("responsable");
                fila[2] = rs.getString("producto");
                fila[3] = String.valueOf(rs.getDouble("cantidad"));
                fila[4] = rs.getString("tipo_movimiento");
                lista.add(fila);
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al ejecutar SP: " + e.getMessage());
    }

    return lista;
}

    public ArrayList<String> obtenerListaActores() {
        ArrayList<String> actores = new ArrayList<>();
        actores.add("TODOS");
        String sql = "SELECT DISTINCT nombres FROM usuarios ORDER BY nombres ASC";
        
        try (Connection cn = new ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                actores.add(rs.getString("nombres"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar actores: " + e.getMessage());
        }
        return actores;
    }

    public ArrayList<String> obtenerListaProductos() {
        ArrayList<String> productos = new ArrayList<>();
        productos.add("TODOS");
        String sql = "SELECT DISTINCT nombre FROM productos ORDER BY nombre ASC";
        
        try (Connection cn = new ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
        return productos;
    }

    
}
