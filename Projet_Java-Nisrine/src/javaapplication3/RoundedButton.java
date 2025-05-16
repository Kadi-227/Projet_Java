package javaapplication3;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private int cornerRadius = 20;

    public RoundedButton() {
        super();
        init();
    }

    public RoundedButton(String text) {
        super(text);
        init();
    }

    public RoundedButton(Icon icon) {
        super(icon);
        init();
    }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.BLACK);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setBackground(new Color(255, 255, 255, 200));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        super.paintComponent(g2);
        g2.dispose();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}