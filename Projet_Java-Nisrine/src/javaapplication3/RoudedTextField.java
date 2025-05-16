package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class RoundedTextField extends JTextField {
    private int cornerRadius = 15;
    private String placeholder = "";

    // Constructeurs avec et sans texte / placeholder
    public RoundedTextField() {
        super();
        init();
    }

    public RoundedTextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        init();
    }

    public RoundedTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        init();
    }

    public RoundedTextField(int columns) {
        super(columns);
        init();
    }

    public RoundedTextField(String text, boolean isActualText) {
        super(isActualText ? text : "");
        if (!isActualText) {
            this.placeholder = text;
        }
        init();
    }

    private void init() {
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 200)); // ✅ Couleur de fond intégrée comme avant
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Fond arrondi
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose();

        super.paintComponent(g);

        // Placeholder s'il n'y a pas de texte
        if (getText().isEmpty() && !isFocusOwner() && placeholder != null && !placeholder.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.GRAY);
            g2d.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            g2d.drawString(placeholder, insets.left + 2, getHeight() / 2 + getFont().getSize() / 2 - 3);
            g2d.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GRAY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.dispose();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return this.placeholder;
    }
}