package alaedesignlapartieréservations;

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

public class TheseDoctoratPanel extends RoundedPanel {
    private JPanel thesesContainer;
    private JTextField searchField;
    private JButton addButton;
    private ArrayList<String[]> theses = new ArrayList<>();
    private Connection conn;

    public TheseDoctoratPanel() {
        super(new Color(0x8BA1B6), 25);
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeDatabaseConnection();
        initializeUI();
        loadTheses();
    }

    private void initializeDatabaseConnection() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/unilib_db?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "alae";
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

        thesesContainer = new JPanel();
        thesesContainer.setLayout(new BoxLayout(thesesContainer, BoxLayout.Y_AXIS));
        thesesContainer.setOpaque(false);
        thesesContainer.setBackground(new Color(0x8BA1B6));
        JScrollPane scrollPane = new JScrollPane(thesesContainer);
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
        addButton.addActionListener(e -> addNewThese());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadTheses() {
        thesesContainer.removeAll();
        try {
            String query = "SELECT d.num_inventaire, d.auteur, d.encadrant, d.annee, d.image " +
                          "FROM document d JOIN these_doctorat t ON d.num_inventaire = t.num_inventaire " +
                          "WHERE d.type_document = 'These Doctorat'";
            System.out.println("Executing query: " + query);
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String numInventaire = rs.getString("num_inventaire");
                String auteur = rs.getString("auteur");
                String encadrant = rs.getString("encadrant");
                String annee = String.valueOf(rs.getInt("annee"));
                String image = rs.getString("image");
                System.out.println("Found thesis: " + numInventaire + ", Auteur: " + auteur);
                addTheseItem(numInventaire, auteur, encadrant, annee, image);
                count++;
            }
            System.out.println("Total theses loaded: " + count);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading theses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        thesesContainer.revalidate();
        thesesContainer.repaint();
    }

    private void performSearch() {
        thesesContainer.removeAll();
        try {
            String query = "SELECT d.num_inventaire, d.auteur, d.encadrant, d.annee, d.image " +
                          "FROM document d JOIN these_doctorat t ON d.num_inventaire = t.num_inventaire " +
                          "WHERE d.type_document = 'These Doctorat' AND (d.auteur LIKE ? OR d.encadrant LIKE ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            String searchQuery = "%" + searchField.getText().trim() + "%";
            if (searchField.getText().trim().equalsIgnoreCase("Chercher")) {
                searchQuery = "%";
            }
            stmt.setString(1, searchQuery);
            stmt.setString(2, searchQuery);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String numInventaire = rs.getString("num_inventaire");
                String auteur = rs.getString("auteur");
                String encadrant = rs.getString("encadrant");
                String annee = String.valueOf(rs.getInt("annee"));
                String image = rs.getString("image");
                addTheseItem(numInventaire, auteur, encadrant, annee, image);
                count++;
            }
            System.out.println("Total search results loaded: " + count);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching theses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        thesesContainer.revalidate();
        thesesContainer.repaint();
    }

