package Controlador;

import Modelo.SesionUsuario;
import Modelo.Lote;
import Modelo.Producto;
import Vista.LoteVista;
import Vista.ProductoIngreso;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author KEVIN
 */
public class ProductoControlador {
    private Producto pmodelo;
    private ProductoIngreso pvista;
    private int idProductoSeleccionado = -1;

    public ProductoControlador() {}

    public ProductoControlador(Producto pmodelo, ProductoIngreso pvista) {
        this.pmodelo = pmodelo;
        this.pvista = pvista;
    }

    public void iniciar() {
        if (pvista == null) {
            JOptionPane.showMessageDialog(null, "Error: La vista de ProductoIngreso es nula.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Cargar Combos de forma segura
            cargarCombos();
            cargarBodegas();

            // 2. Cargar Datos de la Tabla
            cargarDatosTabla(); 

            // Listener para refrescar la tabla si cambia la bodega
            if (pvista.getCbxBodega() != null) {
                pvista.getCbxBodega().addActionListener(e -> cargarDatosTabla());
            }

            // 3. Asignación de Listeners adaptados a los botones de la interfaz
            pvista.addGuardarListener(e -> guardarProductoYPasarALote());
            pvista.addBuscarListener(e -> buscarProducto());
            
            // Listener para botón Regresar (<)
            if (pvista.getBtnRegresar() != null) {
                for (java.awt.event.ActionListener al : pvista.getBtnRegresar().getActionListeners()) {
                    pvista.getBtnRegresar().removeActionListener(al);
                }
                pvista.getBtnRegresar().addActionListener(e -> regresarAlMenu());
            }
            
            pvista.addTablaListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    seleccionarFila();
                }
            });

        } catch (Exception e) {
            System.err.println("Advertencia al inicializar elementos de la vista: " + e.getMessage());
        }

        // 4. Mostrar la vista
        pvista.setLocationRelativeTo(null);
        pvista.setVisible(true);
    }
    
    
    
    
    
    private void regresarAlMenu() {
        pvista.dispose(); 
        
        String rolActivo = SesionUsuario.getRol();

        if (rolActivo != null && rolActivo.trim().toUpperCase().contains("ADMIN")) {
            Vista.MenuAdmin vistaAdmin = new Vista.MenuAdmin();
            Controlador.MenuAdministradorControlador adminCtrl = new Controlador.MenuAdministradorControlador(vistaAdmin);
            adminCtrl.iniciar();
        } else {
            Vista.MenuBodeguero vistaMenu = new Vista.MenuBodeguero();
            Controlador.MenuBodegueroControlador menuCtrl = new Controlador.MenuBodegueroControlador(vistaMenu);
            menuCtrl.iniciar(); 
        }
    }

//    public void cargarDatosTabla() {
//        try {
//            if (pvista.getModeloTabla() == null) return;
//            
//            pvista.getModeloTabla().setRowCount(0);
//            
//            if (pmodelo == null) return;
//
//            ArrayList<String[]> lProds = pmodelo.obtenerProductos();
//
//            if (lProds != null) {
//                for (String[] p : lProds) {
//                    if (p.length >= 6) {
//                        // Coincide con las columnas de tu interfaz:
//                        // [Codigo Producto, Nombre, Descripción, Cantidad, Estado Producto, Estado]
//                        Object[] fila = {
//                            p[1], // Código Producto
//                            p[2], // Nombre
//                            p[3], // Descripción
//                            p[4], // Cantidad
//                            p.length > 5 ? p[5] : "BUENO ESTADO", // Estado Producto
//                            p.length > 7 ? p[7] : "ACTIVO"         // Estado (Activo/Inactivo)
//                        };
//                        pvista.getModeloTabla().addRow(fila);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error cargando tabla de productos: " + e.getMessage());
//        }
//    }
    
    
    public void cargarDatosTabla() {
    try {
        if (pvista.getModeloTabla() == null) return;
        
        pvista.getModeloTabla().setRowCount(0);
        
        if (pmodelo == null) return;

        ArrayList<String[]> lProds = pmodelo.obtenerProductos();

        if (lProds != null) {
            for (String[] p : lProds) {
                if (p.length >= 5) {
                    // Mapeo exacto para las 8 columnas de la tabla
                    Object[] fila = {
                        p[0],                                 // 1. N° (ID Producto)
                        p[1],                                 // 2. Código
                        p[2],                                 // 3. Nombre
                        p[3],                                 // 4. Descripción
                        p[4],                                 // 5. Cantidad
                        p.length > 5 ? p[6] : "BUENO ESTADO", // 6. Estado Producto
                        p.length > 6 ? p[7] : "Sin Lote",     // 7. ID Lote
                        p.length > 7 ? p[5] : "ACTIVO"        // 8. Estado
                    };
                    pvista.getModeloTabla().addRow(fila);
                }
            }
        }
    } catch (Exception e) {
        System.err.println("Error cargando tabla de productos: " + e.getMessage());
    }
}

