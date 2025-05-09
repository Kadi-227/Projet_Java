package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.*;

public class LivrePanel extends RoundedPanel {
    private JPanel livresContainer;
    private JTextField searchField;
    private JButton addButton;
    private Connection conn;

    public LivrePanel() {
        super(new Color(0x8BA1B6), 25); // Blue-gray background with 25px corner radius
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initializeDatabaseConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Cannot proceed without database connection. Please check the logs and restart the application.", "Critical Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        initializeSampleData();
        initializeUI();
        loadLivres();
    }

    private void initializeDatabaseConnection() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/unilib_db?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "alae";
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeSampleData() {
        try {
            conn.setAutoCommit(false);

            String[] sampleLivres = {
                "The Art of Programming,Robert C. Martin,/icons/book1.jpg,2,Programming,PROG001",
                "Calculus Made Easy,Silvanus P. Thompson,/icons/book2.jpg,3,Mathematics,MATH001",
                "Physics for Dummies,Steven Holzner,/icons/book3.jpg,1,Physics,PHYS001"
            };

            for (String livre : sampleLivres) {
                String[] parts = livre.split(",");
                String titre = parts[0];
                String auteur = parts[1];
                String image = parts[2];
                int nbrExemplaires = Integer.parseInt(parts[3]);
                String categorie = parts[4];
                String cote = parts[5];
                String numInventaire = generateUniqueInventoryNumber();

                String checkQuery = "SELECT num_inventaire FROM document WHERE num_inventaire = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, numInventaire);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    numInventaire = generateUniqueInventoryNumber();
                }
                rs.close();
                checkStmt.close();

                String insertDocQuery = "INSERT INTO document (num_inventaire, type_document, titre, auteur, nbr_exemplaires, categorie, cote, image) " +
                                      "VALUES (?, 'Livre', ?, ?, ?, ?, ?, ?)";
                PreparedStatement docStmt = conn.prepareStatement(insertDocQuery);
                docStmt.setString(1, numInventaire);
                docStmt.setString(2, titre);
                docStmt.setString(3, auteur);
                docStmt.setInt(4, nbrExemplaires);
                docStmt.setString(5, categorie);
                docStmt.setString(6, cote);
                docStmt.setString(7, image);
                docStmt.executeUpdate();
                docStmt.close();

                String insertLivreQuery = "INSERT INTO livre (num_inventaire, nbr_exemplaires, categorie, cote) " +
                                        "VALUES (?, ?, ?, ?)";
                PreparedStatement livreStmt = conn.prepareStatement(insertLivreQuery);
                livreStmt.setString(1, numInventaire);
                livreStmt.setInt(2, nbrExemplaires);
                livreStmt.setString(3, categorie);
                livreStmt.setString(4, cote);
                livreStmt.executeUpdate();
                livreStmt.close();
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
                isUnique = false;
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
        try {
            String query = "SELECT d.num_inventaire, d.titre, d.auteur, d.nbr_exemplaires, d.categorie, d.cote, d.image " +
                          "FROM document d JOIN livre l ON d.num_inventaire = l.num_inventaire " +
                          "WHERE d.type_document = 'Livre'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String numInventaire = rs.getString("num_inventaire");
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                String nbrExemplaires = String.valueOf(rs.getInt("nbr_exemplaires"));
                String categorie = rs.getString("categorie");
                String cote = rs.getString("cote");
                String image = rs.getString("image");
                addLivreItem(numInventaire, titre, auteur, nbrExemplaires, categorie, cote, image);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading livres: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        livresContainer.revalidate();
        livresContainer.repaint();
    }

    private void performSearch() {
        String queryText = searchField.getText().trim().toLowerCase();
        if (queryText.equals("chercher")) {
            queryText = "";
        }

        livresContainer.removeAll();
        try {
            String query = "SELECT d.num_inventaire, d.titre, d.auteur, d.nbr_exemplaires, d.categorie, d.cote, d.image " +
                          "FROM document d JOIN livre l ON d.num_inventaire = l.num_inventaire " +
                          "WHERE d.type_document = 'Livre' AND " +
                          "(LOWER(d.num_inventaire) LIKE ? OR LOWER(d.titre) LIKE ? OR LOWER(d.auteur) LIKE ? OR " +
                          "LOWER(d.categorie) LIKE ? OR LOWER(d.cote) LIKE ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            String likeQuery = "%" + queryText + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);
            stmt.setString(3, likeQuery);
            stmt.setString(4, likeQuery);
            stmt.setString(5, likeQuery);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String numInventaire = rs.getString("num_inventaire");
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                String nbrExemplaires = String.valueOf(rs.getInt("nbr_exemplaires"));
                String categorie = rs.getString("categorie");
                String cote = rs.getString("cote");
                String image = rs.getString("image");
                addLivreItem(numInventaire, titre, auteur, nbrExemplaires, categorie, cote, image);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching livres: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
                        newLivre[6] = "/icons/default.jpg";
                    }
                    try {
                        conn.setAutoCommit(false);
                        String insertDocQuery = "INSERT INTO document (num_inventaire, type_document, titre, auteur, nbr_exemplaires, categorie, cote, image) " +
                                               "VALUES (?, 'Livre', ?, ?, ?, ?, ?, ?)";
                        PreparedStatement docStmt = conn.prepareStatement(insertDocQuery);
                        docStmt.setString(1, numInventaire);
                        docStmt.setString(2, newLivre[1]);
                        docStmt.setString(3, newLivre[2]);
                        docStmt.setInt(4, Integer.parseInt(newLivre[3]));
                        docStmt.setString(5, newLivre[4]);
                        docStmt.setString(6, newLivre[5]);
                        docStmt.setString(7, newLivre[6]);
                        docStmt.executeUpdate();
                        docStmt.close();

                        String insertLivreQuery = "INSERT INTO livre (num_inventaire, nbr_exemplaires, categorie, cote) " +
                                                 "VALUES (?, ?, ?, ?)";
                        PreparedStatement livreStmt = conn.prepareStatement(insertLivreQuery);
                        livreStmt.setString(1, numInventaire);
                        livreStmt.setInt(2, Integer.parseInt(newLivre[3]));
                        livreStmt.setString(3, newLivre[4]);
                        livreStmt.setString(4, newLivre[5]);
                        livreStmt.executeUpdate();
                        livreStmt.close();

                        conn.commit();
                        performSearch();
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(LivrePanel.this, "Error adding livre: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void editLivre(String[] livre, JPanel itemPanel) {
        EditLivreFrame frame = new EditLivreFrame(livre);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (frame.isConfirmed()) {
                    String[] updatedLivre = frame.getResult();
                    try {
                        conn.setAutoCommit(false);
                        String updateDocQuery = "UPDATE document SET titre = ?, auteur = ?, nbr_exemplaires = ?, categorie = ?, cote = ?, image = ? " +
                                               "WHERE num_inventaire = ? AND type_document = 'Livre'";
                        PreparedStatement docStmt = conn.prepareStatement(updateDocQuery);
                        docStmt.setString(1, updatedLivre[1]);
                        docStmt.setString(2, updatedLivre[2]);
                        docStmt.setInt(3, Integer.parseInt(updatedLivre[3]));
                        docStmt.setString(4, updatedLivre[4]);
                        docStmt.setString(5, updatedLivre[5]);
                        docStmt.setString(6, updatedLivre[6]);
                        docStmt.setString(7, updatedLivre[0]);
                        docStmt.executeUpdate();
                        docStmt.close();

                        String updateLivreQuery = "UPDATE livre SET nbr_exemplaires = ?, categorie = ?, cote = ? " +
                                                 "WHERE num_inventaire = ?";
                        PreparedStatement livreStmt = conn.prepareStatement(updateLivreQuery);
                        livreStmt.setInt(1, Integer.parseInt(updatedLivre[3]));
                        livreStmt.setString(2, updatedLivre[4]);
                        livreStmt.setString(3, updatedLivre[5]);
                        livreStmt.setString(4, updatedLivre[0]);
                        livreStmt.executeUpdate();
                        livreStmt.close();

                        conn.commit();
                        performSearch();
                    } catch (SQLException e) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(LivrePanel.this, "Error updating livre: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
                try {
                    conn.setAutoCommit(false);
                    String deleteLivreQuery = "DELETE FROM livre WHERE num_inventaire = ?";
                    PreparedStatement livreStmt = conn.prepareStatement(deleteLivreQuery);
                    livreStmt.setString(1, numInventaire);
                    livreStmt.executeUpdate();
                    livreStmt.close();

                    String deleteDocQuery = "DELETE FROM document WHERE num_inventaire = ? AND type_document = 'Livre'";
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
                    JOptionPane.showMessageDialog(LivrePanel.this, "Error deleting livre: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setOpaque(false);
        imageLabel = new JLabel();
        if (livre != null && livre[6] != null) {
            try {
                ImageIcon icon;
                if (livre[6].startsWith("/icons/")) {
                    icon = new ImageIcon(getClass().getResource(livre[6]));
                } else {
                    icon = new ImageIcon(livre[6]);
                }
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                } else {
                    throw new Exception("Image load failed");
                }
            } catch (Exception e) {
                imageLabel.setText("No Image");
            }
            initialImagePath = livre[6];
        } else {
            imageLabel.setText("No Image");
            initialImagePath = "/icons/default.jpg";
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
                try {
                    ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(img));
                        imageLabel.setText(null);
                        initialImagePath = selectedFile.getAbsolutePath();
                    } else {
                        throw new Exception("Image load failed");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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