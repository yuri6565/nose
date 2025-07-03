package vista;

import controlador.Ctrl_Proveedor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.accessibility.Accessible;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import modelo.CheckableItem;
import modelo.ProveedorDatos;

public class proveedornuevo extends javax.swing.JDialog {
    public boolean guardado = false;
    private CheckedComboBox<CheckableItem> cmbProducto;
    private Ctrl_Proveedor ctrlProveedor;
    private List<Departamento> departamentos;

    public proveedornuevo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        departamentos = Departamento.getTodosDepartamentos(); // Initialize departamentos
        initComponents();
        cargarDepartamentos();
        ctrlProveedor = new Ctrl_Proveedor();
        cmbProducto = new CheckedComboBox<>(makeProductModel());
        cmbProducto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jPanel3.add(cmbProducto,  new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 470, 200, 30));
tipoidentificacion5.setVisible(false);
    tipoidentificacion6.setVisible(false);
    tipoidentificacion9.setVisible(false);
    tipoidentificacion7.setVisible(false);
    tipoidentificacion8.setVisible(false);
    tipoidentificacion1.setVisible(false);
    tipoidentificacion4.setVisible(false);
    dirección3.setVisible(false);
    dirección1.setVisible(false);
    
        // Set up listeners
        setTitle("Nuevo Proveedor");
        txtNombre.addActionListener(e -> txtNombre12.requestFocus());
        txtNombre12.addActionListener(e -> txtNombre13.requestFocus());
        txtNombre13.addActionListener(e -> txttelefono.requestFocus());
        txttelefono.addActionListener(e -> txtcorreo.requestFocus());
        txtcorreo.addActionListener(e -> txtdireccion.requestFocus());
        txtdireccion.addActionListener(e -> gh1.requestFocus());
        gh1.addActionListener(e -> {
            actualizarMunicipios();
            gh.requestFocus();
        });
        gh.addActionListener(e -> cmbProducto.requestFocus());
        cmbProducto.addActionListener(e -> btnguardarr.requestFocus());

        // Agregar DocumentFilter para validar entrada en txtNombre
        ((javax.swing.text.AbstractDocument) txtNombre.getDocument()).setDocumentFilter(new IdentificationDocumentFilter());

        // Escuchar cambios en el tipo de identificación
        identificaciontxt.addActionListener(e -> updateValidationRules());

        // Agregar DocumentFilter para validar entrada en txtNombre12 (Nombre)
        ((javax.swing.text.AbstractDocument) txtNombre12.getDocument()).setDocumentFilter(new NameDocumentFilter());

        // Agregar DocumentFilter para validar entrada en txttelefono (Teléfono)
        ((javax.swing.text.AbstractDocument) txttelefono.getDocument()).setDocumentFilter(new PhoneDocumentFilter());

        // Agregar DocumentFilter para validar entrada en txtcorreo (Correo)
        ((javax.swing.text.AbstractDocument) txtcorreo.getDocument()).setDocumentFilter(new EmailDocumentFilter());
    }

    // Método para verificar si se presionó "Guardar"
    public boolean isGuardado() {
        return guardado;
    }

    private void cargarDepartamentos() {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione un departamento");

        // Ordenar departamentos alfabéticamente
        departamentos.sort((d1, d2) -> d1.getNombre().compareTo(d2.getNombre()));

        for (Departamento depto : departamentos) {
            modelo.addElement(depto.getNombre());
        }

        gh1.setModel(modelo);
    }

    private void updateValidationRules() {
        String selectedType = (String) identificaciontxt.getSelectedItem();
        String currentText = txtNombre.getText().trim();

        // Validar el texto actual según el tipo de identificación seleccionado
        if ("CE".equals(selectedType) && !currentText.matches("[a-zA-Z]{0,20}")) {
            txtNombre.setText(""); // Limpiar si contiene caracteres no permitidos
            tipoidentificacion5.setText("Solo letras (máx. 20) para Cédula de Extranjería");
        } else if (("CC".equals(selectedType) || "TI".equals(selectedType) || "NIT".equals(selectedType)) && !currentText.matches("[0-9]{0,10}")) {
            txtNombre.setText(""); // Limpiar si contiene caracteres no permitidos
            tipoidentificacion5.setText("Solo números (máx. 11) para " + selectedType);
        } else {
            tipoidentificacion5.setText(""); // Limpiar mensaje de error
        }
    }

    private void actualizarMunicipios() {
        String departamentoSeleccionado = (String) gh1.getSelectedItem();

        if (departamentoSeleccionado == null || "Seleccione un departamento".equals(departamentoSeleccionado)) {
            DefaultComboBoxModel<String> modeloVacio = new DefaultComboBoxModel<>();
            modeloVacio.addElement("Seleccione un municipio");
            gh.setModel(modeloVacio);
            return;
        }

        for (Departamento depto : departamentos) {
            if (depto.getNombre().equals(departamentoSeleccionado)) {
                DefaultComboBoxModel<String> modeloMunicipios = new DefaultComboBoxModel<>();
                modeloMunicipios.addElement("Seleccione un municipio");

                // Ordenar municipios alfabéticamente
                List<String> municipiosOrdenados = depto.getMunicipios();
                municipiosOrdenados.sort(String::compareTo);

                for (String municipio : municipiosOrdenados) {
                    modeloMunicipios.addElement(municipio);
                }

                gh.setModel(modeloMunicipios);
                break;
            }
        }
    }

    // Método para crear el modelo de productos
    private DefaultComboBoxModel<CheckableItem> makeProductModel() {
        DefaultComboBoxModel<CheckableItem> model = new DefaultComboBoxModel<>();
        List<String> productos = ctrlProveedor.obtenerTodosNombresInventario();
        if (productos != null && !productos.isEmpty()) {
            for (String producto : productos) {
                model.addElement(new CheckableItem(producto, false));
            }
        } else {
            model.addElement(new CheckableItem("No hay productos disponibles", false));
        }
        return model;
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnGuardar2 = new rojeru_san.RSButtonRiple();
        jLabel6 = new javax.swing.JLabel();
        lblProducto1 = new javax.swing.JLabel();
        btnguardarr = new rojeru_san.RSButtonRiple();
        jLabel12 = new javax.swing.JLabel();
        txttelefono = new RSMaterialComponent.RSTextFieldMaterial();
        txtcorreo = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel13 = new javax.swing.JLabel();
        txtNombre = new RSMaterialComponent.RSTextFieldMaterial();
        txtdireccion = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel14 = new javax.swing.JLabel();
        tipoidentificacion4 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        tipoidentificacion5 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        tipoidentificacion6 = new javax.swing.JLabel();
        tipoidentificacion1 = new javax.swing.JLabel();
        tipoidentificacion7 = new javax.swing.JLabel();
        tipoidentificacion8 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtNombre12 = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel15 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tipoidentificacion9 = new javax.swing.JLabel();
        gh = new RSMaterialComponent.RSComboBoxMaterial();
        gh1 = new RSMaterialComponent.RSComboBoxMaterial();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        dirección1 = new javax.swing.JLabel();
        dirección3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        identificaciontxt = new RSMaterialComponent.RSComboBoxMaterial();
        jLabel16 = new javax.swing.JLabel();
        txtNombre13 = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel29 = new javax.swing.JLabel();
        btnguardarr1 = new rojeru_san.RSButtonRiple();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(490, 480));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(46, 49, 82));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Century751 BT", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Crear Proveedor");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        btnGuardar2.setBackground(new java.awt.Color(46, 49, 82));
        btnGuardar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/x.png"))); // NOI18N
        btnGuardar2.setColorHover(new java.awt.Color(204, 0, 0));
        btnGuardar2.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 18)); // NOI18N
        btnGuardar2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnGuardar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardar2ActionPerformed(evt);
            }
        });
        jPanel4.add(btnGuardar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 0, 40, 30));

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 70));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel6.setText("Correo:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 280, -1, -1));

        lblProducto1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lblProducto1.setText("Producto: ");
        jPanel3.add(lblProducto1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 450, -1, -1));

        btnguardarr.setBackground(new java.awt.Color(46, 49, 82));
        btnguardarr.setText("Cancelar");
        btnguardarr.setColorHover(new java.awt.Color(204, 0, 0));
        btnguardarr.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        btnguardarr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarrActionPerformed(evt);
            }
        });
        jPanel3.add(btnguardarr, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 580, 140, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel12.setText("Telefono:");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, -1, -1));

        txttelefono.setForeground(new java.awt.Color(0, 0, 0));
        txttelefono.setColorMaterial(new java.awt.Color(0, 0, 0));
        txttelefono.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttelefono.setPhColor(new java.awt.Color(0, 0, 0));
        txttelefono.setPlaceholder("Ingrese telefono");
        txttelefono.setSelectionColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(txttelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 200, 30));

        txtcorreo.setForeground(new java.awt.Color(0, 0, 0));
        txtcorreo.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtcorreo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtcorreo.setPhColor(new java.awt.Color(0, 0, 0));
        txtcorreo.setPlaceholder("Ingrese su correo..");
        txtcorreo.setSelectionColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(txtcorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 310, 200, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel13.setText("Número:");
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 120, -1, 20));

        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre.setPhColor(new java.awt.Color(0, 0, 0));
        txtNombre.setPlaceholder("ingrese su número");
        txtNombre.setSelectionColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 150, 200, 30));

        txtdireccion.setForeground(new java.awt.Color(0, 0, 0));
        txtdireccion.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtdireccion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtdireccion.setPhColor(new java.awt.Color(0, 0, 0));
        txtdireccion.setPlaceholder("Ingrese la dirección...");
        txtdireccion.setSelectionColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(txtdireccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 390, 200, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel14.setText("Direccion");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 360, -1, -1));

        tipoidentificacion4.setForeground(new java.awt.Color(255, 51, 51));
        tipoidentificacion4.setText("TIPO");
        jPanel3.add(tipoidentificacion4, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 530, 180, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 51, 51));
        jLabel19.setText("*");
        jPanel3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 120, 20, -1));

        tipoidentificacion5.setForeground(new java.awt.Color(255, 51, 51));
        tipoidentificacion5.setText("TIPO");
        jPanel3.add(tipoidentificacion5, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 180, 200, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 51, 51));
        jLabel20.setText("*");
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 460, 10, 10));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel21.setText("Tipo de identificación:");
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, 20));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 51, 51));
        jLabel22.setText("*");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 120, 20, -1));

        tipoidentificacion6.setForeground(new java.awt.Color(255, 51, 51));
        tipoidentificacion6.setText("TIPO");
        jPanel3.add(tipoidentificacion6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 200, -1));

        tipoidentificacion1.setForeground(new java.awt.Color(255, 51, 51));
        tipoidentificacion1.setText("TIPO");
        jPanel3.add(tipoidentificacion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 200, -1));

        tipoidentificacion7.setForeground(new java.awt.Color(255, 51, 51));
        tipoidentificacion7.setText("TIPO");
        jPanel3.add(tipoidentificacion7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 340, 200, -1));

        tipoidentificacion8.setForeground(new java.awt.Color(255, 51, 51));
        tipoidentificacion8.setText("TIPO");
        jPanel3.add(tipoidentificacion8, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 420, 200, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 51, 51));
        jLabel24.setText("*");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 290, 10, 10));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 51, 51));
        jLabel25.setText("*");
        jPanel3.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 370, 10, 10));

        txtNombre12.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre12.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtNombre12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre12.setPhColor(new java.awt.Color(0, 0, 0));
        txtNombre12.setPlaceholder("Ingrese el nombre...");
        txtNombre12.setSelectionColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(txtNombre12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, 200, 30));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel15.setText("Nombre:");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, 20));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 51, 51));
        jLabel26.setText("*");
        jPanel3.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 20, -1));

        tipoidentificacion9.setForeground(new java.awt.Color(255, 51, 51));
        tipoidentificacion9.setText("TIPO");
        jPanel3.add(tipoidentificacion9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 200, -1));

        gh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccionar", " ", " " }));
        gh.setColorMaterial(new java.awt.Color(29, 30, 51));
        gh.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghActionPerformed(evt);
            }
        });
        jPanel3.add(gh, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, 200, 30));

        gh1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccionar", "Bogotá DC", "Boyacá", " " }));
        gh1.setColorMaterial(new java.awt.Color(29, 30, 51));
        gh1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gh1ActionPerformed(evt);
            }
        });
        jPanel3.add(gh1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 200, 30));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel27.setText("Departamento:");
        jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 360, -1, -1));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 51, 51));
        jLabel28.setText("*");
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 300, 10, 10));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel7.setText("Municipio y/o localidad:");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 51, 51));
        jLabel9.setText("*");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 450, 20, -1));

        dirección1.setForeground(new java.awt.Color(255, 51, 51));
        dirección1.setText("jLabel2");
        jPanel3.add(dirección1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 500, 200, -1));

        dirección3.setForeground(new java.awt.Color(255, 51, 51));
        dirección3.setText("jLabel2");
        jPanel3.add(dirección3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 420, 200, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 51, 51));
        jLabel10.setText("*");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 360, 20, -1));

        identificaciontxt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccionar", "CC", "TI", "CE", "NIT" }));
        identificaciontxt.setColorMaterial(new java.awt.Color(29, 30, 51));
        identificaciontxt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        identificaciontxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificaciontxtActionPerformed(evt);
            }
        });
        jPanel3.add(identificaciontxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 200, 30));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel16.setText("Apellido:");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 210, -1, 20));

        txtNombre13.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre13.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtNombre13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre13.setPhColor(new java.awt.Color(0, 0, 0));
        txtNombre13.setPlaceholder("Ingrese el apellido...");
        txtNombre13.setSelectionColor(new java.awt.Color(0, 0, 0));
        jPanel3.add(txtNombre13, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, 200, 30));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(204, 204, 204));
        jLabel29.setText("(opcional)");
        jPanel3.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 210, 70, -1));

        btnguardarr1.setBackground(new java.awt.Color(46, 49, 82));
        btnguardarr1.setText("Guardar");
        btnguardarr1.setColorHover(new java.awt.Color(204, 0, 0));
        btnguardarr1.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        btnguardarr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarr1ActionPerformed(evt);
            }
        });
        jPanel3.add(btnguardarr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 580, 140, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 680));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardar2ActionPerformed
       dispose();
    }//GEN-LAST:event_btnGuardar2ActionPerformed

    private void ghActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghActionPerformed

    private void gh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gh1ActionPerformed
