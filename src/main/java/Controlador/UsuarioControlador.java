/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Usuario;
import Vista.GestionUsuarioVista;
import Vista.InicioSesion;
import Vista.MenuAdmin;
import Vista.MenuBodeguero;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author KEVIN
 */
public class UsuarioControlador {

 private Usuario modelo;
    private InicioSesion uvista; // Vista para Login
    private GestionUsuarioVista vistaGestion;
    private int idUsuarioSeleccionado = -1;

    // Lista en memoria para almacenar los objetos Usuario (incluye el ID)
    private ArrayList<Usuario> listaUsuariosActual = new ArrayList<>();

    public UsuarioControlador() {
    }

    // Constructor para el Login
    public UsuarioControlador(Usuario modelo, InicioSesion uvista) {
        this.modelo = modelo;
        this.uvista = uvista;
    }

    // Constructor para la Gestión de Usuarios (Admin)
    public UsuarioControlador(Usuario modelo, GestionUsuarioVista vistaGestion) {
        this.modelo = modelo;
        this.vistaGestion = vistaGestion;
    }

    // --- MÉTODOS DE LOGIN ---

    public void iniciar() {
        // 1. Remover listeners anteriores del botón para evitar dupplicaciones
        for (java.awt.event.ActionListener al : this.uvista.getBtnIngresar().getActionListeners()) {
            this.uvista.getBtnIngresar().removeActionListener(al);
        }

        // 2. Asignar el nuevo listener
        this.uvista.getBtnIngresar().addActionListener(e -> recuperarUsuario());

        // 3. Mostrar la vista centrada
        this.uvista.setLocationRelativeTo(null);
        this.uvista.setVisible(true);
    }

    public Usuario inicioSesion(String email, String contrasena) {
        Usuario u = null;
        String sql = "{call sp_validar_login(?, ?)}";
        ConexionBDD conectar = new ConexionBDD();

        try (Connection conectado = conectar.conectar();
             CallableStatement cs = conectado.prepareCall(sql)) {

            cs.setString(1, email);
            cs.setString(2, contrasena);

            ResultSet resultado = cs.executeQuery();

            if (resultado.next()) {
               u = new Usuario(
    resultado.getInt("id"),
    resultado.getString("nombres"),
    resultado.getString("email"),
    resultado.getString("contrasena"),
    resultado.getString("rol"),
    "ACTIVO" // 6to parámetro que requiere tu constructor
);
            }

        } catch (SQLException e) {
            System.out.println("Error en el SP de login: " + e.getMessage());
        }

        return u;
    }

