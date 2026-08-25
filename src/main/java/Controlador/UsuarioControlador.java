/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Usuario;
import Vista.InicioSesion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author KEVIN
 */
public class UsuarioControlador {
    
    private Usuario modelo;
    private InicioSesion uvista;
    
    ConexionBDD conectar = new ConexionBDD();
   

 public UsuarioControlador() {
    }

    public UsuarioControlador(Usuario modelo, InicioSesion uvista) {
        this.modelo = modelo;
        this.uvista = uvista;
    }
    
    
   public Usuario inicioSesion(String email, String contrasena, String rol) throws SQLException {
    Usuario u = null;
    String sql = "{call sp_validar_login(?, ?, ?)}";
    ConexionBDD conectar = new ConexionBDD();

    try {
        Connection conectado = conectar.conectar();
        CallableStatement cs = conectado.prepareCall(sql);

        cs.setString(1, email);
        cs.setString(2, contrasena);
        cs.setString(3, rol);

        ResultSet resultado = cs.executeQuery();

        if (resultado.next()) {
            u = new Usuario(
                resultado.getInt("id"),
                resultado.getString("nombres"),
                resultado.getString("email"),
                resultado.getString("contrasena"),
                resultado.getString("rol")
            );
        }

        conectado.close();

    } catch (SQLException e) {
        System.out.println("Error en el SP de login: " + e.getMessage());
    }

    return u;
}
   
   public void recuperarUsuario() throws SQLException {
     String email = uvista.getEmail();
    String contrasena = uvista.getContrasena();
    String rol = uvista.getRol();

    if (!email.isEmpty() && !contrasena.isEmpty()) {

        Usuario usuarioEncontrado = inicioSesion(email, contrasena, rol);

        if (usuarioEncontrado != null) {
            JOptionPane.showMessageDialog(uvista, "Bienvenido/a: " + usuarioEncontrado.getNombres());

            if (usuarioEncontrado.getRol().equalsIgnoreCase("ADMINISTRADOR")) {
              
            } else if (usuarioEncontrado.getRol().equalsIgnoreCase("BODEGUERO")) {
                
            }

            uvista.dispose();
        } else {
            JOptionPane.showMessageDialog(uvista, "Credenciales incorrectas o rol no autorizado.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } else {
        JOptionPane.showMessageDialog(uvista, "Por favor complete todos los campos.");
    }
}

    // INICIAR
   public void iniciar() {
    uvista.agregarListenerIngresar(mi -> {
        try {
            recuperarUsuario();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(uvista, "Error de base de datos: " + e.getMessage());
        }
    });
    uvista.setLocationRelativeTo(null);
    uvista.setVisible(true);
}
}
