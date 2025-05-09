package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class RapportLicencePanel extends RoundedPanel {
    private JPanel rapportsContainer;
    private JTextField searchField;
    private JButton addButton;
    private ArrayList<String[]> rapports = new ArrayList<>();

    public RapportLicencePanel() {
        super(new Color(0x8BA1B6), 25);
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeUI();
        loadRapports();
    }

    private void initializeUI() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        searchField = new JTextField("Chercher", 15);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Chercher")) {
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Chercher");
                }
            }
        });

        JButton searchButton = new JButton(new ImageIcon(getClass().getResource("/icons/search.png")));
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setPreferredSize(new Dimension(30, 30));
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);

        rapportsContainer = new JPanel();
        rapportsContainer.setLayout(new BoxLayout(rapportsContainer, BoxLayout.Y_AXIS));
        rapportsContainer.setOpaque(false);
        rapportsContainer.setBackground(new Color(0x8BA1B6));
        JScrollPane scrollPane = new JScrollPane(rapportsContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        addButton = new JButton("Ajouter");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(0x007BFF));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(90, 35));
        addButton.addActionListener(e -> addNewRapport());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRapports() {
        rapportsContainer.removeAll();
        String[][] initialRapports = {
            {"INV003", "Data Structures Report", "Alice Johnson", "Dr. Lee", "2021"},
            {"INV004", "Physics Project", "Bob Brown", "Dr. Brown", "2020"}
        };
        for (String[] rapport : initialRapports) {
            rapports.add(rapport);
            addRapportItem(rapport[0], rapport[1], rapport[2], rapport[3], rapport[4]);
        }
        rapportsContainer.revalidate();
        rapportsContainer.repaint();
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.equals("chercher")) query = "";
        rapportsContainer.removeAll();
        for (String[] rapport : rapports) {
            boolean matches = false;
            for (String field : rapport) {
                if (field.toLowerCase().contains(query)) {
                    matches = true;
                    break;
                }
            }
            if (matches || query.isEmpty()) {
                addRapportItem(rapport[0], rapport[1], rapport[2], rapport[3], rapport[4]);
            }
        }
        rapportsContainer.revalidate();
        rapportsContainer.repaint();
    }

    private void addNewRapport() {
        EditRapportFrame frame = new EditRapportFrame(null);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] newRapport = frame.getResult();
                    String numInventaire = "INV" + String.format("%03d", (int) (Math.random() * 1000));
                    newRapport[0] = numInventaire;
                    rapports.add(newRapport);
                    performSearch();
                }
            }
        });
    }

    private void editRapport(String[] rapport, JPanel itemPanel) {
        EditRapportFrame frame = new EditRapportFrame(rapport);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] updatedRapport = frame.getResult();
                    int index = rapports.indexOf(rapport);
                    rapports.set(index, updatedRapport);
                    performSearch();
                }
            }
        });
    }

    private void addRapportItem(String numInventaire, String sujet, String auteur, String encadrant, String annee) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setOpaque(true);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.LIGHT_GRAY);
        itemPanel.add(imageLabel, BorderLayout.WEST);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel sujetLabel = new JLabel("Sujet");
        sujetLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(sujetLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel sujetValue = new JLabel(sujet);
        sujetValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(sujetValue, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel auteurLabel = new JLabel("Auteur");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(auteurLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel auteurValue = new JLabel(auteur);
        auteurValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(auteurValue, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel encadrantLabel = new JLabel("Encadrant");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel encadrantValue = new JLabel(encadrant);
        encadrantValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(encadrantValue, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        JLabel anneeLabel = new JLabel("Année");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(anneeLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel anneeValue = new JLabel(annee);
        anneeValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(anneeValue, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel inventoryLabel = new JLabel("Numéro d'inventaire");
        inventoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(inventoryLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel inventoryValue = new JLabel(numInventaire);
        inventoryValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(inventoryValue, gbc);

        itemPanel.add(detailsPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.insets = new Insets(0, 5, 0, 5);
        gbcButtons.anchor = GridBagConstraints.CENTER;

        JButton editButton = new JButton(new ImageIcon(getClass().getResource("/icons/gear.png")));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setPreferredSize(new Dimension(24, 24));
        editButton.addActionListener(e -> editRapport(new String[]{numInventaire, sujet, auteur, encadrant, annee}, itemPanel));
        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        actionPanel.add(editButton, gbcButtons);

        JButton deleteButton = new JButton(new ImageIcon(getClass().getResource("/icons/garbage.png")));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setPreferredSize(new Dimension(24, 24));
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this report?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                rapports.removeIf(r -> r[0].equals(numInventaire));
                rapportsContainer.remove(itemPanel);
                performSearch();
            }
        });
        gbcButtons.gridx = 1; gbcButtons.gridy = 0;
        actionPanel.add(deleteButton, gbcButtons);

        itemPanel.add(actionPanel, BorderLayout.EAST);
        rapportsContainer.add(itemPanel);
        rapportsContainer.add(Box.createVerticalStrut(15));
    }
}

class EditRapportFrame extends JFrame {
    private JTextField sujetField;
    private JTextField auteurField;
    private JTextField encadrantField;
    private JComboBox<String> anneeCombo;
    private String[] result;
    private boolean confirmed;

    public EditRapportFrame(String[] rapport) {
        super("Modifier un Rapport");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0x8BA1B6));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel sujetLabel = new JLabel("Sujet:");
        sujetLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(sujetLabel, gbc);
        gbc.gridx = 1;
        sujetField = new JTextField(rapport != null ? rapport[1] : "", 20);
        formPanel.add(sujetField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel auteurLabel = new JLabel("Auteur:");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(auteurLabel, gbc);
        gbc.gridx = 1;
        auteurField = new JTextField(rapport != null ? rapport[2] : "", 20);
        formPanel.add(auteurField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel encadrantLabel = new JLabel("Encadrant:");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1;
        encadrantField = new JTextField(rapport != null ? rapport[3] : "", 20);
        formPanel.add(encadrantField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel anneeLabel = new JLabel("Année:");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(anneeLabel, gbc);
        gbc.gridx = 1;
        String[] years = {"2020", "2021", "2022", "2023", "2024", "2025"};
        anneeCombo = new JComboBox<>(years);
        if (rapport != null) anneeCombo.setSelectedItem(rapport[4]);
        else anneeCombo.setSelectedItem("2025");
        formPanel.add(anneeCombo, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ajouterButton.setBackground(new Color(0x007BFF));
        ajouterButton.setForeground(Color.WHITE);
        ajouterButton.setFocusPainted(false);
        ajouterButton.addActionListener(e -> {
            result = new String[]{
                rapport != null ? rapport[0] : "",
                sujetField.getText(),
                auteurField.getText(),
                encadrantField.getText(),
                (String) anneeCombo.getSelectedItem()
            };
            confirmed = true;
            dispose();
        });
        buttonPanel.add(ajouterButton);

        JButton annulerButton = new JButton("Annuler");
        annulerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        annulerButton.setBackground(new Color(0x007BFF));
        annulerButton.setForeground(Color.WHITE);
        annulerButton.setFocusPainted(false);
        annulerButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        buttonPanel.add(annulerButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String[] getResult() {
        return result;
    }
}