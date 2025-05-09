package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class MemoireMasterPanel extends RoundedPanel {
    private JPanel memoiresContainer;
    private JTextField searchField;
    private JButton addButton;
    private ArrayList<String[]> memoires = new ArrayList<>();

    public MemoireMasterPanel() {
        super(new Color(0x8BA1B6), 25);
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeUI();
        loadMemoires();
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

        memoiresContainer = new JPanel();
        memoiresContainer.setLayout(new BoxLayout(memoiresContainer, BoxLayout.Y_AXIS));
        memoiresContainer.setOpaque(false);
        memoiresContainer.setBackground(new Color(0x8BA1B6));
        JScrollPane scrollPane = new JScrollPane(memoiresContainer);
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
        addButton.addActionListener(e -> addNewMemoire());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMemoires() {
        memoiresContainer.removeAll();
        String[][] initialMemoires = {
            {"INV007", "AI in Healthcare", "Chris Evans", "Dr. Strange", "2022"},
            {"INV008", "Quantum Computing", "Tony Stark", "Dr. Banner", "2023"}
        };
        for (String[] memoire : initialMemoires) {
            memoires.add(memoire);
            addMemoireItem(memoire[0], memoire[1], memoire[2], memoire[3], memoire[4]);
        }
        memoiresContainer.revalidate();
        memoiresContainer.repaint();
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.equals("chercher")) query = "";
        memoiresContainer.removeAll();
        for (String[] memoire : memoires) {
            boolean matches = false;
            for (String field : memoire) {
                if (field.toLowerCase().contains(query)) {
                    matches = true;
                    break;
                }
            }
            if (matches || query.isEmpty()) {
                addMemoireItem(memoire[0], memoire[1], memoire[2], memoire[3], memoire[4]);
            }
        }
        memoiresContainer.revalidate();
        memoiresContainer.repaint();
    }

    private void addNewMemoire() {
        EditMemoireFrame frame = new EditMemoireFrame(null);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] newMemoire = frame.getResult();
                    String numInventaire = "INV" + String.format("%03d", (int) (Math.random() * 1000));
                    newMemoire[0] = numInventaire;
                    memoires.add(newMemoire);
                    performSearch();
                }
            }
        });
    }

    private void editMemoire(String[] memoire, JPanel itemPanel) {
        EditMemoireFrame frame = new EditMemoireFrame(memoire);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] updatedMemoire = frame.getResult();
                    int index = memoires.indexOf(memoire);
                    memoires.set(index, updatedMemoire);
                    performSearch();
                }
            }
        });
    }

    private void addMemoireItem(String numInventaire, String titre, String auteur, String encadrant, String annee) {
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
        JLabel titreLabel = new JLabel("Titre");
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(titreLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel titreValue = new JLabel(titre);
        titreValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(titreValue, gbc);

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
        editButton.addActionListener(e -> editMemoire(new String[]{numInventaire, titre, auteur, encadrant, annee}, itemPanel));
        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        actionPanel.add(editButton, gbcButtons);

        JButton deleteButton = new JButton(new ImageIcon(getClass().getResource("/icons/garbage.png")));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setPreferredSize(new Dimension(24, 24));
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this memoir?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                memoires.removeIf(m -> m[0].equals(numInventaire));
                memoiresContainer.remove(itemPanel);
                performSearch();
            }
        });
        gbcButtons.gridx = 1; gbcButtons.gridy = 0;
        actionPanel.add(deleteButton, gbcButtons);

        itemPanel.add(actionPanel, BorderLayout.EAST);
        memoiresContainer.add(itemPanel);
        memoiresContainer.add(Box.createVerticalStrut(15));
    }
}

class EditMemoireFrame extends JFrame {
    private JTextField titreField;
    private JTextField auteurField;
    private JTextField encadrantField;
    private JComboBox<String> anneeCombo;
    private String[] result;
    private boolean confirmed;

    public EditMemoireFrame(String[] memoire) {
        super("Modifier un Mémoire");
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
        JLabel titreLabel = new JLabel("Titre:");
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(titreLabel, gbc);
        gbc.gridx = 1;
        titreField = new JTextField(memoire != null ? memoire[1] : "", 20);
        formPanel.add(titreField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel auteurLabel = new JLabel("Auteur:");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(auteurLabel, gbc);
        gbc.gridx = 1;
        auteurField = new JTextField(memoire != null ? memoire[2] : "", 20);
        formPanel.add(auteurField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel encadrantLabel = new JLabel("Encadrant:");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1;
        encadrantField = new JTextField(memoire != null ? memoire[3] : "", 20);
        formPanel.add(encadrantField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel anneeLabel = new JLabel("Année:");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(anneeLabel, gbc);
        gbc.gridx = 1;
        String[] years = {"2020", "2021", "2022", "2023", "2024", "2025"};
        anneeCombo = new JComboBox<>(years);
        if (memoire != null) anneeCombo.setSelectedItem(memoire[4]);
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
                memoire != null ? memoire[0] : "",
                titreField.getText(),
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