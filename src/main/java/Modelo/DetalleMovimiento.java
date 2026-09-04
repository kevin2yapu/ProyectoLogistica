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
        this.conectar = new Controlador.ConexionBDD();
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

    public ArrayList<String> obtenerProductosPorNumeroLote(String numeroLote, int notaMovimientoId) {
        ArrayList<String> lista = new ArrayList<>();

        String sql = "SELECT DISTINCT p.id, p.nombre " +
                     "FROM productos p " +
                     "INNER JOIN inventario_bodega ib ON p.id = ib.producto_id " +
                     "INNER JOIN lotes l ON ib.lote_id = l.id " +
                     "WHERE l.numero_lote = ?";

        try (Connection cn = new Controlador.ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, numeroLote);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar productos por lote: " + e.getMessage());
        }

        return lista;
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
    
    public String[] obtenerCabeceraNota(int notaId) {
        String[] cabecera = new String[7];
        String sql = "SELECT nm.id, nm.tipo_movimiento, " +
                     "COALESCE(bo.nombre, 'N/A') AS origen, " +
                     "COALESCE(bd.nombre, 'N/A') AS destino, " +
                     "u.nombres AS responsable, " +
                     "nm.fecha_movimiento, nm.observacion " +
                     "FROM nota_movimiento nm " +
                     "LEFT JOIN bodegas bo ON nm.bodega_origen_id = bo.id " +
                     "LEFT JOIN bodegas bd ON nm.bodega_destino_id = bd.id " +
                     "INNER JOIN usuarios u ON nm.responsable_id = u.id " +
                     "WHERE nm.id = ?";

        try (Connection cn = new Controlador.ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, notaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cabecera[0] = String.valueOf(rs.getInt("id"));
                    cabecera[1] = rs.getString("tipo_movimiento");
                    cabecera[2] = rs.getString("origen");
                    cabecera[3] = rs.getString("destino");
                    cabecera[4] = rs.getString("responsable");
                    cabecera[5] = rs.getString("fecha_movimiento");
                    cabecera[6] = rs.getString("observacion");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cabecera de la nota: " + e.getMessage());
        }
        return cabecera;
    }
    
    public boolean registrarDetalleYActualizarStock(int notaId, String numeroLote, int productoId, double cantidad, String tipoMovimiento) {
        String sql = "{CALL sp_registrar_detalle_movimiento(?, ?, ?, ?, ?)}";
        
        try (Connection cn = new Controlador.ConexionBDD().conectar();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, notaId);
            cs.setString(2, numeroLote);
            cs.setInt(3, productoId);
            cs.setDouble(4, cantidad);
            cs.setString(5, tipoMovimiento);

            cs.execute();
            return true;

        } catch (SQLException e) {
            System.out.println("Error SQL en registrarDetalle: " + e.getMessage());
            return false;
        }
    }
  
    public ArrayList<String> obtenerLotesPorBodega(int bodegaId) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT l.numero_lote " +
                     "FROM inventario_bodega ib " +
                     "JOIN lotes l ON ib.lote_id = l.id " +
                     "WHERE ib.bodega_id = ? AND ib.stock > 0";

        try (Connection cn = new Controlador.ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, bodegaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString("numero_lote"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar lotes filtrados: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<String> obtenerProductosPorLoteYBodega(int bodegaId, String numeroLote) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.nombre " +
                     "FROM inventario_bodega ib " +
                     "JOIN lotes l ON ib.lote_id = l.id " +
                     "JOIN productos p ON ib.producto_id = p.id " +
                     "WHERE ib.bodega_id = ? AND l.numero_lote = ? AND ib.stock > 0";

        try (Connection cn = new Controlador.ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, bodegaId);
            ps.setString(2, numeroLote);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar productos filtrados: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<String> obtenerTodosLosLotes() {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT numero_lote FROM lotes";

        try (Connection cn = new Controlador.ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("numero_lote"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar todos los lotes: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<String> obtenerTodosLosProductos() {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM productos";

        try (Connection cn = new Controlador.ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar todos los productos: " + e.getMessage());
        }
        return lista;
    }
}