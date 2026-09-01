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

    // Método auxiliar para consultar el ID numérico del lote en la BD
public ArrayList<String> obtenerLotesPorNotaMovimiento(int notaId) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT l.numero_lote " +
                     "FROM lotes l " +
                     "INNER JOIN nota_movimiento nm ON nm.bodega_origen_id = l.bodega_id " +
                     "WHERE nm.id = ?";

        asegurarConexion();

        try (PreparedStatement ps = this.conectado.prepareStatement(sql)) {
            ps.setInt(1, notaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("numero_lote"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener lotes por nota: " + e.getMessage());
        }
        return lista;
    }

    public int obtenerIdLotePorNumero(String numeroLote) {
        int id = -1;
        String sql = "SELECT id FROM lotes WHERE numero_lote = ?";
        asegurarConexion();
        
        try (PreparedStatement ps = this.conectado.prepareStatement(sql)) {
            ps.setString(1, numeroLote);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar ID de lote: " + e.getMessage());
        }
        return id;
    }

    public boolean registrarDetalleYActualizarStock(int notaId, String loteNum, int productoId, int cantidad, String tipoMovimiento) {
    asegurarConexion();

    int loteId = obtenerIdLotePorNumero(loteNum);

    if (loteId == -1) {
        System.err.println("No se encontró el ID para el lote: " + loteNum);
        return false;
    }

    // IMPRESIÓN DE CONTROL: Verifica estos datos en la consola de NetBeans al dar clic en Guardar
    System.out.println("--- EJECUTANDO SP ---");
    System.out.println("Nota ID: " + notaId);
    System.out.println("Lote ID: " + loteId);
    System.out.println("Producto ID: " + productoId);
    System.out.println("Cantidad: " + cantidad);
    System.out.println("Tipo Movimiento: " + tipoMovimiento);

    String sql = "{call sp_registrar_detalle_movimiento(?, ?, ?, ?, ?)}";
    try (CallableStatement cs = this.conectado.prepareCall(sql)) {
        cs.setInt(1, notaId);
        cs.setInt(2, loteId);
        cs.setInt(3, productoId);
        cs.setBigDecimal(4, new java.math.BigDecimal(cantidad));
        cs.setString(5, tipoMovimiento);

        cs.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.err.println("Error SQL en registrarDetalle: " + e.getMessage());
        return false;
    }
}

    // CORREGIDO: Une correctamente inventario y lotes respetando la bodega
    public ArrayList<String> obtenerProductosPorNumeroLote(String numeroLote) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.nombre " +
                     "FROM productos p " +
                     "INNER JOIN inventario_bodega ib ON p.id = ib.producto_id " +
                     "INNER JOIN lotes l ON ib.lote_id = l.id AND ib.bodega_id = l.bodega_id " +
                     "WHERE TRIM(l.numero_lote) = TRIM(?)";

        asegurarConexion();

        try (PreparedStatement ps = this.conectado.prepareStatement(sql)) {
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

 public String[] obtenerCabeceraNota(int notaMovimientoId) {
    String[] cabecera = new String[7];
    
    // Se añade u.bodega_id como fallback para la Bodega Origen
    String sql = "SELECT nm.id, nm.fecha_movimiento, nm.tipo_movimiento, nm.observacion, " +
                 "u.nombres AS responsable, " +
                 "bo.nombre AS bodega_origen, " +
                 "bd.nombre AS bodega_destino " +
                 "FROM nota_movimiento nm " +
                 "LEFT JOIN usuarios u ON nm.responsable_id = u.id " +
                 "LEFT JOIN bodegas bo ON bo.id = COALESCE(nm.bodega_origen_id, u.bodega_id) " +
                 "LEFT JOIN bodegas bd ON nm.bodega_destino_id = bd.id " +
                 "WHERE nm.id = ?"; 

    asegurarConexion();

    try (PreparedStatement ps = this.conectado.prepareStatement(sql)) {
        ps.setInt(1, notaMovimientoId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                cabecera[0] = String.valueOf(rs.getInt("id"));
                cabecera[1] = rs.getString("fecha_movimiento") != null ? rs.getString("fecha_movimiento") : "N/A";
                cabecera[2] = rs.getString("tipo_movimiento") != null ? rs.getString("tipo_movimiento") : "N/A";
                cabecera[3] = rs.getString("bodega_origen") != null ? rs.getString("bodega_origen") : "N/A";
                cabecera[4] = rs.getString("bodega_destino") != null ? rs.getString("bodega_destino") : "N/A";
                cabecera[5] = rs.getString("responsable") != null ? rs.getString("responsable") : "N/A";
                cabecera[6] = rs.getString("observacion") != null ? rs.getString("observacion") : "Sin observaciones";
            }
        }
    } catch (SQLException e) {
        System.err.println("Error en obtenerCabeceraNota: " + e.getMessage());
    }
    return cabecera;
}
 
 
    public ArrayList<String[]> obtenerDetallesPorNota(int notaId) {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT dm.id, l.numero_lote, p.nombre AS producto, dm.cantidad " +
                     "FROM detalle_movimiento dm " +
                     "INNER JOIN lotes l ON dm.lote_id = l.id " +
                     "INNER JOIN productos p ON dm.producto_id = p.id " + 
                     "WHERE dm.nota_movimiento_id = ?";

        asegurarConexion();

        try (PreparedStatement ps = this.conectado.prepareStatement(sql)) {
            ps.setInt(1, notaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] fila = new String[4];
                    fila[0] = String.valueOf(rs.getInt("id"));
                    fila[1] = rs.getString("numero_lote");
                    fila[2] = rs.getString("producto");
                    fila[3] = String.valueOf(rs.getBigDecimal("cantidad"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles por nota: " + e.getMessage());
        }
        return lista;
    }
}