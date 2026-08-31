/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Lote;
import Vista.LoteVista;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author KEVIN
 */
public class LoteControlador {
    private Lote lmodelo;
    private LoteVista lvista;
    private int idLoteSeleccionado = -1;
private Vista.LoteVista vista; // O el nombre exacto de tu clase Vista (ej. LoteVista, RegistrarLoteVista, etc.)
    private Modelo.Lote modelo;
    
    public LoteControlador(Lote lmodelo, LoteVista lvista) {
        this.lmodelo = lmodelo;
        this.lvista = lvista;
    }

    // Carga el catálogo desplegable JComboBox
    public void cargarComboBodegas() {
        lvista.getCbxBodega().removeAllItems();
        ArrayList<String> bodegas = lmodelo.obtenerComboBodegas();
        for (String b : bodegas) {
            lvista.getCbxBodega().addItem(b);
        }
    }

    // Extrae sólo el número ID de la selección del combo (ej: de "1 - Bodega Norte" toma el 1)
    private int obtenerIdBodegaSeleccionada() {
        String seleccion = (String) lvista.getCbxBodega().getSelectedItem();
        if (seleccion != null && seleccion.contains(" - ")) {
            String[] partes = seleccion.split(" - ");
            return Integer.parseInt(partes[0]);
        }
        return -1;
    }

    // Carga las 4 columnas que exige la vista actual
    public void cargarDatosTabla() {
        lvista.getModeloTabla().setRowCount(0);
        ArrayList<String[]> lLotes = lmodelo.obtenerLotes();

        if (lLotes != null) {
            for (String[] l : lLotes) {
                // Fila visual: [0]Codigo Lote, [1]Ubicacion (Nombre Bodega), [2]Fecha Vencimiento, [3]Estado
                Object[] fila = {l[1], l[4], l[5], l[6]};
                lvista.getModeloTabla().addRow(fila);
            }
        }
    }

