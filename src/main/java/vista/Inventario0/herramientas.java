/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista.Inventario0;

import RSMaterialComponent.RSButtonShape;
import controlador.Ctrl_CategoriaHerramienta;
import controlador.Ctrl_InventarioHerramienta;
import controlador.Ctrl_MarcaHerramienta;
import controlador.Ctrl_UnidadHerramienta;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import modelo.Categoria;
import modelo.HerramientaDatos;
import modelo.Marca;
import modelo.Unidad;
import vista.TemaManager;

/**
 *
 * @author ZenBook
 */
public class herramientas extends javax.swing.JPanel {

    private Ctrl_InventarioHerramienta ctrlInventario;
    private JPanel contenedorPrincipal;
private boolean modoOscuro = false;
    /**
     * Creates new form herramientas
     */
    public herramientas() {
        initComponents();
        contenedorPrincipal = new JPanel();
        ctrlInventario = new Ctrl_InventarioHerramienta();

        // Panel contenedor para principalPanel (para agregar márgenes)
        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBackground(new java.awt.Color(245, 246, 250));

        principalPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(10, 10, 10, 10); // Márgenes
        gbc.fill = GridBagConstraints.NONE; // No estirar componentes

        principalPanel.setBackground(new java.awt.Color(245, 246, 250));

        // Agregar márgenes al contenedor (aumentado a 40px en todos los lados)
        contenedorPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contenedorPrincipal.add(principalPanel, BorderLayout.CENTER);

// JScrollPane -----------------------------
        // Configurar el JScrollPane con el contenedor (en lugar de principalPanel directamente)
        JScrollPane scrollPane = new JScrollPane(contenedorPrincipal);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(153, 153, 153);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return new JButton() {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(0, 0);
                    }
                };
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return new JButton() {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(0, 0);
                    }
                };
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }
        });

        // Reemplazar en panelprincipal
        panelprincipal.remove(principalPanel);
        panelprincipal.add(scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 1150, 560));
// JScrollPane ----------------------------- 

        cmbCategoria.removeAllItems(); // Limpia por si acaso
        cmbCategoria.addItem("Seleccione una categoría"); // Primer ítem informativo

        Ctrl_CategoriaHerramienta ctrl = new Ctrl_CategoriaHerramienta();
        List<Categoria> categorias = ctrl.obtenerCategoriasHerramienta();

        for (Categoria cat : categorias) {
            cmbCategoria.addItem(cat.getNombre());
        }
        // Cargar materiales existentes al iniciar
        cargarMateriales();
    }

   // Método para aplicar el tema (oscuro o claro)
public void aplicarTema() {
    boolean oscuro = TemaManager.getInstance().isOscuro();

    Color fondo;
    Color fondoTarjetas;
    Color texto;
    Color primario;
    Color hover;

    if (oscuro) {
        fondo = new Color(21, 21, 33);
        fondoTarjetas = new Color(50, 50, 65);
        texto = Color.WHITE;
        primario = new Color(67, 71, 120);
        hover = new Color(118, 142, 240);
    } else {
        fondo = new Color(245, 246, 250);
        fondoTarjetas = Color.WHITE;
        texto = Color.BLACK;
        primario = new Color(46, 49, 82);
        hover = new Color(67, 150, 209);
    }

    // Aplicar colores a los paneles
    setBackground(fondo);
    panelprincipal.setBackground(fondo);
    contenedorPrincipal.setBackground(fondo);
    principalPanel.setBackground(fondo);

    // Botones
    btnCategoria.setBackground(primario);
    btnCategoria.setBackgroundHover(hover);
    btnMarcas.setBackground(primario);
    btnMarcas.setBackgroundHover(hover);
    btnUnidad.setBackground(primario);
    btnUnidad.setBackgroundHover(hover);
    btnNuevo.setBackground(primario); // Solo fondo, sin hover

    // ComboBox
    cmbCategoria.setBackground(fondo);
    cmbCategoria.setForeground(texto);
    cmbCategoria.setColorMaterial(oscuro ? new Color(200, 200, 200) : new Color(153, 153, 153));

    // Campo de búsqueda
    txtBuscar.setBackground(fondo);
    txtBuscar.setForeground(texto);
    txtBuscar.setColorIcon(texto);
    txtBuscar.setColorMaterial(oscuro ? new Color(200, 200, 200) : new Color(153, 153, 153));
    txtBuscar.setPhColor(oscuro ? new Color(150, 150, 150) : new Color(102, 102, 102));

    // Actualizar tarjetas
    actualizarColorTarjetas(fondoTarjetas, texto);

    // Repintar componentes
    repaint();
    panelprincipal.repaint();
    contenedorPrincipal.repaint();
    principalPanel.repaint();
    btnCategoria.repaint();
    btnMarcas.repaint();
    btnUnidad.repaint();
    btnNuevo.repaint();
    cmbCategoria.repaint();
    txtBuscar.repaint();
}

