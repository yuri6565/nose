/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista;

import controlador.Ctrl_Cliente;
import controlador.Ctrl_Proveedor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import modelo.ProveedorDatos;
import rojeru_san.efectos.ValoresEnum;
import rojerusan.RSLabelIcon;

/**
 *
 * @author ZenBook
 */
public class Proveedor extends javax.swing.JPanel {

    private int id_proveedor;
    private Ctrl_Proveedor proveedorContro;
    private int currentPage = 0;
    private final int PROVEEDORES_POR_PAGINA = 19;
    private List<ProveedorDatos> todosLosProveedores = new ArrayList<>();
    private boolean[] seleccionados;
    private int idproveedor;

    public Proveedor(JFrame jFrame, boolean par) {
        proveedorContro = new Ctrl_Proveedor();
        initComponents();
        SwingUtilities.invokeLater(() -> {
            cargartablaproveedores();
            rSCheckBox1.addActionListener(e -> seleccionarTodo());
            inicializarPopupFiltrosAvanzados();
            System.out.println("tablaclientes initialized with " + tablaclientes.getColumnCount() + " columns");
        });
        aplicarTema();
        TemaManager.getInstance().addThemeChangeListener(this::aplicarTema);
    }

    public void cargartablaproveedores() {
        tablaclientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return columna == 0 || columna == 8 || columna == 9; // "Seleccionar", "Productos", and "Acciones"
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                if (columnIndex == 8) {
                    return ProductCell.class;
                }
                return String.class;
            }
        };

        model.addColumn("Seleccionar");
        model.addColumn("Código");
        model.addColumn("Nombres");
        model.addColumn("Email");
        model.addColumn("Teléfono");
        model.addColumn("Dirección");
        model.addColumn("Estado");
        model.addColumn("Ubicación");
        model.addColumn("Productos");
        model.addColumn("Acciones");

        List<ProveedorDatos> proveedores = proveedorContro.obtenerProveedoresConProductos();
        todosLosProveedores = new ArrayList<>(proveedores);
        System.out.println("Número de proveedores cargados: " + todosLosProveedores.size());

        seleccionados = new boolean[todosLosProveedores.size()];

        for (ProveedorDatos proveedor : todosLosProveedores) {
            List<String> productos = proveedor.getProductos();
            if (productos == null || productos.isEmpty()) {
                productos = proveedorContro.obtenerProductosDeProveedor(proveedor.getId_proveedor());
            }
            String productosResumen = (productos != null && !productos.isEmpty())
                    ? (productos.size() > 2 ? String.join(", ", productos.subList(0, 2)) + " + (" + (productos.size() - 2) + " más)" : String.join(", ", productos))
                    : "Sin productos";
            String ubicacion = (proveedor.getDepartamento() != null ? proveedor.getDepartamento() : "Sin departamento") + "/"
                    + (proveedor.getMunicipio() != null ? proveedor.getMunicipio() : "Sin municipio");
            String nombreCompleto = (proveedor.getNombre() != null ? proveedor.getNombre() : "Sin nombre") + " "
                    + (proveedor.getApellido() != null ? proveedor.getApellido() : "Sin apellido");
            model.addRow(new Object[]{
                false,
                proveedor.getId_proveedor(),
                nombreCompleto,
                proveedor.getCorreo_electronico(),
                proveedor.getTelefono(),
                proveedor.getDireccion(),
                proveedor.getEstado(), // Ensure this is "Activo" or "Inactivo"
                ubicacion,
                new ProductCell(productosResumen, productos),
                "Ver Productos"
            });
        }

        tablaclientes.setModel(model);
        mostrarPagina(currentPage);

        // Configurar renderers y editores
        tablaclientes.getColumnModel().getColumn(0).setCellRenderer(new CustomCheckboxRenderer());
        tablaclientes.getColumnModel().getColumn(0).setCellEditor(new CustomCheckboxEditor());
        tablaclientes.getColumnModel().getColumn(6).setCellRenderer(new EstadoTableCellRenderer()); // Add renderer for Estado
        tablaclientes.getColumnModel().getColumn(8).setCellRenderer(new ProductCellRenderer());
        tablaclientes.getColumnModel().getColumn(8).setCellEditor(new ProductCellEditor());
        tablaclientes.getColumnModel().getColumn(9).setCellRenderer(new ButtonPanelRenderer());
        tablaclientes.getColumnModel().getColumn(9).setCellEditor(new ButtonPanelEditor(new JCheckBox()));

        // Ajustar anchos basados en la imagen (aproximados en píxeles)
        tablaclientes.getColumnModel().getColumn(0).setPreferredWidth(120);
        tablaclientes.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaclientes.getColumnModel().getColumn(2).setPreferredWidth(250);
        tablaclientes.getColumnModel().getColumn(3).setPreferredWidth(240);
        tablaclientes.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaclientes.getColumnModel().getColumn(5).setPreferredWidth(180);
        tablaclientes.getColumnModel().getColumn(6).setPreferredWidth(80); // Adjusted for Estado
        tablaclientes.getColumnModel().getColumn(7).setPreferredWidth(200);
        tablaclientes.getColumnModel().getColumn(8).setPreferredWidth(115);
        tablaclientes.getColumnModel().getColumn(9).setPreferredWidth(80);
        tablaclientes.setRowHeight(29);

        tablaclientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila_point = tablaclientes.rowAtPoint(e.getPoint());
                if (fila_point > -1) {
                    try {
                        id_proveedor = Integer.parseInt(tablaclientes.getValueAt(fila_point, 1).toString());
                    } catch (NumberFormatException ex) {
                        System.out.println("Error al parsear id_proveedor: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private class EstadoTableCellRenderer extends DefaultTableCellRenderer {

        public EstadoTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            boolean oscuro = TemaManager.getInstance().isOscuro();
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(CENTER);
            label.setText(value != null ? value.toString() : "");

            if (isSelected) {
                label.setForeground(oscuro ? Color.WHITE : Color.BLACK);
                label.setBackground(oscuro ? new Color(67, 71, 120) : table.getSelectionBackground());
            } else {
                label.setForeground(oscuro ? Color.WHITE : Color.BLACK);

                String estado = value != null ? value.toString() : "";
                if (oscuro) {
                    switch (estado.toLowerCase()) {
                        case "inactivo":
                            label.setBackground(new Color(153, 0, 51)); // Rojo oscuro
                            break;

                        case "activo":
                            label.setBackground(new Color(31, 123, 21)); // Verde oscuro
                            break;
                        default:
                            label.setBackground(new Color(37, 37, 52));
                            break;
                    }
                } else {
                    switch (estado.toLowerCase()) {
                        case "inactivo":
                            label.setBackground(new Color(255, 204, 204)); // Rojo claro
                            break;

                        case "activo":
                            label.setBackground(new Color(204, 255, 204)); // Verde claro
                            break;
                        default:
                            label.setBackground(Color.WHITE);
                            break;
                    }
                }
            }

            label.setBorder(BorderFactory.createLineBorder(oscuro ? new Color(153, 153, 153) : new Color(153, 153, 153), 1));
            return label;
        }
    }

    private void mostrarPagina(int pagina) {
        DefaultTableModel model = (DefaultTableModel) tablaclientes.getModel();
        model.setRowCount(0);

        int inicio = pagina * PROVEEDORES_POR_PAGINA;
        int fin = Math.min(inicio + PROVEEDORES_POR_PAGINA, todosLosProveedores.size());

        if (inicio >= todosLosProveedores.size()) {
            currentPage = 0;
            inicio = 0;
            fin = Math.min(PROVEEDORES_POR_PAGINA, todosLosProveedores.size());
        }

        for (int i = inicio; i < fin; i++) {
            ProveedorDatos proveedor = todosLosProveedores.get(i);
            List<String> productos = proveedor.getProductos();
            if (productos == null || productos.isEmpty()) {
                productos = proveedorContro.obtenerProductosDeProveedor(proveedor.getId_proveedor());
            }
            String productosResumen = (productos != null && !productos.isEmpty())
                    ? (productos.size() > 2 ? String.join(", ", productos.subList(0, 2)) + " + (" + (productos.size() - 2) + " más)" : String.join(", ", productos))
                    : "Sin productos";
            String ubicacion = (proveedor.getDepartamento() != null ? proveedor.getDepartamento() : "") + "/"
                    + (proveedor.getMunicipio() != null ? proveedor.getMunicipio() : "");
            String nombreCompleto = (proveedor.getNombre() != null ? proveedor.getNombre() : "") + " "
                    + (proveedor.getApellido() != null ? proveedor.getApellido() : "");
            model.addRow(new Object[]{
                seleccionados[i],
                proveedor.getId_proveedor(),
                nombreCompleto,
                proveedor.getCorreo_electronico() != null ? proveedor.getCorreo_electronico() : "",
                proveedor.getTelefono() != null ? proveedor.getTelefono() : "",
                proveedor.getDireccion() != null ? proveedor.getDireccion() : "",
                proveedor.getEstado() != null ? proveedor.getEstado() : "",
                ubicacion,
                new ProductCell(productosResumen, productos), // Store ProductCell for editor
                "Ver Productos"
            });
        }

        int totalPaginas = (int) Math.ceil((double) todosLosProveedores.size() / PROVEEDORES_POR_PAGINA);
        paginacion.setText("Página " + (currentPage + 1) + " de " + totalPaginas);
        Añadir5.setEnabled(currentPage > 0);
        Añadir4.setEnabled(currentPage < totalPaginas - 1);
    }

    private void seleccionarTodo() {
        DefaultTableModel model = (DefaultTableModel) tablaclientes.getModel();
        boolean seleccionado = rSCheckBox1.isSelected();
        int inicio = currentPage * PROVEEDORES_POR_PAGINA;
        int fin = Math.min(inicio + PROVEEDORES_POR_PAGINA, todosLosProveedores.size());

        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(seleccionado, i, 0);
            seleccionados[inicio + i] = seleccionado;
        }
    }

    class ButtonPanelRenderer extends JPanel implements TableCellRenderer {

        private RSLabelIcon editIcon;
        private RSLabelIcon deleteIcon;

        public ButtonPanelRenderer() {
            setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));
            setOpaque(true);

            editIcon = new RSLabelIcon();
            editIcon.setIcons(ValoresEnum.ICONS.EDIT);
            editIcon.setToolTipText("Editar");
            editIcon.setPreferredSize(new Dimension(20, 20));

            deleteIcon = new RSLabelIcon();
            deleteIcon.setIcons(ValoresEnum.ICONS.DELETE);
            deleteIcon.setToolTipText("Eliminar");
            deleteIcon.setPreferredSize(new Dimension(20, 20));

            add(editIcon);
            add(deleteIcon);

            updateTheme();
            TemaManager.getInstance().addThemeChangeListener(this::updateTheme);
        }

        private void updateTheme() {
            boolean oscuro = TemaManager.getInstance().isOscuro();
            Color fondo = oscuro ? new Color(21, 21, 33) : Color.WHITE;
            setBackground(fondo);
            editIcon.setBackground(oscuro ? new Color(67, 71, 120) : new Color(46, 49, 82));
            deleteIcon.setBackground(oscuro ? new Color(67, 71, 120) : new Color(46, 49, 82));
            editIcon.setForeground(new Color(29, 30, 81));
            deleteIcon.setForeground(new Color(29, 30, 81));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(new Color(240, 240, 240));
            } else {
                updateTheme();
            }
            return this;
        }
    }

    class CustomCheckboxRenderer extends JCheckBox implements TableCellRenderer {

        public CustomCheckboxRenderer() {
            setHorizontalAlignment(CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            boolean oscuro = TemaManager.getInstance().isOscuro();
            Color fondo = oscuro ? new Color(21, 21, 33) : Color.WHITE;
            Color borde = oscuro ? new Color(100, 100, 150) : new Color(180, 180, 180);
            Color seleccion = oscuro ? new Color(118, 142, 240) : new Color(72, 92, 188);

            if (isSelected) {
                setBackground(new Color(240, 240, 240));
            } else {
                setBackground(fondo);
            }

            setBorderPainted(true);
            setForeground(seleccion);
            setSelected(Boolean.TRUE.equals(value));
            setBorder(javax.swing.BorderFactory.createLineBorder(borde));
            return this;
        }
    }

    class CustomCheckboxEditor extends DefaultCellEditor {

        private final JCheckBox checkBox;

        public CustomCheckboxEditor() {
            super(new JCheckBox());
            checkBox = (JCheckBox) getComponent();
            checkBox.setOpaque(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            boolean oscuro = TemaManager.getInstance().isOscuro();
            checkBox.setBackground(oscuro ? new Color(21, 21, 33) : Color.WHITE);
            checkBox.setSelected(Boolean.TRUE.equals(value));
            return checkBox;
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }
    }

    class ButtonPanelEditor extends DefaultCellEditor {

        private JPanel panel;
        private RSLabelIcon editIcon;
        private RSLabelIcon deleteIcon;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonPanelEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel();
            panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));
            panel.setOpaque(true);

            editIcon = new RSLabelIcon();
            editIcon.setIcons(ValoresEnum.ICONS.EDIT);
            editIcon.setToolTipText("Editar");
            editIcon.setPreferredSize(new Dimension(20, 20));

            deleteIcon = new RSLabelIcon();
            deleteIcon.setIcons(ValoresEnum.ICONS.DELETE);
            deleteIcon.setToolTipText("Eliminar");
            deleteIcon.setPreferredSize(new Dimension(20, 20));

            panel.add(editIcon);
            panel.add(deleteIcon);

            editIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    isPushed = true;
                    fireEditingStopped();
                    try {
                        String codigoStr = tablaclientes.getValueAt(selectedRow, 1).toString();
                        if (codigoStr == null || codigoStr.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(Proveedor.this, "Código de proveedor no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        int codigo = Integer.parseInt(codigoStr);
                        ProveedorDatos proveedor = proveedorContro.obtenerProveedorPorid(codigo);
                        if (proveedor != null) {
                            proveedorEditar dialog = new proveedorEditar((JFrame) javax.swing.SwingUtilities.getWindowAncestor(Proveedor.this), true, codigo);
                            dialog.setLocationRelativeTo(null);
                            dialog.setVisible(true);
                            if (dialog.isGuardado()) {
                                cargartablaproveedores();
                            }
                        } else {
                            JOptionPane.showMessageDialog(Proveedor.this, "No se encontró el proveedor con código: " + codigo, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(Proveedor.this, "El código del proveedor no es un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Proveedor.this, "Error al editar el proveedor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            deleteIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    isPushed = true;
                    fireEditingStopped();
                    try {
                        int codigo = Integer.parseInt(tablaclientes.getValueAt(selectedRow, 1).toString());
                        if (proveedorContro.tieneProductos(codigo)) {
                            int opcion = JOptionPane.showConfirmDialog(Proveedor.this,
                                    "Este proveedor tiene productos asociados. ¿Desea marcarlo como inactivo?",
                                    "Proveedor con Productos", JOptionPane.YES_NO_OPTION);
                            if (opcion == JOptionPane.YES_OPTION) {
                                if (proveedorContro.desactivar(codigo)) {
                                    JOptionPane.showMessageDialog(Proveedor.this, "Proveedor marcado como inactivo.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                    cargartablaproveedores();
                                } else {
                                    JOptionPane.showMessageDialog(Proveedor.this, "Error al marcar como inactivo.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            int confirm = JOptionPane.showConfirmDialog(Proveedor.this,
                                    "¿Está seguro de que desea eliminar el proveedor con código " + codigo + "?",
                                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                if (proveedorContro.eliminar(codigo)) {
                                    JOptionPane.showMessageDialog(Proveedor.this, "Proveedor eliminado correctamente");
                                    cargartablaproveedores();
                                } else {
                                    JOptionPane.showMessageDialog(Proveedor.this, "Error al eliminar el proveedor");
                                }
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Proveedor.this, "Error al eliminar el proveedor: " + ex.getMessage());
                    }
                }
            });

            updateTheme();
            TemaManager.getInstance().addThemeChangeListener(this::updateTheme);
        }

        private void updateTheme() {
            boolean oscuro = TemaManager.getInstance().isOscuro();
            Color fondo = oscuro ? new Color(21, 21, 33) : Color.WHITE;
            panel.setBackground(fondo);
            editIcon.setBackground(oscuro ? new Color(67, 71, 120) : new Color(46, 49, 82));
            deleteIcon.setBackground(oscuro ? new Color(67, 71, 120) : new Color(46, 49, 82));
            editIcon.setForeground(new Color(29, 30, 81));
            deleteIcon.setForeground(new Color(29, 30, 81));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            isPushed = true;
            selectedRow = row;
            if (row == table.getSelectedRow()) {
                panel.setBackground(new Color(240, 240, 240));
            } else {
                updateTheme();
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
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
            paginacion.setBackground(texto);
            rSCheckBox1.setColorCheck(new Color(21, 21, 33));
            rSButtonMaterialRippleIcon1.setBackground(new Color(21, 21, 33));
            tablaclientes.setBackground(new Color(21, 21, 33));
            tablaclientes.setBackgoundHead(new Color(67, 71, 120));
            tablaclientes.setForegroundHead(new Color(255, 255, 255));
            tablaclientes.setBackgoundHover(new Color(40, 50, 90));
            tablaclientes.setFont(new Font("Tahoma", Font.PLAIN, 15));
            tablaclientes.setColorPrimary(new Color(37, 37, 52));
            tablaclientes.setColorPrimaryText(texto);
            tablaclientes.setColorSecondary(new Color(30, 30, 45));
            tablaclientes.setColorSecundaryText(texto);
            tablaclientes.setColorBorderHead(primario);
            tablaclientes.setColorBorderRows(fondo.darker());
            tablaclientes.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            tablaclientes.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            tablaclientes.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));
            tablaclientes.setEffectHover(true);
            tablaclientes.setShowGrid(true);
            tablaclientes.setGridColor(Color.WHITE);

            btnNuevo1.setBackground(new Color(67, 71, 120));
            btnNuevo1.setBackgroundHover(new Color(118, 142, 240));
            btnNuevo2.setBackground(new Color(67, 71, 120));
            btnNuevo2.setBackgroundHover(new Color(118, 142, 240));
        } else {
            Color fondo = new Color(242, 247, 255);
            Color texto = Color.BLACK;
            Color primario = new Color(72, 92, 188);
            paginacion.setBackground(texto);
            rSButtonMaterialRippleIcon1.setBackground(new Color(242, 247, 255));

            jPanel1.setBackground(fondo);
            txtBuscar.setBackground(fondo);
            txtBuscar.setForeground(texto);
            txtBuscar.setColorIcon(texto);
            txtBuscar.setPhColor(Color.GRAY);

            tablaclientes.setBackground(new Color(255, 255, 255));
            tablaclientes.setBackgoundHead(new Color(46, 49, 82));
            tablaclientes.setForegroundHead(Color.WHITE);
            tablaclientes.setBackgoundHover(new Color(67, 150, 209));
            tablaclientes.setFont(new Font("Tahoma", Font.PLAIN, 15));
            tablaclientes.setColorPrimary(new Color(242, 242, 242));
            tablaclientes.setColorPrimaryText(texto);
            tablaclientes.setColorSecondary(new Color(255, 255, 255));
            tablaclientes.setColorSecundaryText(texto);
            tablaclientes.setColorBorderHead(primario);
            tablaclientes.setColorBorderRows(new Color(0, 0, 0));
            tablaclientes.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            tablaclientes.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            tablaclientes.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));
            tablaclientes.setEffectHover(true);
            tablaclientes.setSelectionBackground(new Color(67, 150, 209));
            tablaclientes.setShowGrid(true);
            tablaclientes.setGridColor(Color.BLACK);

            btnNuevo1.setBackground(new Color(46, 49, 82));
            btnNuevo2.setBackground(new Color(46, 49, 82));
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
        btnNuevo1 = new RSMaterialComponent.RSButtonShape();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaclientes = new RSMaterialComponent.RSTableMetroCustom();
        txtBuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        btnNotificacion1 = new rojerusan.RSLabelIcon();
        Añadir4 = new rojeru_san.RSButtonRiple();
        rSButtonMaterialRippleIcon1 = new RSMaterialComponent.RSButtonMaterialRippleIcon();
        btnNuevo2 = new RSMaterialComponent.RSButtonShape();
        Añadir5 = new rojeru_san.RSButtonRiple();
        rSCheckBox1 = new rojerusan.RSCheckBox();
        paginacion = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1290, 730));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(241, 245, 253));
        jPanel1.setPreferredSize(new java.awt.Dimension(960, 570));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnNuevo1.setBackground(new java.awt.Color(46, 49, 82));
        btnNuevo1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnNuevo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus (2).png"))); // NOI18N
        btnNuevo1.setText(" Nuevo");
        btnNuevo1.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnNuevo1.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnNuevo1.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnNuevo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNuevo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevo1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnNuevo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 60, 110, 30));

        tablaclientes.setForeground(new java.awt.Color(255, 255, 255));
        tablaclientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Seleccionar", "Codigo", "Tipo. Doc", "Nombre", "Correo Electronico", "Telefono", "Direccion", "Estado", "Producto", "Acciones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaclientes.setToolTipText("");
        tablaclientes.setBackgoundHead(new java.awt.Color(255, 255, 51));
        tablaclientes.setBackgoundHover(new java.awt.Color(51, 255, 51));
        tablaclientes.setBorderHead(null);
        tablaclientes.setBorderRows(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        tablaclientes.setColorBorderHead(new java.awt.Color(102, 102, 255));
        tablaclientes.setColorBorderRows(new java.awt.Color(255, 102, 102));
        tablaclientes.setColorPrimary(new java.awt.Color(153, 255, 153));
        tablaclientes.setColorPrimaryText(new java.awt.Color(0, 0, 0));
        tablaclientes.setColorSecondary(new java.awt.Color(0, 204, 153));
        tablaclientes.setColorSecundaryText(new java.awt.Color(30, 30, 45));
        tablaclientes.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        tablaclientes.setFontHead(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        tablaclientes.setFontRowHover(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        tablaclientes.setFontRowSelect(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        tablaclientes.setGridColor(new java.awt.Color(102, 255, 102));
        tablaclientes.setPreferredSize(new java.awt.Dimension(500, 500));
        tablaclientes.setSelectionBackground(new java.awt.Color(67, 150, 209));
        jScrollPane3.setViewportView(tablaclientes);
        tablaclientes.getColumnModel().getColumn(0).setPreferredWidth(10);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 1200, 530));

        txtBuscar.setBackground(new java.awt.Color(242, 247, 255));
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
        jPanel1.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 430, 40));

        btnNotificacion1.setBackground(new java.awt.Color(255, 255, 255));
        btnNotificacion1.setForeground(new java.awt.Color(255, 255, 255));
        btnNotificacion1.setIcons(rojeru_san.efectos.ValoresEnum.ICONS.TUNE);
        btnNotificacion1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNotificacion1MouseClicked(evt);
            }
        });
        jPanel1.add(btnNotificacion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 60, -1, -1));

        Añadir4.setBackground(new java.awt.Color(46, 49, 82));
        Añadir4.setText("Siguiente");
        Añadir4.setColorHover(new java.awt.Color(0, 153, 51));
        Añadir4.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        Añadir4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Añadir4ActionPerformed(evt);
            }
        });
        jPanel1.add(Añadir4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 680, 98, 40));

        rSButtonMaterialRippleIcon1.setBackground(new java.awt.Color(102, 102, 102));
        rSButtonMaterialRippleIcon1.setForeground(new java.awt.Color(253, 126, 20));
        rSButtonMaterialRippleIcon1.setBackgroundHover(new java.awt.Color(242, 247, 255));
        rSButtonMaterialRippleIcon1.setForegroundHover(new java.awt.Color(255, 51, 51));
        rSButtonMaterialRippleIcon1.setForegroundIcon(new java.awt.Color(255, 51, 51));
        rSButtonMaterialRippleIcon1.setForegroundIconHover(new java.awt.Color(255, 51, 51));
        rSButtonMaterialRippleIcon1.setForegroundText(new java.awt.Color(255, 51, 51));
        rSButtonMaterialRippleIcon1.setIcons(rojeru_san.efectos.ValoresEnum.ICONS.DELETE);
        rSButtonMaterialRippleIcon1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSButtonMaterialRippleIcon1ActionPerformed(evt);
            }
        });
        jPanel1.add(rSButtonMaterialRippleIcon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 680, 40, 40));

        btnNuevo2.setBackground(new java.awt.Color(67, 94, 190));
        btnNuevo2.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnNuevo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus (2).png"))); // NOI18N
        btnNuevo2.setText(" Nuevo");
        btnNuevo2.setBackgroundHover(new java.awt.Color(118, 142, 240));
        btnNuevo2.setFocusable(false);
        btnNuevo2.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnNuevo2.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnNuevo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNuevo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevo2ActionPerformed(evt);
            }
        });
        jPanel1.add(btnNuevo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 60, 110, 30));

        Añadir5.setBackground(new java.awt.Color(46, 49, 82));
        Añadir5.setText("Anterior");
        Añadir5.setColorHover(new java.awt.Color(0, 153, 51));
        Añadir5.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        Añadir5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Añadir5ActionPerformed(evt);
            }
        });
        jPanel1.add(Añadir5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 680, 90, 40));

        rSCheckBox1.setForeground(new java.awt.Color(102, 102, 255));
        rSCheckBox1.setText("Seleccionar Todo");
        jPanel1.add(rSCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 680, 190, 20));

        paginacion.setBackground(new java.awt.Color(0, 0, 0));
        paginacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        paginacion.setForeground(new java.awt.Color(51, 51, 51));
        paginacion.setText("Escritorio");
        paginacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paginacionMouseClicked(evt);
            }
        });
        jPanel1.add(paginacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 680, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1340, 730));
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevo1ActionPerformed
        proveedornuevo dialog = new proveedornuevo(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargartablaproveedores();
        }
    }//GEN-LAST:event_btnNuevo1ActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        String textoBusqueda = txtBuscar.getText().trim();
        if (textoBusqueda.isEmpty()) {
            cargartablaproveedores();
        } else {
            cargartablaproveedoresFiltrado(textoBusqueda);
        }

    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnNotificacion1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNotificacion1MouseClicked
        // TODO add your handling code here:
        inicializarPopupFiltrosAvanzados(); // Re-inicializar para actualizar valores
        popupFiltrosAvanzados.show(btnNotificacion1, evt.getX(), evt.getY());

    }//GEN-LAST:event_btnNotificacion1MouseClicked

    private void Añadir4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Añadir4ActionPerformed
        int totalPaginas = (int) Math.ceil((double) todosLosProveedores.size() / PROVEEDORES_POR_PAGINA);
        if (currentPage < totalPaginas - 1) {
            currentPage++;
            mostrarPagina(currentPage);
        }
    }//GEN-LAST:event_Añadir4ActionPerformed

    private void rSButtonMaterialRippleIcon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSButtonMaterialRippleIcon1ActionPerformed
        List<Integer> idsAEliminar = new ArrayList<>();
        List<String> nombresProveedoresAEliminar = new ArrayList<>();
        List<Integer> proveedoresConProductos = new ArrayList<>();
        List<String> proveedoresConProductosNombres = new ArrayList<>();

        DefaultTableModel model = (DefaultTableModel) tablaclientes.getModel();
        int inicio = currentPage * PROVEEDORES_POR_PAGINA;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                int id = Integer.parseInt(model.getValueAt(i, 1).toString());
                String nombre = model.getValueAt(i, 2).toString();
                if (proveedorContro.tieneProductos(id)) {
                    proveedoresConProductos.add(id);
                    proveedoresConProductosNombres.add(nombre);
                } else {
                    idsAEliminar.add(id);
                    nombresProveedoresAEliminar.add(nombre);
                }
                seleccionados[inicio + i] = false;
            }
        }

        if (!proveedoresConProductos.isEmpty()) {
            String mensaje = "No se pueden eliminar los siguientes proveedores porque tienen productos asociados:\n"
                    + String.join(", ", proveedoresConProductosNombres)
                    + "\n¿Desea marcarlos como inactivos?";
            int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Proveedores con Productos", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                for (Integer id : proveedoresConProductos) {
                    if (proveedorContro.desactivar(id)) {
                        JOptionPane.showMessageDialog(this, "Proveedor(es) marcados como inactivo(s).", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al marcar proveedor(es) como inactivo(s).", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        if (!idsAEliminar.isEmpty()) {
            String mensaje = "¿Está seguro que desea eliminar "
                    + (idsAEliminar.size() > 1 ? "estos " + idsAEliminar.size() + " proveedores?" : "este proveedor?")
                    + "\nProveedores: " + String.join(", ", nombresProveedoresAEliminar);
            int confirm = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean todasEliminadas = true;
                for (Integer id : idsAEliminar) {
                    if (!proveedorContro.eliminar(id)) {
                        todasEliminadas = false;
                        JOptionPane.showMessageDialog(this, "Error al eliminar el proveedor con ID: " + id);
                    }
                }
                if (todasEliminadas) {
                    JOptionPane.showMessageDialog(this, "Proveedor(es) eliminado(s) correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                todosLosProveedores.removeIf(proveedor -> idsAEliminar.contains(proveedor.getId_proveedor()));
                mostrarPagina(currentPage);
            }
        } else if (proveedoresConProductos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay proveedores seleccionados para eliminar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
        }

        rSCheckBox1.setSelected(false);

    }//GEN-LAST:event_rSButtonMaterialRippleIcon1ActionPerformed

    private void btnNuevo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevo2ActionPerformed
//        crear_cliente dialog = new crear_cliente(new javax.swing.JFrame(), true);
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//
//        if (dialog.isGuardado()) {
//            cargartablaclientes(); // Use the correct method with all columns
//        }
    }//GEN-LAST:event_btnNuevo2ActionPerformed

    private void Añadir5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Añadir5ActionPerformed
        if (currentPage > 0) {
            currentPage--;
            mostrarPagina(currentPage);
        }
    }//GEN-LAST:event_Añadir5ActionPerformed

    private void paginacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paginacionMouseClicked
        // TODO add your handling code here: 777777
    }//GEN-LAST:event_paginacionMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojeru_san.RSButtonRiple Añadir4;
    private rojeru_san.RSButtonRiple Añadir5;
    private rojerusan.RSLabelIcon btnNotificacion1;
    private RSMaterialComponent.RSButtonShape btnNuevo1;
    private RSMaterialComponent.RSButtonShape btnNuevo2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel paginacion;
    private RSMaterialComponent.RSButtonMaterialRippleIcon rSButtonMaterialRippleIcon1;
    private rojerusan.RSCheckBox rSCheckBox1;
    private RSMaterialComponent.RSTableMetroCustom tablaclientes;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtBuscar;
    // End of variables declaration//GEN-END:variables

    private void cargartablaproveedoresFiltrado(String textoBusqueda) {
        DefaultTableModel model = (DefaultTableModel) tablaclientes.getModel();
        sorter = new TableRowSorter<>(model);
        tablaclientes.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filtros = new ArrayList<>();

        // Filtro de búsqueda por texto (columna "Nombres")
        if (!textoBusqueda.trim().isEmpty()) {
            try {
                filtros.add(RowFilter.regexFilter("(?i)" + textoBusqueda, 2));
            } catch (Exception e) {
                System.out.println("Error al aplicar filtro de búsqueda: " + e.getMessage());
            }
        }

        // Filtros avanzados
        List<String> filtrosEstado = new ArrayList<>();
        for (JCheckBox chk : chkEstados) {
            if (chk.isSelected()) {
                filtrosEstado.add(chk.getText().replace("Estado: ", ""));
            }
        }

        List<String> filtrosDepartamento = new ArrayList<>();
        for (JCheckBox chk : chkDepartamentos) {
            if (chk.isSelected()) {
                filtrosDepartamento.add(chk.getText().replace("Departamento: ", ""));
            }
        }

        List<String> filtrosProducto = new ArrayList<>();
        for (JCheckBox chk : chkProductos) {
            if (chk.isSelected()) {
                filtrosProducto.add(chk.getText().replace("Producto: ", ""));
            }
        }

        if (!filtrosEstado.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)^(" + String.join("|", filtrosEstado) + ")$", 6));
        }

        if (!filtrosDepartamento.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)^(" + String.join("|", filtrosDepartamento) + ").*", 7));
        }

        if (!filtrosProducto.isEmpty()) {
            filtros.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    Object value = entry.getValue(8);
                    if (!(value instanceof ProductCell)) {
                        return false;
                    }
                    ProductCell cell = (ProductCell) value;
                    List<String> productos = cell.getFullList();
                    if (productos == null || productos.isEmpty()) {
                        return false;
                    }
                    for (String producto : filtrosProducto) {
                        if (productos.contains(producto)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        if (!filtros.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filtros));
        } else {
            sorter.setRowFilter(null);
        }

        mostrarPagina(currentPage);
    }

    public void cargartablacliente() {
        tablaclientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Nombre");
        model.addColumn("Correo Electrónico");
        model.addColumn("Teléfono");
        model.addColumn("Dirección");
        model.addColumn("Producto");

        List<modelo.ProveedorDatos> proveedores = proveedorContro.obtenerProveedoresConProductos();
        for (modelo.ProveedorDatos proveedor : proveedores) {
            Object[] fila = new Object[6];
            fila[0] = proveedor.getId_proveedor();
            fila[1] = proveedor.getNombre() != null ? proveedor.getNombre() : "Sin nombre";
            fila[2] = proveedor.getCorreo_electronico() != null ? proveedor.getCorreo_electronico() : "Sin correo";
            fila[3] = proveedor.getTelefono() != null ? proveedor.getTelefono() : "Sin teléfono";
            fila[4] = proveedor.getDireccion() != null ? proveedor.getDireccion() : "Sin dirección";
            fila[5] = obtenerProductosPorProveedor(proveedor.getId_proveedor());
            model.addRow(fila);
        }

        tablaclientes.setModel(model);
        System.out.println("Número de filas en el modelo después de cargar: " + model.getRowCount());

        // Listener para capturar la selección de una fila
        tablaclientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila_point = tablaclientes.rowAtPoint(e.getPoint());
                if (fila_point > -1) {
                    id_proveedor = (int) tablaclientes.getValueAt(fila_point, 0); // Usa el campo de la clase
                }
            }
        });
    }

    private String obtenerProductosPorProveedor(int idProveedor) {
        String[] productos = null; // Inicializa como array para evitar null
        try {
            productos = proveedorContro.obtenerProductosDeProveedor(idProveedor).toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (productos != null) {
            return String.join(", ", productos); // Une los productos con comas
        }
        return "Sin productos";
    }

    private void cargartablaclienteFiltrado(String textoBusqueda) {
        tablaclientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Nombre");
        model.addColumn("Correo Electrónico");
        model.addColumn("Teléfono");
        model.addColumn("Dirección");
        model.addColumn("Producto");

        List<modelo.ProveedorDatos> proveedores = proveedorContro.obtenerProveedoresConProductos()
                .stream()
                .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase()))
                .toList();

        for (modelo.ProveedorDatos proveedor : proveedores) {
            Object[] fila = new Object[6];
            fila[0] = proveedor.getId_proveedor();
            fila[1] = proveedor.getNombre() != null ? proveedor.getNombre() : "Sin nombre";
            fila[2] = proveedor.getCorreo_electronico() != null ? proveedor.getCorreo_electronico() : "Sin correo";
            fila[3] = proveedor.getTelefono() != null ? proveedor.getTelefono() : "Sin teléfono";
            fila[4] = proveedor.getDireccion() != null ? proveedor.getDireccion() : "Sin dirección";
            fila[5] = obtenerProductosPorProveedor(proveedor.getId_proveedor());
            model.addRow(fila);
        }

        tablaclientes.setModel(model);
    }

    private static class ProductCell {

        private final String summary;
        private final List<String> fullList;

        public ProductCell(String summary, List<String> fullList) {
            this.summary = summary;
            this.fullList = fullList;
        }

        public String getSummary() {
            return summary;
        }

        public List<String> getFullList() {
            return fullList;
        }
    }

