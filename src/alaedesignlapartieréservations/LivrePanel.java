package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;

public class LivrePanel extends RoundedPanel {
    private JPanel livresContainer;
    private JTextField searchField;
    private JButton addButton;
    private ArrayList<String[]> livres = new ArrayList<>();

    public LivrePanel() {
        super(new Color(0x8BA1B6), 25); // Blue-gray background with 25px corner radius
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeUI();
        loadLivres();
    }

    private void initializeUI() {
        // Search Panel
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

        // Livres Container
        livresContainer = new JPanel();
        livresContainer.setLayout(new BoxLayout(livresContainer, BoxLayout.Y_AXIS));
        livresContainer.setOpaque(false);
        livresContainer.setBackground(new Color(0x8BA1B6));
        JScrollPane scrollPane = new JScrollPane(livresContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Add Button
        addButton = new JButton("Ajouter");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(0x007BFF));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(90, 35));
        addButton.addActionListener(e -> addNewLivre());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadLivres() {
        livresContainer.removeAll();
        String[][] initialLivres = {
            {"INV001", "Introduction to Java", "John Smith", "5", "Informatique", "A1", "/path/to/default.jpg"},
            {"INV002", "Physics 101", "Jane Doe", "3", "Physique", "B2", "/path/to/default.jpg"}
        };
        for (String[] livre : initialLivres) {
            livres.add(livre);
            addLivreItem(livre[0], livre[1], livre[2], livre[3], livre[4], livre[5], livre[6]);
        }
        livresContainer.revalidate();
        livresContainer.repaint();
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.equals("chercher")) {
            query = "";
        }

        livresContainer.removeAll();
        for (String[] livre : livres) {
            boolean matches = false;
            for (String field : livre) {
                if (field.toLowerCase().contains(query)) {
                    matches = true;
                    break;
                }
            }
            if (matches || query.isEmpty()) {
                addLivreItem(livre[0], livre[1], livre[2], livre[3], livre[4], livre[5], livre[6]);
            }
        }
        livresContainer.revalidate();
        livresContainer.repaint();
    }

    private void addNewLivre() {
        EditLivreFrame frame = new EditLivreFrame(null);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] newLivre = frame.getResult();
                    String numInventaire = "INV" + String.format("%03d", (int) (Math.random() * 1000));
                    newLivre[0] = numInventaire;
                    if (newLivre[6] == null || newLivre[6].isEmpty()) {
                        newLivre[6] = "/path/to/default.jpg"; // Default image if none selected
                    }
                    livres.add(newLivre);
                    performSearch(); // Refresh the UI
                }
            }
        });
    }

    private void editLivre(String[] livre, JPanel itemPanel) {
        EditLivreFrame frame = new EditLivreFrame(livre);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] updatedLivre = frame.getResult();
                    int index = livres.indexOf(livre);
                    livres.set(index, updatedLivre);
                    performSearch(); // Refresh the UI
                }
            }
        });
    }

    private void addLivreItem(String numInventaire, String titre, String auteur, String nbrExemplaires, String categorie, String cote, String imagePath) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setOpaque(true);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        ImageIcon icon = new ImageIcon(imagePath);
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            imageLabel.setOpaque(true);
            imageLabel.setBackground(Color.LIGHT_GRAY);
        }
        itemPanel.add(imageLabel, BorderLayout.WEST);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel titleLabel = new JLabel("Titre");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(titleLabel, gbc);
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
        JLabel nbrLabel = new JLabel("Nombre d'exemplaires");
        nbrLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(nbrLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel nbrValue = new JLabel(nbrExemplaires);
        nbrValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(nbrValue, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        JLabel categorieLabel = new JLabel("Catégorie");
        categorieLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(categorieLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel categorieValue = new JLabel(categorie);
        categorieValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(categorieValue, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel coteLabel = new JLabel("Côté");
        coteLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(coteLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel coteValue = new JLabel(cote);
        coteValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(coteValue, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
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
        editButton.addActionListener(e -> editLivre(new String[]{numInventaire, titre, auteur, nbrExemplaires, categorie, cote, imagePath}, itemPanel));
        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        actionPanel.add(editButton, gbcButtons);

        JButton deleteButton = new JButton(new ImageIcon(getClass().getResource("/icons/garbage.png")));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setPreferredSize(new Dimension(24, 24));
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                livres.removeIf(l -> l[0].equals(numInventaire));
                livresContainer.remove(itemPanel);
                performSearch();
            }
        });
        gbcButtons.gridx = 1; gbcButtons.gridy = 0;
        actionPanel.add(deleteButton, gbcButtons);

        itemPanel.add(actionPanel, BorderLayout.EAST);
        livresContainer.add(itemPanel);
        livresContainer.add(Box.createVerticalStrut(15));
    }
}

class EditLivreFrame extends JFrame {
    private JTextField titreField;
    private JTextField auteurField;
    private JTextField nbrExemplairesField;
    private JComboBox<String> categorieCombo;
    private JTextField coteField;
    private JLabel imageLabel;
    private String[] result;
    private boolean confirmed;
    private String initialImagePath;

    public EditLivreFrame(String[] livre) {
        super("Modifier un Livre");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 350);
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
        titreField = new JTextField(livre != null ? livre[1] : "", 20);
        formPanel.add(titreField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel auteurLabel = new JLabel("Auteur:");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(auteurLabel, gbc);
        gbc.gridx = 1;
        auteurField = new JTextField(livre != null ? livre[2] : "", 20);
        formPanel.add(auteurField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nbrLabel = new JLabel("Nombre d'exemplaires:");
        nbrLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(nbrLabel, gbc);
        gbc.gridx = 1;
        nbrExemplairesField = new JTextField(livre != null ? livre[3] : "", 20);
        formPanel.add(nbrExemplairesField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel categorieLabel = new JLabel("Catégorie:");
        categorieLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(categorieLabel, gbc);
        gbc.gridx = 1;
        String[] categories = {"Informatique", "Mathématiques", "Physique"};
        categorieCombo = new JComboBox<>(categories);
        if (livre != null) categorieCombo.setSelectedItem(livre[4]);
        formPanel.add(categorieCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel coteLabel = new JLabel("Côté:");
        coteLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(coteLabel, gbc);
        gbc.gridx = 1;
        coteField = new JTextField(livre != null ? livre[5] : "", 20);
        formPanel.add(coteField, gbc);

        // Image Selection
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setOpaque(false);
        imageLabel = new JLabel();
        if (livre != null && livre[6] != null) {
            ImageIcon icon = new ImageIcon(livre[6]);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("No Image");
            }
            initialImagePath = livre[6];
        } else {
            imageLabel.setText("No Image");
            initialImagePath = "/path/to/default.jpg";
        }
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imagePanel.add(imageLabel);

        JButton changeImageButton = new JButton("Changer Image");
        changeImageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changeImageButton.setBackground(new Color(0x007BFF));
        changeImageButton.setForeground(Color.WHITE);
        changeImageButton.setFocusPainted(false);
        changeImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                    initialImagePath = selectedFile.getAbsolutePath();
                }
            }
        });
        imagePanel.add(changeImageButton);
        formPanel.add(imagePanel, gbc);

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
                livre != null ? livre[0] : "",
                titreField.getText(),
                auteurField.getText(),
                nbrExemplairesField.getText(),
                (String) categorieCombo.getSelectedItem(),
                coteField.getText(),
                initialImagePath
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