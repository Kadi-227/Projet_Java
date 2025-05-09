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

public class RapportLicencePanel extends RoundedPanel {
    private JPanel rapportsContainer;
    private JTextField searchField;
    private JButton addButton;
    private Connection conn;

    public RapportLicencePanel() {
        super(new Color(0x8BA1B6), 25);
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeDatabaseConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Cannot proceed without database connection. Please check the logs and restart the application.", "Critical Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        initializeSampleData();
        initializeUI();
        loadRapports();
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

    private void initializeSampleData() {
        try {
            conn.setAutoCommit(false);

            String[] sampleRapports = {
                "Introduction to Java Programming,John Doe,Dr. Smith,2023",
                "Database Design Basics,Jane Smith,Prof. Johnson,2024",
                "Advanced Algorithms,Alice Brown,Dr. Lee,2025"
            };

            for (String rapport : sampleRapports) {
                String[] parts = rapport.split(",");
                String titre = parts[0];
                String auteur = parts[1];
                String encadrant = parts[2];
                int annee = Integer.parseInt(parts[3]);
                String numInventaire = generateUniqueInventoryNumber();

                String checkQuery = "SELECT num_inventaire FROM document WHERE num_inventaire = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, numInventaire);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    numInventaire = generateUniqueInventoryNumber(); // Generate a new one if duplicate
                }
                rs.close();
                checkStmt.close();

                String insertDocQuery = "INSERT INTO document (num_inventaire, type_document, titre, auteur, image, encadrant, annee) " +
                                      "VALUES (?, 'Rapport Licence', ?, ?, ?, ?, ?)";
                PreparedStatement docStmt = conn.prepareStatement(insertDocQuery);
                docStmt.setString(1, numInventaire);
                docStmt.setString(2, titre);
                docStmt.setString(3, auteur);
                docStmt.setString(4, "/icons/default.jpg");
                docStmt.setString(5, encadrant);
                docStmt.setInt(6, annee);
                docStmt.executeUpdate();
                docStmt.close();

                String insertRapportQuery = "INSERT INTO rapport_licence (num_inventaire, nbr_exemplaires) " +
                                          "VALUES (?, 1)";
                PreparedStatement rapportStmt = conn.prepareStatement(insertRapportQuery);
                rapportStmt.setString(1, numInventaire);
                rapportStmt.executeUpdate();
                rapportStmt.close();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error initializing sample data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String generateUniqueInventoryNumber() {
        String newNum;
        boolean isUnique;
        do {
            int randomNum = (int) (Math.random() * 1000);
            newNum = "INV" + String.format("%03d", randomNum);
            isUnique = true;
            try {
                String checkQuery = "SELECT num_inventaire FROM document WHERE num_inventaire = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, newNum);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    isUnique = false;
                }
                rs.close();
                checkStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                isUnique = false; // Fallback to retry
            }
        } while (!isUnique);
        return newNum;
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
        try {
            String query = "SELECT d.num_inventaire, d.titre, d.auteur, d.encadrant, d.annee, d.image " +
                          "FROM document d JOIN rapport_licence r ON d.num_inventaire = r.num_inventaire " +
                          "WHERE d.type_document = 'Rapport Licence'";
            System.out.println("Executing query: " + query);
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String numInventaire = rs.getString("num_inventaire");
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                String encadrant = rs.getString("encadrant") != null ? rs.getString("encadrant") : "N/A";
                String annee = rs.getInt("annee") != 0 ? String.valueOf(rs.getInt("annee")) : "N/A";
                String image = rs.getString("image");
                System.out.println("Found rapport: " + numInventaire + ", Titre: " + titre + ", Auteur: " + auteur);
                addRapportItem(numInventaire, titre, auteur, encadrant, annee, image);
                count++;
            }
            System.out.println("Total rapports loaded: " + count);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading rapports: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        rapportsContainer.revalidate();
        rapportsContainer.repaint();
    }

    private void performSearch() {
        rapportsContainer.removeAll();
        try {
            String query = "SELECT d.num_inventaire, d.titre, d.auteur, d.encadrant, d.annee, d.image " +
                          "FROM document d JOIN rapport_licence r ON d.num_inventaire = r.num_inventaire " +
                          "WHERE d.type_document = 'Rapport Licence' AND " +
                          "(LOWER(d.titre) LIKE ? OR LOWER(d.auteur) LIKE ? OR LOWER(d.encadrant) LIKE ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            String searchQuery = "%" + searchField.getText().trim().toLowerCase() + "%";
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
                String encadrant = rs.getString("encadrant") != null ? rs.getString("encadrant") : "N/A";
                String annee = rs.getInt("annee") != 0 ? String.valueOf(rs.getInt("annee")) : "N/A";
                String image = rs.getString("image");
                addRapportItem(numInventaire, titre, auteur, encadrant, annee, image);
                count++;
            }
            System.out.println("Total search results loaded: " + count);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching rapports: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        rapportsContainer.revalidate();
        rapportsContainer.repaint();
    }

    private void addNewRapport() {
        EditRapportLicenceFrame frame = new EditRapportLicenceFrame(null);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] newRapport = frame.getResult();
                    String numInventaire = "INV" + String.format("%03d", (int) (Math.random() * 1000));
                    newRapport[0] = numInventaire;
                    try {
                        conn.setAutoCommit(false);
                        String insertDocQuery = "INSERT INTO document (num_inventaire, type_document, titre, auteur, image, encadrant, annee) " +
                                               "VALUES (?, 'Rapport Licence', ?, ?, ?, ?, ?)";
                        PreparedStatement docStmt = conn.prepareStatement(insertDocQuery);
                        docStmt.setString(1, numInventaire);
                        docStmt.setString(2, newRapport[1]); // titre
                        docStmt.setString(3, newRapport[2]); // auteur
                        docStmt.setString(4, "/icons/default.jpg"); // image
                        docStmt.setString(5, newRapport[3]); // encadrant
                        docStmt.setInt(6, Integer.parseInt(newRapport[4])); // annee
                        docStmt.executeUpdate();
                        docStmt.close();

                        String insertRapportQuery = "INSERT INTO rapport_licence (num_inventaire, nbr_exemplaires) " +
                                                   "VALUES (?, 1)";
                        PreparedStatement rapportStmt = conn.prepareStatement(insertRapportQuery);
                        rapportStmt.setString(1, numInventaire);
                        rapportStmt.executeUpdate();
                        rapportStmt.close();

                        conn.commit();
                        performSearch();
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(RapportLicencePanel.this, "Error adding rapport: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void editRapport(String[] rapport, JPanel itemPanel) {
        EditRapportLicenceFrame frame = new EditRapportLicenceFrame(rapport);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] updatedRapport = frame.getResult();
                    String numInventaire = updatedRapport[0];
                    try {
                        conn.setAutoCommit(false);
                        String updateDocQuery = "UPDATE document SET titre = ?, auteur = ?, encadrant = ?, annee = ? " +
                                               "WHERE num_inventaire = ? AND type_document = 'Rapport Licence'";
                        PreparedStatement docStmt = conn.prepareStatement(updateDocQuery);
                        docStmt.setString(1, updatedRapport[1]); // titre
                        docStmt.setString(2, updatedRapport[2]); // auteur
                        docStmt.setString(3, updatedRapport[3]); // encadrant
                        docStmt.setInt(4, Integer.parseInt(updatedRapport[4])); // annee
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
                        JOptionPane.showMessageDialog(RapportLicencePanel.this, "Error updating rapport: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void addRapportItem(String numInventaire, String titre, String auteur, String encadrant, String annee, String imagePath) {
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
        editButton.addActionListener(e -> editRapport(new String[]{numInventaire, titre, auteur, encadrant, annee, imagePath}, itemPanel));
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
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this rapport?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    conn.setAutoCommit(false);
                    String deleteRapportQuery = "DELETE FROM rapport_licence WHERE num_inventaire = ?";
                    PreparedStatement rapportStmt = conn.prepareStatement(deleteRapportQuery);
                    rapportStmt.setString(1, numInventaire);
                    rapportStmt.executeUpdate();
                    rapportStmt.close();

                    String deleteDocQuery = "DELETE FROM document WHERE num_inventaire = ? AND type_document = 'Rapport Licence'";
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
                    JOptionPane.showMessageDialog(RapportLicencePanel.this, "Error deleting rapport: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        rapportsContainer.add(itemPanel);
        rapportsContainer.add(Box.createVerticalStrut(15));
    }
}