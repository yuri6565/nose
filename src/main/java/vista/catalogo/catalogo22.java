package vista.catalogo;

import controlador.Ctrl_catalogocategoria;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import newscomponents.RSPanelEffect;
import rojeru_san.efectos.ValoresEnum;
import rojerusan.RSLabelIcon;
import rojerusan.RSPanelImage;
import vista.TemaManager;
import vista.alertas.alertaEliminar1;
import vista.alertas.alertaEliminar11;
import vista.alertas.alertaEliminarcategoria111;
import vista.alertas.borradooconexito;

import javax.swing.JFrame;

import javax.swing.JFrame;

import javax.swing.JFrame;

import javax.swing.JFrame;

import vista.alertas.alertaEliminar11;

public class catalogo22 extends javax.swing.JPanel {

    private Map<Integer, Boolean> seleccionados = new HashMap<>();
    private int cardX = 50;
    private int currentPage = 0;
    private Ctrl_catalogocategoria controladorCategoria = new Ctrl_catalogocategoria();
    private static final int CATEGORIAS_POR_PAGINA = 10;
    private java.util.List<modelo.Catalogocategoria> todasLasCategorias = new java.util.ArrayList<>();
    private int idCategoria;
    private String nombreCategoria;
    private JPanel parentPanel;

  public catalogo22(JFrame jFrame, boolean par, JPanel parentPanel) {
        this.parentPanel = parentPanel;
        setOpaque(true); // Ensure catalogo22 is opaque
        initComponents();
        jPanel1.setOpaque(true); // Ensure jPanel1 is opaque
        jPanel2.setOpaque(true); // Ensure jPanel2 is opaque
        panelCards.setOpaque(true); // Ensure panelCards is opaque
        jPanel2.add(panelCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 150, 900, 530));
        cargarCategoriasDesdeBD();
        TemaManager.getInstance().addThemeChangeListener(this::aplicarTema);
        aplicarTema();
    }

    public void mostrarProductos(int idCategoria, String nombreCategoria) {
        // Ocultar jPanel2 para quitar el contenedor de categorías
        jPanel2.setVisible(false);

        // Limpiar y preparar panelCards para mostrar productos
        removeAll(); // Limpiar todo el contenido de catalogo22
        setLayout(new BorderLayout()); // Usar BorderLayout para el contenedor principal

        // Crear el panel de productos
        Productos productosPanel = new Productos(idCategoria, nombreCategoria, parentPanel);
        add(productosPanel, BorderLayout.CENTER);

        // Ajustar tamaño dinámicamente (opcional, ya que Productos lo manejará)
        revalidate();
        repaint();
    }

    // Método para volver a la vista de categorías
    public void volverACategorias() {
        panelCards.removeAll();
        panelCards.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Restaurar layout original
        panelCards.setPreferredSize(new Dimension(970, 450)); // Restaurar tamaño original
        cargarCategoriasDesdeBD();
        jPanel2.setPreferredSize(new Dimension(1320, 700)); // Restaurar tamaño original de jPanel2
        jPanel2.revalidate();
        jPanel2.repaint();
        TemaManager.getInstance().addThemeChangeListener(() -> {
            aplicarTema(); // Update theme when it changes
        });
    }

    // ... (keep initComponents, action listeners, and cargartablacliente as they are)
 public void aplicarTema() {
    boolean oscuro = TemaManager.getInstance().isOscuro();

    if (oscuro) {
        Color fondo = new Color(21, 21, 33);
        Color primario = new Color(40, 60, 150);
        Color texto = Color.WHITE;

        // Apply to the main panel (catalogo22)
        setBackground(fondo);
        // Apply to nested panels
        jPanel1.setBackground(fondo);
        jPanel2.setBackground(fondo);
        panelCards.setBackground(fondo);
        // Apply to text field
        txtBuscar.setOpaque(true);
        txtBuscar.setBackground(fondo);
        txtBuscar.setForeground(texto);
        txtBuscar.setColorIcon(texto);
        txtBuscar.setPhColor(Color.LIGHT_GRAY);
        txtBuscar.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        // Apply to checkbox
        rSButtonMaterialRippleIcon1.setBackground(fondo);
        rSCheckBox1.setBackground(fondo);
        rSCheckBox1.setColorUnCheck(texto);
        rSCheckBox1.setColorCheck(texto);
        rSCheckBox1.setForeground(texto);
        // Apply to pagination label
        paginacion.setForeground(texto);
        // Apply to dynamic category labels
        actualizarColoresLabels(texto);
    } else {
        Color fondo = new Color(247, 247, 255); // Use the desired light blue-white color
        Color texto = Color.BLACK;
        Color primario = new Color(72, 92, 188);
 rSButtonMaterialRippleIcon1.setBackground(fondo);
        // Apply to the main panel (catalogo22)
        setBackground(fondo);
        // Apply to nested panels
        jPanel1.setBackground(fondo);
        jPanel2.setBackground(fondo);
        panelCards.setBackground(new Color(255,255,255)); 
        // Apply to text field
        txtBuscar.setOpaque(true);
        txtBuscar.setBackground(fondo); // Match the panel background
        txtBuscar.setForeground(texto);
        txtBuscar.setColorIcon(texto);
        txtBuscar.setPhColor(Color.DARK_GRAY); // Contrast with new background
        txtBuscar.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        // Apply to checkbox
        rSCheckBox1.setBackground(fondo);
        rSCheckBox1.setColorUnCheck(texto);
        rSCheckBox1.setColorCheck(texto);
        rSCheckBox1.setForeground(texto);
        // Apply to pagination label
        paginacion.setForeground(texto);
        // Apply to dynamic category labels
        actualizarColoresLabels(texto);
    }

    // Ensure all components are repainted
    revalidate();
    repaint();
}

