package alaedesignlapartieréservations;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/unilib_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

public class ReservationsPanel extends RoundedPanel {
    private static final Logger LOGGER = Logger.getLogger(ReservationsPanel.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    private JTable reservationsTable;
    private DefaultTableModel model;
    private JTextField cneField, livreField;
    private JDateChooser dateReserveeField, dateLimiteField;
    private JTextField searchField;
    private JButton validerBtn;
    private boolean isEditing = false;
    private String currentCne = "";
    private String currentDocId = "";
    
    public ReservationsPanel() {
        super(new Color(0x8BA1B6), 25);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeUI();
        loadReservations();
    }

    private void initializeUI() {
        // Top panel with search
        JPanel topPanel = createSearchPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with form and table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(createFormPanel(), BorderLayout.NORTH);
        centerPanel.add(createTableScrollPane(), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with submit button
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        searchField = new JTextField("Entrez le CNE");
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable(searchField.getText());
            }
        });
        
        JButton searchButton = new JButton("🔍");
        searchButton.addActionListener(e -> filterTable(searchField.getText()));
        
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        cneField = new JTextField();
        livreField = new JTextField();
        dateReserveeField = new JDateChooser();
        dateReserveeField.setDateFormatString("dd/MM/yyyy");
        dateLimiteField = new JDateChooser();
        dateLimiteField.setDateFormatString("dd/MM/yyyy");
        
