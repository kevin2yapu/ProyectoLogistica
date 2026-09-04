package Controlador;

import Modelo.SalidaAlmacen;
import Modelo.SesionUsuario;
import Vista.DetalleMovimiento;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DetalleMovimientoControlador implements ActionListener {

    private DetalleMovimiento vista;
    private ConexionBDD conexionBDD;

    // Clase auxiliar para asociar Objetos de Combos con su ID en BD
    public static class ItemCombo {
        private int id;
        private String nombre;

        public ItemCombo(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }

        @Override
        public String toString() {
            return nombre;
        }
    }

    public DetalleMovimientoControlador(DetalleMovimiento vista) {
        this.vista = vista;
        this.conexionBDD = new ConexionBDD();

        // Registrar Eventos de Botones
        this.vista.getBtnGuardar().addActionListener(this);
        if (this.vista.getBtnPDF() != null) {
            this.vista.getBtnPDF().addActionListener(this);
        }
        if (this.vista.getBtnRegresar() != null) {
            this.vista.getBtnRegresar().addActionListener(this);
        }

        // Cargar combos iniciales
        cargarBodegas();
        cargarProductos();

        // Evento cambio de producto -> recalcular desglose de lotes
        this.vista.getCbProducto().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    calcularDesgloseLotes();
                }
            }
        });

        // Evento escribir en Cantidad Requerida -> calcular en tiempo real
        this.vista.getTxtCantidadRequerida().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calcularDesgloseLotes(); }
            @Override public void removeUpdate(DocumentEvent e) { calcularDesgloseLotes(); }
            @Override public void changedUpdate(DocumentEvent e) { calcularDesgloseLotes(); }
        });
    }

    // --- 1. CARGA DE BODEGAS ---
    private void cargarBodegas() {
        int idBodegaUsuario = SesionUsuario.getIdBodega();

        vista.getCbBodegaOrigen().removeAllItems();
        vista.getCbBodegaDestino().removeAllItems();

        String sql = "SELECT id, nombre FROM bodega;";
        
        try (Connection con = conexionBDD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                ItemCombo item = new ItemCombo(id, nombre);

                if (id == idBodegaUsuario) {
                    vista.getCbBodegaOrigen().addItem(item);
                } else {
                    vista.getCbBodegaDestino().addItem(item);
                }
            }

            vista.getCbBodegaOrigen().setEnabled(false);

        } catch (SQLException e) {
            System.err.println("Error al cargar bodegas: " + e.getMessage());
        }
    }

    // --- 2. CARGA DE PRODUCTOS UNIFICADOS CON STOCK EN ORIGEN ---
    public void cargarProductos() {
        // 1. Limpiar la vista
        vista.getCbProducto().removeAllItems();
        
        // 2. Obtener parámetro de sesión
        int idBodegaOrigen = SesionUsuario.getIdBodega();

        // 3. Pedir datos al Modelo
        Modelo.Producto productoModelo = new Modelo.Producto();
        List<String> listaNombres = productoModelo.obtenerNombresProductosPorBodega(idBodegaOrigen);

        // 4. Llenar la Vista
        for (String nombre : listaNombres) {
            vista.getCbProducto().addItem(nombre);
        }
    }

    // --- 3. CÁLCULO EN TIEMPO REAL FIFO PARA MOSTRAR EN LA TABLA ---
