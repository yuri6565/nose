/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista.Caja;

import controlador.Ctrl_CajaIngresos;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument;
import modelo.Ingresos;
import vista.Inventario0.NumberFormatFilterInventario;

/**
 *
 * @author ZenBook
 */
public class iAbonoEditar extends javax.swing.JDialog {

    private Ctrl_CajaIngresos controlador;
    private int idPedido;
    private String numPedido; // Agrega esta variable
    private String[] datos; // Almacena los datos ingresados
    private boolean guardado = false; // Indica si se presionó "Guardar"
    private javax.swing.JSpinner spinnerImporte;

    private int idAbono; // Variable para almacenar el ID del abono a editar

    /**
     * Creates new form iAbonoEditar
     */
    public iAbonoEditar(java.awt.Frame parent, boolean modal, int idAbono, int idPedido, String numPedido, Ctrl_CajaIngresos controlador) {
        super(parent, modal);
        this.controlador = controlador;
        this.idPedido = idPedido;
        this.numPedido = numPedido;
        this.idAbono = idAbono;
        initComponents();

        lblNumero.setText(numPedido);
        cargarDatosAbono(); // Método para cargar los datos del abono

        ((AbstractDocument) txtCantidad.getDocument()).setDocumentFilter(new NumberFormatFilterInventario());

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

    public String[] getDatos() {
        return datos;
    }

    // Método para verificar si se presionó "Guardar"
    public boolean isGuardado() {
        return guardado;
    }

    private void cargarDatosAbono() {
        // Obtener el abono desde la base de datos
        List<Ingresos> abonos = controlador.obtenerAbonosPorPedido(idPedido);
        Ingresos abonoEditar = abonos.stream()
                .filter(a -> a.getIdAbono() == idAbono)
                .findFirst()
                .orElse(null);

        if (abonoEditar != null) {
            // Cargar datos en los campos
            dateFecha.setDate(abonoEditar.getFechaPago());
            
                    // Formatear el monto con separador de miles
        txtCantidad.setText(formatNumber(abonoEditar.getMonto()));

            // Seleccionar el método de pago en el combobox
            for (int i = 0; i < cmbMetodo.getItemCount(); i++) {
                if (cmbMetodo.getItemAt(i).equals(abonoEditar.getMetodoPago())) {
                    cmbMetodo.setSelectedIndex(i);
                    break;
                }
            }

            txtReferencia.setText(abonoEditar.getReferencia() != null ? abonoEditar.getReferencia() : "");
            txtObservaciones.setText(abonoEditar.getObservacion() != null ? abonoEditar.getObservacion() : "");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el abono a editar", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
private String formatNumber(double number) {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setGroupingSeparator('.');
    DecimalFormat df = new DecimalFormat("#,##0", symbols);
    return df.format(number);
}

private double parseFormattedNumber(String text) {
    try {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.parse(text.replaceAll("[^0-9.]", "")).doubleValue();
    } catch (ParseException e) {
        return 0;
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
        jLabel3 = new javax.swing.JLabel();
        dateFecha = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbMetodo = new RSMaterialComponent.RSComboBoxMaterial();
        btnGuardar = new RSMaterialComponent.RSButtonShape();
        txtReferencia = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel6 = new javax.swing.JLabel();
        txtCantidad = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        lblNumero = new javax.swing.JLabel();
        btnSalir1 = new rojeru_san.RSButton();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel3.setText("Observaciones:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 360, -1, -1));

        dateFecha.setBackground(new java.awt.Color(255, 255, 255));
        dateFecha.setToolTipText("");
        jPanel1.add(dateFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 250, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel4.setText("Fecha:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel5.setText("Cantidad:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, -1));

        cmbMetodo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione metodo de pago:", "Efectivo", "Transferencia Bancaria", "Tarjeta de Crédito" }));
        cmbMetodo.setColorMaterial(new java.awt.Color(0, 0, 0));
        cmbMetodo.setFont(new java.awt.Font("Roboto Bold", 0, 14)); // NOI18N
        cmbMetodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMetodoActionPerformed(evt);
            }
        });
        jPanel1.add(cmbMetodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, 250, 30));

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
        jPanel1.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 460, 97, 28));

