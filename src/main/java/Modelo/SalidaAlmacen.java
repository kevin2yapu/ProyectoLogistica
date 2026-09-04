package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalidaAlmacen extends MovimientoAlmacen {

    public SalidaAlmacen() {
        super();
    }

    public SalidaAlmacen(int id, String tipoMovimiento, Integer bodegaOrigenId, Integer bodegaDestinoId, int responsableId, String fechaMovimiento, String observacion) {
        super(id, tipoMovimiento, bodegaOrigenId, bodegaDestinoId, responsableId, fechaMovimiento, observacion);
    }

    @Override
    public boolean impactarInventario(int productoId, int cantidad) {
        int destino = (this.bodegaDestinoId != null) ? this.bodegaDestinoId : 0;
        return impactarInventarioPorId(productoId, cantidad, destino);
    }

    public boolean impactarInventarioPorNombre(String nombreProducto, int cantidadRequerida, int bodegaDestinoId) {
        Controlador.ConexionBDD conexionBDD = new Controlador.ConexionBDD();
        int idBodegaOrigen = (this.getBodegaOrigenId() != null && this.getBodegaOrigenId() > 0) 
                             ? this.getBodegaOrigenId() 
                             : SesionUsuario.getIdBodega();

        // Une lotes con productos mediante el NOMBRE, sin importar si hay IDs de producto duplicados
        String sqlBuscarLotes = "SELECT l.id, l.cantidad, l.numero_lote, l.producto_id, l.fecha_vencimiento " +
                                "FROM lotes l " +
                                "INNER JOIN productos p ON l.producto_id = p.id " +
                                "WHERE LOWER(TRIM(p.nombre)) = LOWER(TRIM(?)) AND l.bodega_id = ? AND l.cantidad > 0 " +
                                "ORDER BY l.id ASC;";

        String sqlRestarOrigen = "UPDATE lotes SET cantidad = cantidad - ? WHERE id = ?;";
        String sqlBuscarDestino = "SELECT id FROM lotes WHERE numero_lote = ? AND bodega_id = ?;";
        String sqlSumarDestino = "UPDATE lotes SET cantidad = cantidad + ? WHERE id = ?;";
        String sqlInsertarDestino = "INSERT INTO lotes (numero_lote, producto_id, bodega_id, cantidad, fecha_vencimiento) VALUES (?, ?, ?, ?, ?);";

        Connection con = null;
        try {
            con = conexionBDD.conectar();
            con.setAutoCommit(false);

            List<LoteDetalle> lotesAfectar = new ArrayList<>();
            int stockTotalDisponible = 0;

            try (PreparedStatement psBuscar = con.prepareStatement(sqlBuscarLotes)) {
                psBuscar.setString(1, nombreProducto.trim());
                psBuscar.setInt(2, idBodegaOrigen);

                try (ResultSet rs = psBuscar.executeQuery()) {
                    while (rs.next()) {
                        int stockLote = rs.getInt("cantidad");
                        lotesAfectar.add(new LoteDetalle(
                            rs.getInt("id"),
                            stockLote,
                            rs.getString("numero_lote"),
                            rs.getInt("producto_id"),
                            rs.getDate("fecha_vencimiento")
                        ));
                        stockTotalDisponible += stockLote;
                    }
                }
            }

            if (stockTotalDisponible < cantidadRequerida) {
                System.err.println("Stock insuficiente: Se requerían " + cantidadRequerida + " pero solo hay " + stockTotalDisponible + " acumulados para " + nombreProducto);
                con.rollback();
                return false;
            }

            int pendiente = cantidadRequerida;

            for (LoteDetalle lote : lotesAfectar) {
                if (pendiente <= 0) break;

                int aDescontar = Math.min(lote.stock, pendiente);

                // 1. Descontar en Origen
                try (PreparedStatement psRestar = con.prepareStatement(sqlRestarOrigen)) {
                    psRestar.setInt(1, aDescontar);
                    psRestar.setInt(2, lote.id);
                    psRestar.executeUpdate();
                }

                // 2. Sumar / Insertar en Destino
                if (bodegaDestinoId > 0) {
                    int idLoteDestino = 0;
                    try (PreparedStatement psCheck = con.prepareStatement(sqlBuscarDestino)) {
                        psCheck.setString(1, lote.codigoLote);
                        psCheck.setInt(2, bodegaDestinoId);
                        try (ResultSet rsD = psCheck.executeQuery()) {
                            if (rsD.next()) {
                                idLoteDestino = rsD.getInt("id");
                            }
                        }
                    }

                    if (idLoteDestino > 0) {
                        try (PreparedStatement psSumar = con.prepareStatement(sqlSumarDestino)) {
                            psSumar.setInt(1, aDescontar);
                            psSumar.setInt(2, idLoteDestino);
                            psSumar.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement psInsert = con.prepareStatement(sqlInsertarDestino)) {
                            psInsert.setString(1, lote.codigoLote);
                            psInsert.setInt(2, lote.productoId);
                            psInsert.setInt(3, bodegaDestinoId);
                            psInsert.setInt(4, aDescontar);
                            psInsert.setDate(5, lote.fechaVencimiento);
                            psInsert.executeUpdate();
                        }
                    }
                }

                pendiente -= aDescontar;
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Error SQL al descontar inventario: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean impactarInventarioPorId(int productoId, int cantidadRequerida, int bodegaDestinoId) {
        // Mantiene compatibilidad obteniendo el nombre del producto primero
        Controlador.ConexionBDD conexionBDD = new Controlador.ConexionBDD();
        String sqlNombre = "SELECT nombre FROM productos WHERE id = ?;";
        String nombre = "";

        try (Connection con = conexionBDD.conectar();
             PreparedStatement ps = con.prepareStatement(sqlNombre)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombre = rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!nombre.isEmpty()) {
            return impactarInventarioPorNombre(nombre, cantidadRequerida, bodegaDestinoId);
        }
        return false;
    }

    private static class LoteDetalle {
        int id, stock, productoId;
        String codigoLote;
        java.sql.Date fechaVencimiento;

        LoteDetalle(int id, int stock, String codigoLote, int productoId, java.sql.Date fechaVencimiento) {
            this.id = id;
            this.stock = stock;
            this.codigoLote = codigoLote;
            this.productoId = productoId;
            this.fechaVencimiento = fechaVencimiento;
        }
    }
}