/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author KEVIN
 */
public class SalidaAlmacen extends MovimientoAlmacen {

    public SalidaAlmacen() {
        super();
    }

    public SalidaAlmacen(int id, String tipoMovimiento, Integer bodegaOrigenId, Integer bodegaDestinoId, int responsableId, String fechaMovimiento, String observacion) {
        super(id, tipoMovimiento, bodegaOrigenId, bodegaDestinoId, responsableId, fechaMovimiento, observacion);
    }

    @Override
    public boolean impactarInventario(int productoId, int cantidad) {
        if (!validarStockDisponible(productoId, cantidad)) {
            System.out.println("Error: Stock insuficiente para realizar la salida.");
            return false;
        }

        String sentenciaSQL = "UPDATE productos SET stock = stock - ? WHERE id = ?;";
        try (PreparedStatement ps = conectado.prepareStatement(sentenciaSQL)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, productoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al aplicar salida: " + e.getMessage());
            return false;
        }
    }

    private boolean validarStockDisponible(int productoId, int cantidad) {
        String sql = "SELECT stock FROM productos WHERE id = ?;";
        try (PreparedStatement ps = conectado.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock") >= cantidad;
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar stock: " + e.getMessage());
        }
        return false;
    }
}