package alaedesignlapartieréservations;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.Timer;

public class InscriptionScreen extends JPanel {
    public InscriptionScreen() {
        setLayout(new BorderLayout());
        Image bgImage = new ImageIcon(getClass().getResource("/icons/fac.jpg")).getImage();
        BackgroundPanel backgroundPanel = new BackgroundPanel(bgImage);
        backgroundPanel.setLayout(new GridBagLayout());
        RoundedPanel formPanel = new RoundedPanel(new GridBagLayout(), 40);
        formPanel.setPreferredSize(new Dimension(900, 500));
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(createStyledLabel("Email :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        JTextField emailField = new JTextField(30);
        styleInputField(emailField);
        formPanel.add(emailField, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(createStyledLabel("CNE :"), gbc);
        gbc.gridx = 1; JTextField cneField = new JTextField(20); styleInputField(cneField); formPanel.add(cneField, gbc);
        gbc.gridx = 2; formPanel.add(createStyledLabel("CIN :"), gbc);
        gbc.gridx = 3; JTextField cinField = new JTextField(20); styleInputField(cinField); formPanel.add(cinField, gbc);

        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(createStyledLabel("Nom :"), gbc);
        gbc.gridx = 1; JTextField nomField = new JTextField(20); styleInputField(nomField); formPanel.add(nomField, gbc);
        gbc.gridx = 2; formPanel.add(createStyledLabel("Prénom :"), gbc);
        gbc.gridx = 3; JTextField prenomField = new JTextField(20); styleInputField(prenomField); formPanel.add(prenomField, gbc);

        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(createStyledLabel("Niveau :"), gbc);
        gbc.gridx = 1;
        JComboBox<String> niveauBox = new JComboBox<>(new String[]{"Licence", "Master", "Doctorat"});
        formPanel.add(niveauBox, gbc);
        gbc.gridx = 2; formPanel.add(createStyledLabel("Filière :"), gbc);
        gbc.gridx = 3;
        JComboBox<String> filiereBox = new JComboBox<>(new String[]{"Informatique", "Mathématiques", "Physique"});
        formPanel.add(filiereBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0; formPanel.add(createStyledLabel("Mot de passe :"), gbc);
        gbc.gridx = 1; JPasswordField passField = new JPasswordField(20); styleInputField(passField); formPanel.add(passField, gbc);
        gbc.gridx = 2; formPanel.add(createStyledLabel("Confirmer mot de passe :"), gbc);
        gbc.gridx = 3; JPasswordField confirmPassField = new JPasswordField(20); styleInputField(confirmPassField); formPanel.add(confirmPassField, gbc);

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 4;
        JLabel messageLabel = new JLabel("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        formPanel.add(messageLabel, gbc);
        
        // Vérification régulière de la réponse de l'admin
Timer checkResponseTimer = new Timer(4000, e -> {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib_db", "root", "")) {
        String cne = cneField.getText();
        if (!cne.isEmpty()) {
            PreparedStatement checkReponse = conn.prepareStatement(
                "SELECT reponse FROM message WHERE cne_etudiant = ? ORDER BY id_message DESC LIMIT 1");
            checkReponse.setString(1, cne);
            ResultSet rsResp = checkReponse.executeQuery();
            if (rsResp.next()) {
                String reponse = rsResp.getString("reponse");
                if ("OK".equalsIgnoreCase(reponse)) {
                    messageLabel.setForeground(new Color(0x006400));
                    messageLabel.setText("Inscription acceptée. Redirection en cours...");
                    
                    // Redirection après 2 secondes :
                    Timer timer = new Timer(2000, evt -> {
                    Window window = SwingUtilities.getWindowAncestor(this);
                    if (window instanceof JFrame) {
                        window.dispose(); // ferme l'ancienne fenêtre
                    }

                    EtudiantInterface etudiantInterface = new EtudiantInterface();
                    etudiantInterface.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    etudiantInterface.setVisible(true);
                });
                timer.setRepeats(false);
                timer.start();
                } else if ("Refuser".equalsIgnoreCase(reponse)) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Inscription refusée. Veuillez vérifier vos informations saisies.");
                }
            } else {
                // Aucun message trouvé = l'admin a supprimé la demande
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Inscription refusée ou supprimée par l'administrateur.");
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
});

        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        RoundedPillButton registerButton = new RoundedPillButton("S'inscrire");
        RoundedPillButton resetButton = new RoundedPillButton("Réinitialiser");
        resetButton.setBackground(Color.GRAY);

        resetButton.addActionListener(e -> {
            cneField.setText(""); cinField.setText(""); nomField.setText(""); prenomField.setText("");
            emailField.setText(""); passField.setText(""); confirmPassField.setText(""); messageLabel.setText("");
        });

        registerButton.addActionListener(e -> {
            String cne = cneField.getText();
            String cin = cinField.getText();
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String niveau = (String) niveauBox.getSelectedItem();
            String filiereNom = (String) filiereBox.getSelectedItem();
            String password = new String(passField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Adresse email invalide.");
                return;
            }

            if (password.length() < 6) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Le mot de passe doit contenir au moins 6 caractères.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Les mots de passe ne correspondent pas.");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib_db", "root", "")) {
                conn.setAutoCommit(false);

                PreparedStatement checkReponse = conn.prepareStatement("SELECT reponse FROM message WHERE cne_etudiant = ? ORDER BY id_message DESC LIMIT 1");
                checkReponse.setString(1, cne);
                ResultSet rsResp = checkReponse.executeQuery();
                if (rsResp.next()) {
                    String reponse = rsResp.getString("reponse");
                    if ("acceptée".equalsIgnoreCase(reponse)) {
                        messageLabel.setForeground(new Color(0x006400));
                        messageLabel.setText("Inscription acceptée. Redirection en cours...");
                        return;
                    } else if ("refusée".equalsIgnoreCase(reponse)) {
                        messageLabel.setForeground(Color.RED);
                        messageLabel.setText("Inscription refusée. Veuillez vérifier vos informations saisies.");
                        return;
                    }
                }

                // Vérifier doublons
                PreparedStatement checkCinStmt = conn.prepareStatement("SELECT 1 FROM utilisateur WHERE cin = ?");
                checkCinStmt.setString(1, cin);
                if (checkCinStmt.executeQuery().next()) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Ce CIN est déjà enregistré.");
                    return;
                }

                PreparedStatement checkCneStmt = conn.prepareStatement("SELECT 1 FROM etudiant WHERE cne = ?");
                checkCneStmt.setString(1, cne);
                if (checkCneStmt.executeQuery().next()) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Ce CNE est déjà enregistré.");
                    return;
                }

                int idFiliere = -1;
                PreparedStatement psFiliere = conn.prepareStatement("SELECT id_filiere FROM filiere WHERE nom_filiere = ?");
                psFiliere.setString(1, filiereNom);
                ResultSet rsFiliere = psFiliere.executeQuery();
                if (rsFiliere.next()) {
                    idFiliere = rsFiliere.getInt("id_filiere");
                } else {
                    throw new SQLException("Filière non trouvée.");
                }

                PreparedStatement ps1 = conn.prepareStatement("INSERT INTO utilisateur (cin, login, password, role, statut, date_bloque, date_dernier_changement_password) VALUES (?, ?, ?, 'etudiant', 'actif', NULL, ?)");
                ps1.setString(1, cin);
                ps1.setString(2, nom + prenom);
                ps1.setString(3, password);
                ps1.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO etudiant (cne, nom, prenom, email, niveau_etudes, id_filiere, cin_utilisateur) VALUES (?, ?, ?, ?, ?, ?, ?)");
                ps2.setString(1, cne);
                ps2.setString(2, nom);
                ps2.setString(3, prenom);
                ps2.setString(4, email);
                ps2.setString(5, niveau);
                ps2.setInt(6, idFiliere);
                ps2.setString(7, cin);
                ps2.executeUpdate();

                PreparedStatement ps3 = conn.prepareStatement("INSERT INTO demande (type_demande, nb_messages_non_lus) VALUES ('Inscription', 1)", Statement.RETURN_GENERATED_KEYS);
                ps3.executeUpdate();
                ResultSet rs = ps3.getGeneratedKeys();
                int demandeId = 0;
                if (rs.next()) demandeId = rs.getInt(1);

                PreparedStatement ps4 = conn.prepareStatement("INSERT INTO message (cne_etudiant, contenu, id_demande) VALUES (?, ?, ?)");
                ps4.setString(1, cne);
                ps4.setString(2, "Demande d'inscription au système de la part de l'étudiant : " + nom + " " + prenom);
                ps4.setInt(3, demandeId);
                ps4.executeUpdate();

                conn.commit();
                
                checkResponseTimer.start();

                
                messageLabel.setForeground(new Color(0x006400));
                messageLabel.setText("Inscription envoyée. En attente de validation par l'admin.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Erreur lors de l'inscription : " + ex.getMessage());
            }
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(resetButton);
        formPanel.add(buttonPanel, gbc);
        backgroundPanel.add(formPanel);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void styleInputField(JTextField field) {
        field.setPreferredSize(new Dimension(350, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 10));
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
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
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
        @Override protected void paintBorder(Graphics g) {}
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