actualizarMunicipios();
    }//GEN-LAST:event_gh1ActionPerformed

    private void identificaciontxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificaciontxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_identificaciontxtActionPerformed

    private void btnguardarrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarrActionPerformed
dispose();

    }//GEN-LAST:event_btnguardarrActionPerformed

    private void btnguardarr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarr1ActionPerformed
                                           
    boolean isValid = true;

    // Ocultar y limpiar mensajes de error previos
    tipoidentificacion5.setText("");
    tipoidentificacion5.setVisible(false);
    tipoidentificacion6.setText("");
    tipoidentificacion6.setVisible(false);
    tipoidentificacion9.setText("");
    tipoidentificacion9.setVisible(false);
    tipoidentificacion7.setText("");
    tipoidentificacion7.setVisible(false);
    tipoidentificacion8.setText("");
    tipoidentificacion8.setVisible(false);
    tipoidentificacion1.setText("");
    tipoidentificacion1.setVisible(false);
    tipoidentificacion4.setText("");
    tipoidentificacion4.setVisible(false);
    dirección3.setText("");
    dirección3.setVisible(false);
    dirección1.setText("");
    dirección1.setVisible(false);

    // Validar campos obligatorios
    if (identificaciontxt.getSelectedIndex() == 0) {
        tipoidentificacion6.setText("Este campo es obligatorio");
        tipoidentificacion6.setVisible(true);
        isValid = false;
    }
    String idText = txtNombre.getText().trim();
    if (idText.isEmpty()) {
        tipoidentificacion5.setText("Este campo es obligatorio");
        tipoidentificacion5.setVisible(true);
        isValid = false;
    } else {
        String selectedType = (String) identificaciontxt.getSelectedItem();
        if ("CE".equals(selectedType)) {
            if (!idText.matches("[a-zA-Z]+")) {
                tipoidentificacion5.setText("Solo letras para Cédula de Extranjería");
                tipoidentificacion5.setVisible(true);
                isValid = false;
            } else if (idText.length() > 20) {
                tipoidentificacion5.setText("Máximo 20 letras para Cédula de Extranjería");
                tipoidentificacion5.setVisible(true);
                isValid = false;
            }
        } else if ("CC".equals(selectedType) || "TI".equals(selectedType) || "NIT".equals(selectedType)) {
            if (!idText.matches("[0-9]+")) {
                tipoidentificacion5.setText("Solo números para " + selectedType);
                tipoidentificacion5.setVisible(true);
                isValid = false;
            } else if (idText.length() > 15) {
                tipoidentificacion5.setText("Máximo 15 números para " + selectedType);
                tipoidentificacion5.setVisible(true);
                isValid = false;
            }
        }
    }
    String nombreText = txtNombre12.getText().trim();
    if (nombreText.isEmpty()) {
        tipoidentificacion9.setText("Este campo es obligatorio");
        tipoidentificacion9.setVisible(true);
        isValid = false;
    } else if (!nombreText.matches("[a-zA-Z\\s]+")) {
        tipoidentificacion9.setText("Solo letras y espacios permitidos");
        tipoidentificacion9.setVisible(true);
        isValid = false;
    } else if (nombreText.length() > 50) {
        tipoidentificacion9.setText("Máximo 50 caracteres");
        tipoidentificacion9.setVisible(true);
        isValid = false;
    }
    String apellidoText = txtNombre13.getText().trim();
    if (!apellidoText.isEmpty()) { // Solo validar si se ingresó algo
        if (!apellidoText.matches("[a-zA-Z\\s]+")) {
            tipoidentificacion9.setText("Solo letras y espacios permitidos para el apellido");
            tipoidentificacion9.setVisible(true);
            isValid = false;
        } else if (apellidoText.length() > 50) {
            tipoidentificacion9.setText("Máximo 50 caracteres para el apellido");
            tipoidentificacion9.setVisible(true);
            isValid = false;
        }
    }
    String telefonoText = txttelefono.getText().trim();
    if (telefonoText.isEmpty()) {
        tipoidentificacion1.setText("Este campo es obligatorio");
        tipoidentificacion1.setVisible(true);
        isValid = false;
    } else if (!telefonoText.matches("[0-9]+") || telefonoText.length() < 10) {
        tipoidentificacion1.setText("Solo números (mínimo 10) permitidos");
        tipoidentificacion1.setVisible(true);
        isValid = false;
    }
    String correoText = txtcorreo.getText().trim();
    if (correoText.isEmpty()) {
        tipoidentificacion7.setText("Este campo es obligatorio");
        tipoidentificacion7.setVisible(true);
        isValid = false;
    } else if (!correoText.endsWith("@gmail.com")) {
        tipoidentificacion7.setText("El correo debe terminar en @gmail.com");
        tipoidentificacion7.setVisible(true);
        isValid = false;
    }
    String direccionText = txtdireccion.getText().trim();
    if (direccionText.isEmpty()) {
        tipoidentificacion8.setText("Este campo es obligatorio");
        tipoidentificacion8.setVisible(true);
        isValid = false;
    }
    if (gh1.getSelectedIndex() == 0) {
        dirección3.setText("Este campo es obligatorio");
        dirección3.setVisible(true);
        isValid = false;
    }
    if (gh.getSelectedIndex() == 0) {
        dirección1.setText("Este campo es obligatorio");
        dirección1.setVisible(true);
        isValid = false;
    }
    if (IntStream.range(0, cmbProducto.getModel().getSize())
            .mapToObj(i -> cmbProducto.getModel().getElementAt(i))
            .noneMatch(CheckableItem::isSelected)) {
        tipoidentificacion4.setText("Seleccione al menos un producto");
        tipoidentificacion4.setVisible(true);
        isValid = false;
    }

    if (!isValid) {
        JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios correctamente.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Crear el objeto ProveedorDatos
    ProveedorDatos proveedor = new ProveedorDatos();
    proveedor.setTipoIdentificacion(identificaciontxt.getSelectedItem().toString());
    try {
        proveedor.setId_proveedor(Integer.parseInt(txtNombre.getText().trim())); // ID como número (para CC, TI, NIT)
    } catch (NumberFormatException e) {
        // Para CE, usar el texto directamente si no es numérico
        if ("CE".equals(identificaciontxt.getSelectedItem().toString())) {
            proveedor.setId_proveedor(0); // O manejar de otra manera según tu lógica
        } else {
            JOptionPane.showMessageDialog(this, "El número de identificación debe ser un valor numérico para " + identificaciontxt.getSelectedItem().toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    proveedor.setNombre(txtNombre12.getText().trim());
    proveedor.setApellido(apellidoText.isEmpty() ? null : apellidoText); // Permitir null si está vacío
    proveedor.setTelefono(txttelefono.getText().trim());
    proveedor.setCorreo_electronico(txtcorreo.getText().trim());
    proveedor.setDireccion(txtdireccion.getText().trim());
    proveedor.setDepartamento(gh1.getSelectedItem().toString());
    proveedor.setMunicipio(gh.getSelectedItem().toString());
    List<String> productos = IntStream.range(0, cmbProducto.getModel().getSize())
            .mapToObj(i -> cmbProducto.getModel().getElementAt(i))
            .filter(CheckableItem::isSelected)
            .map(CheckableItem::toString)
            .collect(Collectors.toList());
    proveedor.setProductos(productos);
    proveedor.setEstado("activo"); // Establecer estado como "activo" para nuevos proveedores

    // Guardar el proveedor
    if (ctrlProveedor.guardarProveedor(proveedor)) {
        guardado = true;
        JOptionPane.showMessageDialog(this, "Proveedor guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    } else {
        JOptionPane.showMessageDialog(this, "Error al guardar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_btnguardarr1ActionPerformed
public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(proveedornuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(proveedornuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(proveedornuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(proveedornuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                proveedornuevo dialog = new proveedornuevo(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojeru_san.RSButtonRiple btnGuardar2;
    private rojeru_san.RSButtonRiple btnguardarr;
    private rojeru_san.RSButtonRiple btnguardarr1;
    private javax.swing.JLabel dirección1;
    private javax.swing.JLabel dirección3;
    private RSMaterialComponent.RSComboBoxMaterial gh;
    private RSMaterialComponent.RSComboBoxMaterial gh1;
    private RSMaterialComponent.RSComboBoxMaterial identificaciontxt;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblProducto1;
    private javax.swing.JLabel tipoidentificacion1;
    private javax.swing.JLabel tipoidentificacion4;
    private javax.swing.JLabel tipoidentificacion5;
    private javax.swing.JLabel tipoidentificacion6;
    private javax.swing.JLabel tipoidentificacion7;
    private javax.swing.JLabel tipoidentificacion8;
    private javax.swing.JLabel tipoidentificacion9;
    private RSMaterialComponent.RSTextFieldMaterial txtNombre;
    private RSMaterialComponent.RSTextFieldMaterial txtNombre12;
    private RSMaterialComponent.RSTextFieldMaterial txtNombre13;
    private RSMaterialComponent.RSTextFieldMaterial txtcorreo;
    private RSMaterialComponent.RSTextFieldMaterial txtdireccion;
    private RSMaterialComponent.RSTextFieldMaterial txttelefono;
    // End of variables declaration//GEN-END:variables
// Clases internas para el CheckedComboBox
 class PhoneDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("[0-9]*")) {
                super.insertString(fb, offset, string, attr);
            } else {
                tipoidentificacion1.setText("Solo números permitidos");
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("[0-9]*")) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                tipoidentificacion1.setText("Solo números permitidos");
            }
        }
    }

    // DocumentFilter para validar el correo (debe terminar en @gmail.com)
  class EmailDocumentFilter extends DocumentFilter {
    private final int MAX_LENGTH = 100; // Maximum length for email

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
        if (isValidEmailInput(string, newText)) {
            super.insertString(fb, offset, string, attr);
            validateEmail(newText);
        } else {
            tipoidentificacion7.setText("Caracteres no permitidos");
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) return;
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
        if (isValidEmailInput(text, newText)) {
            super.replace(fb, offset, length, text, attrs);
            validateEmail(newText);
        } else {
            tipoidentificacion7.setText("Caracteres no permitidos");
        }
    }

    private boolean isValidEmailInput(String input, String fullText) {
        // Allow letters, numbers, dots, underscores, hyphens, and exactly one @ symbol
        if (!input.matches("[a-zA-Z0-9._@-]*")) {
            return false;
        }
        // Ensure the full text doesn't have more than one @ symbol
        long atCount = fullText.chars().filter(ch -> ch == '@').count();
        if (atCount > 1) {
            return false;
        }
        // Ensure the text doesn't contain spaces and is within length limit
        return !fullText.contains(" ") && fullText.length() <= MAX_LENGTH;
    }

    private void validateEmail(String fullText) {
        // Check if the email ends with @gmail.com when the user is done typing
        if (!fullText.isEmpty() && !fullText.endsWith("@gmail.com")) {
            tipoidentificacion7.setText("El correo debe terminar en @gmail.com");
        } else {
            tipoidentificacion7.setText(""); // Clear error message if valid
        }
    }
}
    // Clases internas para el CheckedComboBox
    class NameDocumentFilter extends DocumentFilter {
        private final int MAX_LENGTH = 50; // Máximo de caracteres para el nombre

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
            if (isValidInput(string, newText)) {
                super.insertString(fb, offset, string, attr);
            } else {
                tipoidentificacion9.setText("Solo letras y espacios (máx. 50 caracteres)");
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            if (isValidInput(text, newText)) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                tipoidentificacion9.setText("Solo letras y espacios (máx. 50 caracteres)");
            }
        }

        private boolean isValidInput(String input, String fullText) {
            // Permitir solo letras (a-z, A-Z) y espacios, sin caracteres especiales
            return input.matches("[a-zA-Z\\s]*") && fullText.length() <= MAX_LENGTH;
        }
    }

    // Clase DocumentFilter para validar la identificación
    class IdentificationDocumentFilter extends DocumentFilter {
        private final int MAX_LENGTH_CE = 20; // Máximo para CE
        private final int MAX_LENGTH_OTHERS = 15; // Máximo para CC, TI, NIT

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
            if (isValidInput(string, newText)) {
                super.insertString(fb, offset, string, attr);
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            if (isValidInput(text, newText)) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                showErrorMessage();
            }
        }

        private boolean isValidInput(String input, String fullText) {
            String selectedType = (String) identificaciontxt.getSelectedItem();
            if ("CE".equals(selectedType)) {
                // Solo letras (a-z, A-Z), sin caracteres especiales
                return input.matches("[a-zA-Z]*") && fullText.length() <= MAX_LENGTH_CE;
            } else if ("CC".equals(selectedType) || "TI".equals(selectedType) || "NIT".equals(selectedType)) {
                // Solo números (0-9)
                return input.matches("[0-9]*") && fullText.length() <= MAX_LENGTH_OTHERS;
            }
            return false; // No permitir nada si no hay tipo seleccionado
        }

        private void showErrorMessage() {
            String selectedType = (String) identificaciontxt.getSelectedItem();
            if ("CE".equals(selectedType)) {
                tipoidentificacion5.setText("Solo letras (máx. 10) para Cédula de Extranjería");
            } else if ("CC".equals(selectedType) || "TI".equals(selectedType) || "NIT".equals(selectedType)) {
                tipoidentificacion5.setText("Solo números (máx. 11) para " + selectedType);
            } else {
                tipoidentificacion5.setText("Seleccione un tipo de identificación");
            }
        }
    }

    // Clase para los ítems checkeables
    class CheckableItem {
        private final String text;
        private boolean selected;

        protected CheckableItem(String text, boolean selected) {
            this.text = text;
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    // Clase para el CheckedComboBox
    class CheckedComboBox<E extends CheckableItem> extends JComboBox<E> {
        protected boolean keepOpen;
        private final JPanel panel = new JPanel(new BorderLayout());

        protected CheckedComboBox(ComboBoxModel<E> model) {
            super(model);
            setBackground(new Color(255, 255, 255)); // Fondo blanco para coincidir con jPanel1
            setForeground(Color.DARK_GRAY); // Texto oscuro
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 40); // Aumentar altura para un look más moderno
        }

        @Override
        public void updateUI() {
            setRenderer(null);
            super.updateUI();

            Accessible a = getAccessibleContext().getAccessibleChild(0);
            if (a instanceof ComboPopup) {
                ((ComboPopup) a).getList().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        JList<?> list = (JList<?>) e.getComponent();
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            keepOpen = true;
                            updateItem(list.locationToIndex(e.getPoint()));
                        }
                    }
                });
            }

            DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (index >= 0) {
                        c.setBackground(isSelected ? new Color(0, 120, 215, 50) : new Color(255, 255, 255));
                        c.setForeground(Color.DARK_GRAY);
                    } else {
                        c.setBackground(new Color(0, 0, 0, 0)); // Fondo transparente para el texto seleccionado
                    }
                    return c;
                }
            };
            JCheckBox check = new JCheckBox();
            check.setOpaque(false);
            check.setForeground(new Color(0, 120, 215)); // Color de casilla moderna
            setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                panel.removeAll();
                Component c = renderer.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (index < 0) {
                    String txt = getCheckedItemString(list.getModel());
                    JLabel l = (JLabel) c;
                    l.setText(txt.isEmpty() ? " " : txt);
                    l.setForeground(Color.DARK_GRAY);
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    panel.setOpaque(false); // Hacer el panel transparente
                    panel.setBackground(new Color(0, 0, 0, 0)); // Fondo transparente
                } else {
                    check.setSelected(value.isSelected());
                    panel.add(check, BorderLayout.WEST);
                    panel.setBackground(isSelected ? new Color(0, 120, 215, 50) : new Color(255, 255, 255));
                }
                panel.add(c, BorderLayout.CENTER);
                return panel;
            });
            initActionMap();
        }

        protected void initActionMap() {
            KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
            getInputMap(JComponent.WHEN_FOCUSED).put(ks, "checkbox-select");
            getActionMap().put("checkbox-select", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    Accessible a = getAccessibleContext().getAccessibleChild(0);
                    if (a instanceof ComboPopup) {
                        updateItem(((ComboPopup) a).getList().getSelectedIndex());
                    }
                }
            });
        }

        protected void updateItem(int index) {
            if (isPopupVisible() && index >= 0) {
                E item = getItemAt(index);
                item.setSelected(!item.isSelected());
                setSelectedIndex(-1);
                setSelectedItem(item);
            }
        }

        @Override
        public void setPopupVisible(boolean v) {
            if (keepOpen) {
                keepOpen = false;
            } else {
                super.setPopupVisible(v);
            }
        }

        protected static <E extends CheckableItem> String getCheckedItemString(ListModel<E> model) {
            return IntStream.range(0, model.getSize())
                    .mapToObj(model::getElementAt)
                    .filter(CheckableItem::isSelected)
                    .map(Objects::toString)
                    .sorted()
                    .collect(Collectors.joining(", "));
        }
    }

    // Panel personalizado para un efecto visual moderno
    class ModernPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), getBackground().brighter(), true);
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        }
    }
}

