/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KEVIN
 */
public class InventarioBodega {

    // Método para llenar el JComboBox de Bodegas (ID - Nombre)
    public ArrayList<String> obtenerComboBodegas() {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM bodegas"; 
        ConexionBDD conectar = new ConexionBDD();

        try (Connection conectado = conectar.conectar();
             PreparedStatement ps = conectado.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error combo bodegas: " + e.getMessage());
        }
        return lista;
    }

    // Método para llenar el JComboBox de Lotes (ID - NumeroLote)
    public ArrayList<String> obtenerComboLotes() {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT id, numero_lote FROM lotes"; 
        ConexionBDD conectar = new ConexionBDD();

        try (Connection conectado = conectar.conectar();
             PreparedStatement ps = conectado.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("numero_lote"));
            }
        } catch (SQLException e) {
            System.out.println("Error combo lotes: " + e.getMessage());
        }
        return lista;
    }

    // Método principal para cargar la JTable (Trae el producto y el stock real)
   public List<String[]> buscarInventario(String bodegaNombre, String loteNumero) {
    List<String[]> lista = new ArrayList<>();
    
    StringBuilder sql = new StringBuilder(
        "SELECT p.codigo AS codigo_producto, p.nombre AS producto, l.numero_lote, ib.stock " +
        "FROM inventario_bodega ib " +
        "INNER JOIN productos p ON ib.producto_id = p.id " +
        "INNER JOIN lotes l ON ib.lote_id = l.id " +
        "INNER JOIN bodegas b ON ib.bodega_id = b.id " +
        "WHERE 1=1 "
    );

    // Filtrar por ID de bodega o Nombre según lo seleccionado
    if (bodegaNombre != null && !bodegaNombre.equalsIgnoreCase("TODAS")) {
        // Extrae el número ID por si el combo viene como "1 - Bodega Norte" o simplemente "1"
        String idExtraido = bodegaNombre.split(" ")[0].replace("-", "").trim();
        sql.append(" AND (b.id = ? OR b.nombre = ?) ");
    }
    
    if (loteNumero != null && !loteNumero.equalsIgnoreCase("TODOS")) {
        sql.append(" AND l.numero_lote = ? ");
    }

    ConexionBDD c = new ConexionBDD();
    try (Connection con = c.conectar();
         PreparedStatement ps = con.prepareStatement(sql.toString())) {

        int paramIndex = 1;
        if (bodegaNombre != null && !bodegaNombre.equalsIgnoreCase("TODAS")) {
            String idExtraido = bodegaNombre.split(" ")[0].replace("-", "").trim();
            ps.setString(paramIndex++, idExtraido);
            ps.setString(paramIndex++, bodegaNombre);
        }
        if (loteNumero != null && !loteNumero.equalsIgnoreCase("TODOS")) {
            ps.setString(paramIndex++, loteNumero);
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = rs.getString("codigo_producto");
                fila[1] = rs.getString("producto");
                fila[2] = rs.getString("numero_lote");
                fila[3] = String.valueOf(rs.getDouble("stock"));
                lista.add(fila);
            }
        }
    } catch (SQLException e) {
        System.err.println(">>> ERROR SQL BUSCAR: " + e.getMessage());
    }
    return lista;
}
    
    public ArrayList<String[]> obtenerInventarioPorBodega(int bodegaId) {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT p.codigo_producto, p.nombre AS producto, l.numero_lote, ib.stock " +
                     "FROM inventario_bodega ib " +
                     "INNER JOIN productos p ON ib.producto_id = p.id " +
                     "INNER JOIN lotes l ON ib.lote_id = l.id " +
                     "WHERE ib.bodega_id = ? AND ib.stock > 0";

        ConexionBDD c = new ConexionBDD();
        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bodegaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] fila = new String[4];
                    fila[0] = rs.getString("codigo_producto");
                    fila[1] = rs.getString("producto");
                    fila[2] = rs.getString("numero_lote");
                    fila[3] = String.valueOf(rs.getDouble("stock"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar inventario de bodega: " + e.getMessage());
        }
        return lista;
    }

    
    
}