package Modelo;

import Controlador.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public abstract class MovimientoAlmacen {
    private int id;
    private String tipoMovimiento; 
    
    protected Integer bodegaOrigenId;
    protected Integer bodegaDestinoId;
    
    private int responsableId;
    private String fechaMovimiento;
    private String observacion;

    public MovimientoAlmacen() {
    }

    public MovimientoAlmacen(int id, String tipoMovimiento, Integer bodegaOrigenId, Integer bodegaDestinoId, int responsableId, String fechaMovimiento, String observacion) {
        this.id = id;
        this.tipoMovimiento = tipoMovimiento;
        this.bodegaOrigenId = bodegaOrigenId;
        this.bodegaDestinoId = bodegaDestinoId;
        this.responsableId = responsableId;
        this.fechaMovimiento = fechaMovimiento;
        this.observacion = observacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getBodegaOrigenId() { return bodegaOrigenId; }
    public void setBodegaOrigenId(Integer bodegaOrigenId) { this.bodegaOrigenId = bodegaOrigenId; }

    public Integer getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Integer bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }

    public int getResponsableId() { return responsableId; }
    public void setResponsableId(int responsableId) { this.responsableId = responsableId; }

    public String getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(String fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public abstract boolean impactarInventario(int productoId, int cantidad);

    // OBTENER LISTADO CON TRY-WITH-RESOURCES (Seguro contra fugas de memoria)
    public ArrayList<String[]> obtenerMovimientos() {
        ArrayList<String[]> lregistros = new ArrayList<>();

        String sentenciaSQL = "SELECT nm.id, nm.tipo_movimiento, "
                + "COALESCE(bo.nombre, 'PROVEEDOR') AS origen_nombre, "
                + "COALESCE(bd.nombre, 'CLIENTE') AS destino_nombre, "
                + "u.nombres AS responsable_nombre, " 
                + "nm.fecha_movimiento, nm.observacion "
                + "FROM nota_movimiento nm "
                + "LEFT JOIN bodegas bo ON nm.bodega_origen_id = bo.id "
                + "LEFT JOIN bodegas bd ON nm.bodega_destino_id = bd.id "
                + "INNER JOIN usuarios u ON nm.responsable_id = u.id "
                + "ORDER BY nm.id DESC;";

        ConexionBDD conexion = new ConexionBDD();
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sentenciaSQL);
             ResultSet res = ps.executeQuery()) {

            while (res.next()) {
                String[] lista = new String[7];
                lista[0] = String.valueOf(res.getInt("id"));
                lista[1] = res.getString("tipo_movimiento");
                lista[2] = res.getString("origen_nombre");
                lista[3] = res.getString("destino_nombre");
                lista[4] = res.getString("responsable_nombre");
                lista[5] = res.getString("fecha_movimiento");
                lista[6] = res.getString("observacion");
                lregistros.add(lista);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
        }
        return lregistros;
    }

    public int insertarMovimiento(String tipoMovimiento) {
        String sql = "INSERT INTO nota_movimiento (bodega_origen_id, bodega_destino_id, responsable_id, fecha_movimiento, observacion, tipo_movimiento) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
                     
        int idGenerado = -1;

        try (Connection cn = new ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (tipoMovimiento.contains("ENTRADA") || this.bodegaOrigenId == null || this.bodegaOrigenId <= 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, this.bodegaOrigenId);
            }

            if (this.bodegaDestinoId != null && this.bodegaDestinoId > 0) {
                ps.setInt(2, this.bodegaDestinoId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setInt(3, this.responsableId); 
            ps.setString(4, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            ps.setString(5, this.observacion);
            ps.setString(6, tipoMovimiento);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar movimiento: " + e.getMessage());
        }

        return idGenerado;
    }

    // CORREGIDO: Consulta alineada a la tabla 'lotes' y columna 'cantidad'
    public int obtenerStockDisponible(int idBodega, int idLote, int idProducto) {
        int stockActual = 0;
        String sql = "SELECT cantidad FROM lotes WHERE bodega_id = ? AND id = ? AND producto_id = ?";
        
        ConexionBDD conexion = new ConexionBDD();
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idBodega);
            ps.setInt(2, idLote);
            ps.setInt(3, idProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stockActual = rs.getInt("cantidad");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener stock: " + e.getMessage());
        }
        return stockActual;
    }

    public Integer getBodegaParaStock() {
        if (this.tipoMovimiento != null && this.tipoMovimiento.toUpperCase().contains("ENTRADA")) {
            return this.bodegaDestinoId;
        } else {
            return this.bodegaOrigenId;
        }
    }
}