    public void recuperarUsuario() {
        String email = uvista.getEmail();
        String contrasena = uvista.getContrasena();
        String rolSeleccionado = uvista.getRol();

        if (!email.isEmpty() && !contrasena.isEmpty()) {

            Usuario usuarioEncontrado = inicioSesion(email, contrasena);

            if (usuarioEncontrado != null) {

                String rolBD = usuarioEncontrado.getRol().trim().toUpperCase();
                String rolInterfaz = rolSeleccionado.trim().toUpperCase();

                if (rolBD.contains(rolInterfaz) || rolInterfaz.contains(rolBD)) {

                    SesionUsuario.iniciarSesion(
                        usuarioEncontrado.getId(), 
                        usuarioEncontrado.getNombres(), 
                        usuarioEncontrado.getRol()
                    );

                    JOptionPane.showMessageDialog(uvista, "¡Bienvenido/a: " + usuarioEncontrado.getNombres() + "!");

                    if (rolBD.contains("ADMIN")) {
                        uvista.dispose();
                        MenuAdmin vistaAdmin = new MenuAdmin();
                        MenuAdministradorControlador adminCtrl = new MenuAdministradorControlador(vistaAdmin);
                        adminCtrl.iniciar();
                    } else {
                        uvista.dispose();
                        MenuBodeguero vistaMenu = new MenuBodeguero();
                        MenuBodegueroControlador menuCtrl = new MenuBodegueroControlador(vistaMenu);
                        menuCtrl.iniciar();
                    }

                } else {
                    JOptionPane.showMessageDialog(uvista, "El rol seleccionado no corresponde a este usuario.", "Rol Incorrecto", JOptionPane.WARNING_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(uvista, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(uvista, "Por favor complete todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- MÉTODOS DE GESTIÓN DE USUARIOS ---

    public void iniciarGestion() {
        cargarTablaUsuarios();

        // Evento al hacer clic en la tabla para pasar datos a los campos
        this.vistaGestion.getTblUsuarios().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = vistaGestion.getTblUsuarios().getSelectedRow();
                if (fila >= 0 && fila < listaUsuariosActual.size()) {
                    Usuario seleccionado = listaUsuariosActual.get(fila);
                    idUsuarioSeleccionado = seleccionado.getId();

                    vistaGestion.setCampos(
                        seleccionado.getNombres(),
                        seleccionado.getEmail(),
                        seleccionado.getContrasena(),
                        seleccionado.getRol()
                    );
                }
            }
        });

        // Eventos de los botones de la interfaz de Gestión
        this.vistaGestion.getBtnEditar().addActionListener(e -> editarUsuario());
        this.vistaGestion.getBtnDeshabilitar().addActionListener(e -> deshabilitarUsuario());
        this.vistaGestion.getBtnVolver().addActionListener(e -> regresarAlMenu());

        this.vistaGestion.setLocationRelativeTo(null);
        this.vistaGestion.setVisible(true);
    }

    private void deshabilitarUsuario() {
        int filaSeleccionada = vistaGestion.getTblUsuarios().getSelectedRow();

        if (filaSeleccionada == -1 || filaSeleccionada >= listaUsuariosActual.size()) {
            JOptionPane.showMessageDialog(vistaGestion, "Seleccione un usuario de la tabla para deshabilitar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioSeleccionado = listaUsuariosActual.get(filaSeleccionada);

        int confirmacion = JOptionPane.showConfirmDialog(
            vistaGestion, 
            "¿Está seguro de que desea deshabilitar a " + usuarioSeleccionado.getNombres() + "?", 
            "Confirmar acción", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            modelo.setId(usuarioSeleccionado.getId());

            if (modelo.deshabilitarUsuario()) {
                JOptionPane.showMessageDialog(vistaGestion, "Usuario deshabilitado correctamente.");
                cargarTablaUsuarios();
                vistaGestion.limpiarCampos();
                idUsuarioSeleccionado = -1;
            } else {
                JOptionPane.showMessageDialog(vistaGestion, "Error al deshabilitar el usuario.", "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   private void cargarTablaUsuarios() {
    DefaultTableModel tableModel = (DefaultTableModel) vistaGestion.getTblUsuarios().getModel();
    tableModel.setRowCount(0);

    ArrayList<String[]> datos = modelo.listarUsuarios(); 
    listaUsuariosActual.clear();

    for (String[] fila : datos) {
        // Pasa los 6 parámetros incluyendo fila[5] (el estado)
        Usuario u = new Usuario(
            Integer.parseInt(fila[0]),
            fila[1],
            fila[2],
            fila[3],
            fila[4],
            fila[5] 
        );
        
        listaUsuariosActual.add(u);
        
        // Se cargan las 5 columnas a la vista
        tableModel.addRow(new Object[]{u.getNombres(), u.getEmail(), u.getContrasena(), u.getRol(), u.getEstado()});
    }
}

    private void editarUsuario() {
        if (idUsuarioSeleccionado == -1) {
            JOptionPane.showMessageDialog(vistaGestion, "Seleccione un usuario de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = modelo.actualizarUsuario(
            idUsuarioSeleccionado,
            vistaGestion.getNombres(),
            vistaGestion.getEmail(),
            vistaGestion.getContrasena(),
            vistaGestion.getRol()
        );

        if (exito) {
            JOptionPane.showMessageDialog(vistaGestion, "Usuario actualizado correctamente.");
            cargarTablaUsuarios();
            vistaGestion.limpiarCampos();
            idUsuarioSeleccionado = -1;
        } else {
            JOptionPane.showMessageDialog(vistaGestion, "Error al actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void regresarAlMenu() {
        this.vistaGestion.dispose();
        
        String rolSesion = SesionUsuario.getRol();
        
        if (rolSesion != null && rolSesion.trim().toUpperCase().contains("ADMIN")) {
            MenuAdmin vistaAdmin = new MenuAdmin();
            MenuAdministradorControlador adminCtrl = new MenuAdministradorControlador(vistaAdmin);
            adminCtrl.iniciar();
        } else {
            MenuBodeguero vistaBodeguero = new MenuBodeguero();
            MenuBodegueroControlador bodegueroCtrl = new MenuBodegueroControlador(vistaBodeguero);
            bodegueroCtrl.iniciar();
        }
    }
}