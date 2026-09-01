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

    public LoteControlador(Lote lmodelo, LoteVista lvista) {
        this.lmodelo = lmodelo;
        this.lvista = lvista;
    }

    // Carga el catálogo desplegable JComboBox
    // Carga el catálogo desplegable JComboBox
    public void cargarComboBodegas() {
        lvista.getCbxBodega().removeAllItems();
        ArrayList<String> bodegas = lmodelo.obtenerComboBodegas();
        if (bodegas != null) {
            for (String b : bodegas) {
                lvista.getCbxBodega().addItem(b);
            }
        }
    }

    // Selecciona automáticamente la bodega en la que el usuario inició sesión
    // y deshabilita el ComboBox si el rol es BODEGUERO.
    private void aplicarRestriccionBodega() {
        if (SesionUsuario.getIdBodega() != null) {
            int bodegaSesionId = SesionUsuario.getIdBodega();

            for (int i = 0; i < lvista.getCbxBodega().getItemCount(); i++) {
                String item = lvista.getCbxBodega().getItemAt(i);
                if (item.startsWith(bodegaSesionId + " - ") || item.contains(SesionUsuario.getNombreBodega())) {
                    lvista.getCbxBodega().setSelectedIndex(i);
                    break;
                }
            }

            // Si es bodeguero, bloquea el combo para evitar selección de otras bodegas
            String rol = SesionUsuario.getRol();
            if (rol != null && !rol.trim().toUpperCase().contains("ADMIN")) {
                lvista.getCbxBodega().setEnabled(false);
            }
        }
    }

    // Extrae sólo el número ID de la selección del combo (ej: "1 - Bodega Norte" -> 1)
    private int obtenerIdBodegaSeleccionada() {
        String seleccion = (String) lvista.getCbxBodega().getSelectedItem();
        if (seleccion != null && seleccion.contains(" - ")) {
            String[] partes = seleccion.split(" - ");
            return Integer.parseInt(partes[0].trim());
        }
        return -1;
    }

    // Carga las columnas en la tabla
    public void cargarDatosTabla() {
        lvista.getModeloTabla().setRowCount(0);
        ArrayList<String[]> lLotes = lmodelo.obtenerLotes();

        if (lLotes != null) {
            for (String[] l : lLotes) {
                Object[] fila = {l[1], l[3], l[4], l[5]};
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

            // Permite cambiar la selección en pantalla solo si es Administrador
            String rol = SesionUsuario.getRol();
            if (rol != null && rol.trim().toUpperCase().contains("ADMIN")) {
                for (int i = 0; i < lvista.getCbxBodega().getItemCount(); i++) {
                    if (lvista.getCbxBodega().getItemAt(i).contains(nombreBodega)) {
                        lvista.getCbxBodega().setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    public void agregarLote() {
        String codigoLote = lvista.getCodigoLote().trim();
        int idBodega = obtenerIdBodegaSeleccionada();
        String fecha = lvista.getFechaVencimiento().trim();

        if (codigoLote.isEmpty() || idBodega == -1 || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(lvista, "Por favor complete todos los campos.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(lvista, "Formato de fecha inválido. Use AAAA-MM-DD", "Fecha Incorrecta", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            java.time.LocalDate fechaVencimiento = java.time.LocalDate.parse(fecha);
            java.time.LocalDate fechaActual = java.time.LocalDate.now();

            if (fechaVencimiento.isBefore(fechaActual)) {
                JOptionPane.showMessageDialog(lvista, "La fecha de vencimiento no puede ser anterior a la fecha actual (" + fechaActual + ").", "Fecha Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(lvista, "La fecha ingresada no es válida.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        lmodelo.setCodigoLote(codigoLote);
        lmodelo.setIdBodega(idBodega);
        lmodelo.setFechaVencimiento(fecha);

        if (lmodelo.insertarLote()) {
            JOptionPane.showMessageDialog(lvista, "Lote guardado con éxito.");
            limpiarCampos();
            cargarDatosTabla();
        } else {
            JOptionPane.showMessageDialog(lvista, "Error al guardar en la base de datos.", "Error SQL", JOptionPane.ERROR_MESSAGE);
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

        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(lvista, "Formato de fecha inválido. Use: AAAA-MM-DD", "Fecha Incorrecta", JOptionPane.WARNING_MESSAGE);
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
                    Object[] fila = {l[1], l[3], l[4], l[5]};
                    lvista.getModeloTabla().addRow(fila);
                }
            }
        }
    }

    private void limpiarCampos() {
        this.idLoteSeleccionado = -1;
        lvista.setCodigoLote("");
        lvista.setFechaVencimiento("");
        
        // Mantiene seleccionada la bodega del bodeguero sin resetear a 0
        aplicarRestriccionBodega();
    }

    public void iniciar() {
        cargarComboBodegas();
        aplicarRestriccionBodega(); // Restringe e inhabilita si es bodeguero
        cargarDatosTabla();

        lvista.addGuardarListener(e -> agregarLote());
        lvista.addEditarListener(e -> editarLote());
        lvista.addDeshabilitarListener(e -> deshabilitarLote());
        lvista.addBuscarListener(e -> buscarLote());
        lvista.getBtnRegresar().addActionListener(e -> regresarAlMenu());

        if (lvista.getBtnCrearProducto() != null) {
            lvista.getBtnCrearProducto().addActionListener(e -> irACrearProducto());
        }

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
}