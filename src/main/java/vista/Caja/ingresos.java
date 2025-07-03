/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista.Caja;

import controlador.Ctrl_CajaIngresos;
import controlador.Ctrl_Pedido;
import controlador.GeneradorIngresosPDF;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import modelo.Caja;
import modelo.Ingresos;
import modelo.PedidoDetalle;
import vista.TemaManager;

/**
 *
 * @author ADSO
 */
public final class ingresos extends javax.swing.JPanel {

    private JPanel contenedor; // Referencia al contenedor de Principal
    private Ctrl_CajaIngresos controlador;
    private GeneradorIngresosPDF generadorPDF;
    private Ctrl_Pedido ctrlPedido;

    /**
     * Creates new form Ingresos
     */
    public ingresos(JPanel contenedor) {
        this.contenedor = contenedor;

        // Inicializar el controlador
        controlador = new Ctrl_CajaIngresos();
        generadorPDF = new GeneradorIngresosPDF();
        ctrlPedido = new Ctrl_Pedido();

        initComponents();

        Tabla1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID Pedido", "Nombre pedido", "Cliente", "Monto total", "Pagado", "Debido", "Abonar", "Detalle", "Acciones", "ID"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer que ninguna celda sea editable
                return false;
            }
        });

        TableColumn numPedido = Tabla1.getColumnModel().getColumn(0);
        numPedido.setPreferredWidth(50); // Ajustar el ancho de la columna

        TableColumn nombre = Tabla1.getColumnModel().getColumn(1);
        nombre.setPreferredWidth(120); // Ajustar el ancho de la columna

        TableColumn abonar = Tabla1.getColumnModel().getColumn(6);
        abonar.setPreferredWidth(10); // Ajustar el ancho de la columna

        // Configurar la columna "Detalle"
        TableColumn detalles = Tabla1.getColumnModel().getColumn(7);
        detalles.setPreferredWidth(10);
        detalles.setCellRenderer(new ButtonRenderer());

        TableColumn imprimir = Tabla1.getColumnModel().getColumn(8);
        imprimir.setPreferredWidth(10); // Ajustar el ancho de la columna

        // En el constructor de la clase:
        Tabla1.getColumnModel().getColumn(9).setMinWidth(0);
        Tabla1.getColumnModel().getColumn(9).setMaxWidth(0);
        Tabla1.getColumnModel().getColumn(9).setWidth(0);

        // Cargar datos iniciales
        cargarDatosIniciales();
    }

    // Método para cargar datos iniciales en la tabla
    public void cargarDatosIniciales() {
        DefaultTableModel model = (DefaultTableModel) Tabla1.getModel();

        model.setRowCount(0); // Limpiar la tabla

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("$#,##0.00", symbols);

        List<Ctrl_CajaIngresos.IngresoConDetalles> ingresos = controlador.obtenerIngresos();
        if (ingresos == null || ingresos.isEmpty()) {
            System.out.println("No se encontraron ingresos para cargar en la tabla.");
            return;
        }

        for (Ctrl_CajaIngresos.IngresoConDetalles ingreso : ingresos) {
            model.addRow(new Object[]{
                ingreso.getNumPedido(),
                ingreso.getNombrePedido(), // Ajustado a numPedido
                ingreso.getNombreCliente(),
                df.format(ingreso.getMontoTotal()),
                df.format(ingreso.getPagado()),
                df.format(ingreso.getDebido()),
                "Abonar",
                "Ver",
                "Imprimir",
                ingreso.getIdPedido()
            });
        }
    }

    // Renderizador personalizado para la columna "Ver"
    private class ButtonRenderer extends DefaultTableCellRenderer {

        private final Color textColor = new Color(46, 49, 82);
        private final Font fontNormal = new Font("Tahoma", Font.PLAIN, 14);
        private final Font fontBold = new Font("Tahoma", Font.BOLD, 14);

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            c.setForeground(isSelected ? Color.WHITE : textColor);
            c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            c.setFont(isSelected ? fontBold : fontNormal);

            setHorizontalAlignment(CENTER);
            setText("Ver");

            // Bordes para que se vea como un botón
            setBorder(BorderFactory.createLineBorder(new Color(219, 219, 219), 1));
            return c;
        }
    }

    private void mostrarDetallesPedido(String id) {
        ingDetalles detalles = new ingDetalles(id, contenedor);
        detalles.setSize(1290, 730);
        detalles.setLocation(0, 0);
        contenedor.removeAll();
        contenedor.add(detalles);
        contenedor.revalidate();
        contenedor.repaint();
    }

    // Método auxiliar para formatear moneda
    private String formatCurrency(double amount) {
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        return df.format(amount);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtbuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        btnEliminar1 = new RSMaterialComponent.RSButtonShape();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabla1 = new RSMaterialComponent.RSTableMetroCustom();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1250, 630));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtbuscar.setForeground(new java.awt.Color(0, 0, 0));
        txtbuscar.setColorIcon(new java.awt.Color(0, 0, 0));
        txtbuscar.setColorMaterial(new java.awt.Color(153, 153, 153));
        txtbuscar.setIcons(rojeru_san.efectos.ValoresEnum.ICONS.SEARCH);
        txtbuscar.setPhColor(new java.awt.Color(102, 102, 102));
        txtbuscar.setPlaceholder("Buscar...");
        txtbuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtbuscarActionPerformed(evt);
            }
        });
        jPanel1.add(txtbuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 410, 30));

        btnEliminar1.setBackground(new java.awt.Color(46, 49, 82));
        btnEliminar1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnEliminar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete (1).png"))); // NOI18N
        btnEliminar1.setText(" Eliminar");
        btnEliminar1.setToolTipText("");
        btnEliminar1.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnEliminar1.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnEliminar1.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnEliminar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnEliminar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 40, 110, 30));

        Tabla1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID pedido", "Nombre pedido", "Cliente", "Monto total", "Pagado", "Debido", "Abonar", "Detalle", "Acciones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabla1.setToolTipText("");
        Tabla1.setBackgoundHead(new java.awt.Color(46, 49, 82));
        Tabla1.setBackgoundHover(new java.awt.Color(67, 150, 209));
        Tabla1.setBorderHead(null);
        Tabla1.setBorderRows(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        Tabla1.setColorBorderHead(new java.awt.Color(46, 49, 82));
        Tabla1.setColorBorderRows(new java.awt.Color(46, 49, 82));
        Tabla1.setColorPrimaryText(new java.awt.Color(0, 0, 0));
        Tabla1.setColorSecondary(new java.awt.Color(255, 255, 255));
        Tabla1.setColorSecundaryText(new java.awt.Color(0, 0, 0));
        Tabla1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Tabla1.setFontHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Tabla1.setFontRowHover(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Tabla1.setFontRowSelect(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Tabla1.setRowHeight(23);
        Tabla1.setSelectionBackground(new java.awt.Color(67, 150, 209));
        Tabla1.setShowGrid(false);
        Tabla1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabla1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Tabla1);
        Tabla1.getColumnModel().getColumn(0).setPreferredWidth(10);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 1190, 500));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1301, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1301, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 673, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtbuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtbuscarActionPerformed
        // TODO add your handling code here:
        filtrarTabla();
    }//GEN-LAST:event_txtbuscarActionPerformed

    private void btnEliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar1ActionPerformed
        int selectedRow = Tabla1.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un registro para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el ID del pedido desde la columna oculta (índice 9)
        int idPedido = (int) Tabla1.getValueAt(selectedRow, 9);

        // Obtener los datos completos del ingreso
        Ctrl_CajaIngresos.IngresoConDetalles ingreso = controlador.obtenerIngresos().stream()
                .filter(i -> i.getIdPedido() == idPedido)
                .findFirst()
                .orElse(null);

        if (ingreso == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el registro seleccionado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si el pago está completo (Debido <= 0)
        if (ingreso.getDebido() > 0) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar este registro porque el pago no está completo.\n"
                    + "Monto pendiente: " + formatCurrency(ingreso.getDebido()) + "\n"
                    + "Complete el pago antes de eliminar.",
                    "Pago Incompleto",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmar eliminación
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este registro?\n"
                + "ID Pedido: " + ingreso.getNumPedido() + "\n"
                + "Cliente: " + ingreso.getNombreCliente(),
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = controlador.eliminarIngreso(idPedido);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Registro eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatosIniciales(); // Actualizar la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el registro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminar1ActionPerformed

    private void Tabla1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabla1MouseClicked
        int column = Tabla1.columnAtPoint(evt.getPoint());
        int row = Tabla1.rowAtPoint(evt.getPoint());

        if (row >= 0) {
            int idPedido = (int) Tabla1.getValueAt(row, 9);
            Ctrl_CajaIngresos.IngresoConDetalles ingreso = controlador.obtenerIngresos().stream()
                    .filter(i -> i.getIdPedido() == idPedido).findFirst().orElse(null);

            if (ingreso == null) {
                JOptionPane.showMessageDialog(this, "No se encontraron datos para el pedido ID: " + idPedido, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (column == 6) { // Columna "Abonar"
                try {
                    // Validar si el pedido ya está pagado completamente
                    if (ingreso.getDebido() <= 0) {
                        JOptionPane.showMessageDialog(this,
                                "Este pedido ya ha sido pagado completamente.\n"
                                + "Total pagado: " + formatCurrency(ingreso.getPagado()) + "\n"
                                + "Monto total: " + formatCurrency(ingreso.getMontoTotal()),
                                "Pago Completo",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Obtiene el JFrame padre del JPanel actual
                    java.awt.Frame parentFrame = (java.awt.Frame) SwingUtilities.getWindowAncestor(this);
                    if (parentFrame == null) {
                        JOptionPane.showMessageDialog(this, "No se pudo obtener el JFrame padre.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Obtén el numPedido de la tabla
                    Object numPedidoObj = Tabla1.getValueAt(row, 0);
                    if (numPedidoObj == null) {
                        JOptionPane.showMessageDialog(this, "Número de pedido inválido en la fila seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String numPedido = numPedidoObj.toString();

                    // Crea el diálogo con los parámetros necesarios
                    iAbonoNuevo dialog = new iAbonoNuevo(
                            parentFrame, // Frame padre
                            true, // Modal
                            idPedido, // ID del pedido
                            numPedido,
                            controlador
                    );
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                    // Actualizar la tabla después de cerrar el diálogo
                    if (dialog.isGuardado()) {
                        cargarDatosIniciales(); // Actualizar tabla si se guardó
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error al abrir el diálogo de abono: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

            } else if (column == 7) { // Columna "Detalle" (Ver)
                // Obtener el ID de la columna oculta (índice 7 en el modelo)
                String id = Tabla1.getModel().getValueAt(row, 9).toString();
                mostrarDetallesPedido(id);

            } else if (column == 8) { // Columna "Acciones" (Imprimir)
                try {
                    // Obtener datos completos del pedido
                    Ctrl_Pedido.MaterialConDetalles material = ctrlPedido.obtenerPedidoPorId(idPedido);
                    if (material == null) {
                        JOptionPane.showMessageDialog(this, "No se encontró el pedido para el ID: " + idPedido, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Obtener detalles del pedido
                    List<PedidoDetalle> detalles = ctrlPedido.obtenerDetallesPorPedido(idPedido);
                    if (detalles == null || detalles.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No se encontraron detalles para el pedido ID: " + idPedido, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Formateador de números para moneda
                    DecimalFormat df = new DecimalFormat("$#,##0.00", new DecimalFormatSymbols(Locale.US));

                    // Crear modelo de datos para el PDF
                    DefaultTableModel pdfModel = new DefaultTableModel(
                            new Object[detalles.size() + 3][6], // +3 para filas de totales y abonos
                            new String[]{"Descripción", "Cantidad", "Dimensiones", "Precio Unitario", "Subtotal", "Total"}
                    );

                    // Llenar datos de los detalles
                    double totalGeneral = 0;
                    for (int i = 0; i < detalles.size(); i++) {
                        PedidoDetalle detalle = detalles.get(i);
                        pdfModel.setValueAt(detalle.getDescripcion(), i, 0);
                        pdfModel.setValueAt(detalle.getCantidad(), i, 1);
                        pdfModel.setValueAt(detalle.getDimensiones(), i, 2);
                        pdfModel.setValueAt(df.format(detalle.getPrecioUnitario()), i, 3);
                        pdfModel.setValueAt(df.format(detalle.getSubtotal()), i, 4);
                        pdfModel.setValueAt(df.format(detalle.getTotal()), i, 5);
                        totalGeneral += detalle.getTotal();
                    }

                    // Agregar fila de abonos si existen
                    if (!ingreso.getAbonos().isEmpty()) {
                        int rowAbonos = detalles.size() + 2;
                        pdfModel.setValueAt("ABONOS REGISTRADOS", rowAbonos, 0);

                        // Construir cadena con información de abonos
                        StringBuilder abonosInfo = new StringBuilder();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        for (Ingresos abono : ingreso.getAbonos()) {
                            abonosInfo.append("Abono #").append(abono.getNumAbono())
                                    .append(" - ").append(sdf.format(abono.getFechaPago()))
                                    .append(": ").append(df.format(abono.getMonto()))
                                    .append(" (").append(abono.getMetodoPago()).append(")\n");
                        }
                        pdfModel.setValueAt(abonosInfo.toString(), rowAbonos, 1);
                    }

                    // Generar nombre del archivo
                    String archivoSalida = "ingreso_" + material.getPedido().getNum_pedido() + "_"
                            + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

                    // Generar encabezado con información del cliente y pedido
                    String encabezado = "Cliente: " + ingreso.getNombreCliente()
                            + "\nPedido: " + material.getPedido().getNombre()
                            + " (" + material.getPedido().getNum_pedido() + ")"
                            + "\nFecha: " + (material.getPedido().getFecha_inicio() != null
                            ? new SimpleDateFormat("dd/MM/yyyy").format(material.getPedido().getFecha_inicio()) : "Sin fecha");

                    // Generar el PDF
                    generadorPDF.generarPDF(
                            ingreso.getNombreCliente(), // Nombre del cliente
                            ingreso.getCodigoCliente(), // Código del cliente
                            ingreso.getTelefonoCliente(), // Teléfono del cliente
                            ingreso.getDireccionCliente(), // Dirección del cliente
                            ingreso.getDepartamentoCliente(), // Nuevo: Departamento
                            ingreso.getMunicipioCliente(), // Nuevo: Municipio
                            pdfModel, // Tu DefaultTableModel con los datos
                            df.format(ingreso.getMontoTotal()), // Monto total formateado
                            archivoSalida, // Ruta de salida del PDF
                            material.getPedido().getFecha_inicio() != null
                            ? new SimpleDateFormat("dd/MM/yyyy").format(material.getPedido().getFecha_inicio()) : "Sin fecha",
                            material.getPedido().getNum_pedido(),
                            df.format(ingreso.getPagado()),
                            df.format(ingreso.getDebido())
                    // Fecha del pedido
                    );

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_Tabla1MouseClicked

    private void filtrarTabla() {
        String textoBusqueda = txtbuscar.getText().trim();
        DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<>(modelo);
        Tabla1.setRowSorter(tr);
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (textoBusqueda.isEmpty()) {
            tr.setRowFilter(null);
            return;
        }
        // Expresión regular para detectar si son solo números (1-2 dígitos)
        if (textoBusqueda.matches("\\d+")) {
            // Buscar en ID (columna 0) y fechas (columnas 1 y 2)
            filters.add(RowFilter.regexFilter(textoBusqueda, 0));// ID (coincidencia exacta)
            filters.add(RowFilter.regexFilter(textoBusqueda, 1));
            filters.add(RowFilter.regexFilter(textoBusqueda, 4));

        } // Si contiene letras (aunque sea parcial)
        else {
            // Buscar en Detalle (columna 2) y Categoría (columna 3)
            String regex = "(?i)" + textoBusqueda; // (?i) = ignore case
            filters.add(RowFilter.regexFilter(regex, 2)); // Detalle
            filters.add(RowFilter.regexFilter(regex, 3)); // Categoría
        }

        // Aplicar todos los filtros combinados con OR
        tr.setRowFilter(RowFilter.orFilter(filters));

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSTableMetroCustom Tabla1;
    private RSMaterialComponent.RSButtonShape btnEliminar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtbuscar;
    // End of variables declaration//GEN-END:variables
}
