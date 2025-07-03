/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista.Ventas;

import controlador.Ctrl_Pedido;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import vista.TemaManager;

/**
 *
 * @author ZenBook
 */
public class pedido extends javax.swing.JPanel {

    private JPanel contenedor; // Referencia al contenedor de Principal
    private Ctrl_Pedido controlador;
    private int idPedido;

    /**
     * Creates new form pedido
     */
    public pedido(JPanel contenedor) {
        this.contenedor = contenedor;
        this.controlador = new Ctrl_Pedido();
        initComponents();
        aplicarTema(); // Apply initial theme

        // Register for theme changes
        TemaManager.getInstance().addThemeChangeListener(() -> {
            aplicarTema(); // Update theme when it changes
        });

        // Configurar la columna "Detalle"
        TableColumn detailColumn = tablaM.getColumnModel().getColumn(6);
        detailColumn.setCellRenderer(new ButtonRenderer());
        detailColumn.setPreferredWidth(35); // Ajustar el ancho de la columna
        tablaM.setRowHeight(30); // Ajusta este valor según necesites

        tablaM.setRowHeight(23); // Altura más delgada para las filas

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID pedido", "Nombre", "Estado", "Cliente", "Fecha inicio", "Fecha final", "Detalle", "ID"} // Usa tus nombres de columnas reales
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que todas las celdas sean no editables
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // Ajusta el tipo si usas tipos distintos (Double, Date, etc.)
            }
        };

        tablaM.setModel(modelo);

        tablaM.getColumnModel().getColumn(2).setCellRenderer(new EstadoTableCellRenderer());

        TableColumn column = tablaM.getColumnModel().getColumn(7); // Columna ID
        tablaM.removeColumn(column);

        // Cargar datos desde la base de datos
        cargarDatosIniciales();
    }

    // Método para agregar una nueva fila a la tabla
    public void agregarFilaATabla(Object[] fila) {
        DefaultTableModel model = (DefaultTableModel) tablaM.getModel();
        model.addRow(fila);
    }

    // Cargar datos desde la base de datos
    public void cargarDatosIniciales() {
        DefaultTableModel model = (DefaultTableModel) tablaM.getModel();
        model.setRowCount(0);

        List<Ctrl_Pedido.MaterialConDetalles> pedidos = controlador.obtenerMateriales();
        for (Ctrl_Pedido.MaterialConDetalles pedido : pedidos) {
            model.addRow(new Object[]{
                pedido.getPedido().getNum_pedido(),
                pedido.getPedido().getNombre(),
                pedido.getPedido().getEstado(),
                pedido.getNombreCliente(),
                new java.text.SimpleDateFormat("dd/MM/yyyy").format(pedido.getPedido().getFecha_inicio()),
                new java.text.SimpleDateFormat("dd/MM/yyyy").format(pedido.getPedido().getFecha_fin()),
                "Ver",
                pedido.getPedido().getId_pedido() // Guardar el ID como dato oculto en la última columna
            });
        }
    }

    // Método para manejar búsqueda (conecta con tu campo de búsqueda):
    private void buscarPedidos() {
        String criterio = txtBuscar.getText().trim();
        DefaultTableModel model = (DefaultTableModel) tablaM.getModel();
        model.setRowCount(0);

        List<Ctrl_Pedido.MaterialConDetalles> resultados = controlador.buscarPedidos(criterio);
        for (Ctrl_Pedido.MaterialConDetalles pedido : resultados) {
            model.addRow(new Object[]{
                pedido.getPedido().getNum_pedido(),
                pedido.getPedido().getNombre(),
                pedido.getPedido().getEstado(),
                pedido.getNombreCliente(),
                new SimpleDateFormat("dd/MM/yyyy").format(pedido.getPedido().getFecha_inicio()),
                new SimpleDateFormat("dd/MM/yyyy").format(pedido.getPedido().getFecha_fin()),
                "Ver",
                pedido.getPedido().getId_pedido()
            });
        }
    }

