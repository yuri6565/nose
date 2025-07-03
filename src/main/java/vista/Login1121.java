/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.Contrl_login;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.UsuarioModelo;
import vista.alertas.LoginAlerta;
import vista.alertas.LoginAlertaContrasena;
import vista.alertas.LoginAlertaContrasenaIncorrecta;
import vista.alertas.LoginAlertaUsuario;

/**
 *
 * @author Personal
 */
public class Login1121 extends javax.swing.JFrame {

    private boolean isPasswordVisible = false;

   

    /**
     * Creates new form Login1
     */
    public Login1121() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        txt_usuario.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    passtxt.requestFocus();
                }
            }
        });

        passtxt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    iniciar.doClick();
                }
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(kGradientPanel1, gbc);

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.add(kGradientPanel1, BorderLayout.CENTER);
        setContentPane(fondo);

    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passtxt.setEchoChar('•');
          

        } else {
            passtxt.setEchoChar((char) 0);
           
        }
        isPasswordVisible = !isPasswordVisible;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        kGradientPanel1 = new keeptoo.KGradientPanel();
        jPanel6 = new javax.swing.JPanel();
        rSPanelImage1 = new rojerusan.RSPanelImage();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        iniciar = new rojerusan.RSMaterialButtonRectangle();
        txt_usuario = new RSMaterialComponent.RSTextFieldIconOne();
        passtxt = new RSMaterialComponent.RSPasswordIconOne();
        rSPanelImage2 = new rojerusan.RSPanelImage();
        rSPanelImage3 = new rojerusan.RSPanelImage();

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
            .addGap(0, 180, Short.MAX_VALUE)
        );
        rSPanelImage1Layout.setVerticalGroup(
            rSPanelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );

        jPanel6.add(rSPanelImage1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 180, 180));

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N
        jLabel11.setText("Iniciar Sesión");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 200, -1, -1));

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel7.setText("Usuario");
        jPanel6.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 270, -1, 20));

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel6.setText("Contraseña");
        jPanel6.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(29, 30, 51));
        jLabel2.setText("¿Olvidaste contraseña?");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
        });
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 400, -1, 20));

        iniciar.setBackground(new java.awt.Color(29, 30, 51));
        iniciar.setText("INGRESAR ");
        iniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniciarActionPerformed(evt);
            }
        });
        jPanel6.add(iniciar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 480, 380, 50));

        txt_usuario.setForeground(new java.awt.Color(0, 0, 0));
        txt_usuario.setBorderColor(new java.awt.Color(230, 230, 230));
        txt_usuario.setColorIcon(new java.awt.Color(204, 204, 204));
        txt_usuario.setColorTransparente(true);
        txt_usuario.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        txt_usuario.setIcons(rojeru_san.efectos.ValoresEnum.ICONS.PEOPLE);
        txt_usuario.setPhColor(new java.awt.Color(51, 51, 51));
        txt_usuario.setPlaceholder("ingrese su usuario");
        txt_usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_usuarioActionPerformed(evt);
            }
        });
        jPanel6.add(txt_usuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, 390, 30));

        passtxt.setForeground(new java.awt.Color(0, 0, 0));
        passtxt.setToolTipText("");
        passtxt.setBorderColor(new java.awt.Color(230, 230, 230));
        passtxt.setColorIcon(new java.awt.Color(204, 204, 204));
        passtxt.setPhColor(new java.awt.Color(51, 51, 51));
        passtxt.setPlaceholder("Ingrese su contraseña");
        jPanel6.add(passtxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 370, 360, 30));

        kGradientPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 130, 460, 590));

        rSPanelImage2.setImagen(new javax.swing.ImageIcon(getClass().getResource("/Vector 6.png"))); // NOI18N
        rSPanelImage2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rSPanelImage3.setImagen(new javax.swing.ImageIcon(getClass().getResource("/WhatsApp_Image_2025-03-28_at_10.59.04_AM-removebg-preview.png"))); // NOI18N
        rSPanelImage3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rSPanelImage2.add(rSPanelImage3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 720, 660));

        kGradientPanel1.add(rSPanelImage2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 830, 710));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_usuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_usuarioActionPerformed
    }//GEN-LAST:event_txt_usuarioActionPerformed

    private void iniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarActionPerformed
        autenticarUsuario();
    }//GEN-LAST:event_iniciarActionPerformed

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MousePressed

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
        Correo_electronico dialog = new Correo_electronico();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);


    }//GEN-LAST:event_jLabel2MousePressed

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
            java.util.logging.Logger.getLogger(Login1121.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login1121.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login1121.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login1121.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login1121().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSMaterialButtonRectangle iniciar;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel6;
    private keeptoo.KGradientPanel kGradientPanel1;
    private RSMaterialComponent.RSPasswordIconOne passtxt;
    private rojerusan.RSPanelImage rSPanelImage1;
    private rojerusan.RSPanelImage rSPanelImage2;
    private rojerusan.RSPanelImage rSPanelImage3;
    private RSMaterialComponent.RSTextFieldIconOne txt_usuario;
    // End of variables declaration//GEN-END:variables


    private void autenticarUsuario() {
    String nombreUsuario = txt_usuario.getText().trim();
    String contrasena = new String(passtxt.getPassword()).trim();

    if (nombreUsuario.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El campo de usuario no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
        txt_usuario.requestFocus();
        return;
    }

    if (contrasena.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El campo de contraseña no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
        passtxt.requestFocus();
        return;
    }

    Contrl_login controlUsuario = new Contrl_login();
    UsuarioModelo usuario = new UsuarioModelo();
    usuario.setUsuario(nombreUsuario);
    usuario.setContrasena(contrasena);

    UsuarioModelo usuarioAutenticado = controlUsuario.loginUser(usuario);

    if (usuarioAutenticado != null) {
        JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);

        kla cargando = new kla(new JFrame(), true);
        new Thread(() -> cargando.setVisible(true)).start();

        int idUsuario = usuarioAutenticado.getId_usuario();
        String rol = usuarioAutenticado.getRol();

        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            cargando.dispose();
            this.dispose();
            if ("trabajador".equalsIgnoreCase(rol)) {
                new PrincipalTrap(idUsuario).setVisible(true);
            } else {
                new Principal(idUsuario).setVisible(true);
            }
        });

        timer.setRepeats(false);
        timer.start();
    } else {
        JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos. Verifica tus datos.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
    }
}

}
