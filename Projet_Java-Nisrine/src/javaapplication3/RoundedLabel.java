package javaapplication3;

import javax.swing.*;
import java.awt.*;

public class RoundedLabel extends JLabel {
    private int cornerRadius = 10;

    public RoundedLabel() {
        super();
        init();
    }

    public RoundedLabel(String text) {
        super(text);
        init();
    }

    public RoundedLabel(Icon icon) {
        super(icon);
        init();
    }

    public RoundedLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        init();
    }

    public RoundedLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        init();
    }

    private void init() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2);
        g2.dispose();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}