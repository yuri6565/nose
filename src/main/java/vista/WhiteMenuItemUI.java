/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author Personal
 */
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import javax.swing.JComponent;


public class WhiteMenuItemUI extends BasicMenuItemUI {

    private static final Color HOVER_COLOR = new Color(240, 240, 240); // Gris clarito
    private static final Color NORMAL_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 30, 30);

    public static ComponentUI createUI(JComponent c) {
        return new WhiteMenuItemUI();
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        if (menuItem.isArmed() || menuItem.isSelected()) {
            g.setColor(HOVER_COLOR); // Hover gris claro
        } else {
            g.setColor(NORMAL_COLOR); // Fondo blanco
        }
        g.fillRect(0, 0, menuItem.getWidth(), menuItem.getHeight());
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(TEXT_COLOR);
        g2.setFont(menuItem.getFont());
        g2.drawString(text, textRect.x, textRect.y + g2.getFontMetrics().getAscent());
    }
    
    
}
