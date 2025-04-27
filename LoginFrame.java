package com.unilib.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.unilib.ui.MenuPrincipal;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Connexion à la Bibliothèque");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setLayout(new BorderLayout());

        // Création des composants
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordField = new JPasswordField();
        loginButton = new JButton("Se connecter");

        // Ajout des composants au panel
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Espace vide
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        // Action du bouton
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // --- Ici tu peux connecter ta base de données pour vérifier ---
        // Pour l'instant on met un identifiant fixe pour tester
        if(username.equals("admin") && password.equals("password123")) {
            JOptionPane.showMessageDialog(this, "Connexion réussie !");
           new MenuPrincipal(username).setVisible(true); // <<< Ouvre le MenuPrincipal en lui passant le username
            // new MainMenuFrame().setVisible(true);
            dispose(); // Fermer la fenêtre de login
        } else {
            JOptionPane.showMessageDialog(this, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

