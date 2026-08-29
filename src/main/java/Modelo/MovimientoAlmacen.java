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
    public int insertarMovimiento(String tipo) {
        asegurarConexion();

        try {
            CallableStatement cs = conectado.prepareCall("{call sp_insertar_movimiento(?, ?, ?, ?, ?, ?)}");
            cs.setString(1, tipo);
            
            if (bodegaOrigenId != null) cs.setInt(2, bodegaOrigenId);
            else cs.setNull(2, Types.INTEGER);

            if (bodegaDestinoId != null) cs.setInt(3, bodegaDestinoId);
            else cs.setNull(3, Types.INTEGER);

            cs.setInt(4, responsableId);
            cs.setString(5, observacion);
            
            cs.registerOutParameter(6, Types.INTEGER); // Recibe el ID asignado
            cs.execute();

            int idGenerado = cs.getInt(6);
            cs.close();
            return idGenerado;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar movimiento: " + e.getMessage());
        }
        return -1;
    }
}