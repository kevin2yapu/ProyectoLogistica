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

public class DetalleMovimiento {
private ConexionBDD conectar;
    private Connection conectado;

    private void asegurarConexion() {
        try {
            if (conectado == null || conectado.isClosed()) {
                conectar = new ConexionBDD();
                conectado = (Connection) conectar.conectar();
            }
        } catch (SQLException e) {
            System.err.println("Error en conexión: " + e.getMessage());
        }
    }

    public boolean guardarDetalleConSP(int notaMovimientoId, int loteId, int cantidad, String tipoMovimiento) {
        asegurarConexion();
        String sql = "{CALL sp_registrar_detalle_movimiento(?, ?, ?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setInt(1, notaMovimientoId);
            cs.setInt(2, loteId);
            cs.setInt(3, cantidad);
            cs.setString(4, tipoMovimiento);

            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al ejecutar SP: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String[]> obtenerDetallesPorNota(int notaMovimientoId) {
        asegurarConexion();
        ArrayList<String[]> lista = new ArrayList<>();

        // Trae la información del detalle incluyendo el nombre del producto desde la tabla productos
        String sql = "SELECT dm.nota_movimiento_id, dm.lote_id, p.nombre AS producto_nombre, dm.cantidad "
                   + "FROM detalle_movimiento dm "
                   + "LEFT JOIN productos p ON dm.lote_id = p.lote_id "
                   + "WHERE dm.nota_movimiento_id = ?";

        try (PreparedStatement ps = conectado.prepareStatement(sql)) {
            ps.setInt(1, notaMovimientoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] fila = new String[4];
                    fila[0] = String.valueOf(rs.getInt("nota_movimiento_id"));
                    fila[1] = String.valueOf(rs.getInt("lote_id"));
                    fila[2] = rs.getString("producto_nombre") != null ? rs.getString("producto_nombre") : "N/A";
                    fila[3] = String.valueOf(rs.getInt("cantidad"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles: " + e.getMessage());
        }
        return lista;
    }

    // Nuevo método para obtener la lista de lotes/productos disponibles para la vista
    public ArrayList<String[]> obtenerLotesDisponibles() {
        asegurarConexion();
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT lote_id, nombre, stock FROM productos WHERE lote_id IS NOT NULL";

        try (PreparedStatement ps = conectado.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] item = new String[3];
                item[0] = String.valueOf(rs.getInt("lote_id"));
                item[1] = rs.getString("nombre");
                item[2] = String.valueOf(rs.getBigDecimal("stock"));
                lista.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos y lotes: " + e.getMessage());
        }
        return lista;
    }
    
   public String[] obtenerCabeceraNota(int notaMovimientoId) {
    asegurarConexion();
    String[] cabecera = new String[5];
    // [0] ID, [1] Fecha, [2] Tipo, [3] Observación, [4] Nombre Responsable

    String sql = "SELECT nm.id, nm.fecha_movimiento, nm.tipo_movimiento, nm.observacion, u.nombres AS responsable "
               + "FROM nota_movimiento nm "
               + "LEFT JOIN usuarios u ON nm.responsable_id = u.id "
               + "WHERE nm.id = ?";

    try (PreparedStatement ps = conectado.prepareStatement(sql)) {
        ps.setInt(1, notaMovimientoId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                cabecera[0] = String.valueOf(rs.getInt("id"));
                cabecera[1] = rs.getString("fecha_movimiento") != null ? rs.getString("fecha_movimiento") : "N/A";
                cabecera[2] = rs.getString("tipo_movimiento") != null ? rs.getString("tipo_movimiento") : "N/A";
                cabecera[3] = rs.getString("observacion") != null ? rs.getString("observacion") : "Sin observaciones";
                cabecera[4] = rs.getString("responsable") != null ? rs.getString("responsable") : "N/A";
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener cabecera de la nota: " + e.getMessage());
    }
    return cabecera;
}
   
   
}