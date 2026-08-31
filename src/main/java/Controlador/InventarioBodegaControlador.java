/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.InventarioBodega;
import Vista.InventarioBodegaVista;
import java.util.ArrayList;
import java.util.List;
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
        // 1. Cargar datos iniciales de combos y tabla
        cargarComboBodegas();
        cargarComboLotes();
        cargarDatosTabla();

        // 2. Eventos para actualización automática al cambiar selección en ComboBoxes
        ivista.getCmbBodega().addActionListener(e -> buscarInventario());
        ivista.getCmbLote().addActionListener(e -> buscarInventario());

        // 3. Evento del botón buscar (si la vista dispone de uno)
        if (ivista.getBtnBuscar() != null) {
            ivista.getBtnBuscar().addActionListener(e -> buscarInventario());
        }

        // 4. Evento para regresar al menú principal
        ivista.getBtnRegresar().addActionListener(e -> regresarAlMenu());

        ivista.setLocationRelativeTo(null);
        ivista.setVisible(true);
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

    public void cargarDatosTabla() {
        // Carga inicial pasando filtros vacíos/predeterminados
        buscarInventario();
    }

    public void buscarInventario() {
        String bodegaSel = (String) ivista.getCmbBodega().getSelectedItem();
        String loteSel = (String) ivista.getCmbLote().getSelectedItem();
        
        // Limpiar filas previas de la tabla
        ivista.getModeloTabla().setRowCount(0);
        
        // Obtener datos consultados dinámicamente desde el modelo
        List<String[]> datosFiltrados = imodelo.buscarInventario(bodegaSel, loteSel);

        if (datosFiltrados != null) {
            for (String[] fila : datosFiltrados) {
                ivista.getModeloTabla().addRow(new Object[]{fila[0], fila[1], fila[2], fila[3]});
            }
        }
    }

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
