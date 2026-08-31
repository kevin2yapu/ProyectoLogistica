/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public class Lote {
  private int id;
    private String codigoLote;
    private int idProducto;
    private int idBodega;
    private String fechaVencimiento;
    private String estado;
    
    private ConexionBDD conectar = new ConexionBDD();

    public Lote() {
    }

    public Lote(String codigoLote, int idProducto, int idBodega, String fechaVencimiento, String estado) {
        this.codigoLote = codigoLote;
        this.idProducto = idProducto;
        this.idBodega = idBodega;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdBodega() {
        return idBodega;
    }

    public void setIdBodega(int idBodega) {
        this.idBodega = idBodega;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

   public ArrayList<String[]> obtenerLotes() {
        ArrayList<String[]> lista = new ArrayList<>();
        // INNER JOIN corregido usando la tabla 'bodegas'
        String sql = "SELECT l.id, l.numero_lote, l.bodega_id, b.nombre AS nombre_bodega, " +
                     "l.fecha_vencimiento, l.estado " +
                     "FROM lotes l " +
                     "INNER JOIN bodegas b ON l.bodega_id = b.id ORDER BY l.id DESC";

        try (Connection con = new ConexionBDD().conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("numero_lote"),
                    String.valueOf(rs.getInt("bodega_id")),
                    rs.getString("nombre_bodega"),
                    rs.getString("fecha_vencimiento"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR OBTENER LOTES: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertarLote() {
    String sql = "INSERT INTO lotes (numero_lote, bodega_id, fecha_vencimiento, estado) VALUES (?, ?, ?, 'ACTIVO')";

    try (Connection con = new ConexionBDD().conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, this.codigoLote);
        ps.setInt(2, this.idBodega);
        ps.setString(3, this.fechaVencimiento);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.out.println(">>> ERROR INSERTAR LOTE: " + e.getMessage());
        return false;
    }
}

    public boolean editarLote() {
        String sql = "UPDATE lotes SET numero_lote = ?, bodega_id = ?, fecha_vencimiento = ? WHERE id = ?";

        try (Connection con = new ConexionBDD().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, this.codigoLote);
            ps.setInt(2, this.idBodega);
            ps.setString(3, this.fechaVencimiento);
            ps.setInt(4, this.id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(">>> ERROR EDITAR LOTE: " + e.getMessage());
            return false;
        }
    }

    public boolean deshabilitarLote() {
        String sql = "UPDATE lotes SET estado = 'INACTIVO' WHERE id = ?";

        try (Connection con = new ConexionBDD().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, this.id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(">>> ERROR DESHABILITAR LOTE: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String[]> buscarLotes(String criterio) {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT l.id, l.numero_lote, l.bodega_id, b.nombre AS nombre_bodega, " +
                     "l.fecha_vencimiento, l.estado " +
                     "FROM lotes l " +
                     "INNER JOIN bodegas b ON l.bodega_id = b.id " +
                     "WHERE l.numero_lote LIKE ? ORDER BY l.id DESC";

        try (Connection con = new ConexionBDD().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + criterio + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("numero_lote"),
                    String.valueOf(rs.getInt("bodega_id")),
                    rs.getString("nombre_bodega"),
                    rs.getString("fecha_vencimiento"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR BUSCAR LOTES: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<String> obtenerComboBodegas() {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM bodegas ORDER BY nombre ASC";

        try (Connection con = new ConexionBDD().conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR COMBO BODEGAS: " + e.getMessage());
        }
        return lista;
    }
}

