/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author KEVIN
 */
public class EntradaAlmacen extends MovimientoAlmacen {

    public EntradaAlmacen() {
        super();
    }

    public EntradaAlmacen(int id, String tipoMovimiento, Integer bodegaOrigenId, Integer bodegaDestinoId, 
            int responsableId, String fechaMovimiento, String observacion) {
        super(id, tipoMovimiento, bodegaOrigenId, bodegaDestinoId, responsableId, fechaMovimiento, observacion);
    }

   @Override
public boolean impactarInventario(int productoId, int cantidad) {
    String sentenciaSQL = "UPDATE productos SET stock = stock + ? WHERE id = ?;";
    
    ConexionBDD conexion = new ConexionBDD();
    
    try (Connection con = conexion.conectar();
         PreparedStatement ps = con.prepareStatement(sentenciaSQL)) {
        
        ps.setInt(1, cantidad);
        ps.setInt(2, productoId);
        
        int filasAfectadas = ps.executeUpdate();
        return filasAfectadas > 0;
        
    } catch (SQLException e) {
        System.err.println("Error al aplicar entrada: " + e.getMessage());
        return false;
    }
}
}