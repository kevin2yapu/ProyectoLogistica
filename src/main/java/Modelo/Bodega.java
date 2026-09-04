package Modelo;

import Controlador.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
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

    @Override
    public String toString() {
        return nombre;
    }

    // CONSULTA A LA BASE DE DATOS
    public ArrayList<Bodega> obtenerCatalogo() {
        ArrayList<Bodega> lista = new ArrayList<>();
        ConexionBDD conexion = new ConexionBDD();
        String sql = "SELECT * FROM bodegas;";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Intenta leer 'id', si falla lee 'id_bodega'
                int idBodega;
                try {
                    idBodega = rs.getInt("id");
                } catch (SQLException e) {
                    idBodega = rs.getInt("id_bodega");
                }
                lista.add(new Bodega(idBodega, rs.getString("nombre")));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo de bodegas: " + e.getMessage());
        }

        return lista;
    }

    // Obtiene el ID de la bodega a partir del nombre seleccionado en el combo
    public int obtenerIdPorNombre(String nombreBodega) {
        int idEncontrado = -1;
        ConexionBDD conexion = new ConexionBDD();
        String sql = "SELECT * FROM bodegas WHERE nombre = ?;";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombreBodega);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try {
                        idEncontrado = rs.getInt("id");
                    } catch (SQLException e) {
                        idEncontrado = rs.getInt("id_bodega");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de bodega por nombre: " + e.getMessage());
        }

        return idEncontrado;
    }
}