/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Usuario;
import Vista.InicioSesion;
import Vista.MenuBodeguero;
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

    public UsuarioControlador() {
    }

    public UsuarioControlador(Usuario modelo, InicioSesion uvista) {
        this.modelo = modelo;
        this.uvista = uvista;
    }

    // MÉTODO DE INICIALIZACIÓN DE LA VISTA
    public void iniciar() {
        // Vinculamos el botón de la vista al método de recuperación/autenticación
        this.uvista.getBtnIngresar().addActionListener(e -> recuperarUsuario());
        this.uvista.setLocationRelativeTo(null);
        this.uvista.setVisible(true);
    }

    // CONSULTA A LA BASE DE DATOS
    public Usuario inicioSesion(String email, String contrasena, String rol) {
        Usuario u = null;
        String sql = "{call sp_validar_login(?, ?, ?)}";
        ConexionBDD conectar = new ConexionBDD();

        try (Connection conectado = conectar.conectar();
             CallableStatement cs = conectado.prepareCall(sql)) {

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

        } catch (SQLException e) {
            System.out.println("Error en el SP de login: " + e.getMessage());
        }

        return u;
    }

    // LÓGICA DE NAVEGACIÓN Y SESIÓN
    public void recuperarUsuario() {
        String email = uvista.getEmail();
        String contrasena = uvista.getContrasena();
        String rol = uvista.getRol();

        if (!email.isEmpty() && !contrasena.isEmpty()) {

            Usuario usuarioEncontrado = inicioSesion(email, contrasena, rol);

            if (usuarioEncontrado != null) {
                // 1. Guardar la sesión activa dinámicamente desde la BDD
                SesionUsuario.iniciarSesion(usuarioEncontrado.getId(), usuarioEncontrado.getNombres());

                JOptionPane.showMessageDialog(uvista, "¡Bienvenido/a: " + usuarioEncontrado.getNombres() + "!");

                // 2. Evaluar el Rol para la redirección
                if (usuarioEncontrado.getRol().equalsIgnoreCase("BODEGUERO")) {
                    // Cierra la vista del Login
                    uvista.dispose();

                    // Instancia y abre el menú del Bodeguero
                    MenuBodeguero vistaMenu = new MenuBodeguero();
                    MenuBodegueroControlador menuCtrl = new MenuBodegueroControlador(vistaMenu);
                    menuCtrl.iniciar();
                } else if (usuarioEncontrado.getRol().equalsIgnoreCase("ADMINISTRADOR")) {
                    // Aquí agregas la vista del Administrador cuando la tengas lista
                    uvista.dispose();
                }

            } else {
                JOptionPane.showMessageDialog(uvista, "Credenciales incorrectas o rol no autorizado.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(uvista, "Por favor complete todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }
}