// Método para actualizar los colores de las tarjetas
private void actualizarColorTarjetas(Color fondoTarjetas, Color texto) {
    for (Component comp : principalPanel.getComponents()) {
        if (comp instanceof JPanel) {
            JPanel tarjeta = (JPanel) comp;
            JPanel panelInfo = (JPanel) tarjeta.getComponent(1);
            panelInfo.setBackground(fondoTarjetas);
            JLabel lblNombre = (JLabel) panelInfo.getComponent(0);
            lblNombre.setForeground(texto);
            JLabel lblCategoria = (JLabel) panelInfo.getComponent(1);
            lblCategoria.setForeground(texto);
            JLabel lblEstado = (JLabel) panelInfo.getComponent(2);
            HerramientaDatos material = (HerramientaDatos) tarjeta.getClientProperty("material");
            if (material.getEstado() != null) {
                String estado = material.getEstado().toLowerCase();
                if (estado.contains("disponible")) {
                    lblEstado.setForeground(new Color(50, 200, 50));
                } else if (estado.contains("reparación") || estado.contains("reparacion")) {
                    lblEstado.setForeground(new Color(255, 140, 0));
                } else if (estado.contains("dañado") || estado.contains("danado")) {
                    lblEstado.setForeground(new Color(255, 50, 50));
                } else {
                    lblEstado.setForeground(texto);
                }
            } else {
                lblEstado.setForeground(texto);
            }
            JPanel panelImagen = (JPanel) tarjeta.getComponent(0);
            panelImagen.setBackground(fondoTarjetas);
            JPanel panelBotones = (JPanel) tarjeta.getComponent(2);
            panelBotones.setBackground(fondoTarjetas);
            RSButtonShape verBtn = (RSButtonShape) panelBotones.getComponent(0);
            verBtn.setBackground(new Color(216, 246, 221));
            verBtn.setBackgroundHover(new Color(188, 225, 193));
            RSButtonShape editarBtn = (RSButtonShape) panelBotones.getComponent(1);
            editarBtn.setBackground(new Color(189, 215, 252));
            editarBtn.setBackgroundHover(new Color(166, 199, 245));
            RSButtonShape eliminarBtn = (RSButtonShape) panelBotones.getComponent(2);
            eliminarBtn.setBackground(new Color(242, 199, 207));
            eliminarBtn.setBackgroundHover(new Color(242, 174, 188));
            tarjeta.repaint();
        }
    }
}


    // Método para cargar los materiales desde la base de datos
    private void cargarMateriales() {
        principalPanel.removeAll();
        principalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        List<Ctrl_InventarioHerramienta.MaterialConDetalles> materiales = ctrlInventario.obtenerMateriales();
        for (Ctrl_InventarioHerramienta.MaterialConDetalles materialConDetalles : materiales) {
            agregarMaterial(
                    materialConDetalles.getMaterial(),
                    materialConDetalles.getNombreCategoria(),
                    materialConDetalles.getNombreMarca(),
                    materialConDetalles.getNombreUnidadMedida()
            );
        }

        // Ajustar el tamaño del principalPanel dinámicamente
        int totalTarjetas = principalPanel.getComponentCount();
        int tarjetasPorFila = 5;
        int filas = (int) Math.ceil((double) totalTarjetas / tarjetasPorFila);
        int tarjetaHeight = 310;
        int totalHeight = filas * tarjetaHeight + 20; // Añadir margen
        principalPanel.setPreferredSize(new Dimension(1130, Math.max(totalHeight, 560))); // Ajuste de ancho a 1130

        principalPanel.revalidate();
        principalPanel.repaint();
        if (principalPanel.getParent() instanceof JViewport) {
            ((JViewport) principalPanel.getParent()).setViewPosition(new Point(0, 0));
        }
    }

    // Método para agregar una nueva tarjeta
    public void agregarMaterial(HerramientaDatos material, String nombreCategoria, String nombreMarca, String nombreUnidadMedida) {

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setPreferredSize(new Dimension(210, 300)); // Ancho: 200, Alto: 300
        tarjeta.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // JPanel para la imagen
        JPanel panelImagen = new JPanel(new GridBagLayout());
        panelImagen.setPreferredSize(new Dimension(210, 197));
        panelImagen.setBackground(Color.WHITE);

        JLabel lblImagen = new JLabel();
        if (material.getImagen() != null) {
            ImageIcon icon = new ImageIcon(material.getImagen());
            // Redimensionar la imagen al tamaño del panelImagen (210x197)
            Image img = icon.getImage().getScaledInstance(210, 197, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
        } else {
            lblImagen.setText("No imagen");
        }
        panelImagen.add(lblImagen);
        tarjeta.add(panelImagen, BorderLayout.NORTH);

        // JPanel para la información
        JPanel panelInfo = new JPanel(new GridLayout(3, 1));
        panelInfo.setBackground(new Color(46, 49, 82)); // Color de fondo azul oscuro
        Font fuenteInfo = new Font("Segoe UI", Font.PLAIN, 15); // Fuente Arial, negrita, tamaño 16

        JLabel lblNombre = new JLabel(material.getNombre());
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(fuenteInfo); // Establece la fuente
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
        panelInfo.add(lblNombre);

        JLabel lblCategoria = new JLabel("Categoría: " + nombreCategoria);
        lblCategoria.setForeground(Color.WHITE);
        lblCategoria.setFont(fuenteInfo); // Establece la fuente
        lblCategoria.setHorizontalAlignment(SwingConstants.CENTER);
        panelInfo.add(lblCategoria);

        JLabel lblEstado = new JLabel("Estado: " + material.getEstado());
        lblEstado.setFont(fuenteInfo); // Establece la fuente
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        // Aplicar color según el estado
        switch (material.getEstado().toLowerCase()) {
            case "disponible":
                lblEstado.setForeground(new Color(82, 196, 88)); // Verde
                break;
            case "reparación":
                lblEstado.setForeground(new Color(255, 165, 0)); // Naranja
                break;
            case "dañado":
                lblEstado.setForeground(Color.RED); // Rojo
                break;
            default:
                lblEstado.setForeground(Color.WHITE); // Blanco por defecto
        }
        panelInfo.add(lblEstado);

        tarjeta.add(panelInfo, BorderLayout.CENTER);

        // Almacenar el objeto MaterialDatos y los nombres en la tarjeta
        tarjeta.putClientProperty("material", material);
        tarjeta.putClientProperty("nombreCategoria", nombreCategoria);
        tarjeta.putClientProperty("nombreMarca", nombreMarca);
        tarjeta.putClientProperty("nombreUnidadMedida", nombreUnidadMedida);

        // JPanel para los botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(new Color(46, 49, 82));

        RSButtonShape verBtn = new RSButtonShape(); // Reemplaza "ver.png" con la ruta de tu imagen
        verBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view (1).png")));
        verBtn.setPreferredSize(new Dimension(33, 25)); // Ancho: 30, Alto: 30
        verBtn.setBackground(new Color(216, 246, 221)); // Establece el fondo en rojo
        verBtn.setBackgroundHover(new java.awt.Color(188, 225, 193));
        verBtn.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        verBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        verBtn.setToolTipText("<html><b>Ver detalles de la herramienta</html>");
        //accion del boton de ver
        verBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                herramientaInfo dialog = new herramientaInfo(new javax.swing.JFrame(), true);
                dialog.mostrarMaterial(material); // Muestra la información del material
                dialog.setLocationRelativeTo(null); // Centra el JDialog
                dialog.setVisible(true); // Muestra el JDialog
            }
        });

        RSButtonShape editarBtn = new RSButtonShape();
        editarBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edit.png")));
        editarBtn.setPreferredSize(new Dimension(33, 25)); // Ancho: 30, Alto: 30
        editarBtn.setBackground(new Color(189, 215, 252));
        editarBtn.setBackgroundHover(new java.awt.Color(166, 199, 245));
        editarBtn.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        editarBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editarBtn.setToolTipText("<html><b>Editar herramienta</html>");
        //accion del boton de editar
        editarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el material asociado a la tarjeta
                HerramientaDatos material = (HerramientaDatos) tarjeta.getClientProperty("material");

                // Abrir el JDialog para editar
                herramientaEditar dialog = new herramientaEditar(new javax.swing.JFrame(), true, material);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);

                // Si se guardaron los cambios, actualizar la tarjeta
                if (dialog.isGuardado()) {
                    String nombreCategoria = (String) tarjeta.getClientProperty("nombreCategoria");
                    String nombreMarca = (String) tarjeta.getClientProperty("nombreMarca");
                    String nombreUnidadMedida = (String) tarjeta.getClientProperty("nombreUnidadMedida");
                    actualizarTarjeta(tarjeta, material, nombreCategoria, nombreMarca, nombreUnidadMedida);
                    ctrlInventario.actualizar(material);
                }
            }
        });

        RSButtonShape eliminarBtn = new RSButtonShape();
        eliminarBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete (6).png")));
        eliminarBtn.setPreferredSize(new Dimension(33, 25)); // Ancho: 30, Alto: 30
        eliminarBtn.setBackground(new Color(242, 199, 207));
        eliminarBtn.setBackgroundHover(new java.awt.Color(242, 174, 188));
        eliminarBtn.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        eliminarBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        eliminarBtn.setToolTipText("<html><b>Eliminar herramienta</html>");
        eliminarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mostrar ventana emergente de confirmación
                int confirmacion = JOptionPane.showConfirmDialog(
                        null, // Parent component (null para centrar en la pantalla)
                        "¿Estás seguro de que deseas eliminar este material?\nCódigo: " + material.getIdInventario() + "\nNombre: " + material.getNombre(),
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                // Si el usuario selecciona "Sí", eliminar la tarjeta
                if (confirmacion == JOptionPane.YES_OPTION) {
                    if (ctrlInventario.eliminar(material.getIdInventario())) {
                        principalPanel.remove(tarjeta);
                        principalPanel.revalidate();
                        principalPanel.repaint();
                        JOptionPane.showMessageDialog(null, "Material eliminado correctamente.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar el material.");
                    }
                }
            }
        });

        panelBotones.add(verBtn);
        panelBotones.add(editarBtn);
        panelBotones.add(eliminarBtn);
        tarjeta.add(panelBotones, BorderLayout.SOUTH);

        principalPanel.add(tarjeta);
        principalPanel.revalidate();
        principalPanel.repaint();
        contenedorPrincipal.revalidate(); // Revalidar el contenedor principal también
        contenedorPrincipal.repaint();   // Repintar el contenedor principal también

        // Calcular la altura dinámicamente según 5 tarjetas por fila
        int totalTarjetas = principalPanel.getComponentCount();
        int filas = (int) Math.ceil((double) totalTarjetas / 5); // Número de filas
        int tarjetaHeight = 310; // Altura de la tarjeta + margen
        int totalHeight = filas * tarjetaHeight;
        // Ajustar el ancho y alto considerando el margen (1150 - 80px de márgenes laterales, 560 - 80px de márgenes verticales)
        principalPanel.setPreferredSize(new Dimension(1150 - 80, Math.max(totalHeight, 560 - 80))); // Mínimo ajustado
    }

