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
import java.sql.Types;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public abstract class MovimientoAlmacen {
    private int id;
    private String tipoMovimiento; 
    private Integer bodegaOrigenId;
    private Integer bodegaDestinoId;
    private int responsableId;
    private String fechaMovimiento;
    private String observacion;

    
    
    
    ConexionBDD conectar;
    Connection conectado;
    PreparedStatement ejecutar;
    ResultSet resultado;
    
  protected void asegurarConexion() {
        try {
            if (conectado == null || conectado.isClosed()) {
                conectar = new ConexionBDD();
                conectado = (Connection) conectar.conectar();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar la conexión BDD: " + e.getMessage());
        }
    }

    // Constructor vacío: Inicializa siempre la conexión
    public MovimientoAlmacen() {
        asegurarConexion();
    }

    // Constructor con parámetros: Inicializa los atributos Y la conexión
    public MovimientoAlmacen(int id, String tipoMovimiento, Integer bodegaOrigenId, Integer bodegaDestinoId, int responsableId, String fechaMovimiento, String observacion) {
        this(); // Llama al constructor vacío para asegurar la conexión a la BDD
        this.id = id;
        this.tipoMovimiento = tipoMovimiento;
        this.bodegaOrigenId = bodegaOrigenId;
        this.bodegaDestinoId = bodegaDestinoId;
        this.responsableId = responsableId;
        this.fechaMovimiento = fechaMovimiento;
        this.observacion = observacion;
    }

    public int getId() { 
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }
    
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    

    public Integer getBodegaOrigenId() { 
        return bodegaOrigenId;
    }
    
    public void setBodegaOrigenId(Integer bodegaOrigenId) { 
        this.bodegaOrigenId = bodegaOrigenId; 
    }
    

    public Integer getBodegaDestinoId() {
        return bodegaDestinoId;
    }
    
    public void setBodegaDestinoId(Integer bodegaDestinoId) { 
        this.bodegaDestinoId = bodegaDestinoId; 
    }
    

    public int getResponsableId() { 
        return responsableId;
    }
    
    public void setResponsableId(int responsableId) {
        this.responsableId = responsableId; 
    }

    public String getFechaMovimiento() {
        return fechaMovimiento; 
    }
    public void setFechaMovimiento(String fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
    

    public String getObservacion() { 
        return observacion;
    }
    
    public void setObservacion(String observacion) {
        this.observacion = observacion; 
    }

    
    public abstract boolean impactarInventario(int productoId, int cantidad);

    // OBTENER EL LISTADO TOTAL DE NOTAS DE MOVIMIENTO
    public ArrayList<String[]> obtenerMovimientos() {
    asegurarConexion();
    ArrayList<String[]> lregistros = new ArrayList<>();
    

    // Se usa u.nombres que coincide exactamente con la columna de tu base de datos
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

    try {
        ejecutar = conectado.prepareStatement(sentenciaSQL);
        ResultSet res = ejecutar.executeQuery();

        while (res.next()) {
            String[] lista = new String[7];
            lista[0] = res.getInt("id") + "";
            lista[1] = res.getString("tipo_movimiento");
            lista[2] = res.getString("origen_nombre");
            lista[3] = res.getString("destino_nombre");
            lista[4] = res.getString("responsable_nombre");
            lista[5] = res.getString("fecha_movimiento");
            lista[6] = res.getString("observacion");
            lregistros.add(lista);
        }
        ejecutar.close();
    } catch (SQLException e) {
        System.err.println("Error al obtener movimientos: " + e.getMessage());
    }
    return lregistros;
}
    
    // INSERTAR EN LA TABLA nota_movimiento VIA PROCEDIMIENTO ALMACENADO
   public int insertarMovimiento(String tipoMovimiento) {
        String sql = "INSERT INTO nota_movimiento (bodega_origen_id, bodega_destino_id, responsable_id, fecha_movimiento, observacion, tipo_movimiento) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
                     
        int idGenerado = -1;

        try (Connection cn = new Controlador.ConexionBDD().conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (tipoMovimiento.contains("ENTRADA") || this.bodegaOrigenId == null) {
                ps.setNull(1, java.sql.Types.INTEGER); // bodega_origen_id = NULL
            } else {
                ps.setInt(1, this.bodegaOrigenId);     // bodega_origen_id = ID de Origen
            }

            // Bodega Destino (Siempre requerida)
            if (this.bodegaDestinoId != null) {
                ps.setInt(2, this.bodegaDestinoId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            // Resto de los parámetros
            ps.setInt(3, this.responsableId); 
            ps.setString(4, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            ps.setString(5, this.observacion);
            ps.setString(6, tipoMovimiento);

            ps.executeUpdate();

            // Obtener el ID autonumérico generado (id de la nota)
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar movimiento: " + e.getMessage());
        }

        return idGenerado;
    }
   
   
    public int obtenerStockDisponible(int idBodega, int idLote, int idProducto) {
        int stockActual = 0;
        String sql = "SELECT stock FROM inventario_bodega WHERE bodega_id = ? AND lote_id = ? AND producto_id = ?";
        
        ConexionBDD conexion = new ConexionBDD();
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idBodega);
            ps.setInt(2, idLote);
            ps.setInt(3, idProducto);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stockActual = rs.getInt("stock");
            }
        } catch (Exception e) {
            e.printStackTrace();
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