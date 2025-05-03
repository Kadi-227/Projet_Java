package gestionutilisateurs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private JScrollBar barreDefilement;
    
    // Champs du formulaire
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtCNE;
    private JTextField txtCNI;
    private JTextField txtEmail;
    private JTextField txtFiliere;
    private JTextField txtNiveauEtudes;
    
    // Boutons
    private JButton btnModifier;
    private JButton btnSupprimer;
    
    /**
     * Constructeur
     */
    public InterfaceBibliotheque() {
        // Initialiser le gestionnaire
        gestionnaire = new GestionnaireUtilisateurs();
        
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
        
        // Barre de défilement
        barreDefilement = new JScrollBar(JScrollBar.VERTICAL);
        barreDefilement.setBounds(460, 60, 15, 25);
        panneauRecherche.add(barreDefilement);
        
        // Nom - Colonne gauche
        JLabel lblNom = new JLabel("Nom");
        lblNom.setBounds(130, 200, 100, 25);
        add(lblNom);
        
        txtNom = new JTextField();
        txtNom.setBounds(130, 230, 230, 30);
        add(txtNom);
        
        // CNE - Colonne gauche
        JLabel lblCNEForm = new JLabel("CNE");
        lblCNEForm.setBounds(130, 270, 100, 25);
        add(lblCNEForm);
        
        txtCNE = new JTextField();
        txtCNE.setBounds(130, 300, 230, 30);
        add(txtCNE);
        
        // Email - Colonne gauche
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(130, 340, 100, 25);
        add(lblEmail);
        
        txtEmail = new JTextField();
        txtEmail.setBounds(130, 370, 230, 30);
        add(txtEmail);
        
        // Niveau d'études - Colonne gauche
        JLabel lblNiveau = new JLabel("Niveau d'études");
        lblNiveau.setBounds(130, 410, 150, 25);
        add(lblNiveau);
        
        txtNiveauEtudes = new JTextField();
        txtNiveauEtudes.setBounds(130, 440, 230, 30);
        add(txtNiveauEtudes);
        
        // Prénom - Colonne droite
        JLabel lblPrenom = new JLabel("Prénom");
        lblPrenom.setBounds(430, 200, 100, 25);
        add(lblPrenom);
        
        txtPrenom = new JTextField();
        txtPrenom.setBounds(430, 230, 230, 30);
        add(txtPrenom);
        
        // CNI - Colonne droite
        JLabel lblCNI = new JLabel("CNI");
        lblCNI.setBounds(430, 270, 100, 25);
        add(lblCNI);
        
        txtCNI = new JTextField();
        txtCNI.setBounds(430, 300, 230, 30);
        add(txtCNI);
        
        // Filière - Colonne droite
        JLabel lblFiliere = new JLabel("Filière");
        lblFiliere.setBounds(430, 340, 100, 25);
        add(lblFiliere);
        
        txtFiliere = new JTextField();
        txtFiliere.setBounds(430, 370, 230, 30);
        add(txtFiliere);
        
        // Bouton Modifier
        btnModifier = new JButton("Modifier");
        btnModifier.setBounds(260, 500, 130, 30);
        btnModifier.setBackground(new Color(33, 150, 243));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gestionnaire.getListeUtilisateurs().isEmpty()) {
                    // Créer un nouvel utilisateur avec les valeurs des champs
                    Utilisateur utilisateur = new Utilisateur(
                        txtCNE.getText(),
                        txtNom.getText(),
                        txtPrenom.getText(),
                        txtCNI.getText(),
                        txtEmail.getText(),
                        txtFiliere.getText(),
                        txtNiveauEtudes.getText()
                    );
                    
                    // Modifier l'utilisateur dans le gestionnaire
                    gestionnaire.modifierUtilisateur(0, utilisateur);
                    
                    JOptionPane.showMessageDialog(InterfaceBibliotheque.this, "Utilisateur modifié avec succès!");
                } else {
                    JOptionPane.showMessageDialog(InterfaceBibliotheque.this, 
                        "Aucun utilisateur à modifier!", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
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
                if (!gestionnaire.getListeUtilisateurs().isEmpty()) {
                    // Afficher la boîte de dialogue de confirmation avec les options Oui/Non
                    int reponse = JOptionPane.showConfirmDialog(InterfaceBibliotheque.this, 
                        "Voulez-vous vraiment supprimer cet utilisateur?", 
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                    
                    // Si l'utilisateur clique sur "Oui"
                    if (reponse == JOptionPane.YES_OPTION) {
                        // Supprimer l'utilisateur du gestionnaire
                        gestionnaire.supprimerUtilisateur(0);
                        
                        // Vider les champs
                        txtNom.setText("");
                        txtPrenom.setText("");
                        txtCNE.setText("");
                        txtCNI.setText("");
                        txtEmail.setText("");
                        txtFiliere.setText("");
                        txtNiveauEtudes.setText("");
                        
                        // Vider aussi la recherche
                        lblCNEValeur.setText("");
                        txtRecherche.setText("");
                        lblUtilisateur.setText("");
                        
                        JOptionPane.showMessageDialog(InterfaceBibliotheque.this, "Utilisateur supprimé avec succès!");
                    }
                    // Si l'utilisateur clique sur "Non", rien ne se passe et les données sont conservées
                } else {
                    JOptionPane.showMessageDialog(InterfaceBibliotheque.this, 
                        "Aucun utilisateur à supprimer!", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(btnSupprimer);
    }
    
    /**
     * Affiche les données d'un utilisateur dans les champs
     */
    private void afficherUtilisateur(Utilisateur utilisateur) {
        if (utilisateur != null) {
            txtNom.setText(utilisateur.getNom());
            txtPrenom.setText(utilisateur.getPrenom());
            txtCNE.setText(utilisateur.getCne());
            txtCNI.setText(utilisateur.getCni());
            txtEmail.setText(utilisateur.getEmail());
            txtFiliere.setText(utilisateur.getFiliere());
            txtNiveauEtudes.setText(utilisateur.getNiveauEtudes());
        }
    }
}