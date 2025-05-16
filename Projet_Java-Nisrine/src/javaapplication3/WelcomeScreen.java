package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class WelcomeScreen extends JPanel {
    public WelcomeScreen() {
        initComponents();
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Fond personnalisé
        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getResource("/icons/fac.jpg")).getImage());
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Logo
        ImageIcon originalLogo = new ImageIcon(getClass().getResource("/icons/logo_small.png"));
        Image scaledLogo = originalLogo.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        backgroundPanel.add(logoLabel, gbc);

        // Titre
        gbc.gridy++;
        JLabel title = new JLabel("UNILIB");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(0, 51, 102));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(title, gbc);

        // Sous-titre
        gbc.gridy++;
        JLabel subtitle = new JLabel("UNIVERSITY LIBRARY");
        subtitle.setFont(new Font("Arial", Font.BOLD, 30));
        subtitle.setForeground(new Color(0, 51, 102));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(subtitle, gbc);

        // Bouton arrondi
        gbc.gridy++;
        RoundedPillButton loginButton = new RoundedPillButton("Se connecter");
        loginButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.setContentPane(new LoginScreen());
            topFrame.revalidate();
        }); 
        backgroundPanel.add(loginButton, gbc);
        
        // ➤ Bouton "S'inscrire"
        gbc.gridy++;
        RoundedPillButton registerButton = new RoundedPillButton("S'inscrire");
        registerButton.setBackground(new Color(0, 153, 76)); // vert foncé pour distinction
        registerButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.setContentPane(new InscriptionScreen()); // <-- à créer
            topFrame.revalidate();
        });
        backgroundPanel.add(registerButton, gbc);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    // ➤ Panel de fond avec image
    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // ➤ Bouton "pill" personnalisé (coins parfaitement arrondis)
    static class RoundedPillButton extends JButton {
        public RoundedPillButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 14));
            setPreferredSize(new Dimension(180, 45));
            setOpaque(false);
            setBackground(new Color(0, 102, 204));
            setBorder(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fond arrondi
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

            // Texte
            FontMetrics fm = g.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g).getBounds();
            int x = (getWidth() - stringBounds.width) / 2;
            int y = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Pas de bordure
        }

        @Override
        public boolean contains(int x, int y) {
            return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()).contains(x, y);
        }
    }

    // ➤ Lancement via JFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Accueil");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // plein écran
            frame.setResizable(true);
            frame.setContentPane(new WelcomeScreen());
            frame.setVisible(true);
        });
    }
}