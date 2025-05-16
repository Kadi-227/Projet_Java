package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UtilisateursPanel extends RoundedPanel {
    private RoundedTextField cneField, nomField, prenomField, emailField, searchField,cinField;
    private RoundedComboBox niveauComboBox, filiereComboBox;
    private RoundedButton modifierButton, supprimerButton;
    private RoundedButton searchButton;

    public UtilisateursPanel() {
        // Set the background color to light blue
        super(new Color(0x8BA1B6), 25);  // Adjust the color as per your requirements
        
        // Set layout for organizing components
        setLayout(null);
        
        // Initialize components
        searchField = new RoundedTextField("Recherche par CNE...", 40);  // Placeholder text for search field
        searchField.setForeground(Color.GRAY);  // Placeholder color
        searchButton = new RoundedButton(new ImageIcon(getClass().getResource("/icons/search.png")));
        searchButton.setPreferredSize(new Dimension(24, 24));  // Resize the search button
        cneField = new RoundedTextField("CNE", 20);
        cinField = new RoundedTextField("CIN", 20);
        nomField = new RoundedTextField("NOM", 20);
        prenomField = new RoundedTextField("PRENOM", 20);
        emailField = new RoundedTextField("EMAIL", 40);
        
        niveauComboBox = new RoundedComboBox();
        filiereComboBox = new RoundedComboBox();
        
        // Populate combo boxes
        remplirComboBoxNiveau(niveauComboBox);
        niveauComboBox.setPreferredSize(new Dimension(250, 30)); // Ajuste la largeur et la hauteur
        remplirComboBoxFiliere(filiereComboBox);
        filiereComboBox.setPreferredSize(new Dimension(250, 30));
        
        // Set buttons
        modifierButton = new RoundedButton("Modifier");
        modifierButton.setBackground(Color.blue);
        modifierButton.setForeground(Color.white);
        supprimerButton = new RoundedButton("Supprimer");
        supprimerButton.setBackground(Color.blue);
        supprimerButton.setForeground(Color.white);
        
        // Make CNE et CIN non-editable
        cneField.setEditable(false);
        cinField.setEditable(false);
        
        // Set general properties
        setPreferredSize(new Dimension(600, 400));  // Set preferred panel size
        
        // Title label
        JLabel titleLabel = new JLabel("Gestion des Etudiants");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.DARK_GRAY);
        
        titleLabel.setBounds(250, 20, 500, 40);// Center the title
        add(titleLabel);
        
        // Search Field
        searchField.setBounds(220,100,300,30);
        add(searchField);
                 
        searchButton.setBounds(520,100,30,30);
        add(searchButton);
        
        // Email
        emailField.setBounds(165, 150, 450, 30); 
        add(emailField);
        
        // CNE & CNI
        cneField.setBounds(150, 200, 200, 30); 
        add(cneField); 
        
        cinField.setBounds(430, 200, 200, 30);
        add(cinField); 
        
        // Nom & Prenom
        nomField.setBounds(150, 240, 200, 30);
        add(nomField);
        
        prenomField.setBounds(430, 240, 200, 30); 
        add(prenomField);
        
        
        // Niveau & Filiere
        niveauComboBox.setBounds(150, 280, 200, 30);
        add(niveauComboBox);
        
        filiereComboBox.setBounds(430, 280, 200, 30);
        add(filiereComboBox);
        
        // Modifier & Supprimer Buttons
        modifierButton.setBounds(200, 350, 160, 35);
        add(modifierButton);
        
        supprimerButton.setBounds(420, 350, 160, 35);
        add(supprimerButton);
        
        // Button actions
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechercherEtudiant();
            }
        });
        
        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifierEtudiant();
            }
        });
        
        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerEtudiant();
            }
        });
    }
    
    // Helper method to create a row with a label and a component (like JTextField or ComboBox)
    private JPanel createRow(String labelText, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT)); // Layout for this row
        row.add(new JLabel(labelText));  // Add the label
        row.add(field);  // Add the field (TextField, ComboBox, etc.)
        return row;  // Return the complete row
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib", "root", "");
    }

    // Method for searching the student
    private void rechercherEtudiant() {
        String cne = searchField.getText();  
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM etudiant WHERE cne = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cne);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                emailField.setText(rs.getString("email"));
                nomField.setText(rs.getString("nom"));
                prenomField.setText(rs.getString("prenom"));
                niveauComboBox.setSelectedItem(rs.getString("niveau_etudes"));
                cneField.setText(rs.getString("cne"));
                cinField.setText(rs.getString("cin_utilisateur"));
                cneField.setEditable(false);  // Make CNE non-editable
                cinField.setEditable(false);  // Make CIN non-editable
                                  
                String filiereSql = "SELECT nom_filiere FROM filiere WHERE id_filiere = ?";
                PreparedStatement filiereStmt = conn.prepareStatement(filiereSql);
                filiereStmt.setInt(1, rs.getInt("id_filiere"));
                ResultSet filiereRs = filiereStmt.executeQuery();
                if (filiereRs.next()) {
                    filiereComboBox.setSelectedItem(filiereRs.getString("nom_filiere"));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Étudiant non trouvé.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche de l'étudiant.");
        }
    }

    // Modifie les informations de l'étudiant
    private void modifierEtudiant() {
        // CNE et CIN ne doivent pas être modifiés
        String cne = cneField.getText();// CNE ne peut pas être changé
        String cin = cinField.getText(); // CIN ne peut pas être changé
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String niveau = (String) niveauComboBox.getSelectedItem();
        String filiere = (String) filiereComboBox.getSelectedItem();
        // Vérification si un CNE a été saisi
        if (cne.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le CNE ne peut pas être vide.");
            return;
        }
        if (cin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le CNI ne peut pas être vide.");
            return;
        }
        // Commencer la mise à jour en base de données
        try (Connection conn = getConnection()) {
            // Récupérer l'ID de la filière
            String filiereSql = "SELECT id_filiere FROM filiere WHERE nom_filiere = ?";
            PreparedStatement filiereStmt = conn.prepareStatement(filiereSql);
            filiereStmt.setString(1, filiere);
            ResultSet rs = filiereStmt.executeQuery();
            int idFiliere = -1;
            if (rs.next()) {
                idFiliere = rs.getInt("id_filiere");
            }
            // Mise à jour des informations de l'étudiant, mais on ne touche pas au CNE ni au CIN
            String updateSql = "UPDATE etudiant SET nom = ?, prenom = ?, email = ?, niveau_etudes = ?, id_filiere = ? WHERE cne = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, nom);
                stmt.setString(2, prenom);
                stmt.setString(3, email);
                stmt.setString(4, niveau);
                stmt.setInt(5, idFiliere);
                stmt.setString(6, cne);  // Le CNE est utilisé ici comme clé primaire pour l'identification de l'étudiant
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Les informations de l'étudiant ont été mises à jour avec succès.");
                } else {
                    JOptionPane.showMessageDialog(this, "Aucune modification effectuée.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour des informations.");
        }
    }

    // Supprime un étudiant de la base de données et toutes ses données associées
    private void supprimerEtudiant() {
        String cne = cneField.getText();
        // Démarrer la suppression des enregistrements associés
        Connection conn = null;  // Déclare conn avant le try
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib", "root", "");
            conn.setAutoCommit(false);  // Démarrer une transaction pour effectuer toutes les suppressions ensemble
            // Supprimer les alertes associées à l'étudiant
            String deleteAlertesSql = "DELETE FROM alerte WHERE cne_etudiant = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteAlertesSql)) {
                stmt.setString(1, cne);
                stmt.executeUpdate();
            }
            // Supprimer les avis associés à l'étudiant
            String deleteAvisSql = "DELETE FROM avis WHERE cin = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteAvisSql)) {
                stmt.setString(1, cne);
                stmt.executeUpdate();
            }
            // Supprimer les réservations associées à l'étudiant
            String deleteReservationsSql = "DELETE FROM reservation WHERE cne_etudiant = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteReservationsSql)) {
                stmt.setString(1, cne);
                stmt.executeUpdate();
            }
            // Supprimer les messages associés à l'étudiant (si nécessaire)
            String deleteMessagesSql = "DELETE FROM message WHERE cne_etudiant = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteMessagesSql)) {
                stmt.setString(1, cne);
                stmt.executeUpdate();
            }
            // Supprimer les demandes associées à l'étudiant
            String deleteDemandesSql = "DELETE FROM demande WHERE id_demande IN (SELECT id_demande FROM message WHERE cne_etudiant = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deleteDemandesSql)) {
                stmt.setString(1, cne);
                stmt.executeUpdate();
            }
            // Supprimer l'étudiant de la table `etudiant`
            String deleteEtudiantSql = "DELETE FROM etudiant WHERE cne = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteEtudiantSql)) {
                stmt.setString(1, cne);
                stmt.executeUpdate();
            }
            // Supprimer l'utilisateur associé dans la table `utilisateur`
            String deleteUtilisateurSql = "DELETE FROM utilisateur WHERE cin = (SELECT cin_utilisateur FROM etudiant WHERE cne = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deleteUtilisateurSql)) {
                stmt.setString(1, cne);
                stmt.executeUpdate();
            }
            // Commit la transaction si tout s'est bien passé
            conn.commit();
            JOptionPane.showMessageDialog(this, "L'étudiant et toutes ses données ont été supprimés avec succès.");
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();  // Annule les modifications si une erreur se produit
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'étudiant. Les changements ont été annulés.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);  // Réactive le mode auto-commit
                    conn.close();  // Ferme la connexion à la base de données
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void remplirComboBoxNiveau(RoundedComboBox comboBoxNiveau) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT nom_niveau FROM niveau_etudes";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboBoxNiveau.addItem(rs.getString("nom_niveau"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void remplirComboBoxFiliere(RoundedComboBox comboBoxFiliere) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT nom_filiere FROM filiere";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboBoxFiliere.addItem(rs.getString("nom_filiere"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}