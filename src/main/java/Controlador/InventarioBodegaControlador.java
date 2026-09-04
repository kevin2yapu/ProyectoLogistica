/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.SesionUsuario;
import Modelo.InventarioBodega;
import Vista.InventarioBodegaVista;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author KEVIN
 */
public class InventarioBodegaControlador {

    private InventarioBodega imodelo;
    private InventarioBodegaVista ivista;

    public InventarioBodegaControlador(InventarioBodega imodelo, InventarioBodegaVista ivista) {
        this.imodelo = imodelo;
        this.ivista = ivista;
    }

    public void iniciar() {
        // 1. Cargar datos en los ComboBox
        cargarComboBodegas();
        cargarComboLotes();

        // 2. Aplicar restricción por Rol de Usuario
        aplicarRestriccionPorRol();

        // 3. Registrar eventos DESPUÉS de llenar y ajustar los combos
        ivista.getCmbBodega().addActionListener(e -> buscarInventario());
        ivista.getCmbLote().addActionListener(e -> buscarInventario());

        if (ivista.getBtnBuscar() != null) {
            ivista.getBtnBuscar().addActionListener(e -> buscarInventario());
        }

        if (ivista.getBtnRegresar() != null) {
            ivista.getBtnRegresar().addActionListener(e -> regresarAlMenu());
        }

        // 4. Cargar datos en la tabla por primera vez
        buscarInventario();

        ivista.setLocationRelativeTo(null);
        ivista.setVisible(true);
    }

    private void aplicarRestriccionPorRol() {
    String rolActivo = SesionUsuario.getRol();

    // Si NO es Administrador (es Bodeguero), forzamos la selección de su bodega
    if (rolActivo != null && !rolActivo.trim().toUpperCase().contains("ADMIN")) {
        Integer idBodegaSesion = SesionUsuario.getIdBodega();

        if (idBodegaSesion != null && idBodegaSesion > 0) {
            // Recorremos los ítems del combo para encontrar el que corresponde al ID de la sesión
            for (int i = 0; i < ivista.getCmbBodega().getItemCount(); i++) {
                String item = ivista.getCmbBodega().getItemAt(i);
                
                // Si el combo tiene formato "1" o "1 - Bodega Norte", comparamos con el ID
                if (item.startsWith(String.valueOf(idBodegaSesion))) {
                    ivista.getCmbBodega().setSelectedIndex(i);
                    break;
                }
            }

            // Desactivar el combo para que el bodeguero no pueda cambiarlo a "TODAS"
            ivista.getCmbBodega().setEnabled(false);
        }
    }
}

    public void cargarComboBodegas() {
        ivista.getCmbBodega().removeAllItems();
        ivista.getCmbBodega().addItem("TODAS");
        
        ArrayList<String> bodegas = imodelo.obtenerComboBodegas(); 
        if (bodegas != null) {
            for (String b : bodegas) {
                ivista.getCmbBodega().addItem(b);
            }
        }
    }

    public void cargarComboLotes() {
        ivista.getCmbLote().removeAllItems();
        ivista.getCmbLote().addItem("TODOS");
        
        ArrayList<String> lotes = imodelo.obtenerComboLotes();
        if (lotes != null) {
            for (String l : lotes) {
                ivista.getCmbLote().addItem(l);
            }
        }
    }

   public void buscarInventario() {
    String bodegaSel = (String) ivista.getCmbBodega().getSelectedItem();
    String loteSel = (String) ivista.getCmbLote().getSelectedItem();
    
    if (bodegaSel == null || loteSel == null) {
        return;
    }

    DefaultTableModel modelTabla = ivista.getModeloTabla();
    modelTabla.setRowCount(0); 
    
    List<String[]> datosFiltrados = imodelo.buscarInventario(bodegaSel, loteSel);

    if (datosFiltrados != null) {
        for (String[] fila : datosFiltrados) {
            // Col 0 (BODEGA)       <- bodegaSel (Nombre seleccionado en el ComboBox)
            // Col 1 (LOTE)         <- fila[0]   (15Le, 155A, 777Prue)
            // Col 2 (PRODUCTO)     <- fila[1]   (leche vita, jabón, naranja)
            // Col 3 (STOCK ACTUAL) <- fila[3]   (20.0, 340.0)
            modelTabla.addRow(new Object[]{bodegaSel, fila[0], fila[1], fila[3]});
        }
    }
}
    
//    public void buscarInventario() {
//    String bodegaSel = (String) ivista.getCmbBodega().getSelectedItem();
//    String loteSel = (String) ivista.getCmbLote().getSelectedItem();
//    
//    if (bodegaSel == null || loteSel == null) {
//        return;
//    }
//
//    DefaultTableModel modelTabla = ivista.getModeloTabla();
//    modelTabla.setRowCount(0); // Limpiar la tabla antes de cargar
//
//    List<String[]> datosFiltrados = imodelo.buscarInventario(bodegaSel, loteSel);
//
//    if (datosFiltrados != null) {
//        for (String[] fila : datosFiltrados) {
//            
//
//            modelTabla.addRow(new Object[]{
//                fila.length > 0 ? fila[0] : "",          
//                fila.length > 1 ? fila[1] : "",          
//                fila.length > 2 ? fila[2] : "",         
//                fila.length > 3 ? fila[3] : "",           
//                fila.length > 4 ? fila[4] : "0.0",        
//                fila.length > 5 ? fila[5] : "ACTIVO",     
//                fila.length > 6 ? fila[6] : "Sin Lote",   
//                fila.length > 7 ? fila[7] : "ACTIVO"      
//            });
//        }
//    }
//}

    private void regresarAlMenu() {
        ivista.dispose();
        
        String rolActivo = SesionUsuario.getRol();

        if (rolActivo != null && rolActivo.trim().toUpperCase().contains("ADMIN")) {
            Vista.MenuAdmin vistaAdmin = new Vista.MenuAdmin();
            Controlador.MenuAdministradorControlador adminCtrl = new Controlador.MenuAdministradorControlador(vistaAdmin);
            adminCtrl.iniciar();
        } else {
            Vista.MenuBodeguero vistaBodeguero = new Vista.MenuBodeguero();
            Controlador.MenuBodegueroControlador bodegueroCtrl = new Controlador.MenuBodegueroControlador(vistaBodeguero);
            bodegueroCtrl.iniciar();
        }
    }
    
    
}