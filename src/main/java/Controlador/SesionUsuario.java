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
    private static String nombreUsuario;

    public static void iniciarSesion(int id, String nombre) {
        idUsuario = id;
        nombreUsuario = nombre;
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public static void cerrarSesion() {
        idUsuario = 0;
        nombreUsuario = null;
    }  
}
