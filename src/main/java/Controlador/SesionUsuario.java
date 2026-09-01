/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author KEVIN
 */


///Recordar quién inició sesión sin perder la información,  utilizamos como una memoria
public class SesionUsuario {
private static int idUsuario;
    private static String nombre;
    private static String rol;
    private static Integer idBodega;
    private static String nombreBodega;

    public static void iniciarSesion(int id, String nombreUsuario, String rolUsuario, Integer bodegaId, String bodegaNombre) {
        idUsuario = id;
        nombre = nombreUsuario;
        rol = rolUsuario;
        idBodega = bodegaId;
        nombreBodega = bodegaNombre;
    }

    public static int getIdUsuario() { return idUsuario; }
    public static String getNombre() { return nombre; }
    public static String getRol() { return rol; }
    public static Integer getIdBodega() { return idBodega; }
    public static String getNombreBodega() { return nombreBodega; }

    public static void cerrarSesion() {
        idUsuario = 0;
        nombre = null;
        rol = null;
        idBodega = null;
        nombreBodega = null;
        
      
    }
}