        addFormRow(panel, gbc, 0, "CNE", cneField, "Date réservée", dateReserveeField);
        addFormRow(panel, gbc, 1, "Livre", livreField, "Date limite", dateLimiteField);
        
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                          String label1, JComponent field1,
                          String label2, JComponent field2) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label1), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field1, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel(label2), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panel.add(field2, gbc);
    }

    private JScrollPane createTableScrollPane() {
        String[] columns = {"", "CNE", "Livre", "Date réservée", "Date limite", "Statut", "Alerte", ""};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(model);
        configureTableAppearance();
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return scrollPane;
    }

    private void configureTableAppearance() {
        reservationsTable.setRowHeight(30);
        reservationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reservationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Custom renderers
        reservationsTable.getColumn("Statut").setCellRenderer(new StatusCellRenderer());
        reservationsTable.getColumn("Alerte").setCellRenderer(new IconButtonRenderer("/icons/alert.png", "Envoyer alerte"));
        reservationsTable.getColumnModel().getColumn(7).setCellRenderer(new IconButtonRenderer("/icons/no.png", "Supprimer"));
        reservationsTable.getColumnModel().getColumn(0).setCellRenderer(new IconButtonRenderer("/icons/edit.png", "Modifier"));
        
        // Add mouse listener for actions
        reservationsTable.addMouseListener(new TableActionListener());
    }

    private JPanel createButtonPanel() {
        validerBtn = new JButton("Valider");
        validerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        validerBtn.setBackground(new Color(0x007BFF));
        validerBtn.setForeground(Color.WHITE);
        validerBtn.setFocusPainted(false);
        validerBtn.addActionListener(e -> handleReservationAction());
        
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(validerBtn);
        return panel;
    }

    private void handleReservationAction() {
        if (!validateInputs()) {
            return;
        }
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            
            String cne = cneField.getText().trim();
            String numInventaire = livreField.getText().trim();
            Date dateReservation = dateReserveeField.getDate();
            Date dateLimite = dateLimiteField.getDate();
            
            if (!verifyStudentExists(conn, cne)) {
                showError("Étudiant non trouvé !");
                return;
            }
            
            DocumentInfo docInfo = findDocument(conn, numInventaire);
            if (!docInfo.exists()) {
                showError("Document non trouvé dans la base de données !");
                return;
            }
            
            if (isEditing) {
                updateReservation(conn, cne, numInventaire, dateReservation, dateLimite, docInfo);
            } else {
                
                // Vérifier si l'étudiant a déjà une réservation
                PreparedStatement checkExisting = conn.prepareStatement(
                "SELECT 1 FROM reservation WHERE cne_etudiant = ?");
                checkExisting.setString(1, cne);
                ResultSet rs = checkExisting.executeQuery();
                if (rs.next()) {
                    showError("Cet étudiant a déjà une réservation.");
                    conn.rollback();
                    return;
                }
                
                createReservation(conn, cne, numInventaire, dateReservation, dateLimite, docInfo);
            }
            
            conn.commit();
            updateUIAfterAction(cne, numInventaire, dateReservation, dateLimite);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during reservation", e);
            showError("Erreur lors de la réservation : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (cneField.getText().trim().isEmpty() || 
            livreField.getText().trim().isEmpty() || 
            dateReserveeField.getDate() == null || 
            dateLimiteField.getDate() == null) {
            
            showError("Tous les champs sont obligatoires !");
            return false;
        }
        
        Date dateRes = dateReserveeField.getDate();
        Date dateLim = dateLimiteField.getDate();

        if (!dateLim.after(dateRes)) {
        showError("La date limite doit être strictement après la date de réservation !");
        return false;
        }
        
        return true;
    }

    private void updateUIAfterAction(String cne, String docId, Date resDate, Date limDate) {
        if (isEditing) {
            updateTableRow(cne, docId, resDate, limDate);
        } else {
            addNewTableRow(cne, docId, resDate, limDate);
        }
        resetForm();
    }

    private void updateReservation(Connection conn, String cne, String numInventaire, 
                                 Date dateReservation, Date dateLimite, DocumentInfo docInfo) throws SQLException {
        // Update the reservation
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE reservation SET cne_etudiant=?, num_inventaire_document=?, " +
                "date_reservation=?, date_limite_reservation=? " +
                "WHERE cne_etudiant=? AND num_inventaire_document=?")) {
            
            stmt.setString(1, cne);
            stmt.setString(2, numInventaire);
            stmt.setDate(3, new java.sql.Date(dateReservation.getTime()));
            stmt.setDate(4, new java.sql.Date(dateLimite.getTime()));
            stmt.setString(5, currentCne);
            stmt.setString(6, currentDocId);
            stmt.executeUpdate();
        }
        
        // If document changed, update stocks
        if (!currentDocId.equals(numInventaire)) {
            updateDocumentStocks(conn, numInventaire, docInfo.getTableName(), -1);
            
            // Restore stock for previous document
            DocumentInfo oldDocInfo = findDocument(conn, currentDocId);
            if (oldDocInfo.exists()) {
                updateDocumentStocks(conn, currentDocId, oldDocInfo.getTableName(), 1);
            }
        }
    }

    private void createReservation(Connection conn, String cne, String numInventaire, 
                                 Date dateReservation, Date dateLimite, DocumentInfo docInfo) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO reservation (cne_etudiant, num_inventaire_document, " +
                "date_reservation, date_limite_reservation, statut_reservation) " +
                "VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, cne);
            stmt.setString(2, numInventaire);
            stmt.setDate(3, new java.sql.Date(dateReservation.getTime()));
            stmt.setDate(4, new java.sql.Date(dateLimite.getTime()));
            stmt.setString(5, "en cours");
            stmt.executeUpdate();
        }
        
        updateDocumentStocks(conn, numInventaire, docInfo.getTableName(), -1);
    }

    private DocumentInfo findDocument(Connection conn, String numInventaire) throws SQLException {
    String[] tables = {"livre", "memoire_master", "rapport_licence", "these_doctorat"};
    
    // Check in all document tables directly (remove the document table check)
    for (String table : tables) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT nbr_exemplaires FROM " + table + " WHERE num_inventaire = ?")) {
            
            stmt.setString(1, numInventaire);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new DocumentInfo(table, rs.getInt("nbr_exemplaires"));
            }
        }
    }
    
    return new DocumentInfo(); // Document not found
}

    private boolean documentExists(Connection conn, String numInventaire) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM document WHERE num_inventaire = ?")) {
            stmt.setString(1, numInventaire);
            return stmt.executeQuery().next();
        }
    }

    private boolean verifyStudentExists(Connection conn, String cne) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM etudiant WHERE cne = ?")) {
            stmt.setString(1, cne);
            return stmt.executeQuery().next();
        }
    }

    private void updateDocumentStocks(Connection conn, String numInventaire, String table, int change) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE " + table + " SET nbr_exemplaires = nbr_exemplaires + ? " +
                "WHERE num_inventaire = ?")) {
            
            stmt.setInt(1, change);
            stmt.setString(2, numInventaire);
            
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Failed to update document stock");
            }
        }
    }

    private void updateTableRow(String cne, String docId, Date resDate, Date limDate) {
        for (int row = 0; row < model.getRowCount(); row++) {
            if (model.getValueAt(row, 1).equals(currentCne) && 
                model.getValueAt(row, 2).equals(currentDocId)) {
                
                model.setValueAt(cne, row, 1);
                model.setValueAt(docId, row, 2);
                model.setValueAt(DATE_FORMAT.format(resDate), row, 3);
                model.setValueAt(DATE_FORMAT.format(limDate), row, 4);
                break;
            }
        }
    }

    private void addNewTableRow(String cne, String docId, Date resDate, Date limDate) {
        model.addRow(new Object[]{
            "", // Edit
            cne, 
            docId, 
            DATE_FORMAT.format(resDate), 
            DATE_FORMAT.format(limDate), 
            "en cours", 
            "", // Alert
            ""  // Delete
        });
    }

    private void resetForm() {
        cneField.setText("");
        livreField.setText("");
        dateReserveeField.setDate(null);
        dateLimiteField.setDate(null);
        isEditing = false;
        currentCne = "";
        currentDocId = "";
        validerBtn.setText("Valider");
    }

    private void loadReservations() {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reservation");
             ResultSet rs = stmt.executeQuery()) {
            
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    "", // Edit
                    rs.getString("cne_etudiant"),
                    rs.getString("num_inventaire_document"),
                    rs.getString("date_reservation"),
                    rs.getString("date_limite_reservation"),
                    rs.getString("statut_reservation"),
                    "", // Alert
                    ""  // Delete
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading reservations", e);
            showError("Erreur lors du chargement : " + e.getMessage());
        }
    }

    private void filterTable(String cne) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        reservationsTable.setRowSorter(sorter);
        
        if (cne.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + cne, 1));
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // Inner classes
    private static class DocumentInfo {
        private final String tableName;
        private final int availableCopies;
        
        public DocumentInfo() {
            this("", 0);
        }
        
        public DocumentInfo(String tableName, int availableCopies) {
            this.tableName = tableName;
            this.availableCopies = availableCopies;
        }
        
        public boolean exists() {
            return !tableName.isEmpty();
        }
        
        public String getTableName() {
            return tableName;
        }
        
        public int getAvailableCopies() {
            return availableCopies;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
            panel.setOpaque(true);
            
            try {
                Date dateLimite = DATE_FORMAT.parse((String) table.getValueAt(row, 4));
                Date aujourdHui = new Date();
                
                if (dateLimite.after(aujourdHui)) {
                    panel.setBackground(new Color(144, 238, 144)); // Light green
                } else if (DATE_FORMAT.format(dateLimite).equals(DATE_FORMAT.format(aujourdHui))) {
                    panel.setBackground(new Color(255, 215, 0)); // Gold
                } else {
                    panel.setBackground(new Color(255, 182, 193)); // Light pink
                }
            } catch (Exception e) {
                panel.setBackground(Color.LIGHT_GRAY);
            }
            
            return panel;
        }
    }

    private class IconButtonRenderer extends DefaultTableCellRenderer {
    private final ImageIcon icon;
    private final String tooltip;
    
    public IconButtonRenderer(String iconPath, String tooltip) {
        ImageIcon tempIcon = null;
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
            if (originalIcon != null) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                tempIcon = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load icon: " + iconPath, e);
        }
        this.icon = tempIcon;
        this.tooltip = tooltip;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(16, 16));
        
        if (icon == null) {
            button.setText("?");
        }
        
        return button;
    }
}

    private class TableActionListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = reservationsTable.rowAtPoint(e.getPoint());
            int col = reservationsTable.columnAtPoint(e.getPoint());
            
            if (row < 0 || row >= model.getRowCount()) return;
            
            String cne = (String) model.getValueAt(row, 1);
            String livre = (String) model.getValueAt(row, 2);
            
            try {
                if (col == 0) { // Edit
                    handleEditAction(row, cne, livre);
                } else if (col == 6) { // Alert
                    handleAlertAction(cne);
                } else if (col == 7) { // Delete
                    handleDeleteAction(row, cne, livre);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error handling table action", ex);
                showError("Erreur lors de l'opération: " + ex.getMessage());
            }
        }

        private void handleEditAction(int row, String cne, String livre) throws Exception {
            currentCne = cne;
            currentDocId = livre;
            
            cneField.setText(cne);
            livreField.setText(livre);
            dateReserveeField.setDate(DATE_FORMAT.parse((String) model.getValueAt(row, 3)));
            dateLimiteField.setDate(DATE_FORMAT.parse((String) model.getValueAt(row, 4)));
            
            isEditing = true;
            validerBtn.setText("Mettre à jour");
        }

        private void handleAlertAction(String cne) {
            int choice = JOptionPane.showConfirmDialog(
                ReservationsPanel.this,
                "Envoyer un rappel à l'étudiant " + cne + "?",
                "Confirmation d'alerte",
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                // TODO: Implement actual alert/notification system
                showMessage("Rappel envoyé à l'étudiant " + cne);
            }
        }

        private void handleDeleteAction(int row, String cne, String livre) {
            int confirm = JOptionPane.showConfirmDialog(
                ReservationsPanel.this,
                "Voulez-vous vraiment supprimer cette réservation ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseHelper.getConnection()) {
                    conn.setAutoCommit(false);
                    
                    // 1. Find which table this document belongs to
                    DocumentInfo docInfo = findDocument(conn, livre);
                    if (!docInfo.exists()) {
                        throw new SQLException("Document not found in database");
                    }
                    
                    // 2. Delete the reservation
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "DELETE FROM reservation WHERE cne_etudiant = ? AND num_inventaire_document = ?")) {
                        stmt.setString(1, cne);
                        stmt.setString(2, livre);
                        stmt.executeUpdate();
                    }
                    
                    // 3. Update stock
                    updateDocumentStocks(conn, livre, docInfo.getTableName(), 1);
                    
                    conn.commit();
                    model.removeRow(row);
                    showMessage("Réservation supprimée avec succès.");
                    
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error deleting reservation", ex);
                    showError("Erreur lors de la suppression: " + ex.getMessage());
                }
            }
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}