// Renderer for the "Productos" column
    class ProductCellRenderer extends JPanel implements TableCellRenderer {

        private final javax.swing.JLabel label;

        public ProductCellRenderer() {
            setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
            setOpaque(true);
            label = new javax.swing.JLabel();
            label.setFont(new Font("Tahoma", Font.PLAIN, 12));
            add(label);

            updateTheme();
            TemaManager.getInstance().addThemeChangeListener(this::updateTheme);
        }

        private void updateTheme() {
            boolean oscuro = TemaManager.getInstance().isOscuro();
            Color fondo = oscuro ? new Color(21, 21, 33) : Color.WHITE;
            Color texto = oscuro ? Color.WHITE : Color.BLACK;
            setBackground(fondo);
            label.setForeground(texto);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ProductCell) {
                ProductCell cell = (ProductCell) value;
                label.setText(cell.getSummary());
                if (isSelected) {
                    setBackground(new Color(240, 240, 240));
                    label.setForeground(Color.BLUE);
                } else {
                    updateTheme();
                }
            }
            return this;
        }
    }

// Editor for the "Productos" column (to handle click for "Ver más")
    class ProductCellEditor extends DefaultCellEditor {

        private final JPanel panel;
        private final javax.swing.JLabel label;
        private ProductCell currentCell;

        public ProductCellEditor() {
            super(new JCheckBox());
            panel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
            panel.setOpaque(true);
            label = new javax.swing.JLabel();
            label.setFont(new Font("Tahoma", Font.PLAIN, 12));
            label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (currentCell != null && currentCell.getFullList() != null && !currentCell.getFullList().isEmpty()) {
                        String fullList = String.join("\n", currentCell.getFullList());
                        JOptionPane.showMessageDialog(Proveedor.this, fullList, "Lista de Productos", JOptionPane.INFORMATION_MESSAGE);
                    }
                    fireEditingStopped();
                }
            });
            panel.add(label);

            updateTheme();
            TemaManager.getInstance().addThemeChangeListener(this::updateTheme);
        }

        private void updateTheme() {
            boolean oscuro = TemaManager.getInstance().isOscuro();
            Color fondo = oscuro ? new Color(21, 21, 33) : Color.WHITE;
            Color texto = oscuro ? Color.WHITE : Color.BLACK;
            panel.setBackground(fondo);
            label.setForeground(texto);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (value instanceof ProductCell) {
                currentCell = (ProductCell) value;
                label.setText(currentCell.getSummary());
                if (isSelected) {
                    panel.setBackground(new Color(240, 240, 240));
                    label.setForeground(Color.BLUE);
                } else {
                    updateTheme();
                }
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentCell;
        }
    }

