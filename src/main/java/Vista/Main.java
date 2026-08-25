/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

import Controlador.BodegaControlador;
import Controlador.LoteControlador;
import Controlador.ProductoControlador;
import Controlador.UsuarioControlador;
import Modelo.Bodega;
import Modelo.Lote;
import Modelo.Producto;
import Modelo.Usuario;

/**
 *
 * @author KEVIN
 */
public class Main {
    public static void main(String[] args) {
//        // 1. Instanciar Modelo y Vista
//        Usuario modelo = new Usuario();
//        InicioSesion vista = new InicioSesion();
//
//        // 2. Instanciar Controlador pasando referencias
//        UsuarioControlador controlador = new UsuarioControlador(modelo, vista);
//
//        // 3. Arrancar la aplicación y registrar los listeners
//        controlador.iniciar();
//    }
    
//    Producto modelo = new Producto();
//        ProductoIngreso vista = new ProductoIngreso();
//
//        ProductoControlador controlador = new ProductoControlador(modelo, vista);
//        controlador.iniciar();
//    }
    
   Lote modelo= new Lote();
   LoteVista vista= new LoteVista();
   
   LoteControlador controlador = new LoteControlador (modelo, vista);
        controlador.iniciar();
}
    
//      Bodega modelo= new Bodega();
//   Bodegavista vista= new Bodegavista();
//   
//   BodegaControlador controlador = new BodegaControlador (modelo, vista);
//        controlador.iniciar();
//}
}