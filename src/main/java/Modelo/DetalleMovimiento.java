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

    public DetalleMovimiento() {
        this.conectar = new Controlador.ConexionBDD(); // ¡Faltaba esta inicialización!
    }
    
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

    // Registra el detalle y actualiza stock mediante el Stored Procedure
    public boolean registrarDetalleYActualizarStock(int notaId, String numeroLote, int productoId, int cantidad) {
        String sqlCall = "{call sp_registrar_detalle_y_actualizar_stock(?, ?, ?, ?, ?)}";
        
        ConexionBDD c = new ConexionBDD();
        try (Connection con = c.conectar();
             CallableStatement cs = con.prepareCall(sqlCall)) {

            cs.setInt(1, notaId);
            cs.setString(2, numeroLote);
            cs.setInt(3, productoId);
            cs.setInt(4, cantidad);
            cs.registerOutParameter(5, java.sql.Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(5);
            return resultado == 1; // 1 = Éxito

        } catch (SQLException e) {
            System.err.println("Error al ejecutar el Stored Procedure: " + e.getMessage());
            return false;
        }
    }

    // Consulta los detalles de la nota (CORREGIDO: asigna parámetro ANTES de ejecutar)
    public ArrayList<String[]> obtenerDetallesPorNota(int notaId) {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT dm.nota_movimiento_id, l.numero_lote, p.nombre AS producto, dm.cantidad " +
                     "FROM detalle_movimiento dm " +
                     "INNER JOIN lotes l ON dm.lote_id = l.id " +
                     "INNER JOIN productos p ON dm.producto_id = p.id " +
                     "WHERE dm.nota_movimiento_id = ?";

        ConexionBDD c = new ConexionBDD();
        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, notaId); // Se asigna el ID primero

            try (ResultSet rs = ps.executeQuery()) { // Se ejecuta después
                while (rs.next()) {
                    String[] fila = new String[4];
                    fila[0] = String.valueOf(rs.getInt("nota_movimiento_id"));
                    fila[1] = rs.getString("numero_lote");
                    fila[2] = rs.getString("producto");
                    fila[3] = String.valueOf(rs.getInt("cantidad"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles: " + e.getMessage());
        }
        return lista;
    }

    // Obtiene los productos vinculados a un número de lote para el combo
    public ArrayList<String> obtenerProductosPorNumeroLote(String numeroLote) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.nombre " +
                     "FROM productos p " +
                     "INNER JOIN inventario_bodega ib ON p.id = ib.producto_id " +
                     "INNER JOIN lotes l ON ib.lote_id = l.id " +
                     "WHERE l.numero_lote = ?";

        ConexionBDD c = new ConexionBDD();
        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numeroLote);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar productos por número de lote: " + e.getMessage());
        }
        return lista;
    }

    // Obtiene los datos de la cabecera para la generación del PDF
    public String[] obtenerCabeceraNota(int notaMovimientoId) {
        String[] cabecera = new String[5];
        String sql = "SELECT nm.id, nm.fecha_movimiento, nm.tipo_movimiento, nm.observacion, u.nombres AS responsable " +
                     "FROM nota_movimiento nm " +
                     "LEFT JOIN usuarios u ON nm.responsable_id = u.id " +
                     "WHERE nm.id = ?";

        ConexionBDD c = new ConexionBDD();
        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

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