//    private void calcularDesgloseLotes() {
//        DefaultTableModel modelo = (DefaultTableModel) vista.getTblLotes().getModel();
//        modelo.setRowCount(0); // Limpiar filas de la tabla
//
//        Object itemSeleccionado = vista.getCbProducto().getSelectedItem();
//        String textoCant = vista.getCantidadRequeridaText();
//
//        if (itemSeleccionado == null || textoCant.trim().isEmpty()) {
//            return;
//        }
//
//        String nombreProducto = itemSeleccionado.toString();
//
//        int cantidadRequerida;
//        try {
//            cantidadRequerida = Integer.parseInt(textoCant.trim());
//            if (cantidadRequerida <= 0) return;
//        } catch (NumberFormatException e) {
//            return;
//        }
//
//        int idBodegaOrigen = SesionUsuario.getIdBodega();
//
//        Modelo.Lote loteModelo = new Modelo.Lote();
//        List<Modelo.Lote> lotes = loteModelo.obtenerLotesDisponiblesFIFOPorNombre(nombreProducto, idBodegaOrigen);
//
//        int pendiente = cantidadRequerida;
//
//        for (Modelo.Lote l : lotes) {
//            if (pendiente <= 0) break;
//
//            int stockDisponible = l.getCantidad();
//            int aDescontar = Math.min(stockDisponible, pendiente);
//            pendiente -= aDescontar;
//
//            modelo.addRow(new Object[]{
//                l.getId(),
//                l.getCodigoLote(),
//                l.getFechaVencimiento(),
//                stockDisponible,
//                aDescontar
//            });
//        }
//    }
    
    
    private void calcularDesgloseLotes() {
    DefaultTableModel modelo = (DefaultTableModel) vista.getTblLotes().getModel();
    modelo.setRowCount(0); // Limpiar filas de la tabla

    Object itemSeleccionado = vista.getCbProducto().getSelectedItem();
    if (itemSeleccionado == null) return;

    String nombreProducto = itemSeleccionado.toString();
    int idBodegaOrigen = SesionUsuario.getIdBodega();

    // Consultar lotes desde el modelo
    Modelo.Lote loteModelo = new Modelo.Lote();
    List<Modelo.Lote> lotes = loteModelo.obtenerLotesDisponiblesFIFOPorNombre(nombreProducto, idBodegaOrigen);

    // Leer la cantidad si la escribieron, si no, se evalúa en 0
    String textoCant = vista.getCantidadRequeridaText();
    int cantidadRequerida = 0;
    try {
        if (textoCant != null && !textoCant.trim().isEmpty()) {
            cantidadRequerida = Integer.parseInt(textoCant.trim());
        }
    } catch (NumberFormatException e) {
        cantidadRequerida = 0;
    }

    int pendiente = cantidadRequerida;

    // Mostrar los lotes en la tabla siempre
    for (Modelo.Lote l : lotes) {
        int stockDisponible = l.getCantidad();
        int aDescontar = 0;

        if (pendiente > 0) {
            aDescontar = Math.min(stockDisponible, pendiente);
            pendiente -= aDescontar;
        }

        modelo.addRow(new Object[]{
            l.getId(),
            l.getCodigoLote(),
            l.getFechaVencimiento(),
            stockDisponible,
            aDescontar // Muestra lo que se va a descontar si hay cantidad, o 0 si está vacío
        });
    }
}

    // --- 4. ACCIONES DE BOTONES ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarTransferencia();
        } else if (e.getSource() == vista.getBtnRegresar()) {
            vista.dispose(); 
            Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
            Controlador.MenuBodegueroControlador menuCtrl = new Controlador.MenuBodegueroControlador(vistaMenu);
            menuCtrl.iniciar();
        }
    }

    private void guardarTransferencia() {
        ItemCombo bodegaDestinoSel = (ItemCombo) vista.getCbBodegaDestino().getSelectedItem();
        Object productoSeleccionado = vista.getCbProducto().getSelectedItem();

        if (bodegaDestinoSel == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione una bodega destino válida.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreProducto = productoSeleccionado.toString();

        String cantText = vista.getCantidadRequeridaText();
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantText.trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Ingrese un número válido en la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar el stock disponible desglosado acumulado en la tabla
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblLotes().getModel();
        int totalADescontar = 0;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            totalADescontar += Integer.parseInt(modelo.getValueAt(i, 4).toString());
        }

        if (totalADescontar < cantidad) {
            JOptionPane.showMessageDialog(vista, "Stock insuficiente en los lotes activos para cubrir la cantidad requerida.", "Error de Stock", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bodegaOrigenId = SesionUsuario.getIdBodega();
        int bodegaDestinoId = bodegaDestinoSel.getId();
        int responsableId = SesionUsuario.getIdUsuario();
        String observacion = vista.getObservacionesText();

        SalidaAlmacen salida = new SalidaAlmacen(
            0,
            "TRANSFERENCIA",
            bodegaOrigenId,
            bodegaDestinoId,
            responsableId,
            null,
            observacion
        );

        // CORRECCIÓN: Se procesa impactando inventario por NOMBRE de producto, 
        // lo cual suma dinámicamente el stock de todos los lotes disponibles.
        boolean exito = salida.impactarInventarioPorNombre(nombreProducto, cantidad, bodegaDestinoId);

        if (exito) {
            JOptionPane.showMessageDialog(vista, "¡Transferencia procesada correctamente con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarProductos();
            ((DefaultTableModel) vista.getTblLotes().getModel()).setRowCount(0);
            vista.getTxtCantidadRequerida().setText("");
        } else {
            JOptionPane.showMessageDialog(vista, "Error al procesar la transferencia. Revisa el stock disponible de los lotes.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}