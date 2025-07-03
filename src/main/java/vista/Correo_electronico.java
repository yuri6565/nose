/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;





import vista.alertas.CodigoIncorrectoAlerta;
import vista.alertas.ValidacionCodigoExitoso;
import controlador.Ctrl_Usuarios;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.text.Document;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.util.Arrays;
import java.util.List;
import modelo.Consulta_Usuarios;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ButtonUI;




/**
 *
 * @author Personal
*/
public class Correo_electronico extends javax.swing.JFrame {

    private final List<String> emailDomains = Arrays.asList(
        "@gmail.com", "@hotmail.com", "@yahoo.com", "@outlook.com"
    );
    private int currentDomainIndex = -1;
    private String userPart = "";
    private List<String> matchingDomains = Arrays.asList();
    private boolean isUpdating = false;
    private JPopupMenu suggestionPopup;

    public Correo_electronico() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(kGradientPanel1, gbc);

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.add(kGradientPanel1, BorderLayout.CENTER);
        setContentPane(fondo);

        // Setup autocomplete for txtcorreo
        setupAutoComplete();

        System.out.println("Correo_electronico initialized");
    }
private void setupAutoComplete() {
    suggestionPopup = new JPopupMenu();
    suggestionPopup.setBackground(Color.WHITE); // Set white background
    suggestionPopup.setOpaque(true); // Ensure the background is fully opaque
    suggestionPopup.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // White border to match background
    UIManager.put("PopupMenu.background", Color.WHITE); // Override default look and feel

    // Add DocumentListener to txtcorreo
  Document doc = txtcorreo.getDocument();
doc.addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> updateSuggestions());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> updateSuggestions());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> updateSuggestions());
    }
});


    // Add KeyListener for cycling through domains
    txtcorreo.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP && !matchingDomains.isEmpty()) {
                cycleDomain(-1);
                System.out.println("Cycled up to: " + (currentDomainIndex >= 0 ? matchingDomains.get(currentDomainIndex) : "none"));
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && !matchingDomains.isEmpty()) {
                cycleDomain(1);
                System.out.println("Cycled down to: " + (currentDomainIndex >= 0 ? matchingDomains.get(currentDomainIndex) : "none"));
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER && currentDomainIndex >= 0) {
                applySelectedDomain();
                System.out.println("Enter pressed, selection finalized");
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                handleDeletion();
                System.out.println("Key pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
            }
        }
    });
}
private void updateSuggestions() {
    if (isUpdating) return;
    isUpdating = true;

    try {
        String text = txtcorreo.getText().trim();

        currentDomainIndex = -1;
        suggestionPopup.removeAll();

        if (!text.contains("@")) {
            matchingDomains = Arrays.asList();
            suggestionPopup.setVisible(false);
            return;
        }

        int atIndex = text.lastIndexOf("@");
        userPart = text.substring(0, atIndex + 1); // Incluye la "@"
        String domainPart = text.substring(atIndex + 1).trim();

        // Mostrar todas si el dominio está vacío
        if (domainPart.isEmpty()) {
            matchingDomains = emailDomains;
        } else {
            matchingDomains = emailDomains.stream()
                .filter(domain -> domain.toLowerCase().startsWith("@" + domainPart.toLowerCase()))
                .collect(Collectors.toList());
        }

        if (!matchingDomains.isEmpty()) {
            for (String domain : matchingDomains) {
                JMenuItem item = new JMenuItem(domain);
                item.setUI((ButtonUI) WhiteMenuItemUI.createUI(item)); // Elimina hover azul

                item.setPreferredSize(new Dimension(400, 50));
                item.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                item.setBackground(Color.WHITE);
                item.setOpaque(true);
                item.setForeground(new Color(30, 30, 30));
                item.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                item.setFocusPainted(false);
                item.setRolloverEnabled(false);

                item.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        item.setBackground(new Color(240, 240, 240)); // Gris claro
                        item.repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        item.setBackground(Color.WHITE);
                        item.repaint();
                    }
                });

                item.addActionListener(e -> {
                    txtcorreo.setText(userPart + domain.substring(1));
                    txtcorreo.setCaretPosition(txtcorreo.getText().length());
                    suggestionPopup.setVisible(false);
                });
//
                suggestionPopup.add(item);
            }

            suggestionPopup.show(txtcorreo, 0, txtcorreo.getHeight());
        } else {
            suggestionPopup.setVisible(false);
        }
    } finally {
        isUpdating = false;
    }
}




    private void cycleDomain(int direction) {
        if (matchingDomains.isEmpty()) return;

        currentDomainIndex += direction;
        if (currentDomainIndex < 0) currentDomainIndex = matchingDomains.size() - 1;
        else if (currentDomainIndex >= matchingDomains.size()) currentDomainIndex = 0;

        String selectedDomain = matchingDomains.get(currentDomainIndex);
        txtcorreo.setText(userPart + selectedDomain.substring(1)); // Append only the domain part
        txtcorreo.setCaretPosition(txtcorreo.getText().length());
        updateSuggestions(); // Refresh popup
    }

    private void applySelectedDomain() {
        if (currentDomainIndex >= 0 && !matchingDomains.isEmpty()) {
            txtcorreo.setText(userPart + matchingDomains.get(currentDomainIndex).substring(1));
            txtcorreo.setCaretPosition(txtcorreo.getText().length());
            suggestionPopup.setVisible(false);
        }
    }

    private void handleDeletion() {
        String text = txtcorreo.getText().trim();
        if (text.isEmpty() || !text.contains("@")) {
            matchingDomains = Arrays.asList();
            currentDomainIndex = -1;
            suggestionPopup.setVisible(false);
        } else {
            int atIndex = text.lastIndexOf("@");
            userPart = text.substring(0, atIndex + 1);
            String domainPart = text.substring(atIndex + 1).trim();
            if (domainPart.isEmpty()) {
                matchingDomains = emailDomains; // Reset to all domains when "@" is alone
                updateSuggestions();
            }
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

        kGradientPanel1 = new keeptoo.KGradientPanel();
        jPanel6 = new javax.swing.JPanel();
        rSPanelImage1 = new rojerusan.RSPanelImage();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        rSButtonRound1 = new rojerusan.RSButtonRound();
        txtcorreo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        kGradientPanel1.setkEndColor(new java.awt.Color(239, 248, 255));
        kGradientPanel1.setkStartColor(new java.awt.Color(254, 254, 254));
        kGradientPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rSPanelImage1.setImagen(new javax.swing.ImageIcon(getClass().getResource("/logo_azul.png"))); // NOI18N

        javax.swing.GroupLayout rSPanelImage1Layout = new javax.swing.GroupLayout(rSPanelImage1);
        rSPanelImage1.setLayout(rSPanelImage1Layout);
        rSPanelImage1Layout.setHorizontalGroup(
            rSPanelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );
        rSPanelImage1Layout.setVerticalGroup(
            rSPanelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );

        jPanel6.add(rSPanelImage1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 90, 90));

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N
        jLabel11.setText("Restablece la contraseña");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, -1, -1));

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel7.setText("Dirección de correo electrónico:");
        jPanel6.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, -1, 20));

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jLabel8.setText("volver al inicio de sesion");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel8MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel8MouseReleased(evt);
            }
        });
        jPanel6.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 490, 170, 20));

        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jLabel9.setText("Escribe el correo electronico de tu cuenta para que te ");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 410, 20));

        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        jLabel10.setText("enviamos un codigo de seguridad");
        jPanel6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 410, 20));

        rSButtonRound1.setBackground(new java.awt.Color(29, 30, 51));
        rSButtonRound1.setText("Siguiente");
        rSButtonRound1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSButtonRound1ActionPerformed(evt);
            }
        });
        jPanel6.add(rSButtonRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 400, 410, -1));

        txtcorreo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel6.add(txtcorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 400, 40));

        kGradientPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, 480, 550));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MousePressed
      Login1121 login = new Login1121();
     
    }//GEN-LAST:event_jLabel8MousePressed

    private void rSButtonRound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSButtonRound1ActionPerformed

        verificarCorreo();
 
 

       
    }//GEN-LAST:event_rSButtonRound1ActionPerformed

    private void jLabel8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseReleased

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
         
            cargando11 cargando = new cargando11(new JFrame(), true);

        new Thread(() -> {
            cargando.setVisible(true);
        }).start();

        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            cargando.dispose();

        });

        timer.setRepeats(false);
        timer.start();
         this.dispose();
        Login1121 dialog = new Login1121();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
     

    }//GEN-LAST:event_jLabel8MouseClicked

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
            java.util.logging.Logger.getLogger(Correo_electronico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Correo_electronico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Correo_electronico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Correo_electronico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Correo_electronico().setVisible(true);
            }
        });
    }
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel6;
    private keeptoo.KGradientPanel kGradientPanel1;
    private rojerusan.RSButtonRound rSButtonRound1;
    private rojerusan.RSPanelImage rSPanelImage1;
    private javax.swing.JTextField txtcorreo;
    // End of variables declaration//GEN-END:variables

   private void verificarCorreo() {
    String correo = txtcorreo.getText().trim();

    // Validaciones básicas
    if (correo.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El campo de correo electrónico no puede estar vacío.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!correo.contains("@") || !correo.contains(".")) {
        JOptionPane.showMessageDialog(this, "El correo electrónico ingresado no es válido.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Mostrar pantalla de carga inicial
    cargando11 cargando = new cargando11(new JFrame(), true);
    new Thread(() -> cargando.setVisible(true)).start();

    javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
        cargando.dispose();

        Consulta_Usuarios consulta = new Consulta_Usuarios();
        String usuario = consulta.obtenerCodigoDesdeCorreo(correo);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Correo electrónico encontrado con éxito", "Confirmación", JOptionPane.INFORMATION_MESSAGE);

            cargando11 secondLoading = new cargando11(new JFrame(), true);
            new Thread(() -> secondLoading.setVisible(true)).start();

            javax.swing.Timer secondTimer = new javax.swing.Timer(2000, secondE -> {
                secondLoading.dispose();

                String codigo = consulta.recuperarCuenta(usuario, correo);

                if (codigo != null) {
                    Ctrl_Usuarios controlador = new Ctrl_Usuarios();
                    controlador.enviarCodigoRecuperacion(correo, usuario, codigo);

                    JOptionPane.showMessageDialog(this, "Código enviado con éxito al correo " + correo, "Confirmación", JOptionPane.INFORMATION_MESSAGE);

                    cargando11 finalLoading = new cargando11(new JFrame(), true);
                    new Thread(() -> finalLoading.setVisible(true)).start();

                    javax.swing.Timer finalTimer = new javax.swing.Timer(2000, finalE -> {
                        finalLoading.dispose();

                        Contrasena3 ventana = new Contrasena3();
                        ventana.setCorreoIngresado(correo);
                        ventana.setVisible(true);
                        dispose();
                    });
                    finalTimer.setRepeats(false);
                    finalTimer.start();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al generar el código de recuperación.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            secondTimer.setRepeats(false);
            secondTimer.start();
        } else {
            JOptionPane.showMessageDialog(this, "El correo no está registrado en el sistema.", "Correo no encontrado", JOptionPane.ERROR_MESSAGE);
        }
    });
    timer.setRepeats(false);
    timer.start();
}


}
        

  




