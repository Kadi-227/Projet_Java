package javaapplication3;

import javax.swing.*;
import java.awt.*;

public final class reserve_demande extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainContent;

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
        
        // ➕ Livres
        JPanel livresWrapper = new JPanel(new GridBagLayout());
        livresWrapper.setOpaque(false);
        RoundedPanel livresPanel = new RoundedPanel(new Color(0x8BA1B6), 30);
        livresPanel.setLayout(new BorderLayout());
        livresPanel.setPreferredSize(new Dimension(850, 500));
        livresPanel.add(new LivrePanel(), BorderLayout.CENTER);
        livresWrapper.add(livresPanel);

        JScrollPane scrollLivres = new JScrollPane(livresWrapper);
        scrollLivres.setBorder(null);
        scrollLivres.setOpaque(false);
        scrollLivres.getViewport().setOpaque(false);
        mainContent.add(scrollLivres, "livre");
        
        // ➕ pfe
        JPanel pfeWrapper = new JPanel(new GridBagLayout());
        pfeWrapper.setOpaque(false);
        RoundedPanel pfePanel = new RoundedPanel(new Color(0x8BA1B6), 30);
        pfePanel.setLayout(new BorderLayout());
        pfePanel.setPreferredSize(new Dimension(850, 500));
        pfePanel.add(new RapportLicencePanel(), BorderLayout.CENTER);
        pfeWrapper.add(pfePanel);

        JScrollPane scrollpfe = new JScrollPane(pfeWrapper);
        scrollpfe.setBorder(null);
        scrollpfe.setOpaque(false);
        scrollpfe.getViewport().setOpaque(false);
        mainContent.add(scrollpfe, "rapport_licence");
        
        // ➕ memoire
        JPanel memoireWrapper = new JPanel(new GridBagLayout());
        memoireWrapper.setOpaque(false);
        RoundedPanel memoirePanel = new RoundedPanel(new Color(0x8BA1B6), 30);
        memoirePanel.setLayout(new BorderLayout());
        memoirePanel.setPreferredSize(new Dimension(850, 500));
        memoirePanel.add(new MemoireMasterPanel(), BorderLayout.CENTER);
        memoireWrapper.add(memoirePanel);

        JScrollPane scrollmemoire = new JScrollPane(memoireWrapper);
        scrollmemoire.setBorder(null);
        scrollmemoire.setOpaque(false);
        scrollmemoire.getViewport().setOpaque(false);
        mainContent.add(scrollmemoire, "memoire_master");
        
        // ➕ these
        JPanel theseWrapper = new JPanel(new GridBagLayout());
        theseWrapper.setOpaque(false);
        RoundedPanel thesePanel = new RoundedPanel(new Color(0x8BA1B6), 30);
        thesePanel.setLayout(new BorderLayout());
        thesePanel.setPreferredSize(new Dimension(850, 500));
        thesePanel.add(new TheseDoctoratPanel(), BorderLayout.CENTER);
        theseWrapper.add(thesePanel);

        JScrollPane scrollthese = new JScrollPane(theseWrapper);
        scrollthese.setBorder(null);
        scrollthese.setOpaque(false);
        scrollthese.getViewport().setOpaque(false);
        mainContent.add(scrollthese, "these_doctorat");
        
        // ➕ user
        JPanel userWrapper = new JPanel(new GridBagLayout());
        userWrapper.setOpaque(false);
        RoundedPanel userPanel = new RoundedPanel(new Color(0x8BA1B6), 30);
        userPanel.setLayout(new BorderLayout());
        userPanel.setPreferredSize(new Dimension(850, 500));
        userPanel.add(new UtilisateursPanel(), BorderLayout.CENTER);
        userWrapper.add(userPanel);

        JScrollPane scrolluser = new JScrollPane(userWrapper);
        scrolluser.setBorder(null);
        scrolluser.setOpaque(false);
        scrolluser.getViewport().setOpaque(false);
        mainContent.add(scrolluser, "utilisateurs");

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