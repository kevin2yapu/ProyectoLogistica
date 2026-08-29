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
        // Asignación de eventos a los botones de la vista
        this.vistaMenu.getBtnRegistroLotes().addActionListener(e -> abrirLotes());
        this.vistaMenu.getBtnRegistroProductos().addActionListener(e -> abrirProductos());
        this.vistaMenu.getBtnMovimientoAlmacen().addActionListener(e -> abrirMovimientos());

        this.vistaMenu.setLocationRelativeTo(null);
        this.vistaMenu.setVisible(true);
    }

   private void abrirLotes() {
        this.vistaMenu.dispose(); // Cierra el menú actual
        
        LoteVista vistaLotes = new LoteVista();
        Lote modeloLote = new Lote();

        // Inicializamos el controlador pasándole modelo y vista
        LoteControlador lControlador = new LoteControlador(modeloLote, vistaLotes);
        lControlador.iniciar();
    }

private void abrirProductos() {
    this.vistaMenu.dispose(); // Cierra el menú principal
    
    ProductoIngreso vistaProductos = new ProductoIngreso();
    Producto modeloProducto = new Producto();

    // Se instancia el controlador pasándole sus dependencias
    ProductoControlador pControlador = new ProductoControlador(modeloProducto, vistaProductos);
    
    // Se arranca el controlador que configura la vista y la hace visible
    pControlador.iniciar();
}
    

    private void abrirMovimientos() {
        this.vistaMenu.dispose(); // Cierra el menú

        MovimientoAlmacenVista mVista = new MovimientoAlmacenVista();
        MovimientoAlmacen mModelo = new EntradaAlmacen();
        Bodega bModelo = new Bodega();

        MovimientoControlador mControlador = new MovimientoControlador(mModelo, bModelo, mVista);
        mControlador.iniciar();
    }
    
  

}
