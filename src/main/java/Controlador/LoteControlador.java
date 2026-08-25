/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Lote;
import Vista.LoteVista;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public void cargarDatosTabla() {
        lvista.getModeloTabla().setRowCount(0);
        ArrayList<String[]> lLotes = lmodelo.obtenerLotes();

        if (lLotes != null) {
            for (String[] l : lLotes) {
                Object[] fila = {l[1], l[2], l[3], l[4], l[5], l[6]};
                lvista.getModeloTabla().addRow(fila);
            }
        }
    }

    public void seleccionarFila() {
        int fila = lvista.getFilaSeleccionada();

        if (fila >= 0) {
            ArrayList<String[]> lLotes = lmodelo.obtenerLotes();
            if (lLotes != null && fila < lLotes.size()) {
                // Captura el ID real de MySQL (posición 0)
                this.idLoteSeleccionado = Integer.parseInt(lLotes.get(fila)[0]);
            }

            String codigoLote = lvista.getModeloTabla().getValueAt(fila, 0).toString();
            String idProducto = lvista.getModeloTabla().getValueAt(fila, 1).toString();
            String fecha      = lvista.getModeloTabla().getValueAt(fila, 4).toString();

            lvista.setCodigoLote(codigoLote);
            lvista.setProducto(idProducto);
            lvista.setBodega("1");
            lvista.setFechaVencimiento(fecha);
        }
    }

    public void editarLote() {
        try {
            if (idLoteSeleccionado == -1) {
                JOptionPane.showMessageDialog(lvista, "Seleccione un registro de la tabla primero.");
                return;
            }

            String codigoLote  = lvista.getCodigoLote().trim();
            String strProducto = lvista.getIdProducto().trim();
            String strBodega   = lvista.getIdBodega().trim();
            String fecha       = lvista.getFechaVencimiento().trim();

            if (codigoLote.isEmpty() || strProducto.isEmpty() || strBodega.isEmpty() || fecha.isEmpty()) {
                JOptionPane.showMessageDialog(lvista, "Por favor complete todos los campos.");
                return;
            }

            
            lmodelo.setId(idLoteSeleccionado);
            lmodelo.setCodigoLote(codigoLote);
            lmodelo.setIdProducto(Integer.parseInt(strProducto));
            lmodelo.setIdBodega(Integer.parseInt(strBodega));
            lmodelo.setFechaVencimiento(fecha);

            if (lmodelo.editarLote()) {
                JOptionPane.showMessageDialog(lvista, "Lote actualizado con éxito.");
                limpiarCampos();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(lvista, "Error al actualizar en la base de datos.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(lvista, "ID Producto e ID Bodega deben ser números enteros.");
        }
    }

    public void agregarLote() {
        try {
            String codigoLote  = lvista.getCodigoLote().trim();
            String strProducto = lvista.getIdProducto().trim();
            String strBodega   = lvista.getIdBodega().trim();
            String fecha       = lvista.getFechaVencimiento().trim();

            if (!codigoLote.isEmpty() && !strProducto.isEmpty() && !strBodega.isEmpty() && !fecha.isEmpty()) {
                lmodelo.setCodigoLote(codigoLote);
                lmodelo.setIdProducto(Integer.parseInt(strProducto));
                lmodelo.setIdBodega(Integer.parseInt(strBodega));
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(lvista, "ID Producto e ID Bodega deben ser números enteros.");
        }
    }

    public void deshabilitarLote() {
        String codigoLote = lvista.getCodigoLote().trim();
        if (!codigoLote.isEmpty()) {
            lmodelo.setCodigoLote(codigoLote);
            if (lmodelo.deshabilitarLote()) {
                JOptionPane.showMessageDialog(lvista, "Lote deshabilitado.");
                limpiarCampos();
                cargarDatosTabla();
            }
        } else {
            JOptionPane.showMessageDialog(lvista, "Seleccione un registro.");
        }
    }

    private void limpiarCampos() {
        this.idLoteSeleccionado = -1;
        lvista.setCodigoLote("");
        lvista.setProducto("");
        lvista.setBodega("");
        lvista.setFechaVencimiento("");
    }

    public void iniciar() {
        lvista.addGuardarListener(e -> agregarLote());
        lvista.addEditarListener(e -> editarLote());
        lvista.addDeshabilitarListener(e -> deshabilitarLote());
        lvista.addBuscarListener(e -> buscarLote());
        lvista.addTablaListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        lvista.setLocationRelativeTo(null);
        cargarDatosTabla();
        lvista.setVisible(true);
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
                   
                    Object[] fila = {l[1], l[2], l[3], l[4], l[5], l[6]};
                    lvista.getModeloTabla().addRow(fila);
                }
            }
        }
    }
}