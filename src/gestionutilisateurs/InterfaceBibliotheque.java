package gestionutilisateurs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface principale de la bibliothèque
 */
public class InterfaceBibliotheque extends JFrame {
    // Le gestionnaire d'utilisateurs
    private GestionnaireUtilisateurs gestionnaire;
    
    // Composants de l'interface
    private JLabel lblTitre;
    private JPanel panneauRecherche;
    private JLabel lblCNE;
    private JLabel lblCNEValeur;
    private JTextField txtRecherche;
    private JLabel lblUtilisateur;
    private JButton btnListeUtilisateurs; // Remplace la barre de défilement
    
    // Champs du formulaire
    private JComboBox<String> cmbNom;
    private JComboBox<String> cmbPrenom;
    private JComboBox<String> cmbCNE;
    private JComboBox<String> cmbCNI;
    private JComboBox<String> cmbEmail;
    private JComboBox<String> cmbFiliere;
    private JComboBox<String> cmbNiveauEtudes;
    
    // Boutons
    private JButton btnModifier;
    private JButton btnSupprimer;
    
    /**
     * Constructeur
     */
    public InterfaceBibliotheque() {
        // Initialiser la base de données
        initialiserBaseDeDonnees();
        
        // Initialiser le gestionnaire
        gestionnaire = new GestionnaireUtilisateurs();
        
        // Ajouter un utilisateur par défaut
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM etudiant LIMIT 1");
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    String cne = rs.getString("cne");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String cni = rs.getString("cin_utilisateur");
                    String email = rs.getString("email") != null ? rs.getString("email") : "";
                    
                    // Rechercher la filière
                    String filiere = "Non spécifiée";
                    try {
                        int idFiliere = rs.getInt("id_filiere");
                        if (idFiliere > 0) {
                            PreparedStatement stmtFiliere = conn.prepareStatement(
                                "SELECT nom_filiere FROM filiere WHERE id_filiere = ?");
                            stmtFiliere.setInt(1, idFiliere);
                            ResultSet rsFiliere = stmtFiliere.executeQuery();
                            if (rsFiliere.next()) {
                                filiere = rsFiliere.getString("nom_filiere");
                            }
                            rsFiliere.close();
                            stmtFiliere.close();
                        }
                    } catch (SQLException ex) {
                        // La colonne id_filiere n'existe peut-être pas
                        System.out.println("Attention: id_filiere introuvable, utilisation de la valeur par défaut");
                    }
                    
                    // Récupérer le niveau d'études
                    String niveauEtudes = "Licence";
                    try {
                        // Essayer de récupérer depuis id_niveau
                        int idNiveau = rs.getInt("id_niveau");
                        if (idNiveau > 0) {
                            PreparedStatement stmtNiveau = conn.prepareStatement(
                                "SELECT nom_niveau FROM niveau_etudes WHERE id_niveau = ?");
                            stmtNiveau.setInt(1, idNiveau);
                            ResultSet rsNiveau = stmtNiveau.executeQuery();
                            if (rsNiveau.next()) {
                                niveauEtudes = rsNiveau.getString("nom_niveau");
                            }
                            rsNiveau.close();
                            stmtNiveau.close();
                        }
                    } catch (SQLException ex) {
                        // Essayer de récupérer directement depuis niveau_etudes
                        if (rs.getString("niveau_etudes") != null) {
                            niveauEtudes = rs.getString("niveau_etudes");
                        }
                    }
                    
                    // Créer un utilisateur à partir des données de la base
                    Utilisateur defaultUser = new Utilisateur(cne, nom, prenom, cni, email, filiere, niveauEtudes);
                    gestionnaire.ajouterUtilisateur(defaultUser);
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // En cas d'erreur, ajouter un utilisateur par défaut
            Utilisateur defaultUser = new Utilisateur(
                "K1280439", "Ahmed", "jabir", "X123456", 
                "ahmed.jabir@example.com", "Informatique", "Licence"
            );
            gestionnaire.ajouterUtilisateur(defaultUser);
        }
        
        // Configurer la fenêtre
        setTitle("Gestion des Utilisateurs");
        setSize(800, 600); // Taille moyenne
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Positionner la fenêtre à droite de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - getWidth() - 20; // 20 pixels de marge à droite
        int y = (screenSize.height - getHeight()) / 2; // Centré verticalement
        setLocation(x, y);
        
        // Initialiser les composants
        initComposants();
        