//metodo para actualizar tarjeta
    public void actualizarTarjeta(JPanel tarjeta, HerramientaDatos material, String nombreCategoria, String nombreMarca, String nombreUnidadMedida) {
        // Obtener el panel de información (panelInfo) de la tarjeta
        JPanel panelInfo = (JPanel) tarjeta.getComponent(1); // El segundo componente es panelInfo

        // Actualizar las etiquetas dentro de panelInfo
        JLabel lblNombre = (JLabel) panelInfo.getComponent(0);
        lblNombre.setText(material.getNombre());

        JLabel lblCategoria = (JLabel) panelInfo.getComponent(1);
        lblCategoria.setText("Categoría: " + nombreCategoria);

        JLabel lblEstado = (JLabel) panelInfo.getComponent(2);
        lblEstado.setText("Estado: " + material.getEstado());
        // Aplicar color según el estado
        if (material.getEstado() != null) {
            String estado = material.getEstado().toLowerCase();
            if (estado.contains("disponible")) {
                lblEstado.setForeground(new Color(50, 200, 50));
            } else if (estado.contains("reparación") || estado.contains("reparacion")) {
                lblEstado.setForeground(new Color(255, 140, 0));
            } else if (estado.contains("dañado") || estado.contains("danado")) {
                lblEstado.setForeground(new Color(255, 50, 50));
            } else {
                lblEstado.setForeground(Color.WHITE);
            }
        } else {
            lblEstado.setForeground(Color.WHITE);
        }

        // Actualizar la imagen si es necesario
        JPanel panelImagen = (JPanel) tarjeta.getComponent(0); // El primer componente es panelImagen
        JLabel lblImagen = (JLabel) panelImagen.getComponent(0);
        if (material.getImagen() != null) {
            ImageIcon icon = new ImageIcon(material.getImagen());
            // Redimensionar la imagen al tamaño del panelImagen (210x197)
            Image img = icon.getImage().getScaledInstance(210, 197, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
            lblImagen.setText("");
        } else {
            lblImagen.setIcon(null);
            lblImagen.setText("No imagen");
        }

        // Actualizar los nombres almacenados en la tarjeta
        tarjeta.putClientProperty("material", material);
        tarjeta.putClientProperty("nombreCategoria", nombreCategoria);
        tarjeta.putClientProperty("nombreMarca", nombreMarca);
        tarjeta.putClientProperty("nombreUnidadMedida", nombreUnidadMedida);

        tarjeta.revalidate();
        tarjeta.repaint();
    }

    // Método auxiliar para obtener los nombres de categoría, marca y unidad de medida
    private String[] obtenerDetalles(int idCategoria, int idMarca, int idUnidadMedida) {
        String nombreCategoria = "Sin categoría";
        String nombreMarca = "Sin marca";
        String nombreUnidadMedida = "Sin unidad de medida";

        // Obtener nombre de la categoría
        Ctrl_CategoriaHerramienta ctrlCategoria = new Ctrl_CategoriaHerramienta();
        List<Categoria> categorias = ctrlCategoria.obtenerCategoriasHerramienta();
        for (Categoria cat : categorias) {
            if (cat.getCodigo() == idCategoria) {
                nombreCategoria = cat.getNombre();
                break;
            }
        }

        // Obtener nombre de la marca
        Ctrl_MarcaHerramienta ctrlMarca = new Ctrl_MarcaHerramienta();
        List<Marca> marcas = ctrlMarca.obtenerMarcasHerramienta();
        for (Marca m : marcas) {
            if (m.getCodigo() == idMarca) {
                nombreMarca = m.getNombre();
                break;
            }
        }

        // Obtener nombre de la unidad de medida
        Ctrl_UnidadHerramienta ctrlUnidad = new Ctrl_UnidadHerramienta();
        List<Unidad> unidades = ctrlUnidad.obtenerUnidadesHerramienta();
        for (Unidad um : unidades) {
            if (um.getCodigo() == idUnidadMedida) {
                nombreUnidadMedida = um.getNombre();
                break;
            }
        }

        return new String[]{nombreCategoria, nombreMarca, nombreUnidadMedida};
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelprincipal = new javax.swing.JPanel();
        txtBuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        btnNuevo = new rojeru_san.RSButtonRiple();
        principalPanel = new javax.swing.JPanel();
        btnUnidad = new RSMaterialComponent.RSButtonShape();
        btnCategoria = new RSMaterialComponent.RSButtonShape();
        btnMarcas = new RSMaterialComponent.RSButtonShape();
        cmbCategoria = new RSMaterialComponent.RSComboBoxMaterial();

        setPreferredSize(new java.awt.Dimension(1240, 580));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelprincipal.setBackground(new java.awt.Color(255, 255, 255));
        panelprincipal.setPreferredSize(new java.awt.Dimension(1290, 730));
        panelprincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        panelprincipal.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 420, 30));

        btnNuevo.setBackground(new java.awt.Color(46, 49, 82));
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus (2).png"))); // NOI18N
        btnNuevo.setText(" Nuevo");
        btnNuevo.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        panelprincipal.add(btnNuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 90, 110, 30));

        principalPanel.setBackground(new java.awt.Color(245, 246, 250));
        principalPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelprincipal.add(principalPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 1210, 550));

        btnUnidad.setBackground(new java.awt.Color(46, 49, 82));
        btnUnidad.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnUnidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/medida.png"))); // NOI18N
        btnUnidad.setText(" Unidad medida");
        btnUnidad.setToolTipText("");
        btnUnidad.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnUnidad.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnUnidad.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnUnidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnidadActionPerformed(evt);
            }
        });
        panelprincipal.add(btnUnidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 20, 170, 40));

        btnCategoria.setBackground(new java.awt.Color(46, 49, 82));
        btnCategoria.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnCategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/categorizacion.png"))); // NOI18N
        btnCategoria.setText("Categorias");
        btnCategoria.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnCategoria.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnCategoria.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriaActionPerformed(evt);
            }
        });
        panelprincipal.add(btnCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 140, 40));

        btnMarcas.setBackground(new java.awt.Color(46, 49, 82));
        btnMarcas.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnMarcas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/marca-comercial (1).png"))); // NOI18N
        btnMarcas.setText("Marcas");
        btnMarcas.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnMarcas.setFont(new java.awt.Font("Roboto Bold", 1, 16)); // NOI18N
        btnMarcas.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnMarcas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcasActionPerformed(evt);
            }
        });
        panelprincipal.add(btnMarcas, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 20, 120, 40));

        cmbCategoria.setForeground(new java.awt.Color(153, 153, 153));
        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una categoria:" }));
        cmbCategoria.setColorMaterial(new java.awt.Color(153, 153, 153));
        cmbCategoria.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cmbCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoriaActionPerformed(evt);
            }
        });
        panelprincipal.add(cmbCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 90, 280, 30));

        add(panelprincipal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1290, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        herramientasNuevo dialog = new herramientasNuevo(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (dialog.materialGuardado) {
            HerramientaDatos material = dialog.material;
            int idGenerado = ctrlInventario.insertar(material); // Obtener el ID generado

            if (idGenerado > 0) { // Si el ID es mayor a 0, la inserción fue exitosa
                // Actualizar el ID del material con el valor generado
                material.setIdInventario(idGenerado);

                // Obtener los nombres de categoría, marca y unidad de medida para el nuevo material
                String[] detalles = obtenerDetalles(material.getIdCategoria(), material.getIdMarca(), material.getIdUnidadMedida());
                agregarMaterial(material, detalles[0], detalles[1], detalles[2]);
                JOptionPane.showMessageDialog(null, "Material agregado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al agregar el material.");
            }
        }
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnUnidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnidadActionPerformed
        herramientaUnidad dialog = new herramientaUnidad(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnUnidadActionPerformed

    private void btnCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriaActionPerformed
        herramientasCategoria dialog = new herramientasCategoria(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnCategoriaActionPerformed

    private void btnMarcasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcasActionPerformed
        herramientaMarca dialog = new herramientaMarca(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnMarcasActionPerformed

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCategoriaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSButtonShape btnCategoria;
    private RSMaterialComponent.RSButtonShape btnMarcas;
    private rojeru_san.RSButtonRiple btnNuevo;
    private RSMaterialComponent.RSButtonShape btnUnidad;
    private RSMaterialComponent.RSComboBoxMaterial cmbCategoria;
    private javax.swing.JPanel panelprincipal;
    private javax.swing.JPanel principalPanel;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtBuscar;
    // End of variables declaration//GEN-END:variables
}