        txtReferencia.setForeground(new java.awt.Color(0, 0, 0));
        txtReferencia.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtReferencia.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtReferencia.setPhColor(new java.awt.Color(0, 0, 0));
        txtReferencia.setPlaceholder("Ingrese la referencia...");
        txtReferencia.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtReferencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReferenciaActionPerformed(evt);
            }
        });
        txtReferencia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReferenciaKeyReleased(evt);
            }
        });
        jPanel1.add(txtReferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 250, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel6.setText("Metodo de pago:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, -1, -1));

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
        jPanel1.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel7.setText("Referencia:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, -1, -1));

        txtObservaciones.setColumns(10);
        txtObservaciones.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setRows(1);
        txtObservaciones.setTabSize(1);
        txtObservaciones.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtObservaciones);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 250, 50));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 300, 10));

        lblNumero.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblNumero.setText("PED-12345-01");
        jPanel1.add(lblNumero, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 170, -1));

        btnSalir1.setBackground(new java.awt.Color(255, 255, 255));
        btnSalir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x (3).png"))); // NOI18N
        btnSalir1.setColorHover(new java.awt.Color(211, 0, 52));
        btnSalir1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSalir1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSalir1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSalir1MouseExited(evt);
            }
        });
        btnSalir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalir1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnSalir1, new org.netbeans.lib.awtextra.AbsoluteConstraints(281, 1, 38, 37));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setText("Abono:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 9, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 320, 500));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbMetodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMetodoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMetodoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Validación de campos (igual que antes)
        if (dateFecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione la fecha", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String importeText = txtCantidad.getText().replace(".", "");
        if (importeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cmbMetodo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un método de pago", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación específica para referencia
        String metodoPago = cmbMetodo.getSelectedItem().toString();
        String referencia = txtReferencia.getText().trim();

        if ((metodoPago.equals("Transferencia Bancaria") || metodoPago.equals("Tarjeta de Crédito"))
                && referencia.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La referencia es obligatoria para " + metodoPago,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtReferencia.requestFocus();
            return;
        }

        try {
            double nuevoMonto = parseFormattedNumber(importeText);

            // Obtener el abono actual para comparar
            Ingresos abonoActual = controlador.obtenerAbonoPorId(idAbono);
            double montoActual = abonoActual != null ? abonoActual.getMonto() : 0;
            double diferencia = nuevoMonto - montoActual;

            // Obtener el total pendiente del pedido
            Ctrl_CajaIngresos.IngresoConDetalles ingreso = controlador.obtenerDetallesIngreso(idPedido);
            double debidoActual = ingreso != null ? ingreso.getDebido() + montoActual : 0;

            // Validar que el cambio no exceda el total pendiente
            if (diferencia > debidoActual) {
                JOptionPane.showMessageDialog(this,
                        "El ajuste excede el saldo pendiente. Máximo permitido: $" + (debidoActual + montoActual),
                        "Error en monto",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nuevoMonto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String observacion = txtObservaciones.getText();

            // Actualizar el abono en lugar de insertar
            boolean exito = controlador.actualizarAbono(
                    idAbono,
                    nuevoMonto,
                    metodoPago,
                    referencia,
                    observacion
            );

            if (exito) {
                guardado = true;
                JOptionPane.showMessageDialog(this, "Abono actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el abono", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Formato de cantidad inválido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtReferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReferenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReferenciaActionPerformed

    private void txtReferenciaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReferenciaKeyReleased

    }//GEN-LAST:event_txtReferenciaKeyReleased

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActionPerformed

    private void txtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyReleased
        formatCantidadField();
    }//GEN-LAST:event_txtCantidadKeyReleased

    private void btnSalir1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalir1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSalir1MouseClicked

    private void btnSalir1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalir1MouseEntered
        btnSalir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x (1).png")));
    }//GEN-LAST:event_btnSalir1MouseEntered

    private void btnSalir1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalir1MouseExited
        btnSalir1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x (3).png")));        // TODO add your handling code here:
    }//GEN-LAST:event_btnSalir1MouseExited

    private void btnSalir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalir1ActionPerformed
        guardado = false;
        setVisible(false); // Cerrar el diálogo
    }//GEN-LAST:event_btnSalir1ActionPerformed

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
            java.util.logging.Logger.getLogger(iAbonoEditar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(iAbonoEditar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(iAbonoEditar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(iAbonoEditar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Ctrl_CajaIngresos controlador = new Ctrl_CajaIngresos();
                // Ejemplo con ID de abono 1, ID de pedido 1 y número de pedido "PED-1001"
                iAbonoEditar dialog = new iAbonoEditar(new javax.swing.JFrame(), true, 1, 1, "PED-1001", controlador);
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
    private rojeru_san.RSButton btnSalir1;
    private RSMaterialComponent.RSComboBoxMaterial cmbMetodo;
    private com.toedter.calendar.JDateChooser dateFecha;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblNumero;
    private RSMaterialComponent.RSTextFieldMaterial txtCantidad;
    private javax.swing.JTextArea txtObservaciones;
    private RSMaterialComponent.RSTextFieldMaterial txtReferencia;
    // End of variables declaration//GEN-END:variables
}