// Método auxiliar para actualizar el color de los JLabel dinámicos
private void actualizarColoresLabels(Color color) {
    for (Component comp : panelCards.getComponents()) {
        if (comp instanceof JPanel) { // cardWrapper es un JPanel
            JPanel cardWrapper = (JPanel) comp;
            for (Component subComp : cardWrapper.getComponents()) {
                if (subComp instanceof JPanel) {
                    JPanel subPanel = (JPanel) subComp;
                    for (Component innerComp : subPanel.getComponents()) {
                        if (innerComp instanceof JLabel) { // Encontramos el JLabel (tituloCategoria)
                            ((JLabel) innerComp).setForeground(color);
                        }
                    }
                }
            }
        }
    }
    panelCards.revalidate();
    panelCards.repaint();
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        panelCards = new javax.swing.JPanel();
        txtBuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        Añadir5 = new rojeru_san.RSButtonRiple();
        Añadir4 = new rojeru_san.RSButtonRiple();
        rSButtonMaterialRippleIcon1 = new RSMaterialComponent.RSButtonMaterialRippleIcon();
        paginacion = new javax.swing.JLabel();
        rSCheckBox1 = new rojerusan.RSCheckBox();
        btnNuevo1 = new RSMaterialComponent.RSButtonShape();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(242, 247, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 247, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(312, 61, 1278, -1));

        jPanel2.setBackground(new java.awt.Color(242, 247, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCards.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelCardsLayout = new javax.swing.GroupLayout(panelCards);
        panelCards.setLayout(panelCardsLayout);
        panelCardsLayout.setHorizontalGroup(
            panelCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelCardsLayout.setVerticalGroup(
            panelCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel2.add(panelCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 190, 970, 450));

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
        jPanel2.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 430, 40));

        Añadir5.setBackground(new java.awt.Color(46, 49, 82));
        Añadir5.setText("Anterior");
        Añadir5.setColorHover(new java.awt.Color(0, 153, 51));
        Añadir5.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        Añadir5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Añadir5ActionPerformed(evt);
            }
        });
        jPanel2.add(Añadir5, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 710, 98, 40));

        Añadir4.setBackground(new java.awt.Color(46, 49, 82));
        Añadir4.setText("Siguiente");
        Añadir4.setColorHover(new java.awt.Color(0, 153, 51));
        Añadir4.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        Añadir4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Añadir4ActionPerformed(evt);
            }
        });
        jPanel2.add(Añadir4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 710, 98, 40));

        rSButtonMaterialRippleIcon1.setBackground(new java.awt.Color(242, 247, 255));
        rSButtonMaterialRippleIcon1.setForeground(new java.awt.Color(255, 51, 51));
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
        jPanel2.add(rSButtonMaterialRippleIcon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 710, 40, 40));

        paginacion.setBackground(new java.awt.Color(0, 0, 0));
        paginacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        paginacion.setForeground(new java.awt.Color(51, 51, 51));
        paginacion.setText("Escritorio");
        paginacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paginacionMouseClicked(evt);
            }
        });
        jPanel2.add(paginacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 710, -1, -1));

        rSCheckBox1.setForeground(new java.awt.Color(0, 0, 0));
        rSCheckBox1.setText("Seleccionar todo");
        rSCheckBox1.setColorCheck(new java.awt.Color(255, 255, 255));
        rSCheckBox1.setColorUnCheck(new java.awt.Color(204, 204, 204));
        rSCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSCheckBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(rSCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 700, 158, 29));

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
        jPanel2.add(btnNuevo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 130, 110, 30));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -50, 1620, 800));
        jPanel2.getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
    
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void Añadir4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Añadir4ActionPerformed
        int totalPaginas = (int) Math.ceil((double) todasLasCategorias.size() / CATEGORIAS_POR_PAGINA);
        if (currentPage < totalPaginas - 1) {
            currentPage++;
            mostrarPagina(currentPage);
        }
    }//GEN-LAST:event_Añadir4ActionPerformed

    private void Añadir5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Añadir5ActionPerformed
        if (currentPage > 0) {
            currentPage--;
            mostrarPagina(currentPage);
        }
    }//GEN-LAST:event_Añadir5ActionPerformed

    private void rSCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSCheckBox1ActionPerformed
        boolean seleccionarTodo = rSCheckBox1.isSelected();
        List<String> categoriasConProductos = new ArrayList<>();
        List<Integer> idsConProductos = new ArrayList<>();

        // Inicializar todas las categorías según el estado del checkbox
        for (modelo.Catalogocategoria categoria : todasLasCategorias) {
            int id = categoria.getIdCategoria();
            seleccionados.put(id, seleccionarTodo);
        }

        // Si se seleccionó "Seleccionar todo", verificar y desmarcar categorías con productos
        if (seleccionarTodo) {
            for (modelo.Catalogocategoria categoria : todasLasCategorias) {
                int id = categoria.getIdCategoria();
                if (controladorCategoria.tieneProductos(id)) {
                    categoriasConProductos.add(categoria.getNombre());
                    idsConProductos.add(id);
                    seleccionados.put(id, false); // Desmarcar categorías con productos
                }
            }

            // Mostrar alerta si hay categorías con productos
            if (!categoriasConProductos.isEmpty()) {
                String mensaje = "No se pueden seleccionar las siguientes categorías porque contienen productos:\n"
                        + String.join(", ", categoriasConProductos);
                alertaEliminar11 dialog = new alertaEliminar11(new javax.swing.JFrame(), true,
                        "Error",
                        mensaje,
                        "/warning-triangle-sign-free-vector-removebg-preview.png");
                dialog.setVisible(true);
            }
        } else {
            // Si se desmarca "Seleccionar todo", deseleccionar todas
            for (modelo.Catalogocategoria categoria : todasLasCategorias) {
                seleccionados.put(categoria.getIdCategoria(), false);
            }
        }

        // Actualizar la página visible y el estado de "Seleccionar todo"
        mostrarPagina(currentPage);
        actualizarEstadoSeleccionarTodo();


    }//GEN-LAST:event_rSCheckBox1ActionPerformed

    private void paginacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paginacionMouseClicked
        // TODO add your handling code here: 777777
    }//GEN-LAST:event_paginacionMouseClicked

    private void rSButtonMaterialRippleIcon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSButtonMaterialRippleIcon1ActionPerformed
        List<Integer> idsAEliminar = new ArrayList<>();
        List<String> categoriasConProductos = new ArrayList<>();
        List<String> nombresCategoriasAEliminar = new ArrayList<>();
        List<Integer> idsConProductos = new ArrayList<>();

        // Recolectar IDs y nombres de categorías seleccionadas
        for (Map.Entry<Integer, Boolean> entry : seleccionados.entrySet()) {
            if (entry.getValue()) {
                int id = entry.getKey();
                modelo.Catalogocategoria cat = todasLasCategorias.stream()
                        .filter(categoria -> categoria.getIdCategoria() == id)
                        .findFirst()
                        .orElse(null);
                if (cat != null) {
                    if (controladorCategoria.tieneProductos(id)) {
                        categoriasConProductos.add(cat.getNombre());
                        idsConProductos.add(id);
                    } else {
                        idsAEliminar.add(id);
                        nombresCategoriasAEliminar.add(cat.getNombre());
                    }
                }
            }
        }

        // Si hay categorías con productos, mostrar alerta y deseleccionarlas
        if (!categoriasConProductos.isEmpty()) {
            String mensaje = "No se pueden eliminar las siguientes categorías porque contienen productos:\n"
                    + String.join(", ", categoriasConProductos);
            alertaEliminar11 dialog = new alertaEliminar11(new javax.swing.JFrame(), true,
                    "Error",
                    mensaje,
                    "/warning-triangle-sign-free-vector-removebg-preview.png");
            dialog.setVisible(true);

            // Deseleccionar categorías con productos
            for (Integer id : idsConProductos) {
                seleccionados.put(id, false);
            }
            mostrarPagina(currentPage);
            actualizarEstadoSeleccionarTodo();
            return;
        }

        // Si no hay categorías seleccionadas, mostrar mensaje
        if (idsAEliminar.isEmpty()) {
            alertaEliminar11 dialog = new alertaEliminar11(new javax.swing.JFrame(), true,
                    "Sin selección",
                    "No hay categorías seleccionadas para eliminar.",
                    "/warning-triangle-sign-free-vector-removebg-preview.png");
            dialog.setVisible(true);
            return;
        }

        // Confirmación para eliminar categorías
        String mensaje = "¿Está seguro que desea eliminar "
                + (idsAEliminar.size() > 1 ? "estas " + idsAEliminar.size() + " categorías?" : "esta categoría?")
                + "\nCategorías: " + String.join(", ", nombresCategoriasAEliminar);
        alertaEliminarcategoria111 dialog = new alertaEliminarcategoria111(new javax.swing.JFrame(), true,
                "¿Estás seguro?",
                mensaje,
                "/warning-triangle-sign-free-vector-removebg-preview.png");
        boolean confirm = dialog.confirmarEliminar();

        if (confirm) {
            boolean todasEliminadas = true;
            for (Integer id : idsAEliminar) {
                boolean eliminado = controladorCategoria.eliminar(id);
                if (!eliminado) {
                    todasEliminadas = false;
                    JOptionPane.showMessageDialog(this, "Error al eliminar la categoría con ID: " + id);
                } else {
                    todasLasCategorias.removeIf(c -> c.getIdCategoria() == id);
                    seleccionados.remove(id);
                }
            }

            if (todasEliminadas) {
                borradooconexito exito = new borradooconexito();
                exito.setVisible(true);
            }
            mostrarPagina(currentPage);
        }
    }//GEN-LAST:event_rSButtonMaterialRippleIcon1ActionPerformed

    private void btnNuevo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevo1ActionPerformed
      catalogocategoria cat = new catalogocategoria((JFrame) getTopLevelAncestor(), true);
        cat.setVisible(true);

        String rutaImagen = cat.getRutaImagenSeleccionada();
        String categoriaNombre = cat.getCategoriaNombre();

        if (!categoriaNombre.isEmpty()) {
            try {
                byte[] imagenBytes = null;
                if (rutaImagen != null && !rutaImagen.isEmpty()) {
                    java.nio.file.Path path = java.nio.file.Paths.get(rutaImagen);
                    imagenBytes = java.nio.file.Files.readAllBytes(path);
                }

                modelo.Catalogocategoria categoria = new modelo.Catalogocategoria();
                categoria.setNombre(categoriaNombre);
                categoria.setImagen(imagenBytes);

                boolean guardado = controladorCategoria.guardar(categoria);

                if (guardado) {
                    cargarCategoriasDesdeBD(); // Will now preserve currentPage
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar la categoría en la base de datos.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al procesar la imagen: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debes escribir un nombre para la categoría.");
        }
    }//GEN-LAST:event_btnNuevo1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojeru_san.RSButtonRiple Añadir4;
    private rojeru_san.RSButtonRiple Añadir5;
    private RSMaterialComponent.RSButtonShape btnNuevo1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel paginacion;
    private javax.swing.JPanel panelCards;
    private RSMaterialComponent.RSButtonMaterialRippleIcon rSButtonMaterialRippleIcon1;
    private rojerusan.RSCheckBox rSCheckBox1;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtBuscar;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration                   

    private void agregarCardDesdeBytes(ImageIcon imagen, String categoriaNombre, modelo.Catalogocategoria categoria) {
        JPanel cardWrapper = new JPanel();
        cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
        cardWrapper.setOpaque(false);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setPreferredSize(new Dimension(30, 30));
        checkBox.putClientProperty("categoriaId", categoria.getIdCategoria());
        boolean seleccionada = seleccionados.getOrDefault(categoria.getIdCategoria(), false); // Solo usa el estado de seleccionados
        checkBox.setSelected(seleccionada);
        checkBox.setEnabled(true); // Siempre habilitado
        checkBox.addActionListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            seleccionados.put(categoria.getIdCategoria(), cb.isSelected());
            actualizarEstadoSeleccionarTodo();
        });

        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        checkBoxPanel.setOpaque(false);
        checkBoxPanel.add(checkBox);

        rojerusan.RSPanelCircleImage cardPanel = new rojerusan.RSPanelCircleImage();
        cardPanel.setPreferredSize(new java.awt.Dimension(150, 150));
        cardPanel.setImagen(imagen);
        cardPanel.setColorBorde(new Color(242, 247, 255));
        cardPanel.setOpaque(false);
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    System.out.println("Doble clic en categoría: " + categoria.getNombre() + ", idCategoria: " + categoria.getIdCategoria());
                    int idCategoria = categoria.getIdCategoria();
                    mostrarProductos(idCategoria, categoria.getNombre());
                }
            }
        });

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        iconPanel.setOpaque(false);
        iconPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        RSLabelIcon iconEdit = new RSLabelIcon();
        iconEdit.setIcons(ValoresEnum.ICONS.EDIT);
        iconEdit.setPreferredSize(new Dimension(20, 20));
        iconEdit.setForeground(Color.BLACK);
        iconEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                editarCategoria(categoria);
            }
        });

        RSLabelIcon iconDelete = new RSLabelIcon();
        iconDelete.setIcons(ValoresEnum.ICONS.DELETE);
        iconDelete.setPreferredSize(new Dimension(20, 20));
        iconDelete.setForeground(Color.RED);
        iconDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                eliminar(categoria, cardWrapper);
            }
        });

        iconPanel.add(iconEdit);
        iconPanel.add(iconDelete);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        titlePanel.setOpaque(false);
        JLabel tituloCategoria = new JLabel(categoriaNombre);
        tituloCategoria.setFont(new Font("Century751 BT", 0, 16));
        tituloCategoria.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(tituloCategoria);
        tituloCategoria.setForeground(TemaManager.getInstance().isOscuro() ? Color.WHITE : Color.BLACK);
    titlePanel.add(tituloCategoria);

        cardWrapper.add(Box.createVerticalStrut(5));
        cardWrapper.add(checkBoxPanel);
        cardWrapper.add(cardPanel);
        cardWrapper.add(titlePanel);
        cardWrapper.add(Box.createVerticalStrut(5));
        cardWrapper.add(iconPanel);

        int totalCards = panelCards.getComponentCount();
        int row = totalCards / 5;
        int col = totalCards % 5;
        int cardX = 30 + col * 175;
        int cardY = 10 + row * 250;

        cardWrapper.setBounds(cardX, cardY, 150, 260);
        cardWrapper.setPreferredSize(new Dimension(150, 260));
        panelCards.add(cardWrapper);
        panelCards.revalidate();
        panelCards.repaint();

        int nuevoAlto = (row + 1) * 250 + 10;
        panelCards.setPreferredSize(new Dimension(780, Math.max(nuevoAlto, 450)));
    }

    private void cargarCategoriasDesdeBD() {
        todasLasCategorias = controladorCategoria.obtenerCategorias();
        seleccionados.clear();
        // Calculate total pages
        int totalPaginas = (int) Math.ceil((double) todasLasCategorias.size() / CATEGORIAS_POR_PAGINA);
        // Adjust currentPage if it exceeds the new total pages
        if (currentPage >= totalPaginas && totalPaginas > 0) {
            currentPage = totalPaginas - 1; // Go to the last valid page
        } else if (totalPaginas == 0) {
            currentPage = 0; // No categories, stay at page 0
        }
        // Stay on currentPage otherwise
        mostrarPagina(currentPage);
    }

    private void mostrarPagina(int pagina) {
        panelCards.removeAll();
        panelCards.setLayout(null);

        int inicio = pagina * CATEGORIAS_POR_PAGINA;
        int fin = Math.min(inicio + CATEGORIAS_POR_PAGINA, todasLasCategorias.size());

        for (int i = inicio; i < fin; i++) {
            modelo.Catalogocategoria categoria = todasLasCategorias.get(i);
            ImageIcon icono;
            if (categoria.getImagen() != null && categoria.getImagen().length > 0) {
                icono = new ImageIcon(categoria.getImagen());
            } else {
                // Load default image from resources
                try {
                    icono = new ImageIcon(getClass().getResource("/produccionIcono.png"));
                    if (icono.getIconWidth() == -1) { // Check if image failed to load
                        System.err.println("Default image not found or invalid.");
                        icono = new ImageIcon(); // Empty icon fallback
                    }
                } catch (Exception e) {
                    System.err.println("Error loading default image: " + e.getMessage());
                    icono = new ImageIcon(); // Empty icon fallback
                }
            }
            agregarCardDesdeBytes(icono, categoria.getNombre(), categoria);
        }

        int totalPaginas = (int) Math.ceil((double) todasLasCategorias.size() / CATEGORIAS_POR_PAGINA);
        Añadir5.setEnabled(currentPage > 0);
        Añadir4.setEnabled(currentPage < totalPaginas - 1);
        paginacion.setText("Página " + (currentPage + 1) + " de " + (totalPaginas == 0 ? 1 : totalPaginas));

        panelCards.revalidate();
        panelCards.repaint();
        actualizarEstadoSeleccionarTodo();
    }

    private void eliminar(modelo.Catalogocategoria categoria, JPanel cardWrapper) {
        if (controladorCategoria.tieneProductos(categoria.getIdCategoria())) {
            alertaEliminar11 alertaeli = new alertaEliminar11((JFrame) getTopLevelAncestor(), true, "Error",
                    "No se puede eliminar la categoría '" + categoria.getNombre() + "' porque contiene productos.",
                    "/warning-triangle-sign-free-vector-removebg-preview.png");
            alertaeli.setLocationRelativeTo(this); // Position relative to catalogo22
            alertaeli.setVisible(true);
            return;
        }

        String message = "¿Estás seguro de que desea eliminar la categoría '" + categoria.getNombre() + "'?";
        alertaEliminarcategoria111 dialog = new alertaEliminarcategoria111((JFrame) getTopLevelAncestor(), true, "¿Estás seguro?",
                message, "/warning-triangle-sign-free-vector-removebg-preview.png");
        dialog.setLocationRelativeTo(this); // Position relative to catalogo22
        boolean confirm = dialog.confirmarEliminar();

        if (confirm) {
            boolean eliminado = controladorCategoria.eliminar(categoria.getIdCategoria());
            if (eliminado) {
                todasLasCategorias.removeIf(c -> c.getIdCategoria() == categoria.getIdCategoria());
                seleccionados.remove(categoria.getIdCategoria());
                panelCards.remove(cardWrapper);
                mostrarPagina(currentPage);
                borradooconexito exito = new borradooconexito();
                exito.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la categoría de la base de datos.");
            }
        }
    }

    private void editarCategoria(modelo.Catalogocategoria categoria) {
        // Crear y configurar el diálogo
        catalogocategoria dialogo = new catalogocategoria((JFrame) getTopLevelAncestor(), true);
        dialogo.setCategoriaNombre(categoria.getNombre());
        dialogo.setImagenDesdeBytes(categoria.getImagen());
        dialogo.setVisible(true);

        // Obtener los valores del diálogo
        String nuevoNombre = dialogo.getCategoriaNombre();
        String nuevaRuta = dialogo.getRutaImagenSeleccionada();

        // Depuración: Imprimir los valores devueltos
        System.out.println("nuevoNombre recibido: '" + (nuevoNombre != null ? nuevoNombre : "null") + "'");
        System.out.println("nuevaRuta recibida: '" + (nuevaRuta != null ? nuevaRuta : "null") + "'");

        // Validación estricta del nombre como campo obligatorio
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la categoría es un campo obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Salir del método si el nombre es inválido
        }

        // Actualizar la categoría con el nuevo nombre
        categoria.setNombre(nuevoNombre.trim()); // Usar trim para eliminar espacios innecesarios

        // Manejar la nueva imagen solo si se proporciona una ruta válida
        if (nuevaRuta != null && !nuevaRuta.trim().isEmpty()) {
            try {
                categoria.setImagen(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(nuevaRuta)));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer la nueva imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return; // Salir si falla la lectura de la imagen
            }
        }

        // Intentar actualizar en la base de datos
        boolean actualizado = controladorCategoria.editar(categoria, categoria.getIdCategoria());
        if (actualizado) {
            JOptionPane.showMessageDialog(this, "Categoría actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarCategoriasDesdeBD(); // Recargar las categorías para reflejar los cambios
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la categoría.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstadoSeleccionarTodo() {
        boolean todasSeleccionadas = true;
        boolean algunaSeleccionada = false;
        for (modelo.Catalogocategoria categoria : todasLasCategorias) {
            int id = categoria.getIdCategoria();
            boolean tieneProductos = controladorCategoria.tieneProductos(id);
            boolean seleccionada = seleccionados.getOrDefault(id, false);
            if (!tieneProductos && !seleccionada) {
                todasSeleccionadas = false;
                break;
            } else if (!tieneProductos) {
                algunaSeleccionada = true;
            }
        }
        if (todasSeleccionadas && algunaSeleccionada) {
            rSCheckBox1.setSelected(true);
        } else if (!algunaSeleccionada) {
            rSCheckBox1.setSelected(false);
        } else {
            rSCheckBox1.setSelected(false); // O podrías usar un estado intermedio si el componente lo soporta
        }
    }

}
