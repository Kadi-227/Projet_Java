package javaapplication3;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import javax.swing.Timer;

public class DemandesPanel extends RoundedPanel {
    private JPanel demandesContainer;

    public DemandesPanel() {
        super(new Color(0x8BA1B6), 25);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        RoundedLabel titleLabel = new RoundedLabel("Gestion des Demandes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        demandesContainer = new JPanel();
        demandesContainer.setLayout(new BoxLayout(demandesContainer, BoxLayout.Y_AXIS));
        demandesContainer.setOpaque(false);

        loadDemandes();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(demandesContainer), BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        Timer timer = new Timer(5000, e -> loadDemandes());
        timer.start();
    }

    private void loadDemandes() {
        demandesContainer.removeAll();
        List<Demande> demandes = fetchDemandes();
        for (Demande demande : demandes) {
            JPanel demandeBox = createDemandeBox(demande, demandesContainer);
            demandesContainer.add(demandeBox);
            demandesContainer.add(Box.createVerticalStrut(15));
        }
        demandesContainer.revalidate();
        demandesContainer.repaint();
    }

    private JPanel createDemandeBox(Demande demande, JPanel container) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JPanel iconCnePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        iconCnePanel.setOpaque(false);
        ImageIcon envelopeIcon = new ImageIcon(getClass().getResource("/icons/fillenvelope.png"));
        Image scaledImage = envelopeIcon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        RoundedLabel iconLabel = new RoundedLabel(new ImageIcon(scaledImage));
        RoundedLabel cneLabel = new RoundedLabel(demande.getCne());
        cneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        iconCnePanel.add(iconLabel);
        iconCnePanel.add(cneLabel);

        RoundedButton closeButton = new RoundedButton("X");
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.GRAY);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.addActionListener(e -> {
            container.remove(box);
            container.revalidate();
            container.repaint();
        });

        topPanel.add(iconCnePanel, BorderLayout.WEST);
        topPanel.add(closeButton, BorderLayout.EAST);

        JTextArea messageArea = new JTextArea(demande.getMessage());
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setBackground(Color.WHITE);
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        RoundedButton actionButton = new RoundedButton("Répondre");
        actionButton.setFocusPainted(false);
        actionButton.setBackground(new Color(0x2C3E50));
        actionButton.setForeground(Color.WHITE);

        actionButton.addActionListener(e -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    demande.getMessage(),
                    "Validation d'inscription - CNE: " + demande.getCne(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Accepter", "Refuser"},
                    "Accepter"
            );

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib", "root", "")) {
                conn.setAutoCommit(false);
                PreparedStatement findDemande = conn.prepareStatement("SELECT d.id_demande FROM demande d JOIN message m ON d.id_demande = m.id_demande WHERE m.cne_etudiant = ?");
                findDemande.setString(1, demande.getCne());
                ResultSet rs = findDemande.executeQuery();
                if (rs.next()) {
                    int idDemande = rs.getInt("id_demande");

                    if (choice == JOptionPane.YES_OPTION) {
                        PreparedStatement updateDemande = conn.prepareStatement("UPDATE demande SET type_demande = 'Validée' WHERE id_demande = ?");
                        updateDemande.setInt(1, idDemande);
                        updateDemande.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Inscription acceptée pour " + demande.getCne());
                    } else {
                        PreparedStatement deleteMessages = conn.prepareStatement("DELETE FROM message WHERE id_demande = ?");
                        deleteMessages.setInt(1, idDemande);
                        deleteMessages.executeUpdate();
                        PreparedStatement deleteDemande = conn.prepareStatement("DELETE FROM demande WHERE id_demande = ?");
                        deleteDemande.setInt(1, idDemande);
                        deleteDemande.executeUpdate();
                        PreparedStatement findCin = conn.prepareStatement("SELECT cin_utilisateur FROM etudiant WHERE cne = ?");
                        findCin.setString(1, demande.getCne());
                        ResultSet cinRs = findCin.executeQuery();
                        String cin = null;
                        if (cinRs.next()) {
                            cin = cinRs.getString("cin_utilisateur");
                        }
                        PreparedStatement deleteEtudiant = conn.prepareStatement("DELETE FROM etudiant WHERE cne = ?");
                        deleteEtudiant.setString(1, demande.getCne());
                        deleteEtudiant.executeUpdate();
                        if (cin != null) {
                            PreparedStatement deleteUtilisateur = conn.prepareStatement("DELETE FROM utilisateur WHERE cin = ?");
                            deleteUtilisateur.setString(1, cin);
                            deleteUtilisateur.executeUpdate();
                        }
                        JOptionPane.showMessageDialog(this, "Inscription refusée et supprimée pour " + demande.getCne());
                        container.remove(box);
                        container.revalidate();
                        container.repaint();
                    }

                    // 🔁 Enregistrer la réponse dans le message
                    PreparedStatement getMessageId = conn.prepareStatement(
                        "SELECT id_message FROM message WHERE id_demande = ? ORDER BY id_message DESC LIMIT 1"
                    );
                    getMessageId.setInt(1, idDemande);
                    ResultSet msgRs = getMessageId.executeQuery();
                    
                    if (msgRs.next()) {
                        int idMessage = msgRs.getInt("id_message");

                        PreparedStatement updateReponse = conn.prepareStatement(
                            "UPDATE message SET reponse = ? WHERE id_message = ?"
                        );
                        updateReponse.setString(1, (choice == JOptionPane.YES_OPTION) ? "OK" : "Refuser");
                        updateReponse.setInt(2, idMessage);
                        updateReponse.executeUpdate();
                    } else {
                        JOptionPane.showMessageDialog(this, "Aucun message trouvé pour cette demande.");
                    }                    

                    conn.commit();
                } else {
                    JOptionPane.showMessageDialog(this, "Demande non trouvée.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors du traitement de la demande.");
            }
        });

        bottomPanel.add(actionButton);
        box.add(topPanel, BorderLayout.NORTH);
        box.add(messageArea, BorderLayout.CENTER);
        box.add(bottomPanel, BorderLayout.SOUTH);
        return box;
    }

    private static class Demande {
        private final String cne;
        private final String message;

        public Demande(String cne, String message) {
            this.cne = cne;
            this.message = message;
        }

        public String getCne() { return cne; }

        public String getMessage() { return message; }
    }

    private List<Demande> fetchDemandes() {
        List<Demande> demandes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unilib", "root", "")) {
            String sql = "SELECT m.cne_etudiant, m.contenu FROM message m JOIN demande d ON m.id_demande = d.id_demande WHERE d.type_demande = 'Inscription'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String cne = rs.getString("cne_etudiant");
                String message = rs.getString("contenu");
                demandes.add(new Demande(cne, message));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return demandes;
    }
}