// Renderizador para la columna "Ver"
    private class ButtonRenderer extends DefaultTableCellRenderer {

        private final Color textColor = new Color(46, 49, 82); // Color de texto normal
        private final Font fontNormal = new Font("Tahoma", Font.PLAIN, 14);
        private final Font fontBold = new Font("Tahoma", Font.BOLD, 14);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            c.setForeground(isSelected ? Color.WHITE : Color.BLACK);
            c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            c.setFont(isSelected ? fontBold : fontNormal);

            setHorizontalAlignment(CENTER);
            setText("Ver");

            // Bordes iguales al resto
            setBorder(BorderFactory.createLineBorder(new Color(153, 153, 153), 1));
            tablaM.setRowHeight(23); // Altura más delgada para las filas
            return c;
        }
    }

    // Renderizador para la columna de estado
    private class EstadoTableCellRenderer extends DefaultTableCellRenderer {

        private final Color textColor = new Color(46, 49, 82);
        private final Font fontNormal = new Font("Tahoma", Font.PLAIN, 14);
        private final Font fontBold = new Font("Tahoma", Font.BOLD, 14);

        public EstadoTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER); // Centrar el texto
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // Llamar al método padre primero
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(CENTER);
            label.setText(value != null ? value.toString() : "");

            if (isSelected) {
                // Cuando está seleccionado, texto blanco y fondo de selección
                label.setForeground(Color.WHITE);
                label.setBackground(table.getSelectionBackground());
                label.setFont(fontBold);
            } else {
                // Cuando no está seleccionado, mantener el color original del texto
                label.setForeground(textColor);
                label.setFont(fontNormal);

                // Aplicar colores de fondo según el estado
                String estado = value != null ? value.toString() : "";
                switch (estado.toLowerCase()) {
                    case "pendiente":
                        label.setBackground(new Color(255, 204, 204)); // Rojo claro
                        break;
                    case "proceso":
                        label.setBackground(new Color(255, 255, 153)); // Amarillo claro
                        break;
                    case "finalizado":
                        label.setBackground(new Color(204, 255, 204)); // Verde claro
                        break;
                    default:
                        label.setBackground(Color.WHITE);
                        break;
                }
            }

            // Borde igual al resto de la tabla
            label.setBorder(BorderFactory.createLineBorder(new Color(153, 153, 153), 1));
            tablaM.setRowHeight(23); // Altura más delgada para las filas
            return label;
        }
    }

    private void mostrarDetallesPedido(String id) {
        DetallesPedido detalles = new DetallesPedido(id, contenedor);
        detalles.setSize(1290, 730);
        detalles.setLocation(0, 0);
        contenedor.removeAll();
        contenedor.add(detalles);
        contenedor.revalidate();
        contenedor.repaint();
    }

    public void aplicarTema() {
        boolean oscuro = TemaManager.getInstance().isOscuro();

        if (oscuro) {
            Color fondo = new Color(21, 21, 33);
            Color primario = new Color(40, 60, 150);
            Color texto = Color.WHITE;

            jPanel1.setBackground(fondo);
            txtBuscar.setBackground(fondo);
            txtBuscar.setForeground(texto);
            txtBuscar.setColorIcon(texto);
            txtBuscar.setPhColor(Color.LIGHT_GRAY);

            tablaM.setBackground(new Color(21, 21, 33));
            tablaM.setBackgoundHead(new Color(67, 71, 120));
            tablaM.setForegroundHead(new Color(255, 255, 255));
            tablaM.setBackgoundHover(new Color(40, 50, 90));
            tablaM.setFont(new Font("Tahoma", Font.PLAIN, 15));
            tablaM.setColorPrimary(new Color(37, 37, 52));
            tablaM.setColorPrimaryText(texto);
            tablaM.setColorSecondary(new Color(30, 30, 45));
            tablaM.setColorSecundaryText(texto);
            tablaM.setColorBorderHead(primario);
            tablaM.setColorBorderRows(fondo.darker());
            tablaM.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            tablaM.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            tablaM.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));
            tablaM.setEffectHover(true);
            tablaM.setShowGrid(true);
            tablaM.setGridColor(Color.WHITE); // o el color que desees

            btnEliminar1.setBackground(new Color(67, 71, 120));
            btnNuevo.setBackgroundHover(new Color(118, 142, 240));
            btnNuevo.setBackground(new Color(67, 71, 120));
            btnEliminar1.setBackgroundHover(new Color(118, 142, 240));
            btnEliminar1.setBackground(new Color(67, 71, 120));
            btnEliminar1.setBackgroundHover(new Color(118, 142, 240));
        } else {
            Color fondo = new Color(242, 247, 255);
            Color texto = Color.BLACK;
            Color primario = new Color(72, 92, 188);

            jPanel1.setBackground(fondo);
            txtBuscar.setBackground(fondo);
            txtBuscar.setForeground(texto);
            txtBuscar.setColorIcon(texto);
            txtBuscar.setPhColor(Color.GRAY);

            tablaM.setBackground(new Color(255, 255, 255));
            tablaM.setBackgoundHead(new Color(46, 49, 82));
            tablaM.setForegroundHead(Color.WHITE);
            tablaM.setBackgoundHover(new Color(67, 150, 209));
            tablaM.setFont(new Font("Tahoma", Font.PLAIN, 15));
            tablaM.setColorPrimary(new Color(242, 242, 242));
            tablaM.setColorPrimaryText(texto);
            tablaM.setColorSecondary(new Color(255, 255, 255));
            tablaM.setColorSecundaryText(texto);
            tablaM.setColorBorderHead(primario);
            tablaM.setColorBorderRows(new Color(0, 0, 0));
            tablaM.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            tablaM.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            tablaM.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));
            tablaM.setEffectHover(true);
            tablaM.setSelectionBackground(new Color(67, 150, 209));
            tablaM.setShowGrid(true);
            tablaM.setGridColor(Color.BLACK); // o el color que desees

            btnNuevo.setBackground(new Color(46, 49, 82));
            btnEliminar1.setBackground(new Color(46, 49, 82));
        }
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
        txtBuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        btnNuevo = new RSMaterialComponent.RSButtonShape();
        btnEliminar1 = new RSMaterialComponent.RSButtonShape();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaM = new RSMaterialComponent.RSTableMetroCustom();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1304, 742));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtBuscar.setForeground(new java.awt.Color(0, 0, 0));
        txtBuscar.setColorIcon(new java.awt.Color(0, 0, 0));
        txtBuscar.setColorMaterial(new java.awt.Color(153, 153, 153));
        txtBuscar.setIcons(rojeru_san.efectos.ValoresEnum.ICONS.SEARCH);
        txtBuscar.setPhColor(new java.awt.Color(102, 102, 102));
        txtBuscar.setPlaceholder("Buscar...");
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        jPanel1.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 400, 30));

        btnNuevo.setBackground(new java.awt.Color(46, 49, 82));
        btnNuevo.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus (2).png"))); // NOI18N
        btnNuevo.setText(" Nuevo");
        btnNuevo.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnNuevo.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnNuevo.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnNuevo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        jPanel1.add(btnNuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 40, 110, 30));

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
        jPanel1.add(btnEliminar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 40, 110, 30));

        tablaM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "N° pedido", "Nombre", "Estado", "Cliente", "Fecha Inicio", "Fecha Final", "Detalle", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaM.setBackgoundHead(new java.awt.Color(46, 49, 82));
        tablaM.setBackgoundHover(new java.awt.Color(109, 160, 221));
        tablaM.setBorderHead(null);
        tablaM.setBorderRows(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        tablaM.setColorBorderHead(new java.awt.Color(46, 49, 82));
        tablaM.setColorBorderRows(new java.awt.Color(46, 49, 82));
        tablaM.setColorPrimaryText(new java.awt.Color(0, 0, 0));
        tablaM.setColorSecondary(new java.awt.Color(255, 255, 255));
        tablaM.setColorSecundaryText(new java.awt.Color(0, 0, 0));
        tablaM.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tablaM.setFontHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tablaM.setFontRowHover(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tablaM.setFontRowSelect(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tablaM.setModelSelection(RSMaterialComponent.RSTableMetroCustom.SELECTION_ROWS.MULTIPLE_INTERVAL_SELECTION);
        tablaM.setRowHeight(23);
        tablaM.setSelectionBackground(new java.awt.Color(109, 160, 221));
        tablaM.setShowGrid(false);
        tablaM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablaM);
        tablaM.getColumnModel().getColumn(0).setPreferredWidth(10);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 1200, 500));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1304, 742));
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed

        pedidoNuevo dialog = new pedidoNuevo(new javax.swing.JFrame(), true, this);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnEliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar1ActionPerformed
        int[] selectedRows = tablaM.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un pedido", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar " + selectedRows.length + " pedido(s)?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int eliminados = 0;
        DefaultTableModel model = (DefaultTableModel) tablaM.getModel();

        for (int row : selectedRows) {
            // Obtener el ID de la última columna del modelo (índice 7 en el modelo, no en la vista)
            int modelRow = tablaM.convertRowIndexToModel(row);
            int idPedido = (int) model.getValueAt(modelRow, 7); // <-- Usar el modelo directamente

            if (controlador.eliminarPedido(idPedido)) {
                eliminados++;
            }
        }

        if (eliminados > 0) {
            JOptionPane.showMessageDialog(this, eliminados + " pedido(s) eliminado(s)", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatosIniciales();
        }
    }//GEN-LAST:event_btnEliminar1ActionPerformed

    private void tablaMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMMouseClicked
        int viewColumn = tablaM.columnAtPoint(evt.getPoint());
        int viewRow = tablaM.rowAtPoint(evt.getPoint());

        // Convertir índices de vista a modelo
        int modelColumn = tablaM.convertColumnIndexToModel(viewColumn);
        int modelRow = tablaM.convertRowIndexToModel(viewRow);

        if (modelColumn == 6) { // Columna "Ver" en el modelo (índice 6)
            // Obtener el ID de la columna oculta (índice 7 en el modelo)
            String id = tablaM.getModel().getValueAt(modelRow, 7).toString();
            mostrarDetallesPedido(id);
        }
    }//GEN-LAST:event_tablaMMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSButtonShape btnEliminar1;
    private RSMaterialComponent.RSButtonShape btnNuevo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private RSMaterialComponent.RSTableMetroCustom tablaM;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtBuscar;
    // End of variables declaration//GEN-END:variables
}
