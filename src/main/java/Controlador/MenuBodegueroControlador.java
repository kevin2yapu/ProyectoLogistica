/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Bodega;
import Modelo.EntradaAlmacen;
import Modelo.Lote;
import Modelo.MovimientoAlmacen;
import Modelo.Producto;
import Modelo.Usuario;
import Vista.InicioSesion;
import Vista.LoteVista;
import Vista.MenuBodeguero;
import Vista.MovimientoAlmacenVista;
import Vista.ProductoIngreso;

/**
 *
 * @author KEVIN
 */
public class MenuBodegueroControlador {
    private MenuBodeguero vistaMenu;

    public MenuBodegueroControlador(MenuBodeguero vistaMenu) {
        this.vistaMenu = vistaMenu;
    }

    public void iniciar() {
        // Asignación de eventos a los botones de navegación
        this.vistaMenu.getBtnRegistroLotes().addActionListener(e -> abrirLotes());
        this.vistaMenu.getBtnRegistroProductos().addActionListener(e -> abrirProductos());
        this.vistaMenu.getBtnMovimientoAlmacen().addActionListener(e -> abrirMovimientos());

        // Evento para CERRAR SESIÓN
        this.vistaMenu.getBtnCerrarSesion().addActionListener(e -> {
    vistaMenu.dispose(); // Cierra el menú actual
    
    InicioSesion loginVista = new InicioSesion();
    Usuario loginModelo = new Usuario();
    
    // Conectar el controlador para activar el botón "Ingresar"
    UsuarioControlador loginCtrl = new UsuarioControlador(loginModelo, loginVista);
    loginCtrl.iniciar();
});

        // Configuración y muestra de la vista del menú
        this.vistaMenu.setLocationRelativeTo(null);
        this.vistaMenu.setVisible(true);
    }

    private void abrirLotes() {
        this.vistaMenu.dispose();
        
        LoteVista vistaLotes = new LoteVista();
        Lote modeloLote = new Lote();

        LoteControlador lControlador = new LoteControlador(modeloLote, vistaLotes);
        lControlador.iniciar();
    }

    private void abrirProductos() {
        this.vistaMenu.dispose();
        
        ProductoIngreso vistaProductos = new ProductoIngreso();
        Producto modeloProducto = new Producto();

        ProductoControlador pControlador = new ProductoControlador(modeloProducto, vistaProductos);
        pControlador.iniciar();
    }

    private void abrirMovimientos() {
        this.vistaMenu.dispose();

        MovimientoAlmacenVista mVista = new MovimientoAlmacenVista();
        MovimientoAlmacen mModelo = new EntradaAlmacen();
        Bodega bModelo = new Bodega();

        MovimientoControlador mControlador = new MovimientoControlador(mModelo, bModelo, mVista);
        mControlador.iniciar();
    }
    
    
}
