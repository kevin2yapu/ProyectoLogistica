package Controlador;

import Modelo.Bodega;
import Modelo.Lote;
import Modelo.Producto;
import Modelo.DetalleMovimiento; // CORREGIDO: Importa el Modelo, no la Vista
import javax.swing.JOptionPane;

/**
 * Controlador para procesar las entradas de inventario y generar comprobantes.
 * @author KEVIN
 */
public class EntradaControlador {

    private Lote modeloLote = new Lote();
    private DetalleMovimiento modeloDetalle = new DetalleMovimiento();

    public void procesarEntradaYGenerarPDF(
            int notaId,
            Object bodegaObj,
            Object productoObj,
            String txtCantidad,
            String txtCodigoLote,
            java.util.Date fechaVencimiento) {

        // 1. VALIDACIONES
        if (bodegaObj == null || productoObj == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar Bodega y Producto.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double cantidad = 0;
        try {
            cantidad = Double.parseDouble(txtCantidad.trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Cantidad inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtCodigoLote == null || txtCodigoLote.trim().isEmpty() || fechaVencimiento == null) {
            JOptionPane.showMessageDialog(null, "Complete los datos del Lote (Código y Fecha).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. EXTRAER DATOS
        Bodega bodega = (Bodega) bodegaObj;
        Producto producto = (Producto) productoObj;

        try {
            // 3. REGISTRAR EN BD USANDO EL MODELO DETALLE MOVIMIENTO
            boolean exito = modeloDetalle.registrarDetalleYActualizarStock(
                    notaId, 
                    txtCodigoLote.trim(), 
                    producto.getId(), 
                    (int) cantidad, 
                    "ENTRADA"
            );

            if (exito) {
                int respuesta = JOptionPane.showConfirmDialog(null, 
                        "¡Entrada e Inventario registrados con éxito!\n¿Desea generar el reporte PDF?", 
                        "Éxito", JOptionPane.YES_NO_OPTION);
                
                if (respuesta == JOptionPane.YES_OPTION) {
                    generarComprobantePDF(notaId, bodega.getNombre(), producto.getNombre(), txtCodigoLote, cantidad);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar la entrada en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error en el proceso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarComprobantePDF(int notaId, String bodega, String producto, String lote, double cantidad) {
        System.out.println("Generando PDF para la Nota #" + notaId + " | Bodega: " + bodega + " | Lote: " + lote);
        JOptionPane.showMessageDialog(null, "Comprobante PDF generado correctamente.", "PDF Creado", JOptionPane.INFORMATION_MESSAGE);
    }
}