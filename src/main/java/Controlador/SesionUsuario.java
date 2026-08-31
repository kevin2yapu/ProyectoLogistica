/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author KEVIN
 */
public class SesionUsuario {
 private static int idUsuario;
    private static String nombre;
    private static String rol;

    public static void iniciarSesion(int id, String nombreUsuario, String rolUsuario) {
        idUsuario = id;
        nombre = nombreUsuario;
        rol = rolUsuario;
    }

    public static int getIdUsuario() { return idUsuario; }
    public static String getNombre() { return nombre; }
    public static String getRol() { return rol; }
}