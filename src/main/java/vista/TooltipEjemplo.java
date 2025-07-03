/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author Personal
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TooltipEjemplo  extends JFrame {
    private JTextField txtPassword;
    private JPanel tooltip;

    public TooltipEjemplo () {
        setTitle("Ejemplo de Validación");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lbl = new JLabel("Contraseña:");
        lbl.setBounds(50, 80, 100, 30);
        add(lbl);

        txtPassword = new JTextField();
        txtPassword.setBounds(150, 80, 200, 30);
        add(txtPassword);

        // Mostrar mensaje al salir del campo
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarPassword();
            }
        });

        // Ocultar mensaje si vuelve a escribir
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                ocultarTooltip();
            }
        });
    }

private void validarPassword() {
        String pass = txtPassword.getText().trim();

        // RegEx de contraseña segura
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (!pass.matches(regex)) {
            mostrarTooltip("Debe contener mínimo 8 caracteres, incluyendo al menos una mayúscula, una minúscula, un número y un carácter especial (como !, @, #, etc).");
        }
    }

    private void mostrarTooltip(String mensaje) {
        if (tooltip != null) remove(tooltip);

        tooltip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(200, 55, 55));
                g.fillRoundRect(0, 0, getWidth()-10, getHeight(), 10, 10);

                int[] x = {getWidth()-10, getWidth(), getWidth()-10};
                int[] y = {15, 20, 25};
                g.fillPolygon(x, y, 3);
            }
        };
        tooltip.setLayout(new BorderLayout());
        JLabel lbl = new JLabel("<html><body style='width: 200px;'>" + mensaje + "</body></html>");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tooltip.add(lbl);
        tooltip.setBackground(new Color(200, 55, 55));
        tooltip.setBounds(txtPassword.getX() + txtPassword.getWidth() + 10, txtPassword.getY() - 5, 280, 65);
        add(tooltip);
        tooltip.repaint();
        tooltip.setVisible(true);
        repaint();
    }

    private void ocultarTooltip() {
        if (tooltip != null) {
            remove(tooltip);
            tooltip = null;
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TooltipEjemplo ().setVisible(true);
        });
    }

}

