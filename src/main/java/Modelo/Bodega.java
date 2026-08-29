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
public class Bodega {

    private int id;
    private String nombre;

    public Bodega() {
    }

    public Bodega(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    // CLAVE: El JComboBox llama automáticamente al método toString() 
    // para saber qué texto mostrar en pantalla.
    @Override
    public String toString() {
        return nombre;
    }

    // MÉTODO PARA CONSULTAR EL CATÁLOGO EN BDD
    public ArrayList<Bodega> obtenerCatalogo() {
        ArrayList<Bodega> lista = new ArrayList<>();
        ConexionBDD conectar = new ConexionBDD();

        String sql = "SELECT id, nombre FROM bodegas;";

        try {
            Connection conectado = (Connection) conectar.conectar();
            PreparedStatement ps = conectado.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Bodega(rs.getInt("id"), rs.getString("nombre")));
            }

            rs.close();
            ps.close();
            conectado.close();

        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo de bodegas: " + e.getMessage());
        }

        return lista;
    }
}
