package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class DocumentDialog extends JDialog {
    private JTextField sujetField;
    private JTextField encadrantField;
    private JTextField auteurField;
    private JComboBox<String> anneeCombo;
    private JComboBox<String> categorieCombo;
    private String[] result;
    private boolean isConfirmed;

    public DocumentDialog(Frame parent, String title, String[] initialData) {
        super(parent, title, true);
        setLayout(new BorderLayout(10, 10));
        setSize(400, 300);
        setLocationRelativeTo(parent);
        isConfirmed = false;

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Sujet
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Sujet"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        sujetField = new JTextField(20);
        sujetField.setText(initialData != null ? initialData[1] : "");
        mainPanel.add(sujetField, gbc);

        // Encadrant
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Encadrant"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        encadrantField = new JTextField(20);
        encadrantField.setText(initialData != null ? initialData[2] : "");
        mainPanel.add(encadrantField, gbc);

        // Auteur
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Auteur"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        auteurField = new JTextField(20);
        auteurField.setText(initialData != null ? initialData[3] : "");
        mainPanel.add(auteurField, gbc);

        // Année
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Année"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        String[] years = {"2020", "2021", "2022", "2023", "2024", "2025"};
        anneeCombo = new JComboBox<>(years);
        if (initialData != null) {
            anneeCombo.setSelectedItem(initialData[4]);
        } else {
            anneeCombo.setSelectedItem("2025");
        }
        mainPanel.add(anneeCombo, gbc);

        // Catégorie
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Catégorie"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        String[] categories = {"Informatique", "Mathématiques", "Physique"};
        categorieCombo = new JComboBox<>(categories);
        if (initialData != null) {
            categorieCombo.setSelectedItem(initialData[5]);
        }
        mainPanel.add(categorieCombo, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.setBackground(new Color(0x007BFF));
        ajouterButton.setForeground(Color.WHITE);
        ajouterButton.addActionListener(e -> {
            result = new String[]{
                initialData != null ? initialData[0] : generateNumInventaire(),
                sujetField.getText(),
                encadrantField.getText(),
                auteurField.getText(),
                (String) anneeCombo.getSelectedItem(),
                (String) categorieCombo.getSelectedItem()
            };
            isConfirmed = true;
            dispose();
        });

        JButton annulerButton = new JButton("Annuler");
        annulerButton.setBackground(new Color(0x007BFF));
        annulerButton.setForeground(Color.WHITE);
        annulerButton.addActionListener(e -> dispose());

        buttonPanel.add(ajouterButton);
        buttonPanel.add(annulerButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String generateNumInventaire() {
        return "INV" + String.format("%03d", (int) (Math.random() * 1000));
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public String[] getResult() {
        return result;
    }
}