/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista.Ventas;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import modelo.PedidoDetalle;
import vista.Inventario0.NumberFormatFilterInventario;
import vista.alertas.IngreseNumeroValido;
import vista.alertas.MaterialPrecioNega;
import vista.alertas.PedidoAlertasmedidas;
import vista.alertas.Pedidoanadirdescripcion;
import vista.alertas.PedidocantidadInvalida;
import vista.alertas.Pedidomayorquecero;

/**
 *
 * @author ZenBook
 */
public class nuevoSubdetalles extends javax.swing.JDialog {

    private String[] datos; // Almacena los datos ingresados
    private PedidoDetalle detalle; // Para almacenar el detalle creado
    private boolean confirmado = false; // Para saber si se confirmó la entrada
    private static final List<String> UNIDADES = Arrays.asList("cm", "m", "mm", "cm²", "cm³", "m²", "m³", "in", "ft");

    /**
     * Creates new form nuevoSubdetalles
     */
    public nuevoSubdetalles(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        ((AbstractDocument) txtPrecio.getDocument()).setDocumentFilter(new NumberFormatFilterInventario());
        ((AbstractDocument) txtCantidad.getDocument()).setDocumentFilter(new NumberFormatFilterInventario());

        // Agrega esta línea para configurar el autocompletado
        configurarAutocompletadoUnidades();

        // Agregar esto en el constructor o método de inicialización de tu clase
        txtCantidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtCantidad.getText();

                // Permitir: dígitos, coma, backspace y delete
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                    JOptionPane.showMessageDialog(null,
                            "Solo se permiten números enteros",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

            }
        });

        // Agregar esto en el constructor o método de inicialización de tu clase
        txtPrecio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtCantidad.getText();

                // Permitir: dígitos, coma, backspace y delete
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                    JOptionPane.showMessageDialog(null,
                            "Solo se permiten números enteros",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

            }
        });

        txtAlto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Bloquear solo el punto
                if (c == '.') {
                    e.consume(); // Ignora el punto
                    JOptionPane.showMessageDialog(null,
                            "No se permiten puntos. Use coma para decimales.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        txtAncho.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Bloquear solo el punto
                if (c == '.') {
                    e.consume(); // Ignora el punto
                    JOptionPane.showMessageDialog(null,
                            "No se permiten puntos. Use coma para decimales.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        txtProfundidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Bloquear solo el punto
                if (c == '.') {
                    e.consume(); // Ignora el punto
                    JOptionPane.showMessageDialog(null,
                            "No se permiten puntos. Use coma para decimales.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }

    // Método para obtener el detalle creado
    public PedidoDetalle getDetalle() {
        return confirmado ? detalle : null;
    }

    private void formatNumberField() {
        // Eliminar cualquier carácter que no sea número
        String text = txtPrecio.getText().replaceAll("[^0-9]", "");

        if (!text.isEmpty()) {
            try {
                long amount = Long.parseLong(text);

                // Formatear el número con puntos como separadores de miles
                String formatted = String.format("%,d", amount).replace(",", ".");

                // Evitar bucles infinitos al comparar antes de actualizar
                if (!formatted.equals(txtPrecio.getText())) {
                    txtPrecio.setText(formatted);
                    txtPrecio.setCaretPosition(formatted.length());
                }
            } catch (NumberFormatException e) {
                // Ignorar errores de formato
            }
        }
    }

    // Y añade este método para formatear el campo de cantidad
    private void formatCantidadField() {
        String text = txtCantidad.getText().replaceAll("[^0-9]", "");

        if (!text.isEmpty()) {
            try {
                long cantidad = Long.parseLong(text);
                // Formatear el número con puntos como separadores de miles
                String formatted = String.format("%,d", cantidad).replace(",", ".");

                if (!formatted.equals(txtCantidad.getText())) {
                    txtCantidad.setText(formatted);
                    txtCantidad.setCaretPosition(formatted.length());
                }
            } catch (NumberFormatException e) {
                // Ignorar errores de formato
            }
        }
    }

    private void configurarAutocompletadoUnidades() {
        configurarCampoConAutocompletado(txtAlto);
        configurarCampoConAutocompletado(txtAncho);
        configurarCampoConAutocompletado(txtProfundidad);
    }

    private void configurarCampoConAutocompletado(RSMaterialComponent.RSTextFieldMaterial campo) {
        JPopupMenu popupMenu = new JPopupMenu();
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String text = campo.getText().trim();
                if (e.getKeyChar() == ' ') {
                    String[] partes = text.split("\\s+");
                    if (partes.length == 1 && isNumeric(partes[0])) {
                        popupMenu.removeAll();
                        for (String unidad : UNIDADES) {
                            JMenuItem item = new JMenuItem(unidad);
                            item.addActionListener(evt -> {
                                campo.setText(partes[0] + " " + unidad);
                                popupMenu.setVisible(false);
                            });
                            popupMenu.add(item);
                        }
                        popupMenu.show(campo, campo.getWidth() / 2, campo.getHeight());
                    }
                } else if (text.contains(" ")) {
                    String[] partes = text.split("\\s+");
                    if (partes.length == 2 && isNumeric(partes[0])) {
                        String letra = String.valueOf(e.getKeyChar()).toLowerCase();
                        popupMenu.removeAll();
                        for (String unidad : UNIDADES) {
                            if (unidad.toLowerCase().startsWith(letra)) {
                                JMenuItem item = new JMenuItem(unidad);
                                item.addActionListener(evt -> {
                                    campo.setText(partes[0] + " " + unidad);
                                    popupMenu.setVisible(false);
                                });
                                popupMenu.add(item);
                            }
                        }
                        if (popupMenu.getComponentCount() > 0) {
                            popupMenu.show(campo, campo.getWidth() / 2, campo.getHeight());
                        } else {
                            popupMenu.setVisible(false);
                        }
                    }
                }
            }
        });
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d*([,\\.]\\d+)?"); // Acepta números enteros o con decimales (usando coma o punto)
    }

    private boolean esMedidaValida(String medida) {
        if (medida.trim().isEmpty()) {
            return true;
        }
        String[] partes = medida.trim().split("\\s+");
        if (partes.length < 1 || partes.length > 2) {
            return false;
        }
        try {
            Double.parseDouble(partes[0].replace(",", ".")); // Convertir coma a punto para parseo
            if (partes.length == 2) {
                String unidad = partes[1];
                return UNIDADES.contains(unidad);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
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
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPrecio = new RSMaterialComponent.RSTextFieldMaterial();
        txtProfundidad = new RSMaterialComponent.RSTextFieldMaterial();
        txtCantidad = new RSMaterialComponent.RSTextFieldMaterial();
        btnGuardar = new RSMaterialComponent.RSButtonShape();
        txtAlto = new RSMaterialComponent.RSTextFieldMaterial();
        txtAncho = new RSMaterialComponent.RSTextFieldMaterial();
        btnSalir = new rojeru_san.RSButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(175, 175, 175), 1, true));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setText("Agregar detalle pedido");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 9, -1, -1));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 40, 280, 10));

        txtDescripcion.setColumns(10);
        txtDescripcion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setRows(1);
        txtDescripcion.setTabSize(1);
        txtDescripcion.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtDescripcion);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 270, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Precio unitario:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Descripción:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Dimensiones:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Cantidad:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 70, -1));

        txtPrecio.setForeground(new java.awt.Color(0, 0, 0));
        txtPrecio.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtPrecio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPrecio.setPhColor(new java.awt.Color(0, 0, 0));
        txtPrecio.setPlaceholder("Ingrese el precio...");
        txtPrecio.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioActionPerformed(evt);
            }
        });
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPrecioKeyReleased(evt);
            }
        });
        jPanel1.add(txtPrecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 270, 30));

        txtProfundidad.setForeground(new java.awt.Color(0, 0, 0));
        txtProfundidad.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtProfundidad.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtProfundidad.setPhColor(new java.awt.Color(0, 0, 0));
        txtProfundidad.setPlaceholder("Profundidad");
        txtProfundidad.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtProfundidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProfundidadActionPerformed(evt);
            }
        });
        jPanel1.add(txtProfundidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 230, 90, 30));

        txtCantidad.setForeground(new java.awt.Color(0, 0, 0));
        txtCantidad.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtCantidad.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCantidad.setPhColor(new java.awt.Color(0, 0, 0));
        txtCantidad.setPlaceholder("Ingrese cantidad...");
        txtCantidad.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadActionPerformed(evt);
            }
        });
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadKeyReleased(evt);
            }
        });
        jPanel1.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 270, 30));

        btnGuardar.setBackground(new java.awt.Color(46, 49, 82));
        btnGuardar.setText("Guardar");
        btnGuardar.setBackgroundHover(new java.awt.Color(0, 135, 0));
        btnGuardar.setFont(new java.awt.Font("Roboto Bold", 1, 15)); // NOI18N
        btnGuardar.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnGuardar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jPanel1.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 355, 97, 28));

        txtAlto.setForeground(new java.awt.Color(0, 0, 0));
        txtAlto.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtAlto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAlto.setPhColor(new java.awt.Color(0, 0, 0));
        txtAlto.setPlaceholder("Alto");
        txtAlto.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtAlto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAltoActionPerformed(evt);
            }
        });
        jPanel1.add(txtAlto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 80, 30));

        txtAncho.setForeground(new java.awt.Color(0, 0, 0));
        txtAncho.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtAncho.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAncho.setPhColor(new java.awt.Color(0, 0, 0));
        txtAncho.setPlaceholder("Ancho");
        txtAncho.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtAncho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAnchoActionPerformed(evt);
            }
        });
        jPanel1.add(txtAncho, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 80, 30));

        btnSalir.setBackground(new java.awt.Color(255, 255, 255));
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x (3).png"))); // NOI18N
        btnSalir.setColorHover(new java.awt.Color(211, 0, 52));
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSalirMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSalirMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSalirMouseExited(evt);
            }
        });
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        jPanel1.add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 2, 30, 25));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 310, 394));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void txtProfundidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProfundidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProfundidadActionPerformed

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Validar los campos
        String descripcion = txtDescripcion.getText().trim();
        String cantidadStr = txtCantidad.getText().replace(".", "").trim();
        String alto = txtAlto.getText().trim();
        String ancho = txtAncho.getText().trim();
        String profundidad = txtProfundidad.getText().trim();
        String precioUnitarioStr = txtPrecio.getText().replace(".", "").trim();

        if (descripcion.isEmpty()) {
            Pedidoanadirdescripcion dialog = new Pedidoanadirdescripcion((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        // Validar medidas (alto y ancho son obligatorios, profundidad es opcional)
        if (alto.isEmpty() || ancho.isEmpty()) {
            PedidoAlertasmedidas dialog = new PedidoAlertasmedidas((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        // Construir dimensiones: si profundidad está vacío, solo usar alto x ancho
        String dimensiones = alto + " x " + ancho;
        if (!profundidad.isEmpty()) {
            dimensiones += " x " + profundidad;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                Pedidomayorquecero dialog = new Pedidomayorquecero((Frame) this.getParent(), true);
                dialog.setVisible(true);
                return;
            }
        } catch (NumberFormatException e) {
            PedidocantidadInvalida dialog = new PedidocantidadInvalida((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        // Validar y obtener la cantidad
        int precioUnitario;
        try {
            precioUnitario = Integer.parseInt(txtPrecio.getText().replace(".", "").trim());
            if (precioUnitario < 0) {
                MaterialPrecioNega dialog = new MaterialPrecioNega((Frame) this.getParent(), true);
                dialog.setVisible(true);
                return;
            }
        } catch (NumberFormatException e) {
            IngreseNumeroValido dialog = new IngreseNumeroValido((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        // Calcular subtotal
        double subtotal = cantidad * precioUnitario;

        // Crear el detalle
        detalle = new PedidoDetalle();
        detalle.setDescripcion(descripcion);
        detalle.setCantidad(cantidad);
        detalle.setDimensiones(dimensiones);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(subtotal);
        detalle.setTotal(subtotal);

        confirmado = true;
        dispose();

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyReleased
        formatCantidadField();
    }//GEN-LAST:event_txtCantidadKeyReleased

    private void txtPrecioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioKeyReleased
        formatNumberField();
    }//GEN-LAST:event_txtPrecioKeyReleased

    private void txtAltoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAltoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAltoActionPerformed

    private void txtAnchoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAnchoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAnchoActionPerformed

    private void btnSalirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSalirMouseClicked

    private void btnSalirMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseEntered
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x (1).png")));
    }//GEN-LAST:event_btnSalirMouseEntered

    private void btnSalirMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseExited
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x (3).png")));        // TODO add your handling code here:
    }//GEN-LAST:event_btnSalirMouseExited

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        confirmado = false;
        setVisible(false); // Cerrar el diálogo
    }//GEN-LAST:event_btnSalirActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(nuevoSubdetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(nuevoSubdetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(nuevoSubdetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(nuevoSubdetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                nuevoSubdetalles dialog = new nuevoSubdetalles(new javax.swing.JFrame(), true);
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
    private RSMaterialComponent.RSButtonShape btnGuardar;
    private rojeru_san.RSButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private RSMaterialComponent.RSTextFieldMaterial txtAlto;
    private RSMaterialComponent.RSTextFieldMaterial txtAncho;
    private RSMaterialComponent.RSTextFieldMaterial txtCantidad;
    private javax.swing.JTextArea txtDescripcion;
    private RSMaterialComponent.RSTextFieldMaterial txtPrecio;
    private RSMaterialComponent.RSTextFieldMaterial txtProfundidad;
    // End of variables declaration//GEN-END:variables
}