    private void addNewThese() {
        EditTheseFrame frame = new EditTheseFrame(null);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] newThese = frame.getResult();
                    String numInventaire = "INV" + String.format("%03d", (int) (Math.random() * 1000));
                    newThese[0] = numInventaire;
                    try {
                        conn.setAutoCommit(false);
                        String insertDocQuery = "INSERT INTO document (num_inventaire, type_document, titre, auteur, nbr_exemplaires, image, encadrant, annee) " +
                                               "VALUES (?, 'These Doctorat', ?, ?, 1, ?, ?, ?)";
                        PreparedStatement docStmt = conn.prepareStatement(insertDocQuery);
                        docStmt.setString(1, numInventaire);
                        docStmt.setString(2, "Thesis Title " + numInventaire); // Placeholder titre
                        docStmt.setString(3, newThese[1]); // auteur
                        docStmt.setString(4, "/icons/default.jpg"); // image
                        docStmt.setString(5, newThese[2]); // encadrant
                        docStmt.setInt(6, Integer.parseInt(newThese[3])); // annee
                        docStmt.executeUpdate();
                        docStmt.close();

                        String insertTheseQuery = "INSERT INTO these_doctorat (num_inventaire, nbr_exemplaires) " +
                                                 "VALUES (?, 1)";
                        PreparedStatement theseStmt = conn.prepareStatement(insertTheseQuery);
                        theseStmt.setString(1, numInventaire);
                        theseStmt.executeUpdate();
                        theseStmt.close();

                        conn.commit();
                        performSearch();
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(TheseDoctoratPanel.this, "Error adding thesis: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void editThese(String[] these, JPanel itemPanel) {
        EditTheseFrame frame = new EditTheseFrame(these);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] updatedThese = frame.getResult();
                    String numInventaire = updatedThese[0];
                    try {
                        conn.setAutoCommit(false);
                        String updateDocQuery = "UPDATE document SET auteur = ?, encadrant = ?, annee = ? " +
                                               "WHERE num_inventaire = ? AND type_document = 'These Doctorat'";
                        PreparedStatement docStmt = conn.prepareStatement(updateDocQuery);
                        docStmt.setString(1, updatedThese[1]); // auteur
                        docStmt.setString(2, updatedThese[2]); // encadrant
                        docStmt.setInt(3, Integer.parseInt(updatedThese[3])); // annee
                        docStmt.setString(4, numInventaire);
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
                        JOptionPane.showMessageDialog(TheseDoctoratPanel.this, "Error updating thesis: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void addTheseItem(String numInventaire, String auteur, String encadrant, String annee, String imagePath) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setOpaque(true);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel imageLabel = new JLabel();
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
        JLabel auteurLabel = new JLabel("Auteur");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(auteurLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel auteurValue = new JLabel(auteur);
        auteurValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(auteurValue, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel encadrantLabel = new JLabel("Encadrant");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel encadrantValue = new JLabel(encadrant);
        encadrantValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(encadrantValue, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel anneeLabel = new JLabel("Année");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailsPanel.add(anneeLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel anneeValue = new JLabel(annee);
        anneeValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsPanel.add(anneeValue, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
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

        JButton editButton = new JButton();
        try {
            editButton.setIcon(new ImageIcon(getClass().getResource("/icons/gear.png")));
        } catch (Exception e) {
            editButton.setText("Edit");
        }
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setPreferredSize(new Dimension(24, 24));
        editButton.addActionListener(e -> editThese(new String[]{numInventaire, auteur, encadrant, annee, imagePath}, itemPanel));
        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        actionPanel.add(editButton, gbcButtons);

        JButton deleteButton = new JButton();
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
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this thesis?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    conn.setAutoCommit(false);
                    String deleteTheseQuery = "DELETE FROM these_doctorat WHERE num_inventaire = ?";
                    PreparedStatement theseStmt = conn.prepareStatement(deleteTheseQuery);
                    theseStmt.setString(1, numInventaire);
                    theseStmt.executeUpdate();
                    theseStmt.close();

                    String deleteDocQuery = "DELETE FROM document WHERE num_inventaire = ? AND type_document = 'These Doctorat'";
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
                    JOptionPane.showMessageDialog(TheseDoctoratPanel.this, "Error deleting thesis: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        thesesContainer.add(itemPanel);
        thesesContainer.add(Box.createVerticalStrut(15));
    }
}

class EditTheseFrame extends JFrame {
    private JTextField auteurField;
    private JTextField encadrantField;
    private JComboBox<String> anneeCombo;
    private String[] result;
    private boolean confirmed;

    public EditTheseFrame(String[] these) {
        super("Modifier une Thèse");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
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
        JLabel auteurLabel = new JLabel("Auteur:");
        auteurLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(auteurLabel, gbc);
        gbc.gridx = 1;
        auteurField = new JTextField(these != null ? these[1] : "", 20);
        formPanel.add(auteurField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel encadrantLabel = new JLabel("Encadrant:");
        encadrantLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(encadrantLabel, gbc);
        gbc.gridx = 1;
        encadrantField = new JTextField(these != null ? these[2] : "", 20);
        formPanel.add(encadrantField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel anneeLabel = new JLabel("Année:");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(anneeLabel, gbc);
        gbc.gridx = 1;
        String[] years = {"2020", "2021", "2022", "2023", "2024", "2025"};
        anneeCombo = new JComboBox<>(years);
        if (these != null) anneeCombo.setSelectedItem(these[3]);
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
                these != null ? these[0] : "",
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