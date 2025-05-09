package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SidebarPanel extends JPanel {
    private boolean isCollapsed = false;
    private JPanel buttonsPanel;
    private JButton toggleButton;
    private reserve_demande reserve_demande;
    private JButton selectedButton = null;
    private JPanel logoTogglePanel;
    private JLabel logoLabel;
    private JPanel logoutPanel;
    private JPanel documentsSubMenu;
    private JLabel arrowIconLabel;
    private boolean isSubMenuVisible = false;
    private JLabel badgeLabel;

    public SidebarPanel(reserve_demande reserve_demande) {
        this.reserve_demande = reserve_demande;
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(180, getHeight()));

        logoTogglePanel = new JPanel(new BorderLayout());
        logoTogglePanel.setBackground(Color.WHITE);
        logoTogglePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logoLabel = new JLabel(resizeIcon("/icons/logo_small.png", 70, 70));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        logoTogglePanel.add(logoLabel, BorderLayout.WEST);

        toggleButton = new JButton("❮");
        toggleButton.setFocusPainted(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setBackground(Color.WHITE);
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setPreferredSize(new Dimension(40, 40));
        toggleButton.addActionListener(e -> toggleSidebar());
        logoTogglePanel.add(toggleButton, BorderLayout.EAST);

        add(logoTogglePanel, BorderLayout.NORTH);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.WHITE);

        Runnable addVerticalSpacing = () -> buttonsPanel.add(Box.createVerticalStrut(10));

        addVerticalSpacing.run();
        JPanel documentButtonPanel = new JPanel(new BorderLayout());
        documentButtonPanel.setMaximumSize(new Dimension(160, 40));
        documentButtonPanel.setOpaque(false);
        JButton documentsButton = new JButton("Documents    ", resizeIcon("/icons/document.png", 24, 24));
        documentsButton.setHorizontalAlignment(SwingConstants.LEFT);
        documentsButton.setFocusPainted(false);
        documentsButton.setBorderPainted(false);
        documentsButton.setBackground(Color.WHITE);
        documentsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        arrowIconLabel = new JLabel(resizeIcon("/icons/arrow-down.png", 14, 14));
        arrowIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        arrowIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        arrowIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleDocumentsSubMenu();
            }
        });
        documentButtonPanel.add(documentsButton, BorderLayout.CENTER);
        documentButtonPanel.add(arrowIconLabel, BorderLayout.EAST);
        buttonsPanel.add(createAlignedButtonPanel(documentButtonPanel));

        documentsSubMenu = new JPanel();
        documentsSubMenu.setLayout(new BoxLayout(documentsSubMenu, BoxLayout.Y_AXIS));
        documentsSubMenu.setBackground(Color.white);
        documentsSubMenu.setVisible(false);
        documentsSubMenu.add(createSubMenuButton("Livre"));
        documentsSubMenu.add(createSubMenuButton("Rapport Licence"));
        documentsSubMenu.add(createSubMenuButton("Mémoire de master"));
        documentsSubMenu.add(createSubMenuButton("Thèse de doctorat"));
        buttonsPanel.add(documentsSubMenu);

        addVerticalSpacing.run();
        buttonsPanel.add(createAlignedButtonPanel(createNavButton("Utilisateurs    ", resizeIcon("/icons/user.png", 24, 24))));

        addVerticalSpacing.run();
        buttonsPanel.add(createAlignedButtonPanel(createNavButton("Réservations    ", resizeIcon("/icons/calendar.png", 24, 24), () -> reserve_demande.showPanel("reservations"))));

        addVerticalSpacing.run();
        buttonsPanel.add(createAlignedButtonPanel(createDemandeButtonWithBadge()));

        add(buttonsPanel, BorderLayout.CENTER);

        logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setBackground(Color.WHITE);
        JButton logoutButton = new JButton("Déconnexion", resizeIcon("/icons/logout.png", 24, 24));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
        Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
            JFrame welcomeFrame = new JFrame("Welcome");
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // plein écran
            welcomeFrame.setContentPane(new WelcomeScreen());
            welcomeFrame.pack();
            welcomeFrame.setVisible(true);
        });    
           
        logoutPanel.add(logoutButton);
        add(logoutPanel, BorderLayout.SOUTH);

        Timer timer = new Timer(5000, e -> updateBadge());
        timer.start();
        updateBadge();
    }

    private JPanel createAlignedButtonPanel(Component button) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(180, 40));
        wrapper.add(button);
        return wrapper;
    }

    private JPanel createDemandeButtonWithBadge() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(160, 40));
        wrapper.setMaximumSize(new Dimension(160, 40));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(160, 40));

        JButton demandesButton = new JButton("Demandes", resizeIcon("/icons/envelope.png", 24, 24));
        demandesButton.setHorizontalAlignment(SwingConstants.LEFT);
        demandesButton.setFocusPainted(false);
        demandesButton.setBorderPainted(false);
        demandesButton.setBackground(Color.WHITE);
        demandesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        demandesButton.setBounds(0, 0, 160, 40);
        demandesButton.addActionListener(e -> {
            if (selectedButton != null) selectedButton.setBackground(Color.WHITE);
            demandesButton.setBackground(new Color(0xDCEBFA));
            selectedButton = demandesButton;
            reserve_demande.showPanel("demandes");
            resetBadgeCount();
        });

        badgeLabel = new JLabel("0", SwingConstants.CENTER);
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(Color.RED);
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setFont(new Font("Arial", Font.BOLD, 10));
        badgeLabel.setBounds(130, 6, 16, 16);
        badgeLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        badgeLabel.setVisible(false);

        layeredPane.add(demandesButton, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(badgeLabel, JLayeredPane.PALETTE_LAYER);

        wrapper.add(layeredPane, BorderLayout.CENTER);
        return wrapper;
    }

    private void resetBadgeCount() {
        badgeLabel.setText("0");
        badgeLabel.setVisible(false);
    }

    private JButton createNavButton(String label, ImageIcon icon) {
        return createNavButton(label, icon, null);
    }

    private JButton createNavButton(String label, ImageIcon icon, Runnable action) {
        JButton button = new JButton(label, icon);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setMaximumSize(new Dimension(160, 40));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        Color defaultBg = Color.WHITE;
        Color selectedBg = new Color(0xDCEBFA);
        if (action != null) {
            button.addActionListener(e -> {
                if (selectedButton != null) selectedButton.setBackground(defaultBg);
                button.setBackground(selectedBg);
                selectedButton = button;
                action.run();
            });
        }
        return button;
    }

    private JButton createSubMenuButton(String label) {
        JButton button = new JButton(label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(Color.white);
        button.setMaximumSize(new Dimension(130, 45));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        return button;
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        java.net.URL imageURL = getClass().getResource(path);
        if (imageURL == null) {
            System.err.println("Image not found: " + path);
            return null;
        }
        ImageIcon icon = new ImageIcon(imageURL);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void toggleSidebar() {
        isCollapsed = !isCollapsed;
        setPreferredSize(new Dimension(isCollapsed ? 60 : 180, getHeight()));
        for (Component comp : buttonsPanel.getComponents()) {
            comp.setVisible(!isCollapsed);
        }
        logoLabel.setVisible(!isCollapsed);
        logoutPanel.setVisible(!isCollapsed);
        toggleButton.setText(isCollapsed ? "❯" : "❮");
        revalidate();
        repaint();
    }

    private void toggleDocumentsSubMenu() {
        isSubMenuVisible = !isSubMenuVisible;
        documentsSubMenu.setVisible(isSubMenuVisible);
        arrowIconLabel.setIcon(resizeIcon(
                isSubMenuVisible ? "/icons/arrow-up.png" : "/icons/arrow-down.png", 14, 14));
        revalidate();
        repaint();
    }

    public void updateBadge() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib_db", "root", "")) {
            String sql = "SELECT COUNT(*) FROM demande d " +
                         "JOIN message m ON d.id_demande = m.id_demande " +
                         "WHERE d.type_demande = 'Inscription' AND m.reponse IS NULL";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                badgeLabel.setText(String.valueOf(count));
                badgeLabel.setVisible(count > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
