package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderPanel extends JPanel {
    private JLabel dateTimeLabel;

    public HeaderPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        setBackground(new Color(0x0E2A47));
        setPreferredSize(new Dimension(getWidth(), 90));

        // Icône calendrier
        ImageIcon calendarIcon = resizeIcon("/icons/calendar-icon.png", 48, 48);
        JLabel iconLabel = new JLabel(calendarIcon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 70, 0));

        // Date/heure
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        dateTimeLabel.setForeground(Color.WHITE);

        // Panneau contenant date et heure
        JPanel datePanel = new JPanel(new GridLayout(2, 1))
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0E2A47)); // Couleur bleue foncée
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // coins arrondis
            }
        };
        datePanel.setBackground(new Color(0x0E2A47));
        datePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        datePanel.setPreferredSize(new Dimension(200, 100)); // taille suffisante
        datePanel.add(dateTimeLabel);

        // Conteneur global
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        container.setOpaque(false);
        container.add(iconLabel);
        container.add(datePanel);

        add(container);

        updateDateTime();
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();
    }

    private void updateDateTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        String date = sdfDate.format(new Date());
        String time = sdfTime.format(new Date());
        dateTimeLabel.setText("<html>" + date + "<br>" + time + "</html>");
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        java.net.URL imageURL = getClass().getResource(path);
        if (imageURL == null) {
            System.err.println("Image not found: " + path);
            return null;
        }
        ImageIcon icon = new ImageIcon(imageURL);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}