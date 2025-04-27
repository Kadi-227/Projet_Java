package com.unilib.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {

    private String nomUtilisateur;

    public MenuPrincipal(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
        setTitle("Menu Principal - Gestion de Bibliothèque");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);

        // Bienvenue
        JLabel labelBienvenue = new JLabel("Bienvenue, " + nomUtilisateur + " !");
        labelBienvenue.setFont(new Font("Arial", Font.BOLD, 20));
        labelBienvenue.setHorizontalAlignment(SwingConstants.CENTER);
        labelBienvenue.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(labelBienvenue, BorderLayout.NORTH);

        // Panel avec les boutons
        JPanel panelBoutons = new JPanel(new GridLayout(2, 2, 20, 20));
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnLivres = new JButton("Gérer les Livres");
        JButton btnUtilisateurs = new JButton("Gérer les Utilisateurs");
        JButton btnEmprunts = new JButton("Gérer les Emprunts");
        JButton btnDéconnexion = new JButton("Déconnexion");

        panelBoutons.add(btnLivres);
        panelBoutons.add(btnUtilisateurs);
        panelBoutons.add(btnEmprunts);
        panelBoutons.add(btnDéconnexion);

        add(panelBoutons, BorderLayout.CENTER);

        // Actions des boutons
        btnLivres.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuPrincipal.this, "Ouverture de la gestion des livres...");
                // ouvrir la fenêtre de gestion des livres ici
            }
        });

        btnUtilisateurs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuPrincipal.this, "Ouverture de la gestion des utilisateurs...");
                // ouvrir la fenêtre de gestion des utilisateurs ici
            }
        });

        btnEmprunts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuPrincipal.this, "Ouverture de la gestion des emprunts...");
                // ouvrir la fenêtre de gestion des emprunts ici
            }
        });

        btnDéconnexion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choix = JOptionPane.showConfirmDialog(MenuPrincipal.this, "Êtes-vous sûr de vouloir vous déconnecter ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choix == JOptionPane.YES_OPTION) {
                    dispose(); // Ferme la fenêtre du menu
                    // Retour à la fenêtre de connexion ici
                    new LoginFrame().setVisible(true);
                }
            }
        });
    }

    // Pour tester directement cette classe
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal("Admin").setVisible(true);
            
        });
    }
}
