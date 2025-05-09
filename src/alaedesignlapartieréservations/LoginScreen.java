package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.SwingUtilities;

public class LoginScreen extends JPanel {
    public LoginScreen() {
        setLayout(new BorderLayout());
        Image backgroundImage = new ImageIcon(getClass().getResource("/icons/fac.jpg")).getImage();
        BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundImage);
        backgroundPanel.setLayout(new GridBagLayout());

        RoundedPanel formPanel = new RoundedPanel(new GridBagLayout(), 40);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login
        gbc.gridy = 0;
        JLabel userLabel = createStyledLabel("Login :");
        formPanel.add(userLabel, gbc);
        gbc.gridy++;
        JTextField userField = new JTextField(20);
        styleInputField(userField);
        formPanel.add(userField, gbc);

        // Password
        gbc.gridy++;
        JLabel passLabel = createStyledLabel("Mot de passe :");
        formPanel.add(passLabel, gbc);
        gbc.gridy++;
        JPasswordField passField = new JPasswordField(20);
        styleInputField(passField);
        formPanel.add(passField, gbc);

        // Error Label
        gbc.gridy++;
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(errorLabel, gbc);

        // Buttons
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        RoundedPillButton loginButton = new RoundedPillButton("Se connecter");
        RoundedPillButton resetButton = new RoundedPillButton("Réinitialiser");
        resetButton.setBackground(Color.GRAY);
        resetButton.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            errorLabel.setText("");
        });

        loginButton.addActionListener(e -> {
            String login = userField.getText();
            String password = new String(passField.getPassword());
            if (!checkCredentials(login, password)) {
                errorLabel.setText("Identifiants incorrects !");
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        formPanel.add(buttonPanel, gbc);

        backgroundPanel.add(formPanel);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private boolean checkCredentials(String login, String password) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib_db", "root", "");
            String query = "SELECT * FROM utilisateur WHERE login = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                topFrame.dispose();

                if ("admin".equals(role)) {
                    new reserve_demande().setVisible(true);
                } else if ("etudiant".equals(role)) {
                    new EtudiantInterface().setVisible(true);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void styleInputField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(0, 0, 0, 200));
        return label;
    }

    static class BackgroundPanel extends JPanel {
        private final Image image;
        public BackgroundPanel(Image image) {
            this.image = image;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public class RoundedPillButton extends JButton {
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
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {}
    }

    public class RoundedPanel extends JPanel {
        private final int cornerRadius;
        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g);
        }
    }
}
