/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista.Cotizacion;

import controlador.Ctrl_Producto;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import controlador.Ctrl_Producto;
import controlador.Ctrl_Producto;
import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author tilin del sena
 */
public class InsertarCoti extends javax.swing.JDialog {

    private boolean confirmado = false;
    private String producto;
    private String unidad;
    private String cantidad;
    private String valorUnitario;
    private Ctrl_Producto controladorProducto;

    public InsertarCoti(Frame parent, boolean modal, String unidad1, String valorUnitario1) {
        super(parent, modal);
        initComponents();
        CargarUnidadMed();
        configurarFiltroNumerico(txtValorUni);
        configurarFiltroNumerico(txtCantidad);

        if (valorUnitario1 != null && !valorUnitario1.isEmpty()) {
            txtValorUni.setText(valorUnitario1);
        }
    }

    // Constructor original para nuevo producto
    public InsertarCoti(Frame parent, boolean modal, String unidad1, String valorUnitario1, Ctrl_Producto ctrlProducto) {
        this(parent, modal, unidad1, valorUnitario1); // Reutiliza el constructor básico
        this.controladorProducto = ctrlProducto;
    }

    // Constructor para edición
    public InsertarCoti(Frame parent, boolean modal, String producto, String unidad,
            String cantidad, String valorUnitario) {
        super(parent, modal);
        initComponents();
        CargarUnidadMed();
        txtProducto.setText(producto);
        combox_Unidad.setSelectedItem(unidad);
        txtCantidad.setText(cantidad);
        txtValorUni.setText(valorUnitario);
    }

