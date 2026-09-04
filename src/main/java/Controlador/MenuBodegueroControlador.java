package Controlador;

import Modelo.Bodega;
import Modelo.InventarioBodega;
import Modelo.MovimientoAlmacen;
import Modelo.Producto;
import Modelo.Usuario;
import Vista.InicioSesion;
import Vista.InventarioBodegaVista;
import Vista.MenuBodeguero;
import Vista.MovimientoAlmacenVista;
import Vista.ProductoIngreso;
import Vista.DetalleMovimiento;

/**
 * Controlador para la gestión del Menú del Bodeguero.
 * @author KEVIN
 */
public class MenuBodegueroControlador {
    private MenuBodeguero vistaMenu;

    public MenuBodegueroControlador(MenuBodeguero vistaMenu) {
        this.vistaMenu = vistaMenu;
    }

    public void iniciar() {
        // 1. EVENTO: ENTRADA DE PRODUCTOS
        if (this.vistaMenu.getBtnEntradaProductos() != null) {
            for (java.awt.event.ActionListener al : this.vistaMenu.getBtnEntradaProductos().getActionListeners()) {
                this.vistaMenu.getBtnEntradaProductos().removeActionListener(al);
            }
            this.vistaMenu.getBtnEntradaProductos().addActionListener(e -> abrirEntradaProductos());
        }

        // 2. EVENTO: SALIDA DE PRODUCTOS (Usa el botón de movimiento hacia la vista de detalle)
        if (this.vistaMenu.getBtnMovimientoAlmacen() != null) {
            for (java.awt.event.ActionListener al : this.vistaMenu.getBtnMovimientoAlmacen().getActionListeners()) {
                this.vistaMenu.getBtnMovimientoAlmacen().removeActionListener(al);
            }
            this.vistaMenu.getBtnMovimientoAlmacen().addActionListener(e -> abrirSalidaProductos());
        }

        // 3. EVENTO: INVENTARIO DE BODEGA
        if (this.vistaMenu.getBtnInventarioBodega() != null) {
            for (java.awt.event.ActionListener al : this.vistaMenu.getBtnInventarioBodega().getActionListeners()) {
                this.vistaMenu.getBtnInventarioBodega().removeActionListener(al);
            }
            this.vistaMenu.getBtnInventarioBodega().addActionListener(e -> abrirInventarioBodega());
        }

        // 4. EVENTO: CERRAR SESIÓN
        if (this.vistaMenu.getBtnCerrarSesion() != null) {
            for (java.awt.event.ActionListener al : this.vistaMenu.getBtnCerrarSesion().getActionListeners()) {
                this.vistaMenu.getBtnCerrarSesion().removeActionListener(al);
            }
            this.vistaMenu.getBtnCerrarSesion().addActionListener(e -> {
                this.vistaMenu.dispose(); // Cierra el menú actual
                
                InicioSesion loginVista = new InicioSesion();
                Usuario loginModelo = new Usuario();
                
                UsuarioControlador loginCtrl = new UsuarioControlador(loginModelo, loginVista);
                loginCtrl.iniciar();
            });
        }

        // Configuración y muestra de la vista del menú
        this.vistaMenu.setLocationRelativeTo(null);
        this.vistaMenu.setVisible(true);
    }

    // MÉTODOS PRIVADOS DE NAVEGACIÓN

    private void abrirEntradaProductos() {
        this.vistaMenu.dispose();
        
        ProductoIngreso vistaProductos = new ProductoIngreso();
        Producto modeloProducto = new Producto();

        ProductoControlador pControlador = new ProductoControlador(modeloProducto, vistaProductos);
        pControlador.iniciar();
    }

    private void abrirMovimientos() {
        this.vistaMenu.dispose();

        MovimientoAlmacenVista mVista = new MovimientoAlmacenVista();
        MovimientoAlmacen mModelo = new Modelo.EntradaAlmacen(); 
        Bodega bModelo = new Bodega();

        MovimientoControlador mControlador = new MovimientoControlador(mModelo, bModelo, mVista);
        mControlador.iniciar();
    }
    
    private void abrirInventarioBodega() {
        this.vistaMenu.dispose();

        InventarioBodega modelo = new InventarioBodega();
        InventarioBodegaVista vistaInventario = new InventarioBodegaVista();
        InventarioBodegaControlador controlador = new InventarioBodegaControlador(modelo, vistaInventario);

        controlador.iniciar();
    }
    
    private void abrirSalidaProductos() {
        this.vistaMenu.dispose();

        DetalleMovimiento vistaDetalle = new DetalleMovimiento();
        DetalleMovimientoControlador ctrlDetalle = new DetalleMovimientoControlador(vistaDetalle);

        vistaDetalle.setLocationRelativeTo(null);
        vistaDetalle.setVisible(true);
    }
}