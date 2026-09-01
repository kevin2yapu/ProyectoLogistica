/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Bodega;
import Modelo.EntradaAlmacen;
import Modelo.MovimientoAlmacen;
import Modelo.SalidaAlmacen;
import Vista.MovimientoAlmacenVista;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author KEVIN
 */
public class MovimientoControlador {
    private MovimientoAlmacen mmodelo;
    private Bodega bmodelo;
    private MovimientoAlmacenVista mvista;

    public MovimientoControlador(MovimientoAlmacen mmodelo, Bodega bmodelo, MovimientoAlmacenVista mvista) {
        this.mmodelo = mmodelo;
        this.bmodelo = bmodelo;
        this.mvista = mvista;
    }

    public void iniciar() {
    mvista.getBtnGenerar().addActionListener(e -> registrarMovimiento());
    mvista.getCmbTipoMovimiento().addActionListener(e -> {
        gestionarCamposBodega();
        aplicarRestriccionBodegaOrigen();
    });
    
    mvista.getBtnRegresar().addActionListener(e -> regresarAlMenu());
    
    cargarCatalogoBodegas();
    cargarDatosTabla();
    gestionarCamposBodega();
    
 
    aplicarRestriccionBodegaOrigen();

    mvista.setLocationRelativeTo(null);
    mvista.setVisible(true);
}

    private void cargarCatalogoBodegas() {
        ArrayList<Bodega> lista = bmodelo.obtenerCatalogo();

        DefaultComboBoxModel<Bodega> modelOrigen = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Bodega> modelDestino = new DefaultComboBoxModel<>();

        for (Bodega b : lista) {
            modelOrigen.addElement(b);
            modelDestino.addElement(b);
        }

        mvista.getCmbBodegaOrigen().setModel(modelOrigen);
        mvista.getCmbBodegaDestino().setModel(modelDestino);
    }

    private void regresarAlMenu() {
        mvista.dispose();

        Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
        Controlador.MenuBodegueroControlador menuCtrl = new Controlador.MenuBodegueroControlador(vistaMenu);
        menuCtrl.iniciar();
    }

//   private void gestionarCamposBodega() {
//    String tipo = mvista.getCmbTipoMovimiento().getSelectedItem().toString();
//    
//    if (tipo.equalsIgnoreCase("ENTRADA DE PRODUCTO")) {
//        mvista.getCmbBodegaOrigen().setEnabled(false);
//        mvista.getCmbBodegaDestino().setEnabled(true);
//    } else if (tipo.equalsIgnoreCase("SALIDA DE PRODUCTO")) {
//        mvista.getCmbBodegaOrigen().setEnabled(true);
//        mvista.getCmbBodegaDestino().setEnabled(true); 
//    } else {
//        mvista.getCmbBodegaOrigen().setEnabled(true);
//        mvista.getCmbBodegaDestino().setEnabled(true);
//    }
//}
    
    
    private void gestionarCamposBodega() {
    String tipo = mvista.getCmbTipoMovimiento().getSelectedItem().toString();

    if (tipo.equalsIgnoreCase("ENTRADA DE PRODUCTO")) {
        // En una Entrada, los productos vienen de fuera (Proveedor/Producción).
        // No hay Bodega Origen: se deshabilita y se limpia la selección.
        mvista.getCmbBodegaOrigen().setSelectedIndex(-1);
        mvista.getCmbBodegaOrigen().setEnabled(false);
        
        // La Bodega Destino debe estar activa para elegir a dónde entra el stock
        mvista.getCmbBodegaDestino().setEnabled(true);

    } else if (tipo.equalsIgnoreCase("SALIDA DE PRODUCTO")) {
        // En una Salida/Transferencia, se requieren ambas bodegas
        mvista.getCmbBodegaOrigen().setEnabled(true);
        mvista.getCmbBodegaDestino().setEnabled(true);

        // Se vuelve a aplicar la restricción de rol para fijar la Bodega Origen al bodeguero
        aplicarRestriccionBodegaOrigen();
    }
}
    
