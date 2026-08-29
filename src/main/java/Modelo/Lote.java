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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public class Lote {
  private int id;
    private String codigoLote;
    private int idProducto;
    private int idBodega;
    private String fechaVencimiento;
    private String estado;
    
    private ConexionBDD conectar = new ConexionBDD();

    public Lote() {
    }

    public Lote(String codigoLote, int idProducto, int idBodega, String fechaVencimiento, String estado) {
        this.codigoLote = codigoLote;
        this.idProducto = idProducto;
        this.idBodega = idBodega;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdBodega() {
        return idBodega;
    }

    public void setIdBodega(int idBodega) {
        this.idBodega = idBodega;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

   public ArrayList<String[]> obtenerLotes() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "{call sp_obtener_lotes()}";

        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt("id"));             
                fila[1] = rs.getString("numero_lote");                
                fila[2] = String.valueOf(rs.getInt("producto_id"));    
                fila[3] = rs.getString("nombre_producto");            
                fila[4] = rs.getString("nombre_bodega");            
                fila[5] = rs.getString("fecha_vencimiento");          
                fila[6] = rs.getString("estado");                     
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener lotes: " + e.getMessage());
        }
        return lista;
    }

    // INSERTAR
  public boolean insertarLote() {
        String sql = "{call sp_insertar_lote(?, ?, ?)}"; 
        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, this.codigoLote);
            cs.setInt(2, this.idBodega);
            cs.setString(3, this.fechaVencimiento);

            cs.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar lote: " + e.getMessage());
            return false;
        }
    }

    // EDITAR
   public boolean editarLote() {
        String sql = "{call sp_editar_lote(?, ?, ?, ?)}";
        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, this.id);
            cs.setString(2, this.codigoLote);
            cs.setInt(3, this.idBodega);
            cs.setString(4, this.fechaVencimiento);

            cs.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al editar lote: " + e.getMessage());
            return false;
        }
    }

    // DESHABILITAR
   public boolean deshabilitarLote() {
        String sql = "{call sp_deshabilitar_lote(?)}";
        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, this.codigoLote);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al deshabilitar lote: " + e.getMessage());
            return false;
        }
    }
   
   public ArrayList<String[]> buscarLotes(String criterio) {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "{call sp_buscar_lotes(?)}";

        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, criterio);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("numero_lote");
                fila[2] = String.valueOf(rs.getInt("producto_id"));
                fila[3] = rs.getString("nombre_producto");
                fila[4] = rs.getString("nombre_bodega");
                fila[5] = rs.getString("fecha_vencimiento");
                fila[6] = rs.getString("estado");
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar lotes: " + e.getMessage());
        }
        return lista;
    }
   public ArrayList<String> obtenerComboBodegas() {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM bodegas WHERE estado = 'ACTIVO'";

        try (Connection con = conectar.conectar();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar combo bodegas: " + e.getMessage());
        }
        return lista;
    }
}

    
    