// Popup menu para filtros avanzados
    // Popup menu para filtros avanzados
    private JPopupMenu popupFiltrosAvanzados;
    private List<JCheckBox> chkEstados = new ArrayList<>();
    private List<JCheckBox> chkDepartamentos = new ArrayList<>();
    private List<JCheckBox> chkProductos = new ArrayList<>();
    private rojeru_san.RSButtonRiple btnAplicarFiltros;
    private TableRowSorter<DefaultTableModel> sorter;

    private void inicializarPopupFiltrosAvanzados() {
        popupFiltrosAvanzados = new JPopupMenu();
        chkEstados.clear();
        chkDepartamentos.clear();
        chkProductos.clear();

        List<String> estados = new ArrayList<>();
        List<String> departamentos = new ArrayList<>();
        List<String> productos = new ArrayList<>();
        obtenerValoresUnicos(estados, departamentos, productos);

        for (String estado : estados) {
            JCheckBox chkEstado = new JCheckBox("Estado: " + estado);
            chkEstados.add(chkEstado);
            popupFiltrosAvanzados.add(chkEstado);
        }

        popupFiltrosAvanzados.addSeparator();

        for (String depto : departamentos) {
            JCheckBox chkDepto = new JCheckBox("Departamento: " + depto);
            chkDepartamentos.add(chkDepto);
            popupFiltrosAvanzados.add(chkDepto);
        }

        popupFiltrosAvanzados.addSeparator();

        for (String producto : productos) {
            JCheckBox chkProducto = new JCheckBox("Producto: " + producto);
            chkProductos.add(chkProducto);
            popupFiltrosAvanzados.add(chkProducto);
        }

        btnAplicarFiltros = new rojeru_san.RSButtonRiple();
        btnAplicarFiltros.setText("Aplicar");
        btnAplicarFiltros.setBackground(new Color(46, 49, 82));
        btnAplicarFiltros.setColorHover(new Color(0, 153, 51));
        popupFiltrosAvanzados.add(btnAplicarFiltros);

        boolean oscuro = TemaManager.getInstance().isOscuro();
        Color fondo = oscuro ? new Color(21, 21, 33) : Color.WHITE;
        Color texto = oscuro ? Color.WHITE : Color.BLACK;
        popupFiltrosAvanzados.setBackground(fondo);
        for (JCheckBox chk : chkEstados) {
            chk.setBackground(fondo);
            chk.setForeground(texto);
        }
        for (JCheckBox chk : chkDepartamentos) {
            chk.setBackground(fondo);
            chk.setForeground(texto);
        }
        for (JCheckBox chk : chkProductos) {
            chk.setBackground(fondo);
            chk.setForeground(texto);
        }

        btnAplicarFiltros.addActionListener(e -> {
            aplicarFiltrosAvanzados();
            popupFiltrosAvanzados.setVisible(false);
        });
    }

    private void obtenerValoresUnicos(List<String> estados, List<String> departamentos, List<String> productos) {
        estados.clear();
        departamentos.clear();
        productos.clear();
        Set<String> estadosSet = new HashSet<>();
        Set<String> departamentosSet = new HashSet<>();
        Set<String> productosSet = new HashSet<>();

        for (ProveedorDatos proveedor : todosLosProveedores) {
            if (proveedor.getEstado() != null && !proveedor.getEstado().isEmpty()) {
                estadosSet.add(proveedor.getEstado());
            }
            if (proveedor.getDepartamento() != null && !proveedor.getDepartamento().isEmpty()) {
                departamentosSet.add(proveedor.getDepartamento());
            }
            List<String> productosProveedor = proveedor.getProductos();
            if (productosProveedor == null || productosProveedor.isEmpty()) {
                productosProveedor = proveedorContro.obtenerProductosDeProveedor(proveedor.getId_proveedor());
            }
            if (productosProveedor != null && !productosProveedor.isEmpty()) {
                for (String producto : productosProveedor) {
                    if (producto != null && !producto.isEmpty()) {
                        productosSet.add(producto);
                    }
                }
            }
        }

        estados.addAll(estadosSet);
        departamentos.addAll(departamentosSet.stream().sorted().toList());
        productos.addAll(productosSet.stream().sorted().toList());
    }

    private void aplicarFiltrosAvanzados() {
        List<String> filtrosEstado = new ArrayList<>();
        for (JCheckBox chk : chkEstados) {
            if (chk.isSelected()) {
                filtrosEstado.add(chk.getText().replace("Estado: ", ""));
            }
        }

        List<String> filtrosDepartamento = new ArrayList<>();
        for (JCheckBox chk : chkDepartamentos) {
            if (chk.isSelected()) {
                filtrosDepartamento.add(chk.getText().replace("Departamento: ", ""));
            }
        }

        List<String> filtrosProducto = new ArrayList<>();
        for (JCheckBox chk : chkProductos) {
            if (chk.isSelected()) {
                filtrosProducto.add(chk.getText().replace("Producto: ", ""));
            }
        }

        DefaultTableModel model = (DefaultTableModel) tablaclientes.getModel();
        sorter = new TableRowSorter<>(model);
        tablaclientes.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filtros = new ArrayList<>();

        if (!filtrosEstado.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)^(" + String.join("|", filtrosEstado) + ")$", 6));
        }

        if (!filtrosDepartamento.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)^(" + String.join("|", filtrosDepartamento) + ").*", 7));
        }

        if (!filtrosProducto.isEmpty()) {
            filtros.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    Object value = entry.getValue(8);
                    if (!(value instanceof ProductCell)) {
                        return false;
                    }
                    ProductCell cell = (ProductCell) value;
                    List<String> productos = cell.getFullList();
                    if (productos == null || productos.isEmpty()) {
                        return false;
                    }
                    for (String producto : filtrosProducto) {
                        if (productos.contains(producto)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        if (!filtros.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filtros));
        } else {
            sorter.setRowFilter(null);
        }

        mostrarPagina(currentPage);
    }

}