    public void cargarDatosTabla() {
        DefaultTableModel model = (DefaultTableModel) mvista.getTblMovimientos().getModel();
        model.setRowCount(0);

        ArrayList<String[]> lista = mmodelo.obtenerMovimientos();
        for (String[] fila : lista) {
            Object[] datosFila = {
                fila[1], 
                fila[2], 
                fila[3], 
                fila[4], 
                fila[6], 
                fila[5]  
            };
            model.addRow(datosFila);
        }
    }

//    public void registrarMovimiento() {
//    String tipoStr = mvista.getCmbTipoMovimiento().getSelectedItem().toString();
//    String observacion = mvista.getTxtObservacion().getText().trim();
//
//    Bodega bOrigen = (Bodega) mvista.getCmbBodegaOrigen().getSelectedItem();
//    Bodega bDestino = (Bodega) mvista.getCmbBodegaDestino().getSelectedItem();
//
//    Integer bodegaOrigenId = mvista.getCmbBodegaOrigen().isEnabled() && bOrigen != null ? bOrigen.getId() : null;
//    Integer bodegaDestinoId = mvista.getCmbBodegaDestino().isEnabled() && bDestino != null ? bDestino.getId() : null;
//
//    int responsableId = SesionUsuario.getIdUsuario();
//    String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//
//    if (observacion.isEmpty()) {
//        JOptionPane.showMessageDialog(mvista, "Por favor complete la observación.");
//        return;
//    }
//
//    int idGenerado = -1;
//    String tipoMovimientoBD = "";
//
//    if (tipoStr.equalsIgnoreCase("ENTRADA DE PRODUCTO")) {
//        tipoMovimientoBD = "ENTRADA";
//        mmodelo = new EntradaAlmacen(0, tipoMovimientoBD, bodegaOrigenId, bodegaDestinoId, responsableId, fechaActual, observacion);
//        idGenerado = mmodelo.insertarMovimiento(tipoMovimientoBD);
//    } else if (tipoStr.equalsIgnoreCase("SALIDA DE PRODUCTO")) {
//        tipoMovimientoBD = "SALIDA";
//        mmodelo = new SalidaAlmacen(0, tipoMovimientoBD, bodegaOrigenId, bodegaDestinoId, responsableId, fechaActual, observacion);
//        idGenerado = mmodelo.insertarMovimiento(tipoMovimientoBD);
//    }
//
//    if (idGenerado > 0) {
//        mvista.getTxtObservacion().setText("");
//        cargarDatosTabla();
//        
//        // Cierra la vista actual y abre la vista Detalle pasando el ID
//        abrirDetalleMovimiento(idGenerado, tipoMovimientoBD);
//    } else {
//        JOptionPane.showMessageDialog(mvista, "Error al generar la nota de movimiento.");
//    }
//}
//    
    public void registrarMovimiento() {
    String tipoStr = mvista.getCmbTipoMovimiento().getSelectedItem().toString();
    String observacion = mvista.getTxtObservacion().getText().trim();

    Bodega bOrigen = (Bodega) mvista.getCmbBodegaOrigen().getSelectedItem();
    Bodega bDestino = (Bodega) mvista.getCmbBodegaDestino().getSelectedItem();

    // Ahora captura el ID de Origen directamente al estar habilitado el combo
    Integer bodegaOrigenId = (bOrigen != null) ? bOrigen.getId() : null;
    Integer bodegaDestinoId = (bDestino != null) ? bDestino.getId() : null;

    int responsableId = SesionUsuario.getIdUsuario();
    String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    if (observacion.isEmpty()) {
        JOptionPane.showMessageDialog(mvista, "Por favor complete la observación.");
        return;
    }

    int idGenerado = -1;
    String tipoMovimientoBD = "";

    if (tipoStr.equalsIgnoreCase("ENTRADA DE PRODUCTO")) {
        tipoMovimientoBD = "ENTRADA";
        mmodelo = new EntradaAlmacen(0, tipoMovimientoBD, bodegaOrigenId, bodegaDestinoId, responsableId, fechaActual, observacion);
        idGenerado = mmodelo.insertarMovimiento(tipoMovimientoBD);
    } else if (tipoStr.equalsIgnoreCase("SALIDA DE PRODUCTO")) {
        tipoMovimientoBD = "SALIDA";
        mmodelo = new SalidaAlmacen(0, tipoMovimientoBD, bodegaOrigenId, bodegaDestinoId, responsableId, fechaActual, observacion);
        idGenerado = mmodelo.insertarMovimiento(tipoMovimientoBD);
    }

    if (idGenerado > 0) {
        mvista.getTxtObservacion().setText("");
        cargarDatosTabla();
        abrirDetalleMovimiento(idGenerado, tipoMovimientoBD);
    } else {
        JOptionPane.showMessageDialog(mvista, "Error al generar la nota de movimiento.");
    }
}
    
    private void abrirDetalleMovimiento(int notaMovimientoId, String tipoMovimiento) {
    mvista.dispose(); // Oculta la vista principal de cabecera
    
    // Instancia la vista y el controlador del detalle
    Vista.DetalleMovimiento vistaDetalle = new Vista.DetalleMovimiento();
    Modelo.DetalleMovimiento modeloDetalle = new Modelo.DetalleMovimiento();
    
    Controlador.DetalleMovimientoControlador ctrlDetalle = 
            new Controlador.DetalleMovimientoControlador(vistaDetalle, modeloDetalle, notaMovimientoId, tipoMovimiento);
    
    ctrlDetalle.iniciar();
}

    private void concluirRegistro(String tipo, String origen, String destino, String responsable, String observacion, String fecha) {
        mvista.getTxtObservacion().setText(""); // Limpia la caja de texto
        cargarDatosTabla(); // Refresca los datos reales desde la BDD
        JOptionPane.showMessageDialog(mvista, "Movimiento Registrado Correctamente.");
    }
    
    private void aplicarRestriccionBodegaOrigen() {
    if (SesionUsuario.getIdBodega() != null) {
        int idBodegaSesion = SesionUsuario.getIdBodega();

        // Recorrer los objetos Bodega dentro del ComboBox
        for (int i = 0; i < mvista.getCmbBodegaOrigen().getItemCount(); i++) {
            Bodega b = (Bodega) mvista.getCmbBodegaOrigen().getItemAt(i);
            
            if (b != null && b.getId() == idBodegaSesion) {
                mvista.getCmbBodegaOrigen().setSelectedIndex(i);
                break;
            }
        }

        // Bloquear selección si el rol es Bodeguero
        String rol = SesionUsuario.getRol();
        if (rol != null && !rol.trim().toUpperCase().contains("ADMIN")) {
            mvista.getCmbBodegaOrigen().setEnabled(false);
        }
    }
}
}
