/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public class Usuario {
    private int id;
    private String nombres;
    private String email;
    private String contrasena;
    private String rol ;

    public Usuario() {
    }
    
    public Usuario(int id, String nombres, String email, String contrasena, String rol) {
        this.id = id;
        this.nombres = nombres;
        this.email = email;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    
   public ArrayList<String[]> obtenerTodosLosUsuarios() {
    ConexionBDD conexion = new ConexionBDD();
    ArrayList<String[]> lista = new ArrayList<>();
    String sql = "SELECT id, nombres, email, rol, estado FROM usuarios";

    try (Connection conectado = conexion.conectar();
         PreparedStatement ps = conectado.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String[] fila = new String[5];
            fila[0] = String.valueOf(rs.getInt("id"));
            fila[1] = rs.getString("nombres");
            fila[2] = rs.getString("email");
            fila[3] = rs.getString("rol");
            fila[4] = rs.getInt("estado") == 1 ? "ACTIVO" : "INACTIVO";
            lista.add(fila);
        }
    } catch (SQLException e) {
        System.err.println("Error al listar usuarios: " + e.getMessage());
    }
    return lista;
}

// Cambiar la contraseña del usuario
public boolean cambiarPassword(int idUsuario, String nuevaClave) {
    ConexionBDD conexion = new ConexionBDD();
    String sql = "UPDATE usuarios SET contrasena = ? WHERE id = ?";

    try (Connection conectado = conexion.conectar();
         PreparedStatement ps = conectado.prepareStatement(sql)) {
        ps.setString(1, nuevaClave);
        ps.setInt(2, idUsuario);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al cambiar clave: " + e.getMessage());
        return false;
    }
}

// Activar o Desactivar usuario (1 o 0)
public boolean cambiarEstado(int idUsuario, int nuevoEstado) {
    ConexionBDD conexion = new ConexionBDD();
    String sql = "UPDATE usuarios SET estado = ? WHERE id = ?";

    try (Connection conectado = conexion.conectar();
         PreparedStatement ps = conectado.prepareStatement(sql)) {
        ps.setInt(1, nuevoEstado);
        ps.setInt(2, idUsuario);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al cambiar estado: " + e.getMessage());
        return false;
    }
}

public ArrayList<String[]> listarUsuarios() {
    Controlador.ConexionBDD conexion = new Controlador.ConexionBDD();
    ArrayList<String[]> lista = new ArrayList<>();
    String sql = "SELECT id, nombres, email, contrasena, rol FROM usuarios";

    try (Connection conectado = conexion.conectar();
         PreparedStatement ps = conectado.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String[] fila = new String[5];
            fila[0] = String.valueOf(rs.getInt("id"));
            fila[1] = rs.getString("nombres");
            fila[2] = rs.getString("email");
            fila[3] = rs.getString("contrasena");
            fila[4] = rs.getString("rol");
            lista.add(fila);
        }
    } catch (SQLException e) {
        System.err.println("Error al listar usuarios: " + e.getMessage());
    }
    return lista;
}

public boolean actualizarUsuario(int id, String nombres, String email, String contrasena, String rol) {
    // Validación para evitar sobreescribir con datos vacíos
    if (nombres.trim().isEmpty() || email.trim().isEmpty()) {
        System.err.println("Campos vacíos detectados. Operación cancelada.");
        return false;
    }

    Controlador.ConexionBDD conexion = new Controlador.ConexionBDD();
    String sql = "UPDATE usuarios SET nombres = ?, email = ?, contrasena = ?, rol = ? WHERE id = ?";

    try (Connection conectado = conexion.conectar();
         PreparedStatement ps = conectado.prepareStatement(sql)) {

        ps.setString(1, nombres);
        ps.setString(2, email);
        ps.setString(3, contrasena);
        ps.setString(4, rol);
        ps.setInt(5, id);

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al actualizar usuario: " + e.getMessage());
        return false;
    }
}
    
}