//    public void seleccionarFila() {
//        try {
//            int fila = pvista.getFilaSeleccionada();
//
//            if (fila >= 0 && pvista.getModeloTabla() != null) {
//                String codigo = pvista.getModeloTabla().getValueAt(fila, 0).toString();
//                String nombre = pvista.getModeloTabla().getValueAt(fila, 1).toString();
//                String descripcion = pvista.getModeloTabla().getValueAt(fila, 2).toString();
//
//                pvista.setCodigo(codigo);
//                pvista.setNombre(nombre);
//                pvista.setDescripcion(descripcion);
//
//                if (pvista.getCbxEstadoProducto() != null && pvista.getModeloTabla().getValueAt(fila, 4) != null) {
//                    pvista.getCbxEstadoProducto().setSelectedItem(pvista.getModeloTabla().getValueAt(fila, 4).toString());
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error al seleccionar fila: " + e.getMessage());
//        }
//    }
    
    
    public void seleccionarFila() {
    try {
        int fila = pvista.getFilaSeleccionada();

        if (fila >= 0 && pvista.getModeloTabla() != null) {
            String codigo = pvista.getModeloTabla().getValueAt(fila, 1).toString();      // Columna 1: Código
            String nombre = pvista.getModeloTabla().getValueAt(fila, 2).toString();      // Columna 2: Nombre
            String descripcion = pvista.getModeloTabla().getValueAt(fila, 3).toString(); // Columna 3: Descripción

            pvista.setCodigo(codigo);
            pvista.setNombre(nombre);
            pvista.setDescripcion(descripcion);

            if (pvista.getCbxEstadoProducto() != null && pvista.getModeloTabla().getValueAt(fila, 5) != null) {
                pvista.getCbxEstadoProducto().setSelectedItem(pvista.getModeloTabla().getValueAt(fila, 5).toString());
            }
        }
    } catch (Exception e) {
        System.err.println("Error al seleccionar fila: " + e.getMessage());
    }
}

    private void guardarProductoYPasarALote() {
        // 1. Validar campos obligatorios de la vista actual
        if (pvista.getCodigo().trim().isEmpty() || pvista.getNombre().trim().isEmpty()) {
            JOptionPane.showMessageDialog(pvista, "Complete los campos obligatorios (Código y Nombre).", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        pmodelo.setCodigo(pvista.getCodigo().trim());
        pmodelo.setNombre(pvista.getNombre().trim());
        pmodelo.setDescripcion(pvista.getDescripcion().trim());

        // EXTRAER Y ASIGNAR BODEGA POR NOMBRE
        if (pvista.getCbxBodega() != null && pvista.getCbxBodega().getSelectedItem() != null) {
            String nombreBodegaSel = pvista.getCbxBodega().getSelectedItem().toString();
            
            Modelo.Bodega bodegaModelo = new Modelo.Bodega();
            ArrayList<Modelo.Bodega> lista = bodegaModelo.obtenerCatalogo();
            if (lista != null) {
                for (Modelo.Bodega b : lista) {
                    if (b.getNombre().equalsIgnoreCase(nombreBodegaSel)) {
                        pmodelo.setIdBodega(b.getId());
                        break;
                    }
                }
            }
        }

        if (pvista.getCbxEstadoProducto() != null && pvista.getCbxEstadoProducto().getSelectedItem() != null) {
            pmodelo.setEstadoProducto(pvista.getCbxEstadoProducto().getSelectedItem().toString());
        }

        // 2. Insertar Producto en la Base de Datos
        if (pmodelo.insertarProducto()) {
            int idNuevoProducto = pmodelo.getId(); 

            JOptionPane.showMessageDialog(pvista, "Producto registrado correctamente. Ahora complete la cantidad, el lote y la fecha de vencimiento.", "Paso 1 Completado", JOptionPane.INFORMATION_MESSAGE);

            String nombreProd = pvista.getNombre();

            pvista.dispose(); 

            // 3. Abrir la vista de Lote donde SÍ se gestiona Stock (Cantidad >= 1) y Fecha de Caducidad
            LoteVista vistaLote = new LoteVista();
            Lote modeloLote = new Lote();

            modeloLote.setIdProducto(idNuevoProducto);
            modeloLote.setIdBodega(pmodelo.getIdBodega());

            if (vistaLote.getLblProductoInf() != null) {
                vistaLote.getLblProductoInf().setText(nombreProd);
            }

            if (vistaLote.getCbxBodega() != null && pvista.getCbxBodega() != null) {
                String bodegaNombre = pvista.getCbxBodega().getSelectedItem().toString();
                int bodegaId = pmodelo.getIdBodega();
                
                vistaLote.getCbxBodega().removeAllItems();
                vistaLote.getCbxBodega().addItem(bodegaId + " - " + bodegaNombre);
                vistaLote.getCbxBodega().setEnabled(false);
            }

            LoteControlador loteCtrl = new LoteControlador(modeloLote, vistaLote);
            loteCtrl.iniciar();

        } else {
            JOptionPane.showMessageDialog(pvista, "Error al guardar el producto en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    public void buscarProducto() {
//        String codigo = pvista.getCodigo().trim();
//        String nombre = pvista.getNombre().trim();
//
//        String criterio = !codigo.isEmpty() ? codigo : nombre;
//
//        if (criterio.isEmpty()) {
//            cargarDatosTabla();
//        } else {
//            if (pvista.getModeloTabla() != null) {
//                pvista.getModeloTabla().setRowCount(0);
//                ArrayList<String[]> lProductos = pmodelo.buscarProductos(criterio);
//                
//                if (lProductos != null && !lProductos.isEmpty()) {
//                    for (String[] p : lProductos) {
//                        if (p.length >= 6) {
//                            Object[] fila = {
//                                p[1], // Código
//                                p[2], // Nombre
//                                p[3], // Descripción
//                                p[4], // Cantidad
//                                p.length > 5 ? p[5] : "BUENO ESTADO",
//                                p.length > 7 ? p[7] : "ACTIVO"
//                            };
//                            pvista.getModeloTabla().addRow(fila);
//                        }
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(null, "No se encontraron productos con el criterio: " + criterio);
//                }
//            }
//        }
//    }

    public void buscarProducto() {
    String codigo = pvista.getCodigo().trim();
    String nombre = pvista.getNombre().trim();

    String criterio = !codigo.isEmpty() ? codigo : nombre;

    if (criterio.isEmpty()) {
        cargarDatosTabla();
    } else {
        if (pvista.getModeloTabla() != null) {
            pvista.getModeloTabla().setRowCount(0);
            ArrayList<String[]> lProductos = pmodelo.buscarProductos(criterio);
            
            if (lProductos != null && !lProductos.isEmpty()) {
                for (String[] p : lProductos) {
                    if (p.length >= 5) {
                        Object[] fila = {
                            p[0],                                 // 1. N° (ID)
                            p[1],                                 // 2. Código
                            p[2],                                 // 3. Nombre
                            p[3],                                 // 4. Descripción
                            p[4],                                 // 5. Cantidad
                            p.length > 5 ? p[6] : "BUENO ESTADO", // 6. Estado Producto
                            p.length > 6 ? p[7] : "Sin Lote",     // 7. ID Lote
                            p.length > 7 ? p[5] : "ACTIVO"        // 8. Estado
                        };
                        pvista.getModeloTabla().addRow(fila);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron productos con el criterio: " + criterio);
            }
        }
    }
}
    
    
    public void cargarCombos() {
        if (pvista.getCbxEstadoProducto() != null) {
            pvista.getCbxEstadoProducto().removeAllItems();
            pvista.getCbxEstadoProducto().addItem("BUENO ESTADO");
            pvista.getCbxEstadoProducto().addItem("MAL ESTADO");
        }
    }

    private void limpiarCampos() {
        this.idProductoSeleccionado = -1;
        pvista.setCodigo("");
        pvista.setNombre("");
        pvista.setDescripcion("");

        if (pvista.getCbxEstadoProducto() != null && pvista.getCbxEstadoProducto().getItemCount() > 0) {
            pvista.getCbxEstadoProducto().setSelectedIndex(0);
        }
    }
    
    private void cargarBodegas() {
        if (pvista.getCbxBodega() == null) return;
        
        pvista.getCbxBodega().removeAllItems();
        
        Modelo.Bodega bodegaModelo = new Modelo.Bodega();
        ArrayList<Modelo.Bodega> listaBodegas = bodegaModelo.obtenerCatalogo();
        
        String bodegaActual = SesionUsuario.getNombreBodega();

        if (listaBodegas != null) {
            for (Modelo.Bodega b : listaBodegas) {
                pvista.getCbxBodega().addItem(b.getNombre());
            }
        }

        if (bodegaActual != null && !bodegaActual.isEmpty()) {
            pvista.getCbxBodega().setSelectedItem(bodegaActual);
        }

        pvista.getCbxBodega().setEnabled(false);
    }
    
    
}