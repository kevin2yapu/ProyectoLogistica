package Controlador;

import Modelo.SesionUsuario;
import Modelo.Lote;
import Vista.LoteVista;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    public void cargarComboBodegas() {
        lvista.getCbxBodega().removeAllItems();
        ArrayList<String> bodegas = lmodelo.obtenerComboBodegas();
        if (bodegas != null) {
            for (String b : bodegas) {
                lvista.getCbxBodega().addItem(b);
            }
        }
    }

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

            String rol = SesionUsuario.getRol();
            if (rol != null && !rol.trim().toUpperCase().contains("ADMIN")) {
                lvista.getCbxBodega().setEnabled(false);
            }
        }
    }

    private int obtenerIdBodegaSeleccionada() {
        String seleccion = (String) lvista.getCbxBodega().getSelectedItem();
        if (seleccion != null && seleccion.contains(" - ")) {
            String[] partes = seleccion.split(" - ");
            return Integer.parseInt(partes[0].trim());
        }
        return -1;
    }

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
        String cantidadStr = lvista.getTxtCantidadText(); // Lee del campo de texto de la vista

        // 1. VALIDACIÓN DE CAMPOS VACÍOS
        if (codigoLote.isEmpty() || idBodega == -1 || fecha.isEmpty() || cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(lvista, "Por favor complete todos los campos requeridos.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. VALIDACIÓN DE STOCK (MÍNIMO 10 UNIDADES)
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad < 10) {
                JOptionPane.showMessageDialog(lvista, "El stock del lote no puede ser menor a 10 unidades.", "Validación de Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(lvista, "Ingrese un valor entero numérico válido para la cantidad.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. VALIDACIÓN DE FECHA DE VENCIMIENTO
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(lvista, "Formato de fecha inválido. Use el formato AAAA-MM-DD (Ej: 2026-12-31)", "Fecha Incorrecta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate fechaVencimiento = LocalDate.parse(fecha);
            LocalDate fechaActual = LocalDate.now();

            if (fechaVencimiento.isBefore(fechaActual)) {
                JOptionPane.showMessageDialog(lvista, "La fecha de vencimiento no puede ser anterior a la fecha actual (" + fechaActual + ").", "Fecha Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(lvista, "La fecha ingresada no corresponde a un día de calendario válido.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. GUARDAR Y GENERAR REPORTE PDF
        guardarLoteYGenerarReporte(codigoLote, fecha, idBodega, cantidad);
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
        if (lvista.getTxtCantidad() != null) {
            lvista.getTxtCantidad().setText("");
        }
        aplicarRestriccionBodega();
    }

    public void iniciar() {
        cargarComboBodegas();
        aplicarRestriccionBodega();
        cargarDatosTabla();

        lvista.addGuardarListener(e -> agregarLote());
        lvista.addEditarListener(e -> editarLote());
        lvista.addDeshabilitarListener(e -> deshabilitarLote());
        lvista.addBuscarListener(e -> buscarLote());
        lvista.getBtnRegresar().addActionListener(e -> regresarAlMenu());

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

    // Método para coordinar el PDF de entrada tras guardar el lote con sus valores validados
    private void guardarLoteYGenerarReporte(String codigoLote, String fechaVenc, int bodegaId, int cantidad) {
        try {
            // Asignar los valores al modelo
            lmodelo.setCodigoLote(codigoLote);
            lmodelo.setFechaVencimiento(fechaVenc);
            lmodelo.setIdBodega(bodegaId);
            lmodelo.setCantidad(cantidad);
            lmodelo.setEstado("ACTIVO");

            // Insertar en Base de Datos
            if (lmodelo.insertarLote()) {
                JOptionPane.showMessageDialog(lvista, "Lote registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Generar PDF de Comprobante
                try {
                    String productoNombre = (lvista.getProductoText() != null && !lvista.getProductoText().isEmpty()) 
                            ? lvista.getProductoText() : "Producto Registrado";
                    
                    Controlador.GeneradorPDF pdfGenerator = new Controlador.GeneradorPDF();
                    pdfGenerator.generarComprobanteEntrada(productoNombre, String.valueOf(cantidad), codigoLote, fechaVenc, SesionUsuario.getNombreBodega());
                } catch (Exception e) {
                    System.err.println("Aviso: No se pudo generar el PDF automáticamente - " + e.getMessage());
                }

                limpiarCampos();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(lvista, "Error al guardar el lote en la Base de Datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(lvista, "Ocurrió un error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}