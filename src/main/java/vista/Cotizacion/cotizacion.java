package vista.Cotizacion;

import static com.mysql.cj.conf.PropertyKey.logger;
import controlador.GeneradorCotizacionPDF;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.awt.Desktop;
import javax.swing.JPanel;
import modelo.Cotizacion;
import controlador.CotizacionDAO;
import controlador.Ctrl_Cliente;
import controlador.Ctrl_Cotizacion;
import controlador.Ctrl_Producto;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static java.lang.Double.parseDouble;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import modelo.Cliente;
import modelo.CotizacionDTO;
import modelo.ProductoCotizacionDTO;
import vista.Cotizacion.HistorialCot;
import vista.Cotizacion.InsertarCoti;
import vista.TemaManager;

public class cotizacion extends javax.swing.JPanel {

    private Integer clienteCodigo;
    private boolean clienteIngresado = false;
    private String[] datos; // Almacena los datos ingresados
    private boolean guardado = false; // Indica si se presionó "Guardar"
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(cotizacion.class);
    private HashMap<String, String[]> municipiosPorDepartamento = new HashMap<>();
    private String modelo; // o el tipo correcto
    private double total;  // o el tipo correcto
    private Ctrl_Cotizacion ctrl; // o el tipo correcto de tu controlador

    public cotizacion(JPanel contenedor) {
        initComponents();
        configurarTabla();
        configurarFiltroNumerico(telefonotxt);
        configurarFiltroNumerico(numeroidtxt);
        configurarFiltroTexto();
        calcularTotalAutomatico();
        inicializarMunicipios();
        aplicarTema();
        txt_total.setEditable(false);
        txt_total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_total.setText("$0.00"); // Asegúrate de que tenga un valor inicial        inicializarMunicipios();
        CrearCotizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearCotizacionActionPerformed(evt);
            }
        });
        txt_total.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txt_total.getText().matches("^[0-9]*\\.?[0-9]*$")) {
                    txt_total.setBackground(new Color(255, 200, 200)); // Rojo claro
                } else {
                    txt_total.setBackground(Color.WHITE);
                }
            }
        });
        TemaManager.getInstance().addThemeChangeListener(() -> {
            aplicarTema(); // Update theme when it changes
        });
    }

    public void aplicarTema() {
        boolean oscuro = TemaManager.getInstance().isOscuro();

        if (oscuro) {
            // Configuración para modo oscuro
            Color fondo = new Color(21, 21, 33);
            Color fondoTabla = new Color(30, 30, 45);
            Color encabezado = new Color(67, 71, 120);
            Color texto = Color.WHITE;
            Color hover = new Color(118, 142, 240); // Hover de botones
            Color fondoCampo = new Color(37, 37, 52); // Fondo de campos de texto

            // Paneles
            setBackground(fondo);
            jPanel1.setBackground(fondo);
            jPanel2.setBackground(fondo);

            // JScrollPane y su viewport
            jScrollPane3.setBackground(fondo);
            jScrollPane3.getViewport().setBackground(fondoTabla);
            jScrollPane3.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

            // Labels
            jLabel2.setForeground(texto);
            jLabel7.setForeground(texto);
            jLabel9.setForeground(texto);
            jLabel10.setForeground(texto);
            jLabel11.setForeground(texto);
            jLabel12.setForeground(texto);
            jLabel13.setForeground(texto);
            jLabel14.setForeground(texto);
            jLabel15.setForeground(texto);

            // Campos de texto
            txt_NombreCliente.setBackground(fondoCampo);
            txt_NombreCliente.setForeground(texto);
            txt_NombreCliente.setColorMaterial(encabezado);
            txt_NombreCliente.setPhColor(Color.LIGHT_GRAY);

            txt_ApellidoCliente.setBackground(fondoCampo);
            txt_ApellidoCliente.setForeground(texto);
            txt_ApellidoCliente.setColorMaterial(encabezado);
            txt_ApellidoCliente.setPhColor(Color.LIGHT_GRAY);

            numeroidtxt.setBackground(fondoCampo);
            numeroidtxt.setForeground(texto);
            numeroidtxt.setColorMaterial(encabezado);
            numeroidtxt.setPhColor(Color.LIGHT_GRAY);

            telefonotxt.setBackground(fondoCampo);
            telefonotxt.setForeground(texto);
            telefonotxt.setColorMaterial(encabezado);
            telefonotxt.setPhColor(Color.LIGHT_GRAY);

            direcciontxt.setBackground(fondoCampo);
            direcciontxt.setForeground(texto);
            direcciontxt.setColorMaterial(encabezado);
            direcciontxt.setPhColor(Color.LIGHT_GRAY);

            txt_total.setBackground(fondoCampo);
            txt_total.setForeground(texto);
            txt_total.setColorMaterial(encabezado);
            txt_total.setPhColor(Color.LIGHT_GRAY);

            // Combo boxes
            identificaciontxt.setBackground(fondoCampo);
            identificaciontxt.setForeground(texto);
            identificaciontxt.setColorMaterial(encabezado);

            gh.setBackground(fondoCampo);
            gh.setForeground(texto);
            gh.setColorMaterial(encabezado);

            gh1.setBackground(fondoCampo);
            gh1.setForeground(texto);
            gh1.setColorMaterial(encabezado);

            // Botones
            CrearCotizacion.setBackground(encabezado);
            CrearCotizacion.setBackgroundHover(hover);
            CrearCotizacion.setForeground(texto);
            CrearCotizacion.setForegroundHover(Color.WHITE);

            btnCrearpdf.setBackground(encabezado);
            btnCrearpdf.setBackgroundHover(hover);
            btnCrearpdf.setForeground(texto);
            btnCrearpdf.setForegroundHover(Color.WHITE);

            jButton_anadir_producto.setBackground(encabezado);
            jButton_anadir_producto.setBackgroundHover(hover);
            jButton_anadir_producto.setForeground(texto);
            jButton_anadir_producto.setForegroundHover(Color.WHITE);

            HistorialCoti.setBackground(encabezado);
            HistorialCoti.setBackgroundHover(hover);
            HistorialCoti.setForeground(texto);
            HistorialCoti.setForegroundHover(Color.WHITE);

            // Configuración COMPLETA de la tabla
            Tabla1.setBackground(fondoTabla);
            Tabla1.setForeground(texto);

            // Configuración de filas
            Tabla1.setColorPrimary(new Color(37, 37, 52));  // Filas impares
            Tabla1.setColorSecondary(new Color(30, 30, 45)); // Filas pares
            Tabla1.setColorPrimaryText(texto);
            Tabla1.setColorSecundaryText(texto);

            // Encabezados
            Tabla1.setBackgoundHead(encabezado);
            Tabla1.setForegroundHead(texto);
            Tabla1.setColorBorderHead(encabezado);

            // Selección y hover
            Tabla1.setSelectionBackground(new Color(67, 71, 120));
            Tabla1.setBackgoundHover(new Color(40, 50, 90));

            // Bordes y grid
            Tabla1.setColorBorderRows(new Color(60, 60, 60));
            Tabla1.setGridColor(new Color(80, 80, 80));
            Tabla1.setShowGrid(true);

            // Fuentes
            Tabla1.setFont(new Font("Tahoma", Font.PLAIN, 15));
            Tabla1.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));

            // Efectos
            Tabla1.setEffectHover(true);

        } else {
            // Configuración para modo claro
            Color fondo = new Color(242, 247, 255);
            Color fondoTabla = Color.WHITE;
            Color encabezado = new Color(46, 49, 82);
            Color texto = Color.BLACK;
            Color hover = new Color(0, 153, 51); // Hover de botones, tomado de catalogo.java

            // Paneles
            setBackground(fondo);
            jPanel1.setBackground(fondo);
            jPanel2.setBackground(fondo);

            // JScrollPane y su viewport
            jScrollPane3.setBackground(fondo);
            jScrollPane3.getViewport().setBackground(fondoTabla);
            jScrollPane3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // Labels
            jLabel2.setForeground(texto);
            jLabel7.setForeground(texto);
            jLabel9.setForeground(texto);
            jLabel10.setForeground(texto);
            jLabel11.setForeground(texto);
            jLabel12.setForeground(texto);
            jLabel13.setForeground(texto);
            jLabel14.setForeground(texto);
            jLabel15.setForeground(texto);

            // Campos de texto
            txt_NombreCliente.setBackground(fondo);
            txt_NombreCliente.setForeground(texto);
            txt_NombreCliente.setColorMaterial(encabezado);
            txt_NombreCliente.setPhColor(Color.GRAY);

            txt_ApellidoCliente.setBackground(fondo);
            txt_ApellidoCliente.setForeground(texto);
            txt_ApellidoCliente.setColorMaterial(encabezado);
            txt_ApellidoCliente.setPhColor(Color.GRAY);

            numeroidtxt.setBackground(fondo);
            numeroidtxt.setForeground(texto);
            numeroidtxt.setColorMaterial(encabezado);
            numeroidtxt.setPhColor(Color.GRAY);

            telefonotxt.setBackground(fondo);
            telefonotxt.setForeground(texto);
            telefonotxt.setColorMaterial(encabezado);
            telefonotxt.setPhColor(Color.GRAY);

            direcciontxt.setBackground(fondo);
            direcciontxt.setForeground(texto);
            direcciontxt.setColorMaterial(encabezado);
            direcciontxt.setPhColor(Color.GRAY);

            txt_total.setBackground(fondo);
            txt_total.setForeground(texto);
            txt_total.setColorMaterial(encabezado);
            txt_total.setPhColor(Color.GRAY);

            // Combo boxes
            identificaciontxt.setBackground(fondo);
            identificaciontxt.setForeground(texto);
            identificaciontxt.setColorMaterial(encabezado);

            gh.setBackground(fondo);
            gh.setForeground(texto);
            gh.setColorMaterial(encabezado);

            gh1.setBackground(fondo);
            gh1.setForeground(texto);
            gh1.setColorMaterial(encabezado);

            // Botones
            CrearCotizacion.setBackground(encabezado);
            CrearCotizacion.setBackgroundHover(hover);
            CrearCotizacion.setForeground(texto);
            CrearCotizacion.setForegroundHover(Color.BLACK);

            btnCrearpdf.setBackground(encabezado);
            btnCrearpdf.setBackgroundHover(hover);
            btnCrearpdf.setForeground(texto);
            btnCrearpdf.setForegroundHover(Color.BLACK);

            jButton_anadir_producto.setBackground(encabezado);
            jButton_anadir_producto.setBackgroundHover(hover);
            jButton_anadir_producto.setForeground(texto);
            jButton_anadir_producto.setForegroundHover(Color.BLACK);

            HistorialCoti.setBackground(encabezado);
            HistorialCoti.setBackgroundHover(hover);
            HistorialCoti.setForeground(texto);
            HistorialCoti.setForegroundHover(Color.BLACK);

            // Configuración COMPLETA de la tabla
            Tabla1.setBackground(fondoTabla);
            Tabla1.setForeground(texto);

            // Configuración de filas
            Tabla1.setColorPrimary(new Color(242, 242, 242)); // Filas impares
            Tabla1.setColorSecondary(Color.WHITE); // Filas pares
            Tabla1.setColorPrimaryText(texto);
            Tabla1.setColorSecundaryText(texto);

            // Encabezados
            Tabla1.setBackgoundHead(encabezado);
            Tabla1.setForegroundHead(Color.WHITE);
            Tabla1.setColorBorderHead(encabezado);

            // Selección y hover
            Tabla1.setSelectionBackground(new Color(67, 150, 209));
            Tabla1.setBackgoundHover(new Color(67, 150, 209));

            // Bordes y grid
            Tabla1.setColorBorderRows(new Color(0, 0, 0));
            Tabla1.setGridColor(Color.WHITE);
            Tabla1.setShowGrid(true);

            // Fuentes
            Tabla1.setFont(new Font("Tahoma", Font.PLAIN, 15));
            Tabla1.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));

            // Efectos
            Tabla1.setEffectHover(true);
        }

        // Forzar actualización visual
        Tabla1.repaint();
        Tabla1.getTableHeader().repaint();
        revalidate();
        repaint();
    }

    private void inicializarMunicipios() {
        // Definir municipios por departamento
        municipiosPorDepartamento.put("Bogotá DC", new String[]{
            "Seleccione",
            "Antonio Nariño",
            "Barrios Unidos",
            "Bosa",
            "Chapinero",
            "Ciudad Bolívar",
            "Engativá",
            "Fontibón",
            "Kennedy",
            "La Candelaria",
            "Los Mártires",
            "Puente Aranda",
            "Rafael Uribe Uribe",
            "San Cristóbal",
            "Santa Fe",
            "Suba",
            "Sumapaz",
            "Teusaquillo",
            "Tunjuelito",
            "Usaquén",
            "Usme"
        });
        municipiosPorDepartamento.put("Boyacá", new String[]{
            "Seleccione",
            "Almeida",
            "Aquitania",
            "Arcabuco",
            "Belén",
            "Berbeo",
            "Betéitiva",
            "Boavita",
            "Boyacá",
            "Briceño",
            "Buenavista",
            "Busbanzá",
            "Caldas",
            "Campohermoso",
            "Cerinza",
            "Chinavita",
            "Chiquinquirá",
            "Chíquiza",
            "Chiscas",
            "Chita",
            "Chitaraque",
            "Chivatá",
            "Chivor",
            "Ciénaga",
            "Cómbita",
            "Coper",
            "Corrales",
            "Covarachía",
            "Cubará",
            "Cucaita",
            "Cuítiva",
            "Duitama",
            "El Cocuy",
            "El Espino",
            "Firavitoba",
            "Floresta",
            "Gachantivá",
            "Gámeza",
            "Garagoa",
            "Guacamayas",
            "Guateque",
            "Guayatá",
            "Güicán",
            "Iza",
            "Jenesano",
            "Jericó",
            "Labranzagrande",
            "La Capilla",
            "La Uvita",
            "La Victoria",
            "Macanal",
            "Maripí",
            "Miraflores",
            "Mongua",
            "Monguí",
            "Moniquirá",
            "Motavita",
            "Muzo",
            "Nobsa",
            "Nuevo Colón",
            "Oicatá",
            "Otanche",
            "Pachavita",
            "Paéz",
            "Paipa",
            "Pajarito",
            "Panqueba",
            "Pauna",
            "Paya",
            "Paz de Río",
            "Pesca",
            "Pisba",
            "Puerto Boyacá",
            "Quípama",
            "Ramiriquí",
            "Ráquira",
            "Rondón",
            "Saboyá",
            "Sáchica",
            "Samacá",
            "San Eduardo",
            "San José de Pare",
            "San Luis de Gaceno",
            "San Mateo",
            "San Miguel de Sema",
            "San Pablo de Borbur",
            "Santa María",
            "Santa Rosa de Viterbo",
            "Santa Sofía",
            "Santana",
            "Sativanorte",
            "Sativasur",
            "Siachoque",
            "Soatá",
            "Socha",
            "Socotá",
            "Sogamoso",
            "Somondoco",
            "Sora",
            "Soracá",
            "Sotaquirá",
            "Susacón",
            "Sutamarchán",
            "Sutatenza",
            "Tasco",
            "Tenza",
            "Tibaná",
            "Tibasosa",
            "Tinjacá",
            "Tipacoque",
            "Toca",
            "Togüí",
            "Tópaga",
            "Tota",
            "Tunja",
            "Tununguá",
            "Turmequé",
            "Tuta",
            "Tutazá",
            "Úmbita",
            "Ventaquemada",
            "Villa de Leyva",
            "Viracachá",
            "Zetaquirá"
        });
        // Establecer el modelo inicial para gh (municipios)
        gh.setModel(new javax.swing.DefaultComboBoxModel(municipiosPorDepartamento.get("Bogotá DC")));
    }

    public java.sql.Date getFecha() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    public String[] getDatos() {
        return datos;
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Producto", "Unidad", "Cantidad", "Valor Unitario", "Subtotal", "Editar", "Eliminar"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };
        Tabla1.setModel(modelo);
        Tabla1.getColumnModel().getColumn(0).setPreferredWidth(200);
        Tabla1.getColumnModel().getColumn(1).setPreferredWidth(80);
        Tabla1.getColumnModel().getColumn(2).setPreferredWidth(70);
        Tabla1.getColumnModel().getColumn(3).setPreferredWidth(100);
        Tabla1.getColumnModel().getColumn(4).setPreferredWidth(100);
        Tabla1.getColumnModel().getColumn(5).setPreferredWidth(60);
        Tabla1.getColumnModel().getColumn(6).setPreferredWidth(60);
    }

    private double parseTotal(String totalText) throws NumberFormatException {
        // 1. Eliminar cualquier carácter no ASCII
        String clean = totalText.replaceAll("[^\\x00-\\x7F]", "");

        // 2. Eliminar símbolos de moneda y espacios
        clean = clean.replaceAll("[^\\d.]", "");

        // 3. Limitar a 2 decimales
        if (clean.contains(".")) {
            String[] parts = clean.split("\\.");
            if (parts.length == 2 && parts[1].length() > 2) {
                clean = parts[0] + "." + parts[1].substring(0, 2);
            }
        }

        return Double.parseDouble(clean);
    }

    private CotizacionDTO construirDTO() {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setClienteCodigo(clienteCodigo);
        dto.setFecha(new Date());
        dto.setUsuarioId(Ctrl_Cotizacion.USUARIO_ID);
        List<ProductoCotizacionDTO> productos = new ArrayList<>();
        DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            ProductoCotizacionDTO producto = new ProductoCotizacionDTO(
                    modelo.getValueAt(i, 0).toString(), // detalle
                    modelo.getValueAt(i, 1).toString(), // unidad
                    Integer.parseInt(modelo.getValueAt(i, 2).toString()), // cantidad
                    Double.parseDouble(modelo.getValueAt(i, 3).toString().replace("$", "")));
            producto.setDetalle(modelo.getValueAt(i, 0).toString());
            producto.setUnidad(modelo.getValueAt(i, 1).toString());
            producto.setCantidad(Integer.parseInt(modelo.getValueAt(i, 2).toString()));
            producto.setValorUnitario(parseDouble(modelo.getValueAt(i, 3).toString()));
            productos.add(producto);
        }

        dto.setProductos(productos);
        return dto;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Tabla1 = new RSMaterialComponent.RSTableMetroCustom();
        jButton_anadir_producto = new RSMaterialComponent.RSButtonShape();
        HistorialCoti = new RSMaterialComponent.RSButtonShape();
        txt_total = new RSMaterialComponent.RSTextFieldMaterial();
        btnCrearpdf = new RSMaterialComponent.RSButtonShape();
        txt_NombreCliente = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_ApellidoCliente = new RSMaterialComponent.RSTextFieldMaterial();
        identificaciontxt = new RSMaterialComponent.RSComboBoxMaterial();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        numeroidtxt = new RSMaterialComponent.RSTextFieldMaterial();
        telefonotxt = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        direcciontxt = new RSMaterialComponent.RSTextFieldMaterial();
        CrearCotizacion = new RSMaterialComponent.RSButtonShape();
        gh = new RSMaterialComponent.RSComboBoxMaterial();
        gh1 = new RSMaterialComponent.RSComboBoxMaterial();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        jToggleButton1.setText("jToggleButton1");

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Nombres cliente:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 120, 30));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Tabla1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Producto", "Unidad", "Cantidad", "Valor Unitario", "Subtotal", "Editar", "Eliminar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabla1.setBackgoundHead(new java.awt.Color(46, 49, 82));
        Tabla1.setBackgoundHover(new java.awt.Color(109, 160, 221));
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
        Tabla1.setSelectionBackground(new java.awt.Color(109, 160, 221));
        Tabla1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabla1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Tabla1);
        Tabla1.getColumnModel().getColumn(0).setPreferredWidth(10);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 1150, 370));

        jButton_anadir_producto.setBackground(new java.awt.Color(46, 49, 82));
        jButton_anadir_producto.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jButton_anadir_producto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus (2).png"))); // NOI18N
        jButton_anadir_producto.setText("Añadir Producto");
        jButton_anadir_producto.setBackgroundHover(new java.awt.Color(0, 153, 0));
        jButton_anadir_producto.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        jButton_anadir_producto.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        jButton_anadir_producto.setHideActionText(true);
        jButton_anadir_producto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jButton_anadir_producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_anadir_productoActionPerformed(evt);
            }
        });
        jPanel1.add(jButton_anadir_producto, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 10, 170, 40));

        HistorialCoti.setBackground(new java.awt.Color(46, 49, 82));
        HistorialCoti.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        HistorialCoti.setText("  Historial de Cotizaciones");
        HistorialCoti.setBackgroundHover(new java.awt.Color(0, 153, 0));
        HistorialCoti.setFont(new java.awt.Font("Roboto Bold", 1, 12)); // NOI18N
        HistorialCoti.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        HistorialCoti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HistorialCotiActionPerformed(evt);
            }
        });
        jPanel1.add(HistorialCoti, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 10, 160, 40));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 1220, 350));

        txt_total.setEditable(false);
        txt_total.setBackground(new java.awt.Color(255, 255, 255));
        txt_total.setForeground(new java.awt.Color(0, 0, 0));
        txt_total.setColorMaterial(new java.awt.Color(0, 0, 0));
        txt_total.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txt_total.setPhColor(new java.awt.Color(0, 0, 0));
        txt_total.setPlaceholder("Total...");
        txt_total.setSelectionColor(new java.awt.Color(0, 0, 0));
        txt_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_totalActionPerformed(evt);
            }
        });
        jPanel2.add(txt_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 580, 200, 30));

        btnCrearpdf.setBackground(new java.awt.Color(46, 49, 82));
        btnCrearpdf.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnCrearpdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/file-pdf-solid-60 (2).png"))); // NOI18N
        btnCrearpdf.setText("Crear PDF");
        btnCrearpdf.setBackgroundHover(new java.awt.Color(153, 153, 153));
        btnCrearpdf.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCrearpdf.setForegroundHover(new java.awt.Color(0, 0, 0));
        btnCrearpdf.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnCrearpdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearpdfActionPerformed(evt);
            }
        });
        jPanel2.add(btnCrearpdf, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 550, 170, 70));

        txt_NombreCliente.setForeground(new java.awt.Color(0, 0, 0));
        txt_NombreCliente.setColorMaterial(new java.awt.Color(0, 0, 0));
        txt_NombreCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txt_NombreCliente.setPhColor(new java.awt.Color(0, 0, 0));
        txt_NombreCliente.setPlaceholder("Ingrese Nombre...");
        txt_NombreCliente.setSelectionColor(new java.awt.Color(0, 0, 0));
        txt_NombreCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NombreClienteActionPerformed(evt);
            }
        });
        jPanel2.add(txt_NombreCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 180, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("Total:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 580, 70, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Apellidos cliente:");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, -1, -1));

        txt_ApellidoCliente.setForeground(new java.awt.Color(0, 0, 0));
        txt_ApellidoCliente.setColorMaterial(new java.awt.Color(0, 0, 0));
        txt_ApellidoCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txt_ApellidoCliente.setPhColor(new java.awt.Color(0, 0, 0));
        txt_ApellidoCliente.setPlaceholder("Ingrese Apellidos...");
        txt_ApellidoCliente.setSelectionColor(new java.awt.Color(0, 0, 0));
        txt_ApellidoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_ApellidoClienteActionPerformed(evt);
            }
        });
        jPanel2.add(txt_ApellidoCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 180, 30));

        identificaciontxt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tipo de identificacion:", "CC", "CE", "TI", "RC", "NIT", "PA", "PEP", "PPT", "DNI" }));
        identificaciontxt.setColorMaterial(new java.awt.Color(29, 30, 51));
        identificaciontxt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        identificaciontxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificaciontxtActionPerformed(evt);
            }
        });
        jPanel2.add(identificaciontxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, 200, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Tipo de identificación:");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 20, 180, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Numero de identificación:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 70, -1, -1));

        numeroidtxt.setForeground(new java.awt.Color(0, 0, 0));
        numeroidtxt.setColorMaterial(new java.awt.Color(0, 0, 0));
        numeroidtxt.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        numeroidtxt.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        numeroidtxt.setPhColor(new java.awt.Color(29, 30, 51));
        numeroidtxt.setPlaceholder("Ingrese n° identificacion");
        jPanel2.add(numeroidtxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 60, 200, 30));

        telefonotxt.setForeground(new java.awt.Color(29, 30, 31));
        telefonotxt.setColorMaterial(new java.awt.Color(29, 30, 31));
        telefonotxt.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        telefonotxt.setPhColor(new java.awt.Color(29, 30, 51));
        telefonotxt.setPlaceholder("Ingrese telefono");
        telefonotxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                telefonotxtActionPerformed(evt);
            }
        });
        jPanel2.add(telefonotxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 180, 30));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Departamento:");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 120, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Dirección:");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 100, -1, -1));

        direcciontxt.setForeground(new java.awt.Color(29, 30, 31));
        direcciontxt.setColorMaterial(new java.awt.Color(29, 30, 31));
        direcciontxt.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        direcciontxt.setPhColor(new java.awt.Color(29, 30, 51));
        direcciontxt.setPlaceholder("Ingrese direccion");
        direcciontxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                direcciontxtActionPerformed(evt);
            }
        });
        jPanel2.add(direcciontxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 100, 200, 30));

        CrearCotizacion.setBackground(new java.awt.Color(46, 49, 82));
        CrearCotizacion.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        CrearCotizacion.setText("Crear Cotizacion");
        CrearCotizacion.setBackgroundHover(new java.awt.Color(0, 153, 0));
        CrearCotizacion.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        CrearCotizacion.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        CrearCotizacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CrearCotizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearCotizacionActionPerformed(evt);
            }
        });
        jPanel2.add(CrearCotizacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 550, 160, 70));

        gh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccionar", " ", " " }));
        gh.setColorMaterial(new java.awt.Color(29, 30, 51));
        gh.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghActionPerformed(evt);
            }
        });
        jPanel2.add(gh, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 140, -1, 30));

        gh1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccionar", "Bogotá DC", "Boyacá", " " }));
        gh1.setColorMaterial(new java.awt.Color(29, 30, 51));
        gh1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gh1ActionPerformed(evt);
            }
        });
        jPanel2.add(gh1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 140, -1, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Municipio y/o localidad:");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 140, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Telefono:");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 120, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1300, 680));
    }// </editor-fold>//GEN-END:initComponents

    private void txt_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_totalActionPerformed
        if (Tabla1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la cotización", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarYCrearCliente()) {
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
        CotizacionDAO dao = new CotizacionDAO();
        List<Cotizacion> cotizaciones = new ArrayList<>();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            Cotizacion cotizacion = new Cotizacion();
            try {
                cotizacion.setDetalle(modelo.getValueAt(i, 0).toString());
                cotizacion.setUnidad(modelo.getValueAt(i, 1).toString());
                cotizacion.setCantidad(Integer.parseInt(modelo.getValueAt(i, 2).toString()));
                String valorUnitarioStr = modelo.getValueAt(i, 3).toString().replace("$", "").replace(",", "");
                cotizacion.setValor_unitario(Double.parseDouble(valorUnitarioStr));
                String subtotalStr = modelo.getValueAt(i, 4).toString().replace("$", "").replace(",", "");
                cotizacion.setSub_total(Double.parseDouble(subtotalStr));
                cotizacion.setTotal(Double.parseDouble(txt_total.getText().replace("$", "").replace(",", "")));
                cotizacion.setCliente_codigo(clienteCodigo);
                cotizacion.setFecha(getFecha());
                cotizacion.setUsuario_id_usuario(1); // Ajusta según el ID del usuario logueado
                cotizaciones.add(cotizacion);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al procesar los datos de la fila " + i + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            int idCotizacion = dao.guardarCotizaciones(cotizaciones);
            if (idCotizacion != -1) {
                JOptionPane.showMessageDialog(this, "Cotización guardada exitosamente con ID: " + idCotizacion, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                reiniciarCotizacion();
                guardado = true;
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la cotización en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la cotización: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }//GEN-LAST:event_txt_totalActionPerformed

    private void btnCrearpdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearpdfActionPerformed
        if (Tabla1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la cotización", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!clienteIngresado) {
            if (identificaciontxt.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un tipo de identificación válido");
                return;
            }
            if (numeroidtxt.getText().trim().isEmpty() || txt_NombreCliente.getText().trim().isEmpty()
                    || txt_ApellidoCliente.getText().trim().isEmpty() || telefonotxt.getText().trim().isEmpty()
                    || direcciontxt.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos del cliente son obligatorios");
                return;
            }

            int numero;
            try {
                numero = Integer.parseInt(numeroidtxt.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El número de identificación debe ser un valor numérico");
                return;
            }

            Cliente cliente = new Cliente();
            cliente.setIdentificacion(identificaciontxt.getSelectedItem().toString());

            cliente.setNombre(txt_NombreCliente.getText().trim());
            cliente.setApellido(txt_ApellidoCliente.getText().trim());
            cliente.setTelefono(telefonotxt.getText().trim());
            cliente.setDireccion(direcciontxt.getText().trim());

            Ctrl_Cliente contro = new Ctrl_Cliente();
            if (contro.guardar(cliente)) {
                clienteIngresado = true;
                clienteCodigo = cliente.getId_cliente();
                datos = new String[]{"", cliente.getIdentificacion(), String.valueOf(cliente.getId_cliente()),
                    cliente.getNombre(), cliente.getApellido(), cliente.getTelefono(), cliente.getDireccion()};
                JOptionPane.showMessageDialog(null, "Cliente guardado Exitosamente");

                txt_NombreCliente.setEnabled(false);
                txt_ApellidoCliente.setEnabled(false);
                identificaciontxt.setEnabled(false);
                numeroidtxt.setEnabled(false);
                telefonotxt.setEnabled(false);
                direcciontxt.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Hubo problemas en guardar el cliente");
                return;
            }
        }

        GeneradorCotizacionPDF generador = new GeneradorCotizacionPDF();
        DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
        String total = txt_total.getText().trim();
        String cliente = txt_NombreCliente.getText().trim() + " " + txt_ApellidoCliente.getText().trim();
        String archivoSalida = "cotizacion_" + cliente.replaceAll("[^a-zA-Z0-9_-]", "_") + "_" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".pdf";

        try {
            generador.generarPDF(cliente, modelo, total, archivoSalida);
            File pdfFile = new File(archivoSalida);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(this, "No se puede abrir el PDF automáticamente. El archivo se encuentra en: " + archivoSalida, "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar o abrir PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnCrearpdfActionPerformed

    private void txt_NombreClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NombreClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_NombreClienteActionPerformed

    private void Tabla1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabla1MouseClicked
        if (evt.getClickCount() == 1) {
            int columna = Tabla1.columnAtPoint(evt.getPoint());
            int fila = Tabla1.rowAtPoint(evt.getPoint());

            if (fila >= 0) {
                if (columna == 6) { // Columna Eliminar (ahora índice 6)
                    eliminarFila(fila);
                } else if (columna == 5) { // Columna Editar (ahora índice 5)
                    editarFila(fila);
                }
            }
        }
    }

    private void btnGuardarCotActionPerformed(java.awt.event.ActionEvent evt) throws SQLException {
        guardarCotizacion();
    }

    private void editarFila(int fila) {
        String producto = Tabla1.getValueAt(fila, 0).toString();
        String unidad = Tabla1.getValueAt(fila, 1).toString();
        String cantidad = Tabla1.getValueAt(fila, 2).toString();
        String valorUnitario = Tabla1.getValueAt(fila, 3).toString().replace("$", "").replace(",", "");

        java.awt.Frame parent = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        Ctrl_Producto ctrlProducto = new Ctrl_Producto();
        InsertarCoti dialog = new InsertarCoti(parent, true, "", "", ctrlProducto);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            Object[] datos = dialog.getDatosCoti();
            String nuevoProducto = (String) datos[0];
            String nuevaCantidadTexto = (String) datos[1];
            String nuevoValorUnitarioTexto = (String) datos[2];
            String nuevaUnidad = (String) datos[3];

            int nuevaCantidad = Integer.parseInt(nuevaCantidadTexto);
            double nuevoValorUnitario = Double.parseDouble(nuevoValorUnitarioTexto);
            double nuevoSubtotal = nuevaCantidad * nuevoValorUnitario;

            DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
            modelo.setValueAt(nuevoProducto, fila, 0);
            modelo.setValueAt(nuevaUnidad, fila, 1);
            modelo.setValueAt(nuevaCantidad, fila, 2);
            modelo.setValueAt(String.format("$%,d", (long) nuevoValorUnitario), fila, 3);
            modelo.setValueAt(String.format("$%,d", (long) nuevoSubtotal), fila, 4);

            Tabla1.putClientProperty("filaEditando", null);
            jButton_anadir_producto.setText("  Añadir");
            calcularTotalAutomatico();
            JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
        calcularTotalAutomatico();
    }//GEN-LAST:event_Tabla1MouseClicked


    private void HistorialCotiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HistorialCotiActionPerformed
        try {
            // Asegúrate de que estas variables estén definidas en tu clase
            // Ejemplo de código correcto para la línea 793
            String[] columnNames = {"Columna1", "Columna2", "Columna3"}; // Ajusta los nombres de las columnas
            Object[][] data = {}; // Puedes llenar los datos según tu lógica
            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            Tabla1.setModel(model); // Reemplaza jTable1 con el nombre de tu JTable
            // Usar el constructor correcto de HistorialCot
            HistorialCot h = new HistorialCot(jPanel2, true); // o el constructor que corresponda

            h.setSize(1290, 730);
            h.setLocation(0, 0);

            jPanel2.removeAll();
            jPanel2.add(h);
            jPanel2.revalidate();
            jPanel2.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la cotización: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_HistorialCotiActionPerformed

    private void jButton_anadir_productoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_anadir_productoActionPerformed
        Integer filaEditando = (Integer) Tabla1.getClientProperty("filaEditando");
        if (filaEditando != null) {
            guardarEdicionamiento(filaEditando);
        } else {
            java.awt.Frame parent = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
            Ctrl_Producto ctrlProducto = new Ctrl_Producto();
            InsertarCoti dialog = new InsertarCoti(parent, true, "", "", ctrlProducto);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.isConfirmado()) {
                Object[] datos = dialog.getDatosCoti();
                String producto = (String) datos[0];
                String cantidadTexto = (String) datos[1];
                String valorUnitarioTexto = (String) datos[2];
                String unidad = (String) datos[3];

                try {
                    int cantidad = Integer.parseInt(cantidadTexto);
                    double valorUnitario = Double.parseDouble(valorUnitarioTexto);
                    if (cantidad <= 0 || valorUnitario < 0) {
                        JOptionPane.showMessageDialog(this, "Cantidad debe ser positiva y valor unitario no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double subtotal = cantidad * valorUnitario;

                    DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
                    modelo.addRow(new Object[]{
                        producto,
                        unidad,
                        cantidad,
                        String.format("$%,d", (int) valorUnitario),
                        String.format("$%,d", (int) subtotal),
                        "Editar",
                        "Eliminar"
                    });
                    calcularTotalAutomatico();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error al procesar los datos del producto: asegúrese de que sean numéricos", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
        calcularTotalAutomatico();

    }

    private void limpiarCamposProducto() {

    }//GEN-LAST:event_jButton_anadir_productoActionPerformed

    private void txt_ApellidoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_ApellidoClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_ApellidoClienteActionPerformed

    private void direcciontxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_direcciontxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_direcciontxtActionPerformed

    private void identificaciontxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificaciontxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_identificaciontxtActionPerformed
    private boolean validarTotal() {
        if (txt_total.getText().trim().isEmpty()) {
            // Si está vacío, calcular automáticamente
            calcularTotalAutomatico();
            return true;
        }

        try {
            // Validar formato si el usuario lo modificó manualmente
            String totalText = txt_total.getText().replace("$", "").replace(",", "");
            Double.parseDouble(totalText);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de total inválido. Se recalculará automáticamente.",
                    "Error en Total",
                    JOptionPane.ERROR_MESSAGE);
            calcularTotalAutomatico();
            return true;
        }
    }

    private boolean validarFormatoTotal() {
        String totalText = txt_total.getText().trim();

        // Contar puntos decimales
        long countPuntos = totalText.chars().filter(ch -> ch == '.').count();

        if (countPuntos > 1) {
            JOptionPane.showMessageDialog(this,
                    "El total contiene un formato inválido (múltiples puntos).\nUse un solo punto decimal, ej. 123.45",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    private void CrearCotizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearCotizacionActionPerformed
        System.out.println("DEBUG: Botón CrearCotizacion presionado");

        try {
            String totalText = txt_total.getText().trim().replace("$", "").replace(",", "");
            double total = parseTotal(totalText);

            System.out.println("DEBUG: Validando tabla...");
            if (Tabla1.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay productos", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("DEBUG: Error - No hay productos en la tabla");
                return;
            }
            System.out.println("DEBUG: Tabla tiene " + Tabla1.getRowCount() + " filas");

            System.out.println("DEBUG: Validando cliente...");
            if (!validarYCrearCliente()) {
                System.out.println("DEBUG: Error - Fallo en validarYCrearCliente");
                return;
            }

            System.out.println("DEBUG: Validando clienteCodigo...");
            if (clienteCodigo == null) {
                JOptionPane.showMessageDialog(this, "Error: No se ha seleccionado un cliente válido", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("DEBUG: Error - clienteCodigo es null");
                return;
            }
            System.out.println("DEBUG: clienteCodigo = " + clienteCodigo);

            System.out.println("DEBUG: Validando txt_total...");
            System.out.println("DEBUG: Valor de txt_total antes de procesar: '" + totalText + "'");
            if (totalText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo total está vacío", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("DEBUG: Error - txt_total está vacío");
                return;
            }

            String cleanTotal = totalText.replace("$", "").replace(",", "");
            System.out.println("DEBUG: Valor de cleanTotal: '" + cleanTotal + "'");
            long pointCount = cleanTotal.chars().filter(ch -> ch == '.').count();
            System.out.println("DEBUG: Cantidad de puntos decimales: " + pointCount);
            if (pointCount > 1) {
                JOptionPane.showMessageDialog(this, "El total contiene un formato inválido (múltiples puntos). Use un solo punto decimal, ej. 123.45", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("DEBUG: Error - Múltiples puntos en txt_total");
                return;
            }
            calcularTotalAutomatico(); // Asegúrate de recalcular antes de validar
            if (totalText.isEmpty() || !totalText.matches("\\d+(\\.\\d{1,2})?")) {
                JOptionPane.showMessageDialog(this, "El total está vacío o tiene un formato inválido. Use formato como: 123.45", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            guardarCotizacion(); // Llamada al método unificado
        } catch (Exception e) {
            String mensaje = "Formato numérico inválido en el total.\n"
                    + "Use formato correcto como: 123.45\n"
                    + "Error: " + e.getMessage();

            JOptionPane.showMessageDialog(this, mensaje, "Error en Total", JOptionPane.ERROR_MESSAGE);
            txt_total.requestFocus();
            txt_total.selectAll();
        }
    }//GEN-LAST:event_CrearCotizacionActionPerformed

    private void telefonotxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_telefonotxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_telefonotxtActionPerformed

    private void ghActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghActionPerformed

    private void gh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gh1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gh1ActionPerformed

    private boolean camposClienteLlenos() {
        return !txt_NombreCliente.getText().trim().isEmpty()
                && !txt_ApellidoCliente.getText().trim().isEmpty()
                && !numeroidtxt.getText().trim().isEmpty()
                && identificaciontxt.getSelectedIndex() > 0;
    }

    private boolean validarYCrearCliente() {
        if (clienteIngresado) {
            return true;
        }

        if (identificaciontxt.getSelectedIndex() == 0
                || numeroidtxt.getText().trim().isEmpty()
                || txt_NombreCliente.getText().trim().isEmpty()
                || txt_ApellidoCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos del cliente son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Cliente cliente = new Cliente();
            cliente.setIdentificacion(identificaciontxt.getSelectedItem().toString());
            cliente.setId_cliente(Integer.parseInt(numeroidtxt.getText().trim()));
            cliente.setNombre(txt_NombreCliente.getText().trim());
            cliente.setApellido(txt_ApellidoCliente.getText().trim());
            cliente.setTelefono(telefonotxt.getText().trim());
            cliente.setDireccion(direcciontxt.getText().trim());

            Ctrl_Cliente contro = new Ctrl_Cliente();
            if (contro.guardar(cliente)) {
                clienteIngresado = true;
                clienteCodigo = cliente.getId_cliente();
                bloquearCamposCliente();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de identificación debe ser válido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void formatearTotal() {
        try {
            String textoActual = txt_total.getText().trim();
            if (!textoActual.isEmpty()) {
                double valor = parseTotal(textoActual);
                DecimalFormat df = new DecimalFormat("#,##0.00");
                txt_total.setText(df.format(valor));
            }
        } catch (NumberFormatException e) {
            // Mantener el texto actual para corrección
        }
    }

    private void bloquearCamposCliente() {
        txt_NombreCliente.setEnabled(false);
        txt_ApellidoCliente.setEnabled(false);
        identificaciontxt.setEnabled(false);
        numeroidtxt.setEnabled(false);
        telefonotxt.setEnabled(false);
        direcciontxt.setEnabled(false);
    }

    private void guardarEdicionamiento(Integer filaEditando) {
        throw new UnsupportedOperationException("Not supported yet."); // Debes implementarlo si planeas usarlo
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSButtonShape CrearCotizacion;
    private RSMaterialComponent.RSButtonShape HistorialCoti;
    private RSMaterialComponent.RSTableMetroCustom Tabla1;
    private RSMaterialComponent.RSButtonShape btnCrearpdf;
    private RSMaterialComponent.RSTextFieldMaterial direcciontxt;
    private RSMaterialComponent.RSComboBoxMaterial gh;
    private RSMaterialComponent.RSComboBoxMaterial gh1;
    private RSMaterialComponent.RSComboBoxMaterial identificaciontxt;
    private RSMaterialComponent.RSButtonShape jButton_anadir_producto;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToggleButton jToggleButton1;
    private RSMaterialComponent.RSTextFieldMaterial numeroidtxt;
    private RSMaterialComponent.RSTextFieldMaterial telefonotxt;
    private RSMaterialComponent.RSTextFieldMaterial txt_ApellidoCliente;
    private RSMaterialComponent.RSTextFieldMaterial txt_NombreCliente;
    private RSMaterialComponent.RSTextFieldMaterial txt_total;
    // End of variables declaration//GEN-END:variables

    /*joa nambre*/
    private void calcularTotalAutomatico() {
        DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
        double total = 0.0;

        System.out.println("DEBUG: Iniciando calcularTotalAutomatico(). Número de filas: " + modelo.getRowCount());
        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                String cantidadStr = modelo.getValueAt(i, 2).toString().trim();
                if (cantidadStr.isEmpty() || !cantidadStr.matches("\\d+")) {
                    System.out.println("DEBUG: Cantidad inválida en fila " + (i + 1) + ": " + cantidadStr);
                    continue;
                }
                int cantidad = Integer.parseInt(cantidadStr);

                String valorUnitarioStr = modelo.getValueAt(i, 3).toString().trim().replaceAll("[^0-9.]", "");
                if (valorUnitarioStr.isEmpty() || !valorUnitarioStr.matches("\\d+(\\.\\d+)?")) {
                    System.out.println("DEBUG: Valor Unitario inválido en fila " + (i + 1) + ": " + valorUnitarioStr);
                    continue;
                }
                double valorUnitario = Double.parseDouble(valorUnitarioStr);

                double subtotal = cantidad * valorUnitario;
                total += subtotal;
                modelo.setValueAt(String.format("$%,.2f", subtotal), i, 4);
                System.out.println("DEBUG: Fila " + (i + 1) + ": Cantidad=" + cantidad + ", ValorUnitario=" + valorUnitario + ", Subtotal=" + subtotal);
            } catch (Exception e) {
                System.out.println("DEBUG: Error en fila " + (i + 1) + ": " + e.getMessage());
                continue;
            }
        }

        txt_total.setText(total > 0 ? String.format("$%,.2f", total) : "$0.00");
        System.out.println("DEBUG: Total calculado: " + txt_total.getText());
        txt_total.revalidate();
        txt_total.repaint();
    }

    public void reiniciarCotizacion() {
        DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
        modelo.setRowCount(0);

        clienteIngresado = false;
        clienteCodigo = null;
        txt_NombreCliente.setEnabled(true);
        txt_ApellidoCliente.setEnabled(true);
        identificaciontxt.setEnabled(true);
        numeroidtxt.setEnabled(true);
        telefonotxt.setEnabled(true);
        direcciontxt.setEnabled(true);

        txt_NombreCliente.setText("");
        txt_ApellidoCliente.setText("");
        identificaciontxt.setSelectedIndex(0);
        numeroidtxt.setText("");
        telefonotxt.setText("");
        direcciontxt.setText("");
        limpiarCamposProducto();
        txt_total.setText("$0");
        txt_NombreCliente.requestFocus();
    }

    private boolean guardarCliente() {
        try {
            Cliente cliente = new Cliente();
            cliente.setIdentificacion(identificaciontxt.getSelectedItem().toString());
            cliente.setId_cliente(Integer.parseInt(numeroidtxt.getText().trim()));
            cliente.setNombre(txt_NombreCliente.getText().trim());
            cliente.setApellido(txt_ApellidoCliente.getText().trim());
            cliente.setTelefono(telefonotxt.getText().trim());
            cliente.setDireccion(direcciontxt.getText().trim());

            Ctrl_Cliente contro = new Ctrl_Cliente();
            if (contro.guardar(cliente)) {
                clienteIngresado = true;
                clienteCodigo = cliente.getId_cliente();
                bloquearCamposCliente();
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar cliente: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void configurarFiltroTexto() {
        ((AbstractDocument) txt_NombreCliente.getDocument()).setDocumentFilter(new LetterFilter());
        ((AbstractDocument) txt_ApellidoCliente.getDocument()).setDocumentFilter(new LetterFilter());

        // También puedes agregar un tooltip para informar al usuario
        txt_NombreCliente.setToolTipText("Este campo solo acepta letras y espacios");
        txt_ApellidoCliente.setToolTipText("Este campo solo acepta letras y espacios");
    }

    public class LetterFilter extends DocumentFilter {

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) {
                return;
            }

            if (string.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) {
                return;
            }

            if (text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private void configurarFiltrosNumericos() {
        configurarFiltroNumerico(telefonotxt);
        configurarFiltroNumerico(numeroidtxt);

        // Configuraciones adicionales
        telefonotxt.setToolTipText("Solo números y un separador decimal (ej: 3001234567)");
        numeroidtxt.setToolTipText("Solo números (ej: 123456789)");
    }

    private void configurarFiltroNumerico(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (validarFormatoNumerico(newText, textField)) {
                    super.insertString(fb, offset, text, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (validarFormatoNumerico(newText, textField)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean validarFormatoNumerico(String text, JTextField field) {
                // Reglas diferentes según el campo
                if (field == txt_total) {
                    return text.matches("^[0-9]*([.,][0-9]{0,2})?$"); // Máx 2 decimales
                }

                if (field == telefonotxt) {
                    return text.matches("^[0-9]{0,15}$"); // Solo números, máx 15 dígitos
                } else if (field == numeroidtxt) {
                    return text.matches("^[0-9]{0,20}$"); // Solo números, máx 20 dígitos
                } else if (field == txt_total) {
                    return text.matches("^[0-9]*([.,][0-9]{0,2})?$"); // Máx 2 decimales
                }
                return false;
            }
        });
    }

    private void calcularTotal() {
        DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
        int total = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                // Obtener el subtotal directamente como número
                Object subtotalObj = modelo.getValueAt(i, 4);
                int subtotal = 0;

                if (subtotalObj instanceof Integer) {
                    subtotal = (Integer) subtotalObj;
                } else if (subtotalObj instanceof String) {
                    // Si es String, limpiar y convertir
                    String subtotalStr = ((String) subtotalObj).replace("$", "").replace(",", "");
                    subtotal = Integer.parseInt(subtotalStr);
                } else {
                    throw new NumberFormatException("Formato de subtotal no reconocido");
                }

                total += subtotal;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al calcular el subtotal en la fila " + (i + 1) + "\n"
                        + "Valor problemático: " + modelo.getValueAt(i, 4) + "\n"
                        + "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Formatear con comas como separadores de miles
        txt_total.setText(String.format("$%,d", total));
    }

    private void eliminarFila(int fila) {
        String producto = Tabla1.getValueAt(fila, 0).toString();
        String cantidad = Tabla1.getValueAt(fila, 2).toString();

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "<html>¿Está seguro que desea eliminar el producto:<br><br>"
                + "<b>Producto:</b> " + producto + "<br>"
                + "<b>Cantidad:</b> " + cantidad + "<br><br>"
                + "Esta acción no se puede deshacer.</html>",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
            modelo.removeRow(fila);
            calcularTotal(); // Llamar explícitamente para actualizar el total

            JOptionPane.showMessageDialog(
                    this,
                    "Producto eliminado correctamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );
            calcularTotalAutomatico();
        }
    }

    private void guardarCotizacion() {
        if (!validarFormatoTotal()) {
            return;
        }

        if (Tabla1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la cotización", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validarYCrearCliente()) {
            return;
        }

        try {
            DefaultTableModel modelo = (DefaultTableModel) Tabla1.getModel();
            String totalText = txt_total.getText().replace("$", "").replace(",", "");
            double total = Double.parseDouble(totalText);

            Ctrl_Cotizacion ctrl = new Ctrl_Cotizacion();
            int idCotizacion = ctrl.guardarCotizacion(clienteCodigo, modelo, total);

            if (idCotizacion > 0) {
                JOptionPane.showMessageDialog(this,
                        "Cotización guardada exitosamente con ID: " + idCotizacion,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                reiniciarCotizacion();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar la cotización",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de total inválido",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
