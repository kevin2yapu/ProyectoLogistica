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
public class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private double stock;
    private String estado;
    private String estadoProducto;
    private Integer idLote;
    private ConexionBDD conectar = new ConexionBDD();
    
   
   
    public Producto(){
        
    }

    public Producto(int id, String codigo, String nombre, String descripcion, double stock, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.estado = estado;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
   

public ArrayList<String[]> obtenerProductos() {
    ArrayList<String[]> lista = new ArrayList<>();
    String sql = "SELECT p.id, p.codigo, p.nombre, p.descripcion, "
               + "COALESCE(SUM(ib.stock), 0) AS stock, "
               + "p.estado, p.estado_producto, "
               + "COALESCE(GROUP_CONCAT(DISTINCT l.numero_lote SEPARATOR ', '), 'Sin Lote') AS lote "
               + "FROM productos p "
               + "LEFT JOIN inventario_bodega ib ON p.id = ib.producto_id "
               + "LEFT JOIN lotes l ON ib.lote_id = l.id "
               + "GROUP BY p.id, p.codigo, p.nombre, p.descripcion, p.estado, p.estado_producto";

    try (Connection con = conectar.conectar();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String[] fila = new String[8];
            fila[0] = String.valueOf(rs.getInt("id"));
            fila[1] = rs.getString("codigo");
            fila[2] = rs.getString("nombre");
            fila[3] = rs.getString("descripcion");
            fila[4] = String.valueOf(rs.getDouble("stock"));
            fila[5] = rs.getString("estado");
            fila[6] = rs.getString("estado_producto");
            fila[7] = rs.getString("lote"); // Muestra los lotes reales asociados en bodega
            lista.add(fila);
        }
    } catch (SQLException e) {
        System.out.println(">>> ERROR SQL EN OBTENER PRODUCTOS: " + e.getMessage());
    }
    return lista;
}

    // INSERTAR PRODUCTO
    public boolean insertarProducto() {
        String sql = "{call sp_ingresar_producto(?, ?, ?, ?, ?, ?)}";
        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, this.codigo);
            cs.setString(2, this.nombre);
            cs.setString(3, this.descripcion);
            cs.setDouble(4, this.stock);
            cs.setString(5, this.estadoProducto);

            if (this.idLote != null && this.idLote > 0) {
                cs.setInt(6, this.idLote);
            } else {
                cs.setNull(6, java.sql.Types.INTEGER);
            }

            cs.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // ACTUALIZAR PRODUCTO
    public boolean actualizarProducto() {
        String sql = "{call sp_actualizar_producto(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, this.id);
            cs.setString(2, this.codigo);
            cs.setString(3, this.nombre);
            cs.setString(4, this.descripcion);
            cs.setDouble(5, this.stock);
            cs.setString(6, this.estadoProducto);

            if (this.idLote != null && this.idLote > 0) {
                cs.setInt(7, this.idLote);
            } else {
                cs.setNull(7, java.sql.Types.INTEGER);
            }

            cs.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean deshabilitarProductoBD(int idProducto) {
        String sql = "{call sp_deshabilitar_producto(?)}";
        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idProducto);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al deshabilitar producto: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String[]> buscarProductos(String criterio) {
        ArrayList<String[]> lregistros = new ArrayList<>();
        String sentenciaSQL = "{call sp_buscar_producto(?)}";

        try (Connection conectado = conectar.conectar();
             CallableStatement cs = conectado.prepareCall(sentenciaSQL)) {

            cs.setString(1, criterio);
            try (ResultSet res = cs.executeQuery()) {
                while (res.next()) {
                    String[] lista = new String[8];
                    lista[0] = String.valueOf(res.getInt("id"));
                    lista[1] = res.getString("codigo");
                    lista[2] = res.getString("nombre");
                    lista[3] = res.getString("descripcion");
                    lista[4] = String.valueOf(res.getDouble("stock"));
                    lista[5] = res.getString("estado");
                    lista[6] = res.getString("estado_producto");
                    lista[7] = res.getString("lote");
                    lregistros.add(lista);
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR SQL EN BUSCAR PRODUCTOS: " + e.getMessage());
        }
        return lregistros;
    }

    public ArrayList<String> obtenerComboLotes() {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT id, numero_lote FROM lotes WHERE estado = 'ACTIVO' ORDER BY id DESC";

        try (Connection con = conectar.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("numero_lote"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener combo lotes: " + e.getMessage());
        }
        return lista;
    }

    public boolean guardarDetalleConSP(int notaMovimientoId, int loteId, int productoId, int cantidad, String tipoMovimiento) {
        String sql = "{CALL sp_registrar_detalle_movimiento(?, ?, ?, ?, ?)}";

        ConexionBDD c = new ConexionBDD();
        try (Connection con = c.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, notaMovimientoId);
            cs.setInt(2, loteId);
            cs.setInt(3, productoId);
            cs.setInt(4, cantidad);
            cs.setString(5, tipoMovimiento);

            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en SP sp_registrar_detalle_movimiento: " + e.getMessage());
            return false;
        }
    }
    
    public ArrayList<String> obtenerProductosPorLote(int idLote) {
    ArrayList<String> lista = new ArrayList<>();
    String sql = "SELECT DISTINCT p.id, p.nombre " +
                 "FROM productos p " +
                 "INNER JOIN inventario_bodega ib ON p.id = ib.producto_id " +
                 "WHERE ib.lote_id = ?";

    try (Connection con = conectar.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idLote);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Formato idéntico al del combo de lotes: "ID - NOMBRE"
                lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        }
    } catch (SQLException e) {
        System.out.println(">>> ERROR AL CARGAR PRODUCTOS POR LOTE: " + e.getMessage());
    }
    return lista;
}

}
