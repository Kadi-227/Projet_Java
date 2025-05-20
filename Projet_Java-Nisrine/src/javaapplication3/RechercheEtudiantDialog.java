package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class RechercheEtudiantDialog extends JDialog {
    private RoundedTextField nomField, prenomField;
    private RoundedButton rechercherButton;
    private JList<String> resultList;
    private DefaultListModel<String> listModel;
    private UtilisateursPanel utilisateursPanel;

    public RechercheEtudiantDialog(JFrame parent, UtilisateursPanel utilisateursPanel) {
    super(parent, "Recherche Étudiant", true);
    this.utilisateursPanel = utilisateursPanel;
    setLayout(new BorderLayout(15, 15)); // ➕ marges globales
    setSize(450, 420); // un peu plus large
    setLocationRelativeTo(parent);

    // ➕ Panel d'entrée
    JPanel inputPanel = new JPanel();
    inputPanel.setBackground(new Color(0x8BA1B6)); // ✔ Fond bleu clair
    inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // ✔ Marges internes
    inputPanel.setLayout(new GridLayout(3, 2, 10, 10)); 
    inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // ➕ marges internes
    inputPanel.setLayout(new GridLayout(3, 2, 10, 10));

    nomField = new RoundedTextField();
    prenomField = new RoundedTextField();
    rechercherButton = new RoundedButton("Rechercher");
    
    rechercherButton.addActionListener(e -> {
        System.out.println("Clic détecté !");
        rechercherEtudiants();
    });

    inputPanel.add(new JLabel("Nom :"));
    inputPanel.add(nomField);
    inputPanel.add(new JLabel("Prénom :"));
    inputPanel.add(prenomField);
    inputPanel.add(new JLabel());
    inputPanel.add(rechercherButton);

    add(inputPanel, BorderLayout.NORTH);

    // ➕ Recherche via touche Entrée
    nomField.addActionListener(e -> rechercherEtudiants());
    prenomField.addActionListener(e -> rechercherEtudiants());

    // ➕ Liste de résultats
    listModel = new DefaultListModel<>();
    resultList = new JList<>(listModel);
    resultList.setBorder(BorderFactory.createTitledBorder("Résultats de recherche"));
    JScrollPane scrollPane = new JScrollPane(resultList);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // ➕ marges horizontales
    add(scrollPane, BorderLayout.CENTER);

    // ➕ Double-clic pour sélection
    resultList.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                String selectedValue = resultList.getSelectedValue();
                if (selectedValue.contains("CNE:")) {
                    String cne = selectedValue.substring(selectedValue.indexOf("CNE:") + 4).trim();
                    utilisateursPanel.rechercherEtudiantParCNE(cne);
                    dispose();
                }
            }
        }
    });

    // ➕ Footer avec bouton Fermer
    JPanel footer = new JPanel();
    footer.setBackground(new Color(0x8BA1B6)); // ✔ même fond que le haut
    footer.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10));
    footer.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // ➕ espace à droite

    RoundedButton fermerButton = new RoundedButton("Fermer");
    fermerButton.setBackground(Color.BLUE);         // 🔵 fond bleu
    fermerButton.setForeground(Color.WHITE);        // 🟢 texte blanc
    fermerButton.addActionListener(e -> dispose());
    footer.add(fermerButton);

    add(footer, BorderLayout.SOUTH);
}

    private void rechercherEtudiants() {
        listModel.clear();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib", "root", "")) {
            String sql = "SELECT nom, prenom, cne FROM etudiant WHERE nom LIKE ? AND prenom LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + nom + "%");
            stmt.setString(2, "%" + prenom + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String result = rs.getString("nom") + " " + rs.getString("prenom") + " | CNE:" + rs.getString("cne");
                listModel.addElement(result);
            }

            if (listModel.isEmpty()) {
                listModel.addElement("Aucun étudiant trouvé.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche.");
        }
    }
}
