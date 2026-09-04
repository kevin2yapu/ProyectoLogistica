package Controlador;

import Modelo.SesionUsuario;
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

    private void gestionarCamposBodega() {
        String tipo = mvista.getCmbTipoMovimiento().getSelectedItem().toString().toUpperCase();

        if (tipo.contains("ENTRADA")) {
            mvista.getCmbBodegaOrigen().setSelectedIndex(-1);
            mvista.getCmbBodegaOrigen().setEnabled(false);
            mvista.getCmbBodegaDestino().setEnabled(true);
        } else {
            mvista.getCmbBodegaOrigen().setEnabled(true);
            mvista.getCmbBodegaDestino().setEnabled(true);
            aplicarRestriccionBodegaOrigen();
        }
    }
    
    public void cargarDatosTabla() {
        DefaultTableModel model = (DefaultTableModel) mvista.getTblMovimientos().getModel();
        model.setRowCount(0);

        ArrayList<String[]> lista = mmodelo.obtenerMovimientos();
        for (String[] fila : lista) {
            String tipoMov = fila[1]; 

            if (tipoMov != null && tipoMov.trim().equalsIgnoreCase("TRANSFERENCIA")) {
                tipoMov = "SALIDA";
            }

            Object[] datosFila = {
                tipoMov,  
                fila[2],  
                fila[3],  
                fila[4],  
                fila[6],  
                fila[5]   
            };
            
            model.addRow(datosFila);
        }
    }

    public void registrarMovimiento() {
        String tipoStr = mvista.getCmbTipoMovimiento().getSelectedItem().toString().toUpperCase();
        String observacion = mvista.getTxtObservacion().getText().trim();

        Bodega bOrigen = (Bodega) mvista.getCmbBodegaOrigen().getSelectedItem();
        Bodega bDestino = (Bodega) mvista.getCmbBodegaDestino().getSelectedItem();

        Integer bodegaOrigenId = null;
        if (!tipoStr.contains("ENTRADA")) {
            if (bOrigen != null) {
                bodegaOrigenId = bOrigen.getId();
            } else if (SesionUsuario.getIdBodega() != null) {
                bodegaOrigenId = SesionUsuario.getIdBodega();
            }
        }
        
        Integer bodegaDestinoId = (bDestino != null) ? bDestino.getId() : null;
        int responsableId = SesionUsuario.getIdUsuario();
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if (observacion.isEmpty()) {
            JOptionPane.showMessageDialog(mvista, "Por favor complete la observación.");
            return;
        }

        if (bodegaDestinoId == null) {
            JOptionPane.showMessageDialog(mvista, "Debe seleccionar una bodega de destino.");
            return;
        }

        if (tipoStr.contains("ENTRADA")) {
            bodegaOrigenId = null; 
        } else {
            if (bodegaOrigenId == null) {
                JOptionPane.showMessageDialog(mvista, "Debe seleccionar o tener asignada una bodega de origen.");
                return;
            }

            if (bodegaOrigenId.equals(bodegaDestinoId)) {
                JOptionPane.showMessageDialog(mvista, 
                    "No se puede realizar una SALIDA hacia la misma bodega de origen.", 
                    "Movimiento Inválido", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int idGenerado = -1;
        String tipoMovimientoBD = "";

        if (tipoStr.contains("ENTRADA")) {
            tipoMovimientoBD = "ENTRADA";
            mmodelo = new EntradaAlmacen(0, tipoMovimientoBD, bodegaOrigenId, bodegaDestinoId, responsableId, fechaActual, observacion);
            idGenerado = mmodelo.insertarMovimiento(tipoMovimientoBD);
        } else {
            tipoMovimientoBD = "TRANSFERENCIA";
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
        mvista.dispose(); 
        
        Vista.DetalleMovimiento vistaDetalle = new Vista.DetalleMovimiento();
        Controlador.DetalleMovimientoControlador ctrlDetalle = 
                new Controlador.DetalleMovimientoControlador(vistaDetalle);
    }

    private void aplicarRestriccionBodegaOrigen() {
        if (SesionUsuario.getIdBodega() != null) {
            int idBodegaSesion = SesionUsuario.getIdBodega();

            for (int i = 0; i < mvista.getCmbBodegaOrigen().getItemCount(); i++) {
                Bodega b = (Bodega) mvista.getCmbBodegaOrigen().getItemAt(i);
                
                if (b != null && b.getId() == idBodegaSesion) {
                    mvista.getCmbBodegaOrigen().setSelectedIndex(i);
                    break;
                }
            }

            String rol = SesionUsuario.getRol();
            if (rol != null && !rol.trim().toUpperCase().contains("ADMIN")) {
                mvista.getCmbBodegaOrigen().setEnabled(false);
            }
        }
    }
}