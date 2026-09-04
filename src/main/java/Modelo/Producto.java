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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    private int idBodega;
    private ConexionBDD conectar = new ConexionBDD();
    private Integer idLote;
    
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

    public int getIdBodega() {
        return idBodega;
    }

    public void setIdBodega(int idBodega) {
        this.idBodega = idBodega;
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
    
    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
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

    public ArrayList<String> obtenerProductosPorLote(int idLote, int idBodega) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.nombre " +
                     "FROM productos p " +
                     "INNER JOIN inventario_bodega ib ON p.id = ib.producto_id " +
                     "WHERE ib.lote_id = ? AND ib.bodega_id = ?";

        try (Connection con = conectar.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLote);
            ps.setInt(2, idBodega);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR AL CARGAR PRODUCTOS POR LOTE: " + e.getMessage());
        }
        return lista;
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
                fila[5] = rs.getString("estado_producto"); // Posición 5: Estado Físico ("BUENO ESTADO")
                fila[6] = rs.getString("lote");            // Posición 6: Lote
                fila[7] = rs.getString("estado");          // Posición 7: Estado de la BD ("ACTIVO")
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR SQL EN OBTENER PRODUCTOS: " + e.getMessage());
        }
        return lista;
    }

    public Connection getConexion() throws SQLException {
        return conectar.conectar();
    }

    public boolean insertarProducto() {
        String sql = "INSERT INTO productos (codigo, nombre, descripcion, estado_producto, estado) VALUES (?, ?, ?, ?, 'ACTIVO')";
        
        try {
            PreparedStatement ps = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, this.codigo);
            ps.setString(2, this.nombre);
            ps.setString(3, this.descripcion);
            ps.setString(4, this.estadoProducto);

            int res = ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
            return res > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

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
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.codigo, p.nombre, p.descripcion, "
                   + "COALESCE(SUM(ib.stock), 0) AS stock, "
                   + "p.estado, p.estado_producto, "
                   + "COALESCE(GROUP_CONCAT(DISTINCT l.numero_lote SEPARATOR ', '), 'Sin Lote') AS lote "
                   + "FROM productos p "
                   + "LEFT JOIN inventario_bodega ib ON p.id = ib.producto_id "
                   + "LEFT JOIN lotes l ON ib.lote_id = l.id "
                   + "WHERE p.codigo LIKE ? OR p.nombre LIKE ? "
                   + "GROUP BY p.id, p.codigo, p.nombre, p.descripcion, p.estado, p.estado_producto";

        try (Connection con = conectar.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, "%" + criterio + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] fila = new String[8];
                    fila[0] = String.valueOf(rs.getInt("id"));
                    fila[1] = rs.getString("codigo");
                    fila[2] = rs.getString("nombre");
                    fila[3] = rs.getString("descripcion");
                    fila[4] = String.valueOf(rs.getDouble("stock"));
                    fila[5] = rs.getString("estado_producto"); // Posición 5: Estado Físico ("BUENO ESTADO")
                    fila[6] = rs.getString("lote");            // Posición 6: Lote
                    fila[7] = rs.getString("estado");          // Posición 7: Estado de la BD ("ACTIVO")
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR SQL EN BUSCAR PRODUCTOS: " + e.getMessage());
        }
        return lista;
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
                    lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> ERROR AL CARGAR PRODUCTOS POR LOTE: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public String toString() {
        return nombre != null ? nombre : "";
    }

    public ArrayList<Producto> obtenerCatalogo() {
        ArrayList<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, codigo, nombre, descripcion FROM productos WHERE estado = 'ACTIVO'";

        try (Connection con = conectar.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setCodigo(rs.getString("codigo"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo de productos: " + e.getMessage());
        }

        return lista;
    }

    public ArrayList<String> obtenerProductosDisponiblesPorBodega(int idBodega) {
        ArrayList<String> listaProductos = new ArrayList<>();
        String sql = "SELECT DISTINCT p.nombre " +
                     "FROM productos p " +
                     "INNER JOIN lotes l ON l.producto_id = p.id " +
                     "WHERE l.bodega_id = ? AND l.cantidad > 0 " +
                     "GROUP BY p.nombre " +
                     "ORDER BY p.nombre ASC;";

        Controlador.ConexionBDD conexion = new Controlador.ConexionBDD();

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idBodega);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                listaProductos.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por bodega: " + e.getMessage());
        }

        return listaProductos;
    }
    
    public List<String> obtenerNombresProductosPorBodega(int idBodega) {
        List<String> listaProductos = new ArrayList<>();
        String sql = "SELECT p.nombre " +
                     "FROM productos p " +
                     "INNER JOIN lotes l ON p.id = l.producto_id " +
                     "WHERE l.bodega_id = ? AND l.cantidad > 0 " +
                     "GROUP BY p.nombre " +
                     "ORDER BY p.nombre ASC;";

        ConexionBDD conexionBDD = new ConexionBDD();

        try (Connection con = conexionBDD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idBodega);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                listaProductos.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos en Modelo: " + e.getMessage());
        }

        return listaProductos;
    }
    
    public int obtenerIdPorNombre(String nombreProducto) {
        int id = 0;
        String sql = "SELECT id FROM productos WHERE nombre = ? LIMIT 1;";
        Controlador.ConexionBDD conexionBDD = new Controlador.ConexionBDD();

        try (Connection con = conexionBDD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ID de producto: " + e.getMessage());
        }

        return id;
    }
}