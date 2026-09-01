/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.InventarioBodega;
import Modelo.Lote;
import Modelo.ModeloReportes;
import Modelo.Usuario;
import Vista.InicioSesion;
import Vista.InventarioBodegaVista;
import Vista.LoteVista;
import Vista.MenuAdmin;
import Vista.VistaReporte;

/**
 *
 * @author KEVIN
 */
public class MenuAdministradorControlador {
    
    private MenuAdmin vista;

    public MenuAdministradorControlador(MenuAdmin vista) {
        this.vista = vista;
    }

    public void iniciar() {
        this.vista.setLocationRelativeTo(null);

        // 1. Botón: ADMINISTRAR USUARIOS
        this.vista.getBtnAdministrarUsuarios().addActionListener(e -> {
            vista.dispose();
            Vista.GestionUsuarioVista uVista = new Vista.GestionUsuarioVista();
            Modelo.Usuario uModelo = new Modelo.Usuario();
            
            Controlador.UsuarioControlador ctrl = new Controlador.UsuarioControlador(uModelo, uVista);
            ctrl.iniciarGestion(); 
        });

        // 2. Botón: GESTIÓN DE LOTES Y PRODUCTOS
        this.vista.getBtnGestionLotesProductos().addActionListener(e -> {
            vista.dispose();
            LoteVista lVista = new LoteVista();
            Lote lModelo = new Lote();
            LoteControlador ctrl = new LoteControlador(lModelo, lVista);
            ctrl.iniciar();
        });

        // 3. Botón: INVENTARIO BODEGAS
        this.vista.getBtnInventarioBodegas().addActionListener(e -> {
            vista.dispose();
            InventarioBodegaVista iVista = new InventarioBodegaVista();
            InventarioBodega iModelo = new InventarioBodega();
            InventarioBodegaControlador ctrl = new InventarioBodegaControlador(iModelo, iVista);
            ctrl.iniciar();
        });

        // 4. Botón: VISUALIZAR REPORTES
        this.vista.getbtnVisualizarReportes().addActionListener(e -> {
            vista.dispose(); // Oculta/Cierra el menú administrador
            VistaReporte rVista = new VistaReporte();
            ModeloReportes rModelo = new ModeloReportes();
            
            // Se vincula la vista con su controlador
            ReportesControlador rCtrl = new ReportesControlador(rVista, rModelo);
            rVista.setLocationRelativeTo(null);
            rVista.setVisible(true);
        });

        // 5. Botón: CERRAR SESIÓN
        this.vista.getBtnCerrarSesion().addActionListener(e -> {
            vista.dispose();
            
            InicioSesion loginVista = new InicioSesion();
            Usuario loginModelo = new Usuario();
            
            UsuarioControlador loginCtrl = new UsuarioControlador(loginModelo, loginVista);
            loginCtrl.iniciar();
        });

        this.vista.setVisible(true);
    }
}
