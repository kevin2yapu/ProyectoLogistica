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
    private MovimientoAlmacen modelo;

    public MovimientoControlador(MovimientoAlmacen mmodelo, Bodega bmodelo, MovimientoAlmacenVista mvista) {
        this.mmodelo = mmodelo;
        this.bmodelo = bmodelo;
        this.mvista = mvista;
        this.modelo = modelo;
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
            // En ENTRADA: deshabilitar y desseleccionar Origen
            mvista.getCmbBodegaOrigen().setSelectedIndex(-1);
            mvista.getCmbBodegaOrigen().setEnabled(false);
            mvista.getCmbBodegaDestino().setEnabled(true);

        } else {
            // En TRANSFERENCIA: habilitar ambos combos y aplicar restricciones según ROL
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
//        // 1. Lectura de campos
//        String tipoStr = mvista.getCmbTipoMovimiento().getSelectedItem().toString().toUpperCase();
//        String observacion = mvista.getTxtObservacion().getText().trim();
//
//        Bodega bOrigen = (Bodega) mvista.getCmbBodegaOrigen().getSelectedItem();
//        Bodega bDestino = (Bodega) mvista.getCmbBodegaDestino().getSelectedItem();
//
//        // 2. Extracción corregida de IDs de bodega:
//        // Se valida el contenido del objeto bOrigen en lugar de verificar si el JComboBox está enabled.
//        Integer bodegaOrigenId = null;
//        if (!tipoStr.contains("ENTRADA")) {
//            if (bOrigen != null) {
//                bodegaOrigenId = bOrigen.getId();
//            } else if (SesionUsuario.getIdBodega() != null) {
//                bodegaOrigenId = SesionUsuario.getIdBodega(); // Fallback directo desde sesión
//            }
//        }
//        
//        Integer bodegaDestinoId = (bDestino != null) ? bDestino.getId() : null;
//
//        // 3. Datos de la sesión y fecha
//        int responsableId = SesionUsuario.getIdUsuario();
//        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//
//        // 4. Validaciones
//        if (observacion.isEmpty()) {
//            JOptionPane.showMessageDialog(mvista, "Por favor complete la observación.");
//            return;
//        }
//
//        if (bodegaDestinoId == null) {
//            JOptionPane.showMessageDialog(mvista, "Debe seleccionar una bodega de destino.");
//            return;
//        }
//
//        if (!tipoStr.contains("ENTRADA") && bodegaOrigenId == null) {
//            JOptionPane.showMessageDialog(mvista, "Debe seleccionar o tener asignada una bodega de origen.");
//            return;
//        }
//
//        // 5. Inserción adaptada a ENTRADA o TRANSFERENCIA
//        int idGenerado = -1;
//        String tipoMovimientoBD = "";
//
//        if (tipoStr.contains("ENTRADA")) {
//            tipoMovimientoBD = "ENTRADA";
//            mmodelo = new EntradaAlmacen(0, tipoMovimientoBD, bodegaOrigenId, bodegaDestinoId, responsableId, fechaActual, observacion);
//            idGenerado = mmodelo.insertarMovimiento(tipoMovimientoBD);
//        } else {
//            tipoMovimientoBD = "TRANSFERENCIA";
//            mmodelo = new SalidaAlmacen(0, tipoMovimientoBD, bodegaOrigenId, bodegaDestinoId, responsableId, fechaActual, observacion);
//            idGenerado = mmodelo.insertarMovimiento(tipoMovimientoBD);
//        }
//
//        // 6. Confirmación y navegación a Detalle
//        if (idGenerado > 0) {
//            mvista.getTxtObservacion().setText("");
//            cargarDatosTabla();
//            abrirDetalleMovimiento(idGenerado, tipoMovimientoBD);
//        } else {
//            JOptionPane.showMessageDialog(mvista, "Error al generar la nota de movimiento.");
//        }
//    }
    
    public void registrarMovimiento() {
    // 1. Lectura de campos
    String tipoStr = mvista.getCmbTipoMovimiento().getSelectedItem().toString().toUpperCase();
    String observacion = mvista.getTxtObservacion().getText().trim();

    Bodega bOrigen = (Bodega) mvista.getCmbBodegaOrigen().getSelectedItem();
    Bodega bDestino = (Bodega) mvista.getCmbBodegaDestino().getSelectedItem();

    // 2. Extracción corregida de IDs de bodega:
    Integer bodegaOrigenId = null;
    if (!tipoStr.contains("ENTRADA")) {
        if (bOrigen != null) {
            bodegaOrigenId = bOrigen.getId();
        } else if (SesionUsuario.getIdBodega() != null) {
            bodegaOrigenId = SesionUsuario.getIdBodega(); // Fallback directo desde sesión
        }
    }
    
    Integer bodegaDestinoId = (bDestino != null) ? bDestino.getId() : null;

    // 3. Datos de la sesión y fecha
    int responsableId = SesionUsuario.getIdUsuario();
    Integer bodegaSesionId = SesionUsuario.getIdBodega(); // ID de la bodega del usuario actual (Ej: Bodega Norte)
    String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    // 4. Validaciones
    if (observacion.isEmpty()) {
        JOptionPane.showMessageDialog(mvista, "Por favor complete la observación.");
        return;
    }

    if (bodegaDestinoId == null) {
        JOptionPane.showMessageDialog(mvista, "Debe seleccionar una bodega de destino.");
        return;
    }

    // --- VALIDACIÓN SOLICITADA ---
    // A) Si es ENTRADA y estás en Bodega Norte, no puedes ingresar hacia tu misma Bodega Norte.
    // B) Si es TRANSFERENCIA, la bodega de origen y destino no pueden ser la misma.
    if (tipoStr.contains("ENTRADA")) {
    // En ENTRADA, el Origen siempre es N/A (null)
    bodegaOrigenId = null; 
    
    // OPCIONAL: Si deseas validar que la Entrada se realice obligatoriamente 
    // en la bodega donde está el usuario posicionado:
    /* 
    if (bodegaSesionId != null && !bodegaSesionId.equals(bodegaDestinoId)) {
        JOptionPane.showMessageDialog(mvista, 
            "Las entradas solo pueden realizarse hacia su bodega actual.", 
            "Bodega Inválida", 
            JOptionPane.WARNING_MESSAGE);
        return;
    } 
    */

} else {
    // Es TRANSFERENCIA: Requiere Origen y que Origen != Destino
    if (bodegaOrigenId == null) {
        JOptionPane.showMessageDialog(mvista, "Debe seleccionar o tener asignada una bodega de origen.");
        return;
    }

    if (bodegaOrigenId.equals(bodegaDestinoId)) {
        JOptionPane.showMessageDialog(mvista, 
            "No se puede realizar una TRANSFERENCIA hacia la misma bodega de origen.", 
            "Movimiento Inválido", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
}

    // 5. Inserción adaptada a ENTRADA o TRANSFERENCIA
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

    // 6. Confirmación y navegación a Detalle
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
        Modelo.DetalleMovimiento modeloDetalle = new Modelo.DetalleMovimiento();
        
        Controlador.DetalleMovimientoControlador ctrlDetalle = 
                new Controlador.DetalleMovimientoControlador(vistaDetalle, modeloDetalle, notaMovimientoId, tipoMovimiento);
        
        ctrlDetalle.iniciar();
    }

    private void concluirRegistro(String tipo, String origen, String destino, String responsable, String observacion, String fecha) {
        mvista.getTxtObservacion().setText(""); 
        cargarDatosTabla(); 
        JOptionPane.showMessageDialog(mvista, "Movimiento Registrado Correctamente.");
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