    private void cargarProductosAutocompletar() {
        if (controladorProducto != null) {
            List<String> productos = controladorProducto.obtenerNombresProductos();
            // Implementa autocompletado aquí si lo necesitas
            // AutoCompleteDecorator.decorate(txtProducto, productos, false);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Object[] getDatosCoti() {
        return new Object[]{
            txtProducto.getText().trim(),
            txtCantidad.getText().trim(),
            txtValorUni.getText().trim(),
            combox_Unidad.getSelectedItem().toString() // Añadir unidad
        };

    }

    private void CargarUnidadMed() {
        combox_Unidad.removeAllItems();
        combox_Unidad.addItem("Seleccione unidad");
        String[] unidades = {"Metro", "Centímetro", "Unidad"};
        for (String unidad : unidades) {
            combox_Unidad.addItem(unidad);
        }
    }

    private void añadirProducto() {
        String producto = txtProducto.getText().trim();
        String unidad = combox_Unidad.getSelectedItem().toString();
        String cantidadTexto = txtCantidad.getText().trim();
        String valorUnitarioTexto = txtValorUni.getText().trim();

        if (producto.isEmpty() || unidad.equals("Seleccione unidad") || cantidadTexto.isEmpty() || valorUnitarioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos del producto", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int cantidad = Integer.parseInt(cantidadTexto);
            double valorUnitario = Double.parseDouble(valorUnitarioTexto);
            double subtotal = cantidad * valorUnitario;
            // Los datos se pasarán al padre a través de getDatosCoti()
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad y valor unitario deben ser numéricos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtProducto.setText("");
        combox_Unidad.setSelectedIndex(0);
        txtCantidad.setText("");
        txtValorUni.setText("");
        txtProducto.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        btnGuardar = new rojeru_san.RSButtonRiple();
        btnCancelar = new rojeru_san.RSButtonRiple();
        txtProducto = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel3 = new javax.swing.JLabel();
        combox_Unidad = new RSMaterialComponent.RSComboBoxMaterial();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCantidad = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel6 = new javax.swing.JLabel();
        txtValorUni = new RSMaterialComponent.RSTextFieldMaterial();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(46, 49, 82));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setBackground(new java.awt.Color(0, 0, 0));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Detalles  Cotizacion:");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 210, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 50));

        btnGuardar.setBackground(new java.awt.Color(46, 49, 82));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus (2).png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 18)); // NOI18N
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jPanel1.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, 140, -1));

        btnCancelar.setBackground(new java.awt.Color(46, 49, 82));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/salida (1).png"))); // NOI18N
        btnCancelar.setText("Volver");
        btnCancelar.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 18)); // NOI18N
        btnCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 260, 140, -1));

        txtProducto.setForeground(new java.awt.Color(0, 0, 0));
        txtProducto.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtProducto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtProducto.setPhColor(new java.awt.Color(0, 0, 0));
        txtProducto.setPlaceholder("Ingrese nombre pedido...");
        txtProducto.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProductoActionPerformed(evt);
            }
        });
        jPanel1.add(txtProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 180, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel3.setText("Pedido :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 90, 30));

        combox_Unidad.setColorMaterial(new java.awt.Color(0, 0, 0));
        combox_Unidad.setFont(new java.awt.Font("Roboto Bold", 0, 14)); // NOI18N
        combox_Unidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_UnidadActionPerformed(evt);
            }
        });
        jPanel1.add(combox_Unidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 180, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setText("Medida:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, 70, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel4.setText("Cantidad:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 90, 30));

        txtCantidad.setForeground(new java.awt.Color(0, 0, 0));
        txtCantidad.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtCantidad.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCantidad.setPhColor(new java.awt.Color(0, 0, 0));
        txtCantidad.setPlaceholder("Ingrese cantidad..");
        txtCantidad.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadActionPerformed(evt);
            }
        });
        jPanel1.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 180, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setText("Valor Unitario:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 150, 120, 30));

        txtValorUni.setForeground(new java.awt.Color(0, 0, 0));
        txtValorUni.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtValorUni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtValorUni.setPhColor(new java.awt.Color(0, 0, 0));
        txtValorUni.setPlaceholder("Ingrese Valor Unitario..");
        txtValorUni.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtValorUni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValorUniActionPerformed(evt);
            }
        });
        jPanel1.add(txtValorUni, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 190, 180, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String producto = txtProducto.getText().trim();
        String unidad = combox_Unidad.getSelectedItem().toString();
        String cantidadTexto = txtCantidad.getText().trim();
        String valorUnitarioTexto = txtValorUni.getText().trim();

        if (producto.isEmpty() || unidad.equals("Seleccione unidad") || cantidadTexto.isEmpty() || valorUnitarioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos del producto", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadTexto);
            double valorUnitario = Double.parseDouble(valorUnitarioTexto);
            if (cantidad <= 0 || valorUnitario < 0) {
                JOptionPane.showMessageDialog(this, "Cantidad debe ser positiva y valor unitario no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            confirmado = true; // Confirmar solo si es válido
            this.dispose(); // Cerrar después de validar
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad y valor unitario deben ser numéricos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed
    private boolean validarCampos() {
        if (txtProducto.getText().trim().isEmpty()) {
            mostrarError("El campo Producto es obligatorio", txtProducto);
            return false;
        }

        if (combox_Unidad.getSelectedIndex() == 0) {
            mostrarError("Seleccione una unidad de medida", combox_Unidad);
            return false;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                mostrarError("La cantidad debe ser mayor a cero", txtCantidad);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número válido", txtCantidad);
            return false;
        }
        try {
            int valorUnitario = Integer.parseInt(txtCantidad.getText().trim());
            if (valorUnitario <= 0) {
                mostrarError("La cantidad debe ser mayor a cero", txtCantidad);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número válido", txtCantidad);
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje, JComponent componente) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        componente.requestFocus();
    }
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductoActionPerformed

    private void combox_UnidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combox_UnidadActionPerformed

    }//GEN-LAST:event_combox_UnidadActionPerformed

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActionPerformed

    private void txtValorUniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorUniActionPerformed

    }//GEN-LAST:event_txtValorUniActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Crea y muestra el diálogo */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Usa el constructor básico para el main
                InsertarCoti dialog = new InsertarCoti(new javax.swing.JFrame(), true, "", "");
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
    private rojeru_san.RSButtonRiple btnCancelar;
    private rojeru_san.RSButtonRiple btnGuardar;
    private RSMaterialComponent.RSComboBoxMaterial combox_Unidad;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private RSMaterialComponent.RSTextFieldMaterial txtCantidad;
    private RSMaterialComponent.RSTextFieldMaterial txtProducto;
    private RSMaterialComponent.RSTextFieldMaterial txtValorUni;
    // End of variables declaration//GEN-END:variables
private void configurarFiltrosNumericos() {
        configurarFiltroNumerico(txtCantidad);
        configurarFiltroNumerico(txtValorUni);

        // Configuraciones adicionales
        txtCantidad.setToolTipText("Solo números y un separador decimal (ej: 3001234567)");
        txtValorUni.setToolTipText("Solo números (ej: 123456789)");
    }

    private void configurarFiltroNumerico(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (validarFormatoNumerico(newText, textField)) {
                    super.insertString(fb, offset, text, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (validarFormatoNumerico(newText, textField)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean validarFormatoNumerico(String text, JTextField field) {
                // Reglas diferentes según el campo
                if (field == txtCantidad) {
                    return text.matches("^[0-9]*([.,][0-9]{0,2})?$"); // Máx 2 decimales
                }

                if (field == txtCantidad) {
                    return text.matches("^[0-9]{0,15}$"); // Solo números, máx 15 dígitos
                } else if (field == txtValorUni) {
                    return text.matches("^[0-9]{0,20}$"); // Solo números, máx 20 dígitos
                } else if (field == txtCantidad) {
                    return text.matches("^[0-9]*([.,][0-9]{0,2})?$"); // Máx 2 decimales
                }
                return false;
            }
        });
    }
}