    public void seleccionarFila() {
        int fila = lvista.getFilaSeleccionada();

        if (fila >= 0) {
            ArrayList<String[]> lLotes = lmodelo.obtenerLotes();
            if (lLotes != null && fila < lLotes.size()) {
                this.idLoteSeleccionado = Integer.parseInt(lLotes.get(fila)[0]);
            }

            String codigoLote = lvista.getModeloTabla().getValueAt(fila, 0).toString();
            String nombreBodega = lvista.getModeloTabla().getValueAt(fila, 1).toString();
            String fecha = lvista.getModeloTabla().getValueAt(fila, 2).toString();

            lvista.setCodigoLote(codigoLote);
            lvista.setFechaVencimiento(fecha);

            // Seleccionar automáticamente la bodega correspondiente en el ComboBox
            for (int i = 0; i < lvista.getCbxBodega().getItemCount(); i++) {
                if (lvista.getCbxBodega().getItemAt(i).contains(nombreBodega)) {
                    lvista.getCbxBodega().setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void agregarLote() {
        String codigoLote = lvista.getCodigoLote().trim();
        int idBodega = obtenerIdBodegaSeleccionada();
        String fecha = lvista.getFechaVencimiento().trim();

        if (!codigoLote.isEmpty() && idBodega != -1 && !fecha.isEmpty()) {
            lmodelo.setCodigoLote(codigoLote);
            lmodelo.setIdBodega(idBodega);
            lmodelo.setFechaVencimiento(fecha);

            if (lmodelo.insertarLote()) {
                JOptionPane.showMessageDialog(lvista, "Lote guardado con éxito.");
                limpiarCampos();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(lvista, "Error al guardar en la base de datos.");
            }
        } else {
            JOptionPane.showMessageDialog(lvista, "Por favor complete todos los campos.");
        }
    }

    public void editarLote() {
        if (idLoteSeleccionado == -1) {
            JOptionPane.showMessageDialog(lvista, "Seleccione un registro de la tabla primero.");
            return;
        }

        String codigoLote = lvista.getCodigoLote().trim();
        int idBodega = obtenerIdBodegaSeleccionada();
        String fecha = lvista.getFechaVencimiento().trim();

        if (codigoLote.isEmpty() || idBodega == -1 || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(lvista, "Por favor complete todos los campos.");
            return;
        }

        lmodelo.setId(idLoteSeleccionado);
        lmodelo.setCodigoLote(codigoLote);
        lmodelo.setIdBodega(idBodega);
        lmodelo.setFechaVencimiento(fecha);

        if (lmodelo.editarLote()) {
            JOptionPane.showMessageDialog(lvista, "Lote actualizado con éxito.");
            limpiarCampos();
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(lvista, "Error al actualizar en la base de datos.");
        }
    }

    public void deshabilitarLote() {
        if (idLoteSeleccionado != -1) {
            lmodelo.setId(idLoteSeleccionado);
            if (lmodelo.deshabilitarLote()) {
                JOptionPane.showMessageDialog(lvista, "Lote deshabilitado.");
                limpiarCampos();
                cargarDatosTabla();
            }
        } else {
            JOptionPane.showMessageDialog(lvista, "Seleccione un registro de la tabla primero.");
        }
    }

    public void buscarLote() {
        String criterio = lvista.getCodigoLote().trim();

        if (criterio.isEmpty()) {
            cargarDatosTabla();
        } else {
            lvista.getModeloTabla().setRowCount(0);
            ArrayList<String[]> lLotes = lmodelo.buscarLotes(criterio);

            if (lLotes != null) {
                for (String[] l : lLotes) {
                    Object[] fila = {l[1], l[4], l[5], l[6]};
                    lvista.getModeloTabla().addRow(fila);
                }
            }
        }
    }

    private void limpiarCampos() {
        this.idLoteSeleccionado = -1;
        lvista.setCodigoLote("");
        lvista.setFechaVencimiento("");
        if (lvista.getCbxBodega().getItemCount() > 0) {
            lvista.getCbxBodega().setSelectedIndex(0);
        }
    }

    public void iniciar() {
        cargarComboBodegas();
        cargarDatosTabla();
        
        lvista.addGuardarListener(e -> agregarLote());
        lvista.addEditarListener(e -> editarLote());
        lvista.addDeshabilitarListener(e -> deshabilitarLote());
        lvista.addBuscarListener(e -> buscarLote());
        lvista.getBtnRegresar().addActionListener(e -> regresarAlMenu());
        lvista.getBtnCrearProducto().addActionListener(e -> irACrearProducto()); 
        
        lvista.addTablaListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        lvista.setLocationRelativeTo(null);
        lvista.setVisible(true);
    }
    
    private void regresarAlMenu() {
        lvista.dispose(); 
        
        // Verificamos el rol guardado en la sesión
        String rolActivo = SesionUsuario.getRol();

        if (rolActivo != null && rolActivo.trim().toUpperCase().contains("ADMIN")) {
            Vista.MenuAdmin vistaAdmin = new Vista.MenuAdmin();
            Controlador.MenuAdministradorControlador adminCtrl = new Controlador.MenuAdministradorControlador(vistaAdmin);
            adminCtrl.iniciar();
        } else {
            Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
            Controlador.MenuBodegueroControlador menuControlador = new Controlador.MenuBodegueroControlador(vistaMenu);
            menuControlador.iniciar();
        }
    }
    
    private void irACrearProducto() {
        lvista.dispose();

        Vista.ProductoIngreso vistaProducto = new Vista.ProductoIngreso();
        Modelo.Producto modeloProducto = new Modelo.Producto();
        Controlador.ProductoControlador ctrlProducto = 
                new Controlador.ProductoControlador(modeloProducto, vistaProducto);
        
        ctrlProducto.iniciar(); 
    }
    
    public void guardarLote() {
    String codigo = vista.getTxtCodigoLote().getText().trim();
    String fechaStr = vista.getTxtFechaVencimiento().getText().trim();

    // Validar formato YYYY-MM-DD con Expresión Regular limpia
    if (!fechaStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
        javax.swing.JOptionPane.showMessageDialog(vista, 
            "Formato de fecha inválido. Debe usar el formato: AAAA-MM-DD (Ejemplo: 2026-12-31)", 
            "Fecha Incorrecta", 
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    int idBodega = obtenerIdBodegaSeleccionada();
    boolean guardado = modelo.guardarLote(codigo, idBodega, fechaStr);

    if (guardado) {
        javax.swing.JOptionPane.showMessageDialog(vista, "Lote e Inventario guardados correctamente");
        cargarTablaLotes();
    } else {
        javax.swing.JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos");
    }
}
    
   public void cargarTablaLotes() {
    javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) vista.getTblLotes().getModel();
    model.setRowCount(0); // Limpiar la tabla actual

    java.util.ArrayList<String[]> lista = modelo.obtenerLotes(); 
    for (String[] fila : lista) {
        model.addRow(fila); // Agrega directamente el arreglo de String
    }
}
}