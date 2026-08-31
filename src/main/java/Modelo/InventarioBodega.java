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
    public List<String[]> buscarInventario(String bodegaSel, String loteSel) {
    List<String[]> lista = new ArrayList<>();

    // Consulta directa leyendo productos y lotes
    StringBuilder sql = new StringBuilder(
        "SELECT 'Bodega Principal' AS bodega, l.numero_lote AS lote, " +
        "p.nombre AS producto, p.stock AS stock_actual " +
        "FROM productos p " +
        "INNER JOIN lotes l ON p.lote_id = l.id " +
        "WHERE 1=1 "
    );

    String idLote = "";
    if (loteSel != null && !loteSel.trim().equalsIgnoreCase("TODOS") && loteSel.contains("-")) {
        idLote = loteSel.split("-")[0].trim();
        sql.append(" AND l.id = ?");
    }

    ConexionBDD conectar = new ConexionBDD();

    try (Connection conectado = conectar.conectar();
         PreparedStatement ps = conectado.prepareStatement(sql.toString())) {

        if (!idLote.isEmpty()) {
            ps.setInt(1, Integer.parseInt(idLote));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("bodega"),
                    rs.getString("lote"),
                    rs.getString("producto"),
                    String.valueOf(rs.getBigDecimal("stock_actual"))
                });
            }
        }

    } catch (SQLException e) {
        System.out.println(">>> ERROR SQL BUSCAR: " + e.getMessage());
    }

    return lista;
}
}