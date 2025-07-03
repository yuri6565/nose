/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista.Cotizacion;

import controlador.CotizacionDAO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Conexion;
import controlador.Ctrl_Cotizacion;
import java.awt.Container;
import java.text.SimpleDateFormat;
import static java.time.Clock.offset;
import java.util.ArrayList;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import modelo.Cotizacion;

/**/
public class HistorialCot extends javax.swing.JPanel {

    private JPanel contenedor;
    private Ctrl_Cotizacion controlador;

    public HistorialCot(Container contenedor, boolean par) throws SQLException {
        this.contenedor = (JPanel) contenedor;
        Ctrl_Cotizacion controlador = new Ctrl_Cotizacion();
        initComponents();
        configurarTabla();
        cargarDatosIniciales();
    }

    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Detalle", "Unidad", "Cantidad", "Fecha", "Valor Unitario", "Subtotal", "Total", "Usuario", "Cliente", "Ver"}
        ) {
            Class[] types = new Class[]{
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class,
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class,
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        tablaM.setModel(modelo);

        // Configurar renderizador para la columna "Ver"
        tablaM.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
        tablaM.getColumnModel().getColumn(0).setPreferredWidth(50);  // Código
        tablaM.getColumnModel().getColumn(1).setPreferredWidth(150); // Detalle
        tablaM.getColumnModel().getColumn(2).setPreferredWidth(80);  // Unidad
        tablaM.getColumnModel().getColumn(3).setPreferredWidth(60);  // Cantidad
        tablaM.getColumnModel().getColumn(4).setPreferredWidth(100); // Fecha
        tablaM.getColumnModel().getColumn(5).setPreferredWidth(80);  // Valor Unitario
        tablaM.getColumnModel().getColumn(6).setPreferredWidth(80);  // Subtotal
        tablaM.getColumnModel().getColumn(7).setPreferredWidth(80);  // Total
        tablaM.getColumnModel().getColumn(8).setPreferredWidth(60);  // Usuario
        tablaM.getColumnModel().getColumn(9).setPreferredWidth(100); // Cliente
        tablaM.getColumnModel().getColumn(10).setPreferredWidth(50); // Ver

        tablaM.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int column = tablaM.getColumnModel().getColumnIndexAtX(evt.getX());
                int row = evt.getY() / tablaM.getRowHeight();
                if (row < tablaM.getRowCount() && row >= 0 && column == 10) {
                    String id = tablaM.getValueAt(row, 0).toString();
                    mostrarDetallesCotizacion(id);
                }
            }
        });
    }

// Método para agregar una nueva fila a la tabla
    public void agregarFilaATabla(Object[] fila) {
        DefaultTableModel model = (DefaultTableModel) tablaM.getModel();
        model.addRow(fila);
    }

    // Método para agregar una cotización
    public void agregarCotizacion(Cotizacion cotizacion) {
        DefaultTableModel model = (DefaultTableModel) tablaM.getModel();
        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy").format(cotizacion.getFecha());
        String cliente = obtenerNombreCliente(cotizacion.getCliente_codigo());
        model.addRow(new Object[]{
            cotizacion.getId_cotizacion(),
            cotizacion.getDetalle(),
            cotizacion.getUnidad(),
            cotizacion.getCantidad(),
            fechaFormateada,
            cotizacion.getValor_unitario(),
            cotizacion.getSub_total(),
            cotizacion.getTotal(),
            cotizacion.getUsuario_id_usuario(),
            cliente,
            "Ver"
        });

        // Ordenar por código descendente
        javax.swing.RowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
        tablaM.setRowSorter(sorter);
        java.util.List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
    }

    private void cargarDatosIniciales() throws SQLException {
        DefaultTableModel modelo = (DefaultTableModel) tablaM.getModel();
        modelo.setRowCount(0);

        // Usa valores por defecto
        int offset = 0;
        int limit = 50;

        List<Cotizacion> cotizaciones = controlador.obtenerCotizaciones(offset, limit);
        for (Cotizacion cotizacion : cotizaciones) {
            agregarCotizacion(cotizacion);
        }
    }

    private String obtenerNombreCliente(Integer clienteCodigo) {
        try (Connection con = Conexion.getConnection(); PreparedStatement pstmt = con.prepareStatement("SELECT CONCAT(nombre, ' ', apellido) AS nombre_completo FROM cliente WHERE codigo = ?")) {
            pstmt.setInt(1, clienteCodigo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre_completo");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener nombre del cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return "Sin cliente";
    }

    private void mostrarDetallesCotizacion(String id) {

        DetallesCot detalles = new DetallesCot(id, contenedor);
        detalles.setSize(1290, 730);
        detalles.setLocation(0, 0);
        contenedor.removeAll();
        contenedor.add(detalles);
        contenedor.revalidate();
        contenedor.repaint();

    }

    int setHeight(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void setLocationRelativeTo(cotizacion aThis) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private class ButtonRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = new JButton("Ver");
            button.setBackground(isSelected ? table.getSelectionBackground() : new Color(46, 49, 82));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Tahoma", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return button;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {

        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Ver" : value.toString();
            selectedRow = row;
            JButton button = new JButton(label);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    String id = table.getValueAt(selectedRow, 0).toString();
                    mostrarDetallesPedido(id);
                }
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = true;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void mostrarDetallesPedido(String id) {
        DetallesCot detalles = new DetallesCot(id, contenedor);
        detalles.setSize(1290, 730);
        detalles.setLocation(0, 0);
        contenedor.removeAll();
        contenedor.add(detalles);
        contenedor.revalidate();
        contenedor.repaint();
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
        cmbCategoria = new RSMaterialComponent.RSComboBoxMaterial();
        txtBuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaM = new RSMaterialComponent.RSTableMetroCustom();
        btnVolver = new RSMaterialComponent.RSButtonShape();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1304, 742));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbCategoria.setForeground(new java.awt.Color(153, 153, 153));
        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una categoria:" }));
        cmbCategoria.setColorMaterial(new java.awt.Color(153, 153, 153));
        cmbCategoria.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel1.add(cmbCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 60, 290, 40));

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
        jPanel1.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 410, 40));

        tablaM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Fecha", "Cliente", "Aceptar", "Detalles", "null"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
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

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 1210, 500));

        btnVolver.setBackground(new java.awt.Color(46, 49, 82));
        btnVolver.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnVolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/volver (1).png"))); // NOI18N
        btnVolver.setText(" Volver cotización");
        btnVolver.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnVolver.setFont(new java.awt.Font("Roboto Bold", 1, 17)); // NOI18N
        btnVolver.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnVolver.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });
        jPanel1.add(btnVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 190, 30));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1304, 742));
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void tablaMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMMouseClicked

    }//GEN-LAST:event_tablaMMouseClicked

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        cotizacion c = new cotizacion(contenedor);
        c.setSize(1290, 730);
        c.setLocation(0, 0);
        contenedor.removeAll();
        contenedor.add(c);
        contenedor.revalidate();
        contenedor.repaint();
    }//GEN-LAST:event_btnVolverActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSButtonShape btnVolver;
    private RSMaterialComponent.RSComboBoxMaterial cmbCategoria;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private RSMaterialComponent.RSTableMetroCustom tablaM;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtBuscar;
    // End of variables declaration//GEN-END:variables
}
