/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Bodega;
import Vista.Bodegavista;
import Vista.ProductoIngreso;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author KEVIN
 */
public class BodegaControlador {
 private Bodega bmodelo;
    private Bodegavista bvista; // Instancia de la vista

    public BodegaControlador() {
    }

    public BodegaControlador(Bodega bmodelo, Bodegavista bvista) {
        this.bmodelo = bmodelo;
        this.bvista = bvista; // Se asigna correctamente para evitar que quede null
    }

    public void cargarDatosTabla() {
        bvista.getModeloTabla().setRowCount(0);
        ArrayList<String[]> lBodegas = bmodelo.obtenerBodegas();

        for (String[] b : lBodegas) {
            Object[] fila = {b[1], b[2], b[3]}; 
            bvista.getModeloTabla().addRow(fila);
        }
    }

    public void seleccionarFila() {
        int fila = bvista.getFilaSeleccionada();
        if (fila >= 0) {
            String nombre = bvista.getModeloTabla().getValueAt(fila, 0).toString();
            String ubicacion = bvista.getModeloTabla().getValueAt(fila, 1).toString();

            bvista.setNombreBodega(nombre);
            bvista.setUbicacion(ubicacion);
        }
    }

    public void agregarBodega() {
        String nombre = bvista.getNombreBodega().trim();
        String ubicacion = bvista.getUbicacion().trim();

        if (!nombre.isEmpty()) {
            bmodelo.setNombre(nombre);
            bmodelo.setUbicacion(ubicacion);

            if (bmodelo.insertarBodega()) {
                bvista.limpiarCampos();
                cargarDatosTabla();
            }
        }
    }

    public void editarBodega() {
        int fila = bvista.getFilaSeleccionada();
        if (fila >= 0) {
            String nombre = bvista.getNombreBodega().trim();
            String ubicacion = bvista.getUbicacion().trim();

            bmodelo.setNombre(nombre);
            bmodelo.setUbicacion(ubicacion);

            if (bmodelo.editarBodega()) {
                bvista.limpiarCampos();
                cargarDatosTabla();
            }
        }
    }

    public void deshabilitarBodega() {
        int fila = bvista.getFilaSeleccionada();
        if (fila >= 0) {
            String nombre = bvista.getNombreBodega().trim();
            bmodelo.setNombre(nombre);

            if (bmodelo.deshabilitarBodega()) {
                bvista.limpiarCampos();
                cargarDatosTabla();
            }
        }
    }

    public void iniciar() {
        // Vinculación de listeners apuntando a bvista (Bodegavista)
        bvista.addGuardarListener(e -> agregarBodega());
        bvista.addEditarListener(e -> editarBodega());
        bvista.addDeshabilitarListener(e -> deshabilitarBodega());

        bvista.addTablaListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        bvista.setLocationRelativeTo(null);
        this.cargarDatosTabla();
        bvista.setVisible(true);
    }
}