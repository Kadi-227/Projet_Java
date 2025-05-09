package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;

public class reserve_demande extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContent;

    public reserve_demande() {
        setTitle("UNILIB - Réservations & Demandes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // ➤ Plein écran
        setLocationRelativeTo(null);
        setResizable(true);

        // 🌄 Fond personnalisé
        BackgroundPanel background = new BackgroundPanel("/icons/background.png");
        setContentPane(background);
        background.setLayout(new BorderLayout());

        // ➤ Barre latérale
        SidebarPanel sidebar = new SidebarPanel(this);
        background.add(sidebar, BorderLayout.WEST);

        // ➤ Panel central
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // ➤ En-tête
        HeaderPanel header = new HeaderPanel();
        centerPanel.add(header, BorderLayout.NORTH);

        // ➤ Zone centrale avec CardLayout
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setOpaque(false);

        // ➕ Reservations
        JPanel reservationsWrapper = new JPanel(new GridBagLayout());
        reservationsWrapper.setOpaque(false);
        RoundedPanel reservationsPanel = new RoundedPanel(new Color(0x8BA1B6), 30);
        reservationsPanel.setLayout(new BorderLayout());
        reservationsPanel.setPreferredSize(new Dimension(850, 500));
        reservationsPanel.add(new ReservationsPanel(), BorderLayout.CENTER);
        reservationsWrapper.add(reservationsPanel);

        JScrollPane scrollReservations = new JScrollPane(reservationsWrapper);
        scrollReservations.setBorder(null);
        scrollReservations.setOpaque(false);
        scrollReservations.getViewport().setOpaque(false);
        mainContent.add(scrollReservations, "reservations");

        // ➕ Demandes
        JPanel demandesWrapper = new JPanel(new GridBagLayout());
        demandesWrapper.setOpaque(false);
        RoundedPanel demandesPanel = new RoundedPanel(new Color(0x8BA1B6), 30);
        demandesPanel.setLayout(new BorderLayout());
        demandesPanel.setPreferredSize(new Dimension(850, 500));
        demandesPanel.add(new DemandesPanel(), BorderLayout.CENTER);
        demandesWrapper.add(demandesPanel);

        JScrollPane scrollDemandes = new JScrollPane(demandesWrapper);
        scrollDemandes.setBorder(null);
        scrollDemandes.setOpaque(false);
        scrollDemandes.getViewport().setOpaque(false);
        mainContent.add(scrollDemandes, "demandes");

        // ➤ Ajout du mainContent
        centerPanel.add(mainContent, BorderLayout.CENTER);
        background.add(centerPanel, BorderLayout.CENTER);

        // ➤ Panel par défaut
        showPanel("reservations");
    }

    // ➤ Méthode publique pour la navigation
    public void showPanel(String name) {
        cardLayout.show(mainContent, name);
        mainContent.revalidate();
        mainContent.repaint();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new reserve_demande().setVisible(true);
        });
    }
}