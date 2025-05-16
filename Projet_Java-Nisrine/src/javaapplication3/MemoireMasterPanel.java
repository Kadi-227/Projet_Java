package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MemoireMasterPanel extends RoundedPanel {
    private JPanel memoiresContainer;
    private RoundedTextField searchField;
    private RoundedButton addButton;
    private ArrayList<String[]> memoires = new ArrayList<>();
    private Connection conn;

    public MemoireMasterPanel() {
        super(new Color(0x8BA1B6), 25);
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeDatabaseConnection();
        initializeUI();
        loadMemoires();
    }

    private void initializeDatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost/unilib";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeUI() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        searchField = new RoundedTextField("Chercher", 15);
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

        RoundedButton searchButton = new RoundedButton(new ImageIcon(getClass().getResource("/icons/search.png")));
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

        addButton = new RoundedButton("Ajouter");
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
        try {
            String query = "SELECT d.num_inventaire, d.titre, d.auteur, d.encadrant, d.annee, d.image " +
                          "FROM document d JOIN memoire_master m ON d.num_inventaire = m.num_inventaire " +
                          "WHERE d.type_document = 'Memoire Master'";
            System.out.println("Executing query: " + query);
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String numInventaire = rs.getString("num_inventaire");
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                String encadrant = rs.getString("encadrant");
                String annee = String.valueOf(rs.getInt("annee"));
                String image = rs.getString("image");
                System.out.println("Found memoir: " + numInventaire + ", Titre: " + titre + ", Auteur: " + auteur);
                addMemoireItem(numInventaire, titre, auteur, encadrant, annee, image);
                count++;
            }
            System.out.println("Total memoirs loaded: " + count);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading memoirs: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        memoiresContainer.revalidate();
        memoiresContainer.repaint();
    }

    private void performSearch() {
        memoiresContainer.removeAll();
        try {
            String query = "SELECT d.num_inventaire, d.titre, d.auteur, d.encadrant, d.annee, d.image " +
                          "FROM document d JOIN memoire_master m ON d.num_inventaire = m.num_inventaire " +
                          "WHERE d.type_document = 'Memoire Master' AND (d.titre LIKE ? OR d.auteur LIKE ? OR d.encadrant LIKE ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            String searchQuery = "%" + searchField.getText().trim() + "%";
            if (searchField.getText().trim().equalsIgnoreCase("Chercher")) {
                searchQuery = "%";
            }
            stmt.setString(1, searchQuery);
            stmt.setString(2, searchQuery);
            stmt.setString(3, searchQuery);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String numInventaire = rs.getString("num_inventaire");
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                String encadrant = rs.getString("encadrant");
                String annee = String.valueOf(rs.getInt("annee"));
                String image = rs.getString("image");
                addMemoireItem(numInventaire, titre, auteur, encadrant, annee, image);
                count++;
            }
            System.out.println("Total search results loaded: " + count);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching memoirs: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
                    try {
                        conn.setAutoCommit(false);
                        String insertDocQuery = "INSERT INTO document (num_inventaire, type_document, titre, auteur, nbr_exemplaires, image, encadrant, annee) " +
                                               "VALUES (?, 'Memoire Master', ?, ?, 1, ?, ?, ?)";
                        PreparedStatement docStmt = conn.prepareStatement(insertDocQuery);
                        docStmt.setString(1, numInventaire);
                        docStmt.setString(2, newMemoire[1]); // titre
                        docStmt.setString(3, newMemoire[2]); // auteur
                        docStmt.setString(4, "/icons/default.jpg"); // image
                        docStmt.setString(5, newMemoire[3]); // encadrant
                        docStmt.setInt(6, Integer.parseInt(newMemoire[4])); // annee
                        docStmt.executeUpdate();
                        docStmt.close();

                        String insertMemoireQuery = "INSERT INTO memoire_master (num_inventaire, nbr_exemplaires) " +
                                                   "VALUES (?, 1)";
                        PreparedStatement memoireStmt = conn.prepareStatement(insertMemoireQuery);
                        memoireStmt.setString(1, numInventaire);
                        memoireStmt.executeUpdate();
                        memoireStmt.close();

                        conn.commit();
                        performSearch();
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(MemoireMasterPanel.this, "Error adding memoir: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            conn.setAutoCommit(true);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
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
                    String numInventaire = updatedMemoire[0];
                    try {
                        conn.setAutoCommit(false);
                        String updateDocQuery = "UPDATE document SET titre = ?, auteur = ?, encadrant = ?, annee = ? " +
                                               "WHERE num_inventaire = ? AND type_document = 'Memoire Master'";
                        PreparedStatement docStmt = conn.prepareStatement(updateDocQuery);
                        docStmt.setString(1, updatedMemoire[1]); // titre
                        docStmt.setString(2, updatedMemoire[2]); // auteur
                        docStmt.setString(3, updatedMemoire[3]); // encadrant
                        docStmt.setInt(4, Integer.parseInt(updatedMemoire[4])); // annee
                        docStmt.setString(5, numInventaire);
                        docStmt.executeUpdate();
                        docStmt.close();

                        conn.commit();
                        performSearch();
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(MemoireMasterPanel.this, "Error updating memoir: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            conn.setAutoCommit(true);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void addMemoireItem(String numInventaire, String titre, String auteur, String encadrant, String annee, String imagePath) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setOpaque(true);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        RoundedLabel imageLabel = new RoundedLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        try {
            ImageIcon icon;
            if (imagePath != null && imagePath.startsWith("/icons/")) {
                icon = new ImageIcon(getClass().getResource(imagePath));
            } else {
                icon = new ImageIcon(imagePath);
            }
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                throw new Exception("Image load failed");
            }
        } catch (Exception e) {
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            imageLabel.setOpaque(true);
            imageLabel.setBackground(Color.LIGHT_GRAY);
            imageLabel.setText("No Image");
        }
        itemPanel.add(imageLabel, BorderLayout.WEST);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        RoundedLabel titreLabel = new RoundedLabel("Titre");
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(titreLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel titreValue = new RoundedLabel(titre);
        titreValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(titreValue, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        RoundedLabel auteurLabel = new RoundedLabel("Auteur");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(auteurLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel auteurValue = new RoundedLabel(auteur);
        auteurValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(auteurValue, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        RoundedLabel encadrantLabel = new RoundedLabel("Encadrant");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel encadrantValue = new RoundedLabel(encadrant);
        encadrantValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(encadrantValue, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        RoundedLabel anneeLabel = new RoundedLabel("Année");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(anneeLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel anneeValue = new RoundedLabel(annee);
        anneeValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(anneeValue, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        RoundedLabel inventoryLabel = new RoundedLabel("Numéro d'inventaire");
        inventoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(inventoryLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        RoundedLabel inventoryValue = new RoundedLabel(numInventaire);
        inventoryValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(inventoryValue, gbc);

        itemPanel.add(detailsPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.insets = new Insets(0, 5, 0, 5);
        gbcButtons.anchor = GridBagConstraints.CENTER;

        RoundedButton editButton = new RoundedButton();
        try {
            editButton.setIcon(new ImageIcon(getClass().getResource("/icons/edit.png")));
        } catch (Exception e) {
            editButton.setText("Edit");
        }
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setPreferredSize(new Dimension(24, 24));
        editButton.addActionListener(e -> editMemoire(new String[]{numInventaire, titre, auteur, encadrant, annee, imagePath}, itemPanel));
        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        actionPanel.add(editButton, gbcButtons);

        RoundedButton deleteButton = new RoundedButton();
        try {
            deleteButton.setIcon(new ImageIcon(getClass().getResource("/icons/garbage.png")));
        } catch (Exception e) {
            deleteButton.setText("Delete");
        }
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setPreferredSize(new Dimension(24, 24));
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this memoir?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    conn.setAutoCommit(false);
                    String deleteMemoireQuery = "DELETE FROM memoire_master WHERE num_inventaire = ?";
                    PreparedStatement memoireStmt = conn.prepareStatement(deleteMemoireQuery);
                    memoireStmt.setString(1, numInventaire);
                    memoireStmt.executeUpdate();
                    memoireStmt.close();

                    String deleteDocQuery = "DELETE FROM document WHERE num_inventaire = ? AND type_document = 'Memoire Master'";
                    PreparedStatement docStmt = conn.prepareStatement(deleteDocQuery);
                    docStmt.setString(1, numInventaire);
                    docStmt.executeUpdate();
                    docStmt.close();

                    conn.commit();
                    performSearch();
                } catch (SQLException ex) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex2) {
                        ex2.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(MemoireMasterPanel.this, "Error deleting memoir: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
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
    private RoundedTextField titreField;
    private RoundedTextField auteurField;
    private RoundedTextField encadrantField;
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
        RoundedLabel titreLabel = new RoundedLabel("Titre:");
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(titreLabel, gbc);
        gbc.gridx = 1;
        titreField = new RoundedTextField(memoire != null ? memoire[1] : "", 20);
        formPanel.add(titreField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        RoundedLabel auteurLabel = new RoundedLabel("Auteur:");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(auteurLabel, gbc);
        gbc.gridx = 1;
        auteurField = new RoundedTextField(memoire != null ? memoire[2] : "", 20);
        formPanel.add(auteurField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        RoundedLabel encadrantLabel = new RoundedLabel("Encadrant:");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1;
        encadrantField = new RoundedTextField(memoire != null ? memoire[3] : "", 20);
        formPanel.add(encadrantField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        RoundedLabel anneeLabel = new RoundedLabel("Année:");
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
        RoundedButton ajouterButton = new RoundedButton("Ajouter");
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

        RoundedButton annulerButton = new RoundedButton("Annuler");
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