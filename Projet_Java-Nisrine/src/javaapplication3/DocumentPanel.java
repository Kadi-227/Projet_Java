package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class DocumentPanel extends RoundedPanel {
    private JPanel documentsContainer;
    private RoundedTextField searchField;
    private RoundedButton addButton;
    private ArrayList<String[]> documents = new ArrayList<>();

    public DocumentPanel() {
        super(new Color(0x8BA1B6), 25); // Blue-gray background with 25px corner radius
        setLayout(new BorderLayout(5, 5)); // Reduced outer padding
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Reduced padding
        initializeUI();
        loadDocuments();
    }

    private void initializeUI() {
        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Reduced padding
        searchField = new RoundedTextField("Chercher", 15);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add border for clarity

        // Add FocusListener to handle placeholder text
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

        // Search Button with Icon
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/icons/search.png"));
        RoundedButton searchButton = new RoundedButton(searchIcon);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setPreferredSize(new Dimension(30, 30)); // Adjust size to fit icon
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);

        // Documents Container
        documentsContainer = new JPanel();
        documentsContainer.setLayout(new BoxLayout(documentsContainer, BoxLayout.Y_AXIS));
        documentsContainer.setOpaque(false);
        documentsContainer.setBackground(new Color(0x8BA1B6)); // Match parent background
        JScrollPane scrollPane = new JScrollPane(documentsContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Add Button
        addButton = new RoundedButton("Ajouter");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Slightly smaller font
        addButton.setBackground(new Color(0x007BFF));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(90, 35)); // Reduced size
        addButton.addActionListener(e -> addNewDocument());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5)); // Padding
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadDocuments() {
        documentsContainer.removeAll();
        String[][] initialDocuments = {
            {"INV003", "Data Structures", "Dr. Lee", "Alice Johnson", "2021", "Mathématiques"},
            {"INV004", "Physics for Beginners", "Dr. Brown", "Bob Brown", "2020", "Physique"},
            {"INV005", "Thesis on AI", "Dr. Davis", "Eve Davis", "2024", "Informatique"},
            {"INV006", "Machine Learning", "Dr. Wilson", "Sam Wilson", "2023", "Informatique"}
        };
        for (String[] doc : initialDocuments) {
            documents.add(doc);
            addDocumentItem(doc[0], doc[1], doc[2], doc[3], doc[4], doc[5]);
        }
        documentsContainer.revalidate();
        documentsContainer.repaint();
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.equals("chercher")) {
            query = ""; // Treat "Chercher" placeholder as an empty query
        }

        documentsContainer.removeAll();
        for (String[] doc : documents) {
            boolean matches = false;
            for (String field : doc) {
                if (field.toLowerCase().contains(query)) {
                    matches = true;
                    break;
                }
            }
            if (matches || query.isEmpty()) {
                addDocumentItem(doc[0], doc[1], doc[2], doc[3], doc[4], doc[5]);
            }
        }
        documentsContainer.revalidate();
        documentsContainer.repaint();
    }

    private void addNewDocument() {
        DocumentDialog dialog = new DocumentDialog(null, "Ajouter un Document", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            String[] newDoc = dialog.getResult();
            documents.add(newDoc);
            performSearch(); // Refresh the UI with the current search query
        }
    }

    private void editDocument(String[] doc, JPanel itemPanel) {
        DocumentDialog dialog = new DocumentDialog(null, "Modifier un Document", doc);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            String[] updatedDoc = dialog.getResult();
            int index = documents.indexOf(doc);
            documents.set(index, updatedDoc);
            performSearch(); // Refresh the UI with the current search query
        }
    }

    private void addDocumentItem(String numInventaire, String titre, String encadrant, String auteur, String annee, String categorie) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5)); // Reduced internal padding
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setOpaque(true);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5) // Reduced inner padding
        ));

        // Placeholder Image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100)); // Increased width to 100 from 60
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.LIGHT_GRAY);
        itemPanel.add(imageLabel, BorderLayout.WEST);

        // Details Panel with GridBagLayout for better alignment
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5); // Reduced insets
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel titleLabel = new JLabel("Titre");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel titreValue = new JLabel(titre);
        titreValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(titreValue, gbc);

        // Encadrant
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        RoundedLabel encadrantLabel = new RoundedLabel("Encadrant");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel encadrantValue = new RoundedLabel(encadrant);
        encadrantValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(encadrantValue, gbc);

        // Auteur
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        RoundedLabel authorLabel = new RoundedLabel("Auteur");
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(authorLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel auteurValue = new RoundedLabel(auteur);
        auteurValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(auteurValue, gbc);

        // Année
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        RoundedLabel anneeLabel = new RoundedLabel("Année");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(anneeLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel anneeValue = new RoundedLabel(annee);
        anneeValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(anneeValue, gbc);

        // Catégorie
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1;
        RoundedLabel categoryLabel = new RoundedLabel("Catégorie");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel categorieValue = new RoundedLabel(categorie);
        categorieValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(categorieValue, gbc);

        // Numéro d'inventaire
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 1;
        RoundedLabel inventoryLabel = new RoundedLabel("Numéro d'inventaire");
        inventoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(inventoryLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel inventoryValue = new RoundedLabel(numInventaire);
        inventoryValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(inventoryValue, gbc);

        itemPanel.add(detailsPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.insets = new Insets(0, 5, 0, 5);
        gbcButtons.anchor = GridBagConstraints.CENTER;

        // Edit Button with Gear Icon
        ImageIcon gearIcon = new ImageIcon(getClass().getResource("/icons/gear.png"));
        RoundedButton editButton = new RoundedButton(gearIcon);
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setPreferredSize(new Dimension(24, 24)); // Adjusted size for better visibility
        editButton.addActionListener(e -> editDocument(new String[]{numInventaire, titre, encadrant, auteur, annee, categorie}, itemPanel));
        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        actionPanel.add(editButton, gbcButtons);

        // Delete Button with Garbage Icon
        ImageIcon garbageIcon = new ImageIcon(getClass().getResource("/icons/garbage.png"));
        RoundedButton deleteButton = new RoundedButton(garbageIcon);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setPreferredSize(new Dimension(24, 24)); // Adjusted size for better visibility
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete this document?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                String[] docToRemove = new String[]{numInventaire, titre, encadrant, auteur, annee, categorie};
                for (int i = 0; i < documents.size(); i++) {
                    String[] existingDoc = documents.get(i);
                    boolean match = true;
                    for (int j = 0; j < docToRemove.length; j++) {
                        if (!docToRemove[j].equals(existingDoc[j])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        documents.remove(i);
                        documentsContainer.remove(itemPanel);
                        performSearch(); // Refresh the UI with the current search query
                        break;
                    }
                }
            }
        });
        gbcButtons.gridx = 1; gbcButtons.gridy = 0;
        actionPanel.add(deleteButton, gbcButtons);

        itemPanel.add(actionPanel, BorderLayout.EAST);

        documentsContainer.add(itemPanel);
        documentsContainer.add(Box.createVerticalStrut(15)); // Increased vertical gap for better spacing
    }
}