/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista;

import controlador.Ctrl_Cliente;
import controlador.Ctrl_Perfil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultCellEditor;
import static javax.swing.GroupLayout.Alignment.CENTER;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import rojeru_san.efectos.ValoresEnum;
import rojerusan.RSLabelIcon;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
/**
 *
 * @author ZenBook
 */
public class Usuarios1 extends javax.swing.JPanel {

    private int id_usuario;
    private Ctrl_Perfil controlador;
    private int idUsuario;
private int columnaFiltro = -1;



// In Usuarios1 class, add a field for the TableRowSorter
private TableRowSorter<DefaultTableModel> sorter;

// In the constructor or cargarTablaUsuarios, initialize the sorter
public Usuarios1(JFrame jFrame, boolean par) {
    controlador = new Ctrl_Perfil();
    this.id_usuario = -1;
    initComponents();
    // Immediately replace the model
    SwingUtilities.invokeLater(() -> {
        cargarTablaUsuarios();
        System.out.println("Table model initialized with " + tablaUsuarios.getColumnCount() + " columns");
    });
    inicializarMenuFiltros();
    aplicarTema();
    TemaManager.getInstance().addThemeChangeListener(this::aplicarTema);

    // Initialize sorter
    sorter = new TableRowSorter<>((DefaultTableModel) tablaUsuarios.getModel());
    tablaUsuarios.setRowSorter(sorter);

    // Add DocumentListener to txtBuscar for real-time filtering
    txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { filtrarUsuarios(); }
        @Override
        public void removeUpdate(DocumentEvent e) { filtrarUsuarios(); }
        @Override
        public void changedUpdate(DocumentEvent e) { filtrarUsuarios(); }
    });

}
public void cargarTablaUsuarios() {
    SwingUtilities.invokeLater(() -> {
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column < 0 || column >= getColumnCount() || row < 0 || row >= getRowCount()) {
                    System.err.println("Invalid access: row=" + row + ", column=" + column);
                    return false;
                }
                return column == 0 || column == 7;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
        };

        model.addColumn("Selec");
        model.addColumn("Código");
        model.addColumn("Nombre");
        model.addColumn("Apellido");
        model.addColumn("Usuario");
        model.addColumn("Correo Electrónico");
        model.addColumn("Rol");
        model.addColumn("Acciones");

        List<modelo.UsuarioModelo> usuarios = controlador.obtenerUsuarios();
        if (usuarios == null || usuarios.isEmpty()) {
            System.out.println("No users found, initializing empty table");
        } else {
            for (modelo.UsuarioModelo usuario : usuarios) {
                Object[] fila = new Object[8];
                fila[0] = false;
                fila[1] = usuario.getId_usuario();
                fila[2] = usuario.getNombre() != null ? usuario.getNombre() : "Sin nombre";
                fila[3] = usuario.getApellido() != null ? usuario.getApellido() : "Sin apellido";
                fila[4] = usuario.getUsuario() != null ? usuario.getUsuario() : "Sin usuario";
                fila[5] = usuario.getCorreo_electronico() != null ? usuario.getCorreo_electronico() : "Sin correo";
                fila[6] = usuario.getRol() != null ? usuario.getRol() : "Sin rol";
                fila[7] = "";
                model.addRow(fila);
            }
        }

        try {
            tablaUsuarios.setModel(model);
            System.out.println("Table model set: " + model.getColumnCount() + " columns, " + model.getRowCount() + " rows");
        } catch (Exception e) {
            System.err.println("Error setting table model: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            tablaUsuarios.getColumnModel().getColumn(0).setCellRenderer(new CustomCheckboxRenderer());
            tablaUsuarios.getColumnModel().getColumn(0).setCellEditor(new CustomCheckboxEditor());
            tablaUsuarios.getColumnModel().getColumn(7).setCellRenderer(new ButtonPanelRenderer());
            tablaUsuarios.getColumnModel().getColumn(7).setCellEditor(new ButtonPanelEditor(new JCheckBox()));

            tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(70);
            tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(90);
            tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(100);
            tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(100);
            tablaUsuarios.getColumnModel().getColumn(5).setPreferredWidth(250);
            tablaUsuarios.getColumnModel().getColumn(6).setPreferredWidth(100);
            tablaUsuarios.getColumnModel().getColumn(7).setPreferredWidth(80);
            tablaUsuarios.setRowHeight(24);
        } catch (Exception e) {
            System.err.println("Error configuring table columns: " + e.getMessage());
            e.printStackTrace();
        }

        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila_point = tablaUsuarios.rowAtPoint(e.getPoint());
                if (fila_point > -1 && fila_point < tablaUsuarios.getRowCount() && tablaUsuarios.getColumnCount() > 1) {
                    id_usuario = (int) tablaUsuarios.getValueAt(fila_point, 1);
                    System.out.println("Selected user ID: " + id_usuario);
                } else {
                    System.err.println("Invalid row selection: row=" + fila_point);
                }
            }
        });

        sorter = new TableRowSorter<>(model);
        tablaUsuarios.setRowSorter(sorter);
    });
}

// In ButtonPanelRenderer
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
        editIcon.setForeground(new Color(21, 21, 33)); // Green for edit icon
         deleteIcon.setForeground(new Color(29, 30, 81)); // Color azul que pediste

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

// In ButtonPanelEditor
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
                    int idUsuario = (int) tablaUsuarios.getValueAt(selectedRow, 1);
                    editar_usuario2 dialog = new editar_usuario2(new JFrame(), true, idUsuario,
                            (DefaultTableModel) tablaUsuarios.getModel(), selectedRow);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    if (dialog.isGuardado()) {
                        cargarTablaUsuarios();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Usuarios1.this, "Error al abrir el formulario de edición: " + ex.getMessage());
                }
            }
        });

        deleteIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPushed = true;
                fireEditingStopped();
                try {
                    int idUsuario = (int) tablaUsuarios.getValueAt(selectedRow, 1);
                    int confirm = JOptionPane.showConfirmDialog(Usuarios1.this,
                            "¿Está seguro de que desea eliminar este usuario?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Ctrl_Perfil controlador = new Ctrl_Perfil();
                        boolean eliminado = controlador.eliminar(idUsuario);
                        if (eliminado) {
                            JOptionPane.showMessageDialog(Usuarios1.this, "Usuario eliminado correctamente.");
                            cargarTablaUsuarios();
                        } else {
                            JOptionPane.showMessageDialog(Usuarios1.this, "Error al eliminar el usuario.");
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Usuarios1.this, "Error al eliminar el usuario: " + ex.getMessage());
                }
            }
        });

        updateTheme();
        TemaManager.getInstance().addThemeChangeListener(this::updateTheme);
    }

    private void updateTheme() {
        boolean oscuro = TemaManager.getInstance().isOscuro();
        Color bgColor = oscuro ? new Color(21, 21, 33) : new Color(255, 255, 255);
        panel.setBackground(bgColor);
        editIcon.setBackground(oscuro ? new Color(67, 71, 120) : new Color(46, 49, 82));
        deleteIcon.setBackground(oscuro ? new Color(67, 71, 120) : new Color(46, 49, 82));
        editIcon.setForeground(Color.GREEN); // Green for edit icon
        deleteIcon.setForeground(Color.RED); // Red for delete icon
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
            rSCheckBox1.setForeground(new Color(255, 255, 255));
 
  rSCheckBox1.setColorUnCheck(new Color(255, 255, 255));
            tablaUsuarios.setBackground(fondo); // Explicitly set table background
            tablaUsuarios.setBackgoundHead(new Color(67, 71, 120));
                tablaUsuarios.setForegroundHead(new Color(255, 255, 255));
            tablaUsuarios.setBackgoundHover(new Color(40, 50, 90));
            tablaUsuarios.setFont(new Font("Tahoma", Font.PLAIN, 15));
            tablaUsuarios.setColorPrimary(new Color(37, 37, 52));
            tablaUsuarios.setColorPrimaryText(texto);
            tablaUsuarios.setColorSecondary(new Color(30, 30, 45));
            tablaUsuarios.setColorSecundaryText(texto);
            tablaUsuarios.setColorBorderHead(primario);
            tablaUsuarios.setColorBorderRows(fondo.darker());
            tablaUsuarios.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            tablaUsuarios.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            tablaUsuarios.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));
            tablaUsuarios.setEffectHover(true);
            tablaUsuarios.setShowGrid(true);
            tablaUsuarios.setGridColor(Color.WHITE);
rSButtonMaterialRippleIcon1.setForegroundIcon(fondo);   //[46,49,82]
 rSButtonMaterialRippleIcon1.setBackground(new Color(67, 71, 120));
  rSButtonMaterialRippleIcon1.setForegroundHover(new Color(67, 71, 120));
   rSButtonMaterialRippleIcon1.setForegroundIconHover(new Color(67, 71, 120));
     rSButtonMaterialRippleIcon1.setForegroundText(new Color(67, 71, 120));
         rSButtonMaterialRippleIcon1.setBackground(new Color(67, 71, 120));
            btnNuevo1.setBackground(new Color(67, 71, 120));
            btnNuevo1.setBackgroundHover(new Color(118, 142, 240));
        
        } else {
            Color fondo = new Color(241,245,253);
            Color texto = Color.BLACK;
            Color primario = new Color(72, 92, 188);
rSButtonMaterialRippleIcon1.setForegroundIcon(new Color(72, 92, 188));   //[46,49,82]
 rSButtonMaterialRippleIcon1.setBackground(new Color(247,247,255));
  rSButtonMaterialRippleIcon1.setForegroundHover(new Color(21, 21, 33));
   rSButtonMaterialRippleIcon1.setForegroundIconHover(new Color(21, 21, 33));
            jPanel1.setBackground(fondo);
            txtBuscar.setBackground(fondo);
            txtBuscar.setForeground(texto);
            txtBuscar.setColorIcon(texto);
            txtBuscar.setPhColor(Color.GRAY);
                rSCheckBox1.setForeground(new Color(67, 71, 120));
 
  rSCheckBox1.setColorUnCheck(new Color(67, 71, 120));
            tablaUsuarios.setBackground(fondo); // Explicitly set table background
            tablaUsuarios.setBackgoundHead(new Color(46, 49, 82));
            tablaUsuarios.setForegroundHead(new Color(255, 255, 255));
            tablaUsuarios.setBackgoundHover(new Color(67, 150, 209));
            tablaUsuarios.setFont(new Font("Tahoma", Font.PLAIN, 15));
            tablaUsuarios.setColorPrimary(new Color(242, 242, 242));
            tablaUsuarios.setColorPrimaryText(texto);
            tablaUsuarios.setColorSecondary(new Color(255, 255, 255));
            tablaUsuarios.setColorSecundaryText(texto);
            tablaUsuarios.setColorBorderHead(primario);
            tablaUsuarios.setColorBorderRows(new Color(0, 0, 0));
            tablaUsuarios.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            tablaUsuarios.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            tablaUsuarios.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));
            tablaUsuarios.setEffectHover(true);
            tablaUsuarios.setSelectionBackground(new Color(67, 150, 209));
            tablaUsuarios.setShowGrid(true);
            tablaUsuarios.setGridColor(Color.BLACK);

            btnNuevo1.setBackground(new Color(46, 49, 82));
          
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
        tablaUsuarios = new RSMaterialComponent.RSTableMetroCustom();
        txtBuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        btnNotificacion1 = new rojerusan.RSLabelIcon();
        btnNuevo2 = new RSMaterialComponent.RSButtonShape();
        rSCheckBox1 = new rojerusan.RSCheckBox();
        rSButtonMaterialRippleIcon1 = new RSMaterialComponent.RSButtonMaterialRippleIcon();
        Añadir5 = new rojeru_san.RSButtonRiple();
        Añadir4 = new rojeru_san.RSButtonRiple();

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

        tablaUsuarios.setBackground(new java.awt.Color(204, 0, 0));
        tablaUsuarios.setForeground(new java.awt.Color(255, 255, 255));
        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Seleccionar", "Codigo", "Nombre", "Apellido", "Usuario", "Correo Electronico", "Rol", "Acciones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaUsuarios.setToolTipText("");
        tablaUsuarios.setBackgoundHead(new java.awt.Color(255, 255, 51));
        tablaUsuarios.setBackgoundHover(new java.awt.Color(51, 255, 51));
        tablaUsuarios.setBorderHead(null);
        tablaUsuarios.setBorderRows(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        tablaUsuarios.setColorBorderHead(new java.awt.Color(102, 102, 255));
        tablaUsuarios.setColorBorderRows(new java.awt.Color(255, 102, 102));
        tablaUsuarios.setColorPrimary(new java.awt.Color(153, 255, 153));
        tablaUsuarios.setColorPrimaryText(new java.awt.Color(0, 0, 0));
        tablaUsuarios.setColorSecondary(new java.awt.Color(0, 204, 153));
        tablaUsuarios.setColorSecundaryText(new java.awt.Color(30, 30, 45));
        tablaUsuarios.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        tablaUsuarios.setFontHead(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        tablaUsuarios.setFontRowHover(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        tablaUsuarios.setFontRowSelect(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        tablaUsuarios.setGridColor(new java.awt.Color(102, 255, 102));
        tablaUsuarios.setPreferredSize(new java.awt.Dimension(500, 500));
        tablaUsuarios.setSelectionBackground(new java.awt.Color(67, 150, 209));
        jScrollPane3.setViewportView(tablaUsuarios);
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(10);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 1160, 530));

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

        rSCheckBox1.setForeground(new java.awt.Color(102, 102, 255));
        rSCheckBox1.setText("Seleccionar Todo");
        rSCheckBox1.setColorCheck(new java.awt.Color(0, 204, 51));
        rSCheckBox1.setColorUnCheck(new java.awt.Color(204, 153, 0));
        jPanel1.add(rSCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 670, 190, 20));

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

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1290, 730));
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevo1ActionPerformed
        crear_usuario dialog = new crear_usuario(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarTablaUsuarios(); // Recargar la tabla solo si se guardó correctamente
        }
    }//GEN-LAST:event_btnNuevo1ActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
filtrarUsuarios();
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnNotificacion1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNotificacion1MouseClicked
      menuFiltros.show(btnNotificacion1, 0, btnNotificacion1.getHeight());
    }//GEN-LAST:event_btnNotificacion1MouseClicked

    private void btnNuevo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevo2ActionPerformed
        crear_cliente dialog = new crear_cliente(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
//
//        if (dialog.isGuardado()) {
//            cargartablaclientes(); // Use the correct method with all columns
//        }
    }//GEN-LAST:event_btnNuevo2ActionPerformed

    private void rSButtonMaterialRippleIcon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSButtonMaterialRippleIcon1ActionPerformed
//        List<Integer> idsAEliminar = new ArrayList<>();
//        List<String> nombresClientesAEliminar = new ArrayList<>();
//        List<Integer> clientesConPedidos = new ArrayList<>();
//        List<String> clientesConPedidosNombres = new ArrayList<>();
//
//        // Collect selected clients
//        DefaultTableModel model = (DefaultTableModel) tablaclientes.getModel();
//        int inicio = currentPage * CLIENTES_POR_PAGINA;
//        for (int i = 0; i < model.getRowCount(); i++) {
//            if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
//                int id = Integer.parseInt(model.getValueAt(i, 1).toString());
//                String nombre = model.getValueAt(i, 3).toString() + " " + model.getValueAt(i, 4).toString();
//                if (controlador.tienePedidos(id)) {
//                    clientesConPedidos.add(id);
//                    clientesConPedidosNombres.add(nombre);
//                } else {
//                    idsAEliminar.add(id);
//                    nombresClientesAEliminar.add(nombre);
//                }
//                seleccionados[inicio + i] = false; // Deselect
//            }
//        }
//
//        // Handle clients with associated orders
//        if (!clientesConPedidos.isEmpty()) {
//            String mensaje = "No se pueden eliminar los siguientes clientes porque tienen pedidos asociados:\n" +
//            String.join(", ", clientesConPedidosNombres) +
//            "\n¿Desea marcarlos como inactivos?";
//            int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Clientes con Pedidos", JOptionPane.YES_NO_OPTION);
//            if (opcion == JOptionPane.YES_OPTION) {
//                for (Integer id : clientesConPedidos) {
//                    if (controlador.desactivar(id)) {
//                        JOptionPane.showMessageDialog(this, "Cliente(s) marcados como inactivo(s).", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                    } else {
//                        JOptionPane.showMessageDialog(this, "Error al marcar cliente(s) como inactivo(s).", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//            }
//        }
//
//        // Handle deletion of clients without orders
//        if (!idsAEliminar.isEmpty()) {
//            String mensaje = "¿Está seguro que desea eliminar " +
//            (idsAEliminar.size() > 1 ? "estos " + idsAEliminar.size() + " clientes?" : "este cliente?") +
//            "\nClientes: " + String.join(", ", nombresClientesAEliminar);
//            int confirm = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
//            if (confirm == JOptionPane.YES_OPTION) {
//                boolean todasEliminadas = true;
//                for (Integer id : idsAEliminar) {
//                    if (!controlador.eliminar(id)) {
//                        todasEliminadas = false;
//                        JOptionPane.showMessageDialog(this, "Error al eliminar el cliente con ID: " + id);
//                    }
//                }
//                if (todasEliminadas) {
//                    JOptionPane.showMessageDialog(this, "Cliente(s) eliminado(s) correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                }
//                todasLasClientes.removeIf(cliente -> idsAEliminar.contains(cliente.getId_cliente()));
//                mostrarPagina(currentPage);
//            }
//        } else if (clientesConPedidos.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "No hay clientes seleccionados para eliminar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
//        }
//
//        rSCheckBox1.setSelected(false); // Reset "Seleccionar Todo"
//
    }//GEN-LAST:event_rSButtonMaterialRippleIcon1ActionPerformed

    private void Añadir5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Añadir5ActionPerformed
//        if (currentPage > 0) {
//            currentPage--;
//            mostrarPagina(currentPage);
//        }
    }//GEN-LAST:event_Añadir5ActionPerformed

    private void Añadir4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Añadir4ActionPerformed
//        int totalPaginas = (int) Math.ceil((double) todasLasClientes.size() / CLIENTES_POR_PAGINA);
//        if (currentPage < totalPaginas - 1) {
//            currentPage++;
//            mostrarPagina(currentPage);
//        }
    }//GEN-LAST:event_Añadir4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojeru_san.RSButtonRiple Añadir4;
    private rojeru_san.RSButtonRiple Añadir5;
    private rojerusan.RSLabelIcon btnNotificacion1;
    private RSMaterialComponent.RSButtonShape btnNuevo1;
    private RSMaterialComponent.RSButtonShape btnNuevo2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private RSMaterialComponent.RSButtonMaterialRippleIcon rSButtonMaterialRippleIcon1;
    private rojerusan.RSCheckBox rSCheckBox1;
    private RSMaterialComponent.RSTableMetroCustom tablaUsuarios;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtBuscar;
    // End of variables declaration//GEN-END:variables
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
            setBackground(new Color(240, 240, 240)); // Light gray for selection/hover
        } else {
            setBackground(fondo);
        }

        setBorderPainted(true);
        setForeground(seleccion);
        setSelected(Boolean.TRUE.equals(value));
        setBorder(javax.swing.BorderFactory.createLineBorder(borde));
        setOpaque(true); // Ensure background is painted

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

private JPopupMenu menuFiltros = new JPopupMenu();

public void inicializarMenuFiltros() {
    JMenuItem itemTodos = new JMenuItem("Todos");
    JMenuItem itemNombre = new JMenuItem("Nombre");
    JMenuItem itemApellido = new JMenuItem("Apellido");
    JMenuItem itemUsuario = new JMenuItem("Usuario");
    JMenuItem itemCorreo = new JMenuItem("Correo electrónico");

    menuFiltros.add(itemTodos);
    menuFiltros.add(itemNombre);
    menuFiltros.add(itemApellido);
    menuFiltros.add(itemUsuario);
    menuFiltros.add(itemCorreo);

    itemTodos.addActionListener(e -> { columnaFiltro = -1; filtrarUsuarios(); });
    itemNombre.addActionListener(e -> { columnaFiltro = 2; filtrarUsuarios(); });
    itemApellido.addActionListener(e -> { columnaFiltro = 3; filtrarUsuarios(); });
    itemUsuario.addActionListener(e -> { columnaFiltro = 4; filtrarUsuarios(); });
    itemCorreo.addActionListener(e -> { columnaFiltro = 5; filtrarUsuarios(); });

    txtBuscar.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                menuFiltros.show(txtBuscar, e.getX(), e.getY());
            }
        }
    });
}

private void filtrarUsuarios() {
    String texto = txtBuscar.getText().trim().toLowerCase();

    if (texto.length() == 0) {
        sorter.setRowFilter(null);
    } else {
        try {
            if (columnaFiltro == -1) {
                // Filter across all relevant columns
                sorter.setRowFilter(RowFilter.orFilter(Arrays.asList(
                    RowFilter.regexFilter("(?i)" + texto, 2), // Nombre
                    RowFilter.regexFilter("(?i)" + texto, 3), // Apellido
                    RowFilter.regexFilter("(?i)" + texto, 4), // Usuario
                    RowFilter.regexFilter("(?i)" + texto, 5)  // Correo
                )));
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, columnaFiltro));
            }
        } catch (Exception e) {
            System.out.println("Error applying filter: " + e.getMessage());
        }
    

}
}
}