        // Afficher les données du premier utilisateur
        if (!gestionnaire.getListeUtilisateurs().isEmpty()) {
            afficherUtilisateur(gestionnaire.getUtilisateur(0));
        }
    }
    
    /**
     * Initialise et corrige la structure complète de la base de données
     */
    private void initialiserBaseDeDonnees() {
        Connection conn = null;
        try {
            conn = ConnexionBD.getConnexion();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de se connecter à la base de données.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Statement stmt = conn.createStatement();
            
            try {
                // Désactiver les contraintes de clé étrangère temporairement
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                
                // Créer la table utilisateur
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS utilisateur (" +
                    "cin VARCHAR(20) PRIMARY KEY, " +
                    "login VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL" +
                    ")"
                );
                
                // Créer la table filiere
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS filiere (" +
                    "id_filiere INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom_filiere VARCHAR(100) NOT NULL UNIQUE" +
                    ")"
                );
                
                // Créer la table niveau_etudes
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS niveau_etudes (" +
                    "id_niveau INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom_niveau VARCHAR(50) NOT NULL UNIQUE" +
                    ")"
                );
                
                // Insérer des niveaux d'études par défaut
                stmt.execute("INSERT IGNORE INTO niveau_etudes (nom_niveau) VALUES ('Licence'), ('Master'), ('Doctorat')");
                
                // Insérer des filières par défaut
                stmt.execute("INSERT IGNORE INTO filiere (nom_filiere) VALUES ('Informatique'), ('Mathématiques'), ('Physique'), ('Non spécifiée')");
                
                // Supprimer la table etudiant si elle existe
                stmt.execute("DROP TABLE IF EXISTS etudiant");
                
                // Recréer la table etudiant avec les bonnes contraintes
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS etudiant (" +
                    "cne VARCHAR(20) PRIMARY KEY, " +
                    "nom VARCHAR(50) NOT NULL, " +
                    "prenom VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "cin_utilisateur VARCHAR(20), " +
                    "id_filiere INT, " +
                    "id_niveau INT, " +
                    "niveau_etudes VARCHAR(50), " +
                    "FOREIGN KEY (cin_utilisateur) REFERENCES utilisateur(cin) ON DELETE CASCADE, " +
                    "FOREIGN KEY (id_filiere) REFERENCES filiere(id_filiere) ON DELETE SET NULL, " +
                    "FOREIGN KEY (id_niveau) REFERENCES niveau_etudes(id_niveau) ON DELETE SET NULL" +
                    ")"
                );
                
                // Réactiver les contraintes de clé étrangère
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
                
                System.out.println("Structure de la base de données initialisée avec succès");
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'initialisation de la structure: " + ex.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur de connexion à la base de données: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Initialise les composants de l'interface
     */
    private void initComposants() {
        // Configuration du conteneur principal
        getContentPane().setBackground(new Color(148, 173, 193)); // Bleu-gris clair
        setLayout(null); // Layout absolu
        
        // Titre "Utilisateurs"
        lblTitre = new JLabel("Utilisateurs");
        lblTitre.setBounds(50, 40, 150, 30);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitre);
        
        // Panneau blanc de recherche
        panneauRecherche = new JPanel();
        panneauRecherche.setBackground(Color.WHITE);
        panneauRecherche.setBounds(200, 40, 500, 120);
        panneauRecherche.setLayout(null);
        add(panneauRecherche);
        
        // Label CNE dans le panneau de recherche
        lblCNE = new JLabel("CNE");
        lblCNE.setBounds(20, 10, 40, 20);
        panneauRecherche.add(lblCNE);
        
        // Valeur du CNE en gras
        lblCNEValeur = new JLabel("K1280439");
        lblCNEValeur.setFont(new Font("Arial", Font.BOLD, 14));
        lblCNEValeur.setBounds(20, 30, 100, 20);
        panneauRecherche.add(lblCNEValeur);
        
        // Champ de recherche
        txtRecherche = new JTextField("128043921");
        txtRecherche.setBounds(20, 60, 400, 25);
        panneauRecherche.add(txtRecherche);
        
        // Nom de l'utilisateur (Ahmed jabir)
        lblUtilisateur = new JLabel("Ahmed jabir");
        lblUtilisateur.setBounds(20, 90, 150, 20);
        lblUtilisateur.setForeground(Color.GRAY);
        panneauRecherche.add(lblUtilisateur);
        
        // Bouton de liste des utilisateurs (pour remplacer la barre de défilement)
        btnListeUtilisateurs = new JButton("\u25BC"); // Caractère Unicode pour la flèche vers le bas
        btnListeUtilisateurs.setBounds(460, 60, 25, 25);
        btnListeUtilisateurs.setFont(new Font("Arial", Font.BOLD, 10));
        btnListeUtilisateurs.setFocusPainted(false);
        btnListeUtilisateurs.setBorderPainted(false);
        btnListeUtilisateurs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                afficherListeUtilisateurs();
            }
        });
        panneauRecherche.add(btnListeUtilisateurs);
        
        // Nom - Colonne gauche
        JLabel lblNom = new JLabel("Nom");
        lblNom.setBounds(130, 200, 100, 25);
        add(lblNom);
        
        cmbNom = new JComboBox<>();
        cmbNom.setEditable(true);
        cmbNom.setBounds(130, 230, 230, 30);
        chargerNoms();
        add(cmbNom);
        
        // CNE - Colonne gauche
        JLabel lblCNEForm = new JLabel("CNE");
        lblCNEForm.setBounds(130, 270, 100, 25);
        add(lblCNEForm);
        
        cmbCNE = new JComboBox<>();
        cmbCNE.setEditable(true);
        cmbCNE.setBounds(130, 300, 230, 30);
        chargerCNEs();
        add(cmbCNE);
        
        // Email - Colonne gauche
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(130, 340, 100, 25);
        add(lblEmail);
        
        cmbEmail = new JComboBox<>();
        cmbEmail.setEditable(true);
        cmbEmail.setBounds(130, 370, 230, 30);
        chargerEmails();
        add(cmbEmail);
        
        // Niveau d'études - Colonne gauche
        JLabel lblNiveau = new JLabel("Niveau d'études");
        lblNiveau.setBounds(130, 410, 150, 25);
        add(lblNiveau);
        
        cmbNiveauEtudes = new JComboBox<>();
        cmbNiveauEtudes.setEditable(true);
        cmbNiveauEtudes.setBounds(130, 440, 230, 30);
        chargerNiveauxEtudes();
        add(cmbNiveauEtudes);
        
        // Prénom - Colonne droite
        JLabel lblPrenom = new JLabel("Prénom");
        lblPrenom.setBounds(430, 200, 100, 25);
        add(lblPrenom);
        
        cmbPrenom = new JComboBox<>();
        cmbPrenom.setEditable(true);
        cmbPrenom.setBounds(430, 230, 230, 30);
        chargerPrenoms();
        add(cmbPrenom);
        
        // CNI - Colonne droite
        JLabel lblCNI = new JLabel("CNI");
        lblCNI.setBounds(430, 270, 100, 25);
        add(lblCNI);
        
        cmbCNI = new JComboBox<>();
        cmbCNI.setEditable(true);
        cmbCNI.setBounds(430, 300, 230, 30);
        chargerCNIs();
        add(cmbCNI);
        
        // Filière - Colonne droite
        JLabel lblFiliere = new JLabel("Filière");
        lblFiliere.setBounds(430, 340, 100, 25);
        add(lblFiliere);
        
        cmbFiliere = new JComboBox<>();
        cmbFiliere.setEditable(true);
        cmbFiliere.setBounds(430, 370, 230, 30);
        chargerFilieres();
        add(cmbFiliere);
        
        // Bouton Modifier
        btnModifier = new JButton("Modifier");
        btnModifier.setBounds(260, 500, 130, 30);
        btnModifier.setBackground(new Color(33, 150, 243));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Créer un nouvel utilisateur avec les valeurs des champs
                    Utilisateur utilisateur = new Utilisateur(
                        cmbCNE.getSelectedItem().toString(),
                        cmbNom.getSelectedItem().toString(),
                        cmbPrenom.getSelectedItem().toString(),
                        cmbCNI.getSelectedItem().toString(),
                        cmbEmail.getSelectedItem().toString(),
                        cmbFiliere.getSelectedItem().toString(),
                        cmbNiveauEtudes.getSelectedItem().toString()
                    );
                    
                    // Vérifier si le CIN existe dans la table utilisateur, sinon l'ajouter
                    sauvegarderUtilisateur(utilisateur);
                    
                    // Modifier l'utilisateur dans le gestionnaire
                    if (gestionnaire.getListeUtilisateurs().isEmpty()) {
                        gestionnaire.ajouterUtilisateur(utilisateur);
                    } else {
                        gestionnaire.modifierUtilisateur(0, utilisateur);
                    }
                    
                    // Mettre à jour l'affichage
                    lblCNEValeur.setText(cmbCNE.getSelectedItem().toString());
                    lblUtilisateur.setText(cmbNom.getSelectedItem().toString() + " " + cmbPrenom.getSelectedItem().toString());
                    
                    // Afficher un message de confirmation
                    JOptionPane.showMessageDialog(
                        InterfaceBibliotheque.this, 
                        "Utilisateur modifié avec succès!", 
                        "Confirmation", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        InterfaceBibliotheque.this, 
                        "Erreur lors de la modification: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            }
        });
        add(btnModifier);
        
        // Bouton Supprimer
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setBounds(410, 500, 130, 30);
        btnSupprimer.setBackground(new Color(33, 150, 243));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Afficher la boîte de dialogue de confirmation avec les options Oui/Non
                    int reponse = JOptionPane.showConfirmDialog(
                        InterfaceBibliotheque.this, 
                        "Voulez-vous vraiment supprimer cet utilisateur?", 
                        "Confirmation", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    // Si l'utilisateur clique sur "Oui"
                    if (reponse == JOptionPane.YES_OPTION) {
                        String cne = cmbCNE.getSelectedItem().toString();
                        
                        // Supprimer de la base de données
                        supprimerUtilisateur(cne);
                        
                        // Supprimer du gestionnaire
                        if (!gestionnaire.getListeUtilisateurs().isEmpty()) {
                            gestionnaire.supprimerUtilisateur(0);
                        }
                        
                        // Vider les champs
                        cmbNom.setSelectedItem("");
                        cmbPrenom.setSelectedItem("");
                        cmbCNE.setSelectedItem("");
                        cmbCNI.setSelectedItem("");
                        cmbEmail.setSelectedItem("");
                        cmbFiliere.setSelectedItem("");
                        cmbNiveauEtudes.setSelectedItem("");
                        
                        // Vider aussi la recherche
                        lblCNEValeur.setText("");
                        txtRecherche.setText("");
                        lblUtilisateur.setText("");
                        
                        // Afficher un message de confirmation
                        JOptionPane.showMessageDialog(
                            InterfaceBibliotheque.this, 
                            "Utilisateur supprimé avec succès!", 
                            "Confirmation", 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        InterfaceBibliotheque.this, 
                        "Erreur lors de la suppression: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            }
        });
        add(btnSupprimer);
    }
    
    /**
     * Charge les noms depuis la base de données
     */
    private void chargerNoms() {
        cmbNom.removeAllItems();
        boolean donneesChargees = false;
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT nom FROM etudiant ORDER BY nom");
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    cmbNom.addItem(rs.getString("nom"));
                    donneesChargees = true;
                }
                
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucune donnée n'a été chargée, ajouter des valeurs par défaut
        if (!donneesChargees) {
            cmbNom.addItem("Ahmed");
            cmbNom.addItem("Durand");
            cmbNom.addItem("Dupont");
            cmbNom.addItem("Martin");
        }
    }
    
    /**
     * Charge les prénoms depuis la base de données
     */
    private void chargerPrenoms() {
        cmbPrenom.removeAllItems();
        boolean donneesChargees = false;
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT prenom FROM etudiant ORDER BY prenom");
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    cmbPrenom.addItem(rs.getString("prenom"));
                    donneesChargees = true;
                }
                
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucune donnée n'a été chargée, ajouter des valeurs par défaut
        if (!donneesChargees) {
            cmbPrenom.addItem("jabir");
            cmbPrenom.addItem("Pierre");
            cmbPrenom.addItem("Jean");
            cmbPrenom.addItem("Marie");
        }
    }
    
    /**
     * Charge les CNEs depuis la base de données
     */
    private void chargerCNEs() {
        cmbCNE.removeAllItems();
        boolean donneesChargees = false;
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT cne FROM etudiant ORDER BY cne");
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    cmbCNE.addItem(rs.getString("cne"));
                    donneesChargees = true;
                }
                
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucune donnée n'a été chargée, ajouter des valeurs par défaut
        if (!donneesChargees) {
            cmbCNE.addItem("K1280439");
            cmbCNE.addItem("11223");
            cmbCNE.addItem("12345");
            cmbCNE.addItem("67890");
        }
    }
    
    /**
     * Charge les CNIs depuis la base de données
     */
    private void chargerCNIs() {
        cmbCNI.removeAllItems();
        boolean donneesChargees = false;
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT cin FROM utilisateur ORDER BY cin");
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    cmbCNI.addItem(rs.getString("cin"));
                    donneesChargees = true;
                }
                
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucune donnée n'a été chargée, ajouter des valeurs par défaut
        if (!donneesChargees) {
            cmbCNI.addItem("X123456");
            cmbCNI.addItem("AA1234");
            cmbCNI.addItem("BB5678");
            cmbCNI.addItem("CC9012");
        }
    }
    
    /**
     * Charge les emails depuis la base de données
     */
    private void chargerEmails() {
        cmbEmail.removeAllItems();
        boolean donneesChargees = false;
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT email FROM etudiant WHERE email IS NOT NULL AND email != '' ORDER BY email");
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    cmbEmail.addItem(rs.getString("email"));
                    donneesChargees = true;
                }
                
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucune donnée n'a été chargée, ajouter des valeurs par défaut
        if (!donneesChargees) {
            cmbEmail.addItem("ahmed.jabir@example.com");
            cmbEmail.addItem("pierre.durand@example.com");
            cmbEmail.addItem("jean.dupont@example.com");
            cmbEmail.addItem("marie.martin@example.com");
        }
    }
    
    /**
     * Charge les filières depuis la base de données
     */
    private void chargerFilieres() {
        cmbFiliere.removeAllItems();
        boolean donneesChargees = false;
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT nom_filiere FROM filiere ORDER BY nom_filiere");
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    cmbFiliere.addItem(rs.getString("nom_filiere"));
                    donneesChargees = true;
                }
                
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucune donnée n'a été chargée, ajouter des valeurs par défaut
        if (!donneesChargees) {
            cmbFiliere.addItem("Non spécifiée");
            cmbFiliere.addItem("Informatique");
            cmbFiliere.addItem("Mathématiques");
            cmbFiliere.addItem("Physique");
        } else {
            // Ajouter l'option "Non spécifiée" même si d'autres données ont été chargées
            boolean nonSpecifieePresente = false;
            for (int i = 0; i < cmbFiliere.getItemCount(); i++) {
                if (cmbFiliere.getItemAt(i).equals("Non spécifiée")) {
                    nonSpecifieePresente = true;
                    break;
                }
            }
            if (!nonSpecifieePresente) {
                cmbFiliere.addItem("Non spécifiée");
            }
        }
    }
    
    /**
     * Charge les niveaux d'études depuis la base de données
     */
    private void chargerNiveauxEtudes() {
        cmbNiveauEtudes.removeAllItems();
        boolean donneesChargees = false;
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                // Vérifier si la table niveau_etudes existe
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "niveau_etudes", null);
                boolean tableExists = tables.next();
                tables.close();
                
                if (tableExists) {
                    // Utiliser la table niveau_etudes
                    PreparedStatement stmt = conn.prepareStatement(
                        "SELECT DISTINCT nom_niveau FROM niveau_etudes ORDER BY nom_niveau");
                    ResultSet rs = stmt.executeQuery();
                    
                    while (rs.next()) {
                        cmbNiveauEtudes.addItem(rs.getString("nom_niveau"));
                        donneesChargees = true;
                    }
                    
                    rs.close();
                    stmt.close();
                } else {
                    // Utiliser directement la colonne niveau_etudes de la table etudiant
                    PreparedStatement stmt = conn.prepareStatement(
                        "SELECT DISTINCT niveau_etudes FROM etudiant WHERE niveau_etudes IS NOT NULL AND niveau_etudes != '' ORDER BY niveau_etudes");
                    ResultSet rs = stmt.executeQuery();
                    
                    while (rs.next()) {
                        cmbNiveauEtudes.addItem(rs.getString("niveau_etudes"));
                        donneesChargees = true;
                    }
                    
                    rs.close();
                    stmt.close();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucune donnée n'a été chargée, ajouter des valeurs par défaut
        if (!donneesChargees) {
            cmbNiveauEtudes.addItem("Licence");
            cmbNiveauEtudes.addItem("Master");
            cmbNiveauEtudes.addItem("Doctorat");
        }
    }
    
    /**
     * Affiche un menu avec les utilisateurs disponibles
     */
    private void afficherListeUtilisateurs() {
        JPopupMenu menu = new JPopupMenu();
        
        List<Utilisateur> utilisateurs = new ArrayList<>();
        
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null) {
                // Vérifier les colonnes disponibles
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet columnsNiveau = metaData.getColumns(null, null, "etudiant", "id_niveau");
                boolean idNiveauExists = columnsNiveau.next();
                columnsNiveau.close();
                
                ResultSet columnsFiliere = metaData.getColumns(null, null, "etudiant", "id_filiere");
                boolean idFiliereExists = columnsFiliere.next();
                columnsFiliere.close();
                
                String sql;
                if (idNiveauExists && idFiliereExists) {
                    sql = "SELECT e.cne, e.nom, e.prenom, e.cin_utilisateur, e.email, " +
                          "f.nom_filiere, n.nom_niveau, e.niveau_etudes " +
                          "FROM etudiant e " +
                          "LEFT JOIN filiere f ON e.id_filiere = f.id_filiere " +
                          "LEFT JOIN niveau_etudes n ON e.id_niveau = n.id_niveau " +
                          "ORDER BY e.nom, e.prenom";
                } else {
                    sql = "SELECT e.cne, e.nom, e.prenom, e.cin_utilisateur, e.email, e.niveau_etudes " +
                          "FROM etudiant e " +
                          "ORDER BY e.nom, e.prenom";
                }
                
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    String cne = rs.getString("cne");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String cni = rs.getString("cin_utilisateur");
                    String email = rs.getString("email") != null ? rs.getString("email") : "";
                    
                    String filiere = "Non spécifiée";
                    if (idFiliereExists && rs.getString("nom_filiere") != null) {
                        filiere = rs.getString("nom_filiere");
                    }
                    
                    String niveauEtudes = "Licence";
                    if (idNiveauExists && rs.getString("nom_niveau") != null) {
                        niveauEtudes = rs.getString("nom_niveau");
                    } else if (rs.getString("niveau_etudes") != null) {
                        niveauEtudes = rs.getString("niveau_etudes");
                    }
                    
                    utilisateurs.add(new Utilisateur(cne, nom, prenom, cni, email, filiere, niveauEtudes));
                }
                
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Si aucun utilisateur n'a été trouvé, ajouter celui par défaut
        if (utilisateurs.isEmpty() && !gestionnaire.getListeUtilisateurs().isEmpty()) {
            utilisateurs.add(gestionnaire.getUtilisateur(0));
        }
        
        // Créer un élément de menu pour chaque utilisateur
        for (final Utilisateur utilisateurObj : utilisateurs) {
            String texte = utilisateurObj.getNom() + " " + utilisateurObj.getPrenom();
            JMenuItem item = new JMenuItem(texte);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    afficherUtilisateur(utilisateurObj);
                }
            });
            menu.add(item);
        }
        
        menu.show(btnListeUtilisateurs, 0, btnListeUtilisateurs.getHeight());
    }
    
    /**
     * Affiche les données d'un utilisateur dans l'interface
     */
    private void afficherUtilisateur(Utilisateur utilisateur) {
        cmbNom.setSelectedItem(utilisateur.getNom());
        cmbPrenom.setSelectedItem(utilisateur.getPrenom());
        cmbCNE.setSelectedItem(utilisateur.getCne());
        cmbCNI.setSelectedItem(utilisateur.getCni());
        cmbEmail.setSelectedItem(utilisateur.getEmail());
        cmbFiliere.setSelectedItem(utilisateur.getFiliere());
        cmbNiveauEtudes.setSelectedItem(utilisateur.getNiveauEtudes());
        
        lblCNEValeur.setText(utilisateur.getCne());
        lblUtilisateur.setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
    }
    
    /**
     * Sauvegarde un utilisateur dans la base de données
     */
    private void sauvegarderUtilisateur(Utilisateur utilisateur) {
        Connection conn = null;
        try {
            conn = ConnexionBD.getConnexion();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de se connecter à la base de données.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Désactiver l'auto-commit pour gérer une transaction
            conn.setAutoCommit(false);
            
            try {
                // 1. D'abord, vérifier si le CIN existe dans la table utilisateur
                String cin = utilisateur.getCni();
                int count;
                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) as count FROM utilisateur WHERE cin = ?");
                checkStmt.setString(1, cin);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                count = rs.getInt("count");
                rs.close();
                checkStmt.close();
                
                // 2. Si le CIN n'existe pas, l'ajouter à la table utilisateur
                if (count == 0) {
                    // Générer un login unique basé sur le prénom et le nom
                    String login = utilisateur.getPrenom().toLowerCase() + 
                                  utilisateur.getNom().toLowerCase() + 
                                  String.valueOf((int)(Math.random() * 1000));
                    
                    PreparedStatement insertUserStmt = conn.prepareStatement(
                        "INSERT INTO utilisateur (cin, login, password, role) VALUES (?, ?, ?, ?)");
                    insertUserStmt.setString(1, cin);
                    insertUserStmt.setString(2, login);
                    insertUserStmt.setString(3, "motdepasse" + ((int)(Math.random() * 1000)));
                    insertUserStmt.setString(4, "etudiant");
                    insertUserStmt.executeUpdate();
                    insertUserStmt.close();
                }
                
                // 3. Trouver ou créer l'ID de la filière
                Integer idFiliere = null;
                if (!utilisateur.getFiliere().equals("Non spécifiée")) {
                    PreparedStatement checkFiliereStmt = conn.prepareStatement(
                        "SELECT id_filiere FROM filiere WHERE nom_filiere = ?");
                    checkFiliereStmt.setString(1, utilisateur.getFiliere());
                    ResultSet rsFiliere = checkFiliereStmt.executeQuery();
                    
                    if (rsFiliere.next()) {
                        idFiliere = rsFiliere.getInt("id_filiere");
                    } else {
                        PreparedStatement insertFiliereStmt = conn.prepareStatement(
                            "INSERT INTO filiere (nom_filiere) VALUES (?)", 
                            Statement.RETURN_GENERATED_KEYS);
                        insertFiliereStmt.setString(1, utilisateur.getFiliere());
                        insertFiliereStmt.executeUpdate();
                        
                        ResultSet rsKeys = insertFiliereStmt.getGeneratedKeys();
                        if (rsKeys.next()) {
                            idFiliere = rsKeys.getInt(1);
                        }
                        rsKeys.close();
                        insertFiliereStmt.close();
                    }
                    
                    rsFiliere.close();
                    checkFiliereStmt.close();
                }
                
                // 4. Trouver ou créer l'ID du niveau d'études
                Integer idNiveau = null;
                PreparedStatement checkNiveauStmt = conn.prepareStatement(
                    "SELECT id_niveau FROM niveau_etudes WHERE nom_niveau = ?");
                checkNiveauStmt.setString(1, utilisateur.getNiveauEtudes());
                ResultSet rsNiveau = checkNiveauStmt.executeQuery();
                
                if (rsNiveau.next()) {
                    idNiveau = rsNiveau.getInt("id_niveau");
                } else {
                    PreparedStatement insertNiveauStmt = conn.prepareStatement(
                        "INSERT INTO niveau_etudes (nom_niveau) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS);
                    insertNiveauStmt.setString(1, utilisateur.getNiveauEtudes());
                    insertNiveauStmt.executeUpdate();
                    
                    ResultSet rsKeys = insertNiveauStmt.getGeneratedKeys();
                    if (rsKeys.next()) {
                        idNiveau = rsKeys.getInt(1);
                    }
                    rsKeys.close();
                    insertNiveauStmt.close();
                }
                rsNiveau.close();
                checkNiveauStmt.close();
                
                // 5. Vérifier si l'étudiant existe déjà
                PreparedStatement checkEtudiantStmt = conn.prepareStatement(
                    "SELECT COUNT(*) as count FROM etudiant WHERE cne = ?");
                checkEtudiantStmt.setString(1, utilisateur.getCne());
                ResultSet rsEtudiant = checkEtudiantStmt.executeQuery();
                rsEtudiant.next();
                count = rsEtudiant.getInt("count");
                rsEtudiant.close();
                checkEtudiantStmt.close();
                
                // 6. Mettre à jour ou insérer l'étudiant
                if (count > 0) {
                    // Mise à jour d'un étudiant existant
                    String updateSQL = "UPDATE etudiant SET nom = ?, prenom = ?, cin_utilisateur = ?, " +
                                     "email = ?, niveau_etudes = ?, id_filiere = ?, id_niveau = ? WHERE cne = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                    updateStmt.setString(1, utilisateur.getNom());
                    updateStmt.setString(2, utilisateur.getPrenom());
                    updateStmt.setString(3, utilisateur.getCni());
                    updateStmt.setString(4, utilisateur.getEmail());
                    updateStmt.setString(5, utilisateur.getNiveauEtudes());
                    updateStmt.setObject(6, idFiliere); // Peut être NULL
                    updateStmt.setObject(7, idNiveau); // Peut être NULL
                    updateStmt.setString(8, utilisateur.getCne());
                    updateStmt.executeUpdate();
                    updateStmt.close();
                } else {
                    // Ajout d'un nouvel étudiant
                    String insertSQL = "INSERT INTO etudiant (cne, nom, prenom, cin_utilisateur, email, niveau_etudes, id_filiere, id_niveau) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
                    insertStmt.setString(1, utilisateur.getCne());
                    insertStmt.setString(2, utilisateur.getNom());
                    insertStmt.setString(3, utilisateur.getPrenom());
                    insertStmt.setString(4, utilisateur.getCni());
                    insertStmt.setString(5, utilisateur.getEmail());
                    insertStmt.setString(6, utilisateur.getNiveauEtudes());
                    insertStmt.setObject(7, idFiliere); // Peut être NULL
                    insertStmt.setObject(8, idNiveau); // Peut être NULL
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }
                
                // Valider la transaction
                conn.commit();
                
                // 7. Rafraîchir les listes déroulantes
                chargerNoms();
                chargerPrenoms();
                chargerCNEs();
                chargerCNIs();
                chargerEmails();
                chargerFilieres();
                chargerNiveauxEtudes();
                
            } catch (SQLException ex) {
                // En cas d'erreur, annuler la transaction
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                throw ex;
            } finally {
                // Rétablir l'auto-commit
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Erreur lors de la sauvegarde: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Supprime un utilisateur de la base de données
     */
    private void supprimerUtilisateur(String cne) {
        Connection conn = null;
        try {
            conn = ConnexionBD.getConnexion();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, 
                    "Impossible de se connecter à la base de données.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Désactiver l'auto-commit pour gérer une transaction
            conn.setAutoCommit(false);
            
            try {
                // 1. Récupérer le CIN de l'utilisateur à partir du CNE
                String cin = null;
                PreparedStatement stmtGetCIN = conn.prepareStatement(
                    "SELECT cin_utilisateur FROM etudiant WHERE cne = ?");
                stmtGetCIN.setString(1, cne);
                ResultSet rs = stmtGetCIN.executeQuery();
                if (rs.next()) {
                    cin = rs.getString("cin_utilisateur");
                }
                rs.close();
                stmtGetCIN.close();
                
                if (cin == null) {
                    throw new SQLException("Utilisateur introuvable avec ce CNE: " + cne);
                }
                
                // 2. Supprimer l'étudiant
                PreparedStatement stmtDeleteStudent = conn.prepareStatement(
                    "DELETE FROM etudiant WHERE cne = ?");
                stmtDeleteStudent.setString(1, cne);
                stmtDeleteStudent.executeUpdate();
                stmtDeleteStudent.close();
                
                // 3. Vérifier si le CIN est encore utilisé par d'autres étudiants
                PreparedStatement stmtCheckCIN = conn.prepareStatement(
                    "SELECT COUNT(*) as count FROM etudiant WHERE cin_utilisateur = ?");
                stmtCheckCIN.setString(1, cin);
                rs = stmtCheckCIN.executeQuery();
                boolean cinEncoreUtilise = false;
                if (rs.next()) {
                    cinEncoreUtilise = rs.getInt("count") > 0;
                }
                rs.close();
                stmtCheckCIN.close();
                
                // 4. Si le CIN n'est plus utilisé, supprimer aussi l'utilisateur
                if (!cinEncoreUtilise) {
                    PreparedStatement stmtDeleteUser = conn.prepareStatement(
                        "DELETE FROM utilisateur WHERE cin = ?");
                    stmtDeleteUser.setString(1, cin);
                    stmtDeleteUser.executeUpdate();
                    stmtDeleteUser.close();
                }
                
                // Valider la transaction
                conn.commit();
                
                // 5. Rafraîchir les listes déroulantes
                chargerNoms();
                chargerPrenoms();
                chargerCNEs();
                chargerCNIs();
                chargerEmails();
                chargerFilieres();
                chargerNiveauxEtudes();
                
            } catch (SQLException ex) {
                // En cas d'erreur, annuler la transaction
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                throw ex;
            } finally {
                // Rétablir l'auto-commit
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la suppression de l'utilisateur: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}