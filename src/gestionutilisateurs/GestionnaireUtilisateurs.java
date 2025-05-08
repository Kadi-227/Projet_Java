package gestionutilisateurs;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Classe pour gérer la liste des utilisateurs via la base de données
 */
public class GestionnaireUtilisateurs {
    private final List<Utilisateur> listeUtilisateurs;
    private final Connection connexion;
    
    /**
     * Constructeur qui initialise la liste et se connecte à la base de données
     */
    public GestionnaireUtilisateurs() {
        listeUtilisateurs = new ArrayList<>();
        connexion = ConnexionBD.getConnexion();
        chargerUtilisateurs();
    }
    
    /**
     * Charge tous les utilisateurs depuis la base de données
     */
    private void chargerUtilisateurs() {
        listeUtilisateurs.clear();
        
        try {
            String sql = "SELECT e.cne, e.nom, e.prenom, u.cin, e.email, f.nom_filiere, n.nom_niveau " +
                        "FROM etudiant e " +
                        "LEFT JOIN filiere f ON e.id_filiere = f.id_filiere " +
                        "LEFT JOIN niveau_etudes n ON e.id_niveau = n.id_niveau " +
                        "LEFT JOIN utilisateur u ON e.cin_utilisateur = u.cin";
            
            Statement stmt = connexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                    rs.getString("cne"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("cin"),
                    rs.getString("email"),
                    rs.getString("nom_filiere"),
                    rs.getString("nom_niveau")
                );
                listeUtilisateurs.add(utilisateur);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors du chargement des utilisateurs : " + e.getMessage(), 
                "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Retourne la liste des utilisateurs
     */
    public List<Utilisateur> getListeUtilisateurs() {
        return listeUtilisateurs;
    }
    
    /**
     * Ajoute un utilisateur à la base de données
     */
    public void ajouterUtilisateur(Utilisateur utilisateur) {
        try {
            // Récupérer les IDs de filière et niveau
            int idFiliere = getIdFiliere(utilisateur.getFiliere());
            int idNiveau = getIdNiveau(utilisateur.getNiveauEtudes());
            
            // Vérifier si l'utilisateur existe déjà
            if (chercherParCNE(utilisateur.getCne()) != null) {
                JOptionPane.showMessageDialog(null, 
                    "Un utilisateur avec ce CNE existe déjà.", 
                    "Erreur d'ajout", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insérer l'utilisateur
            String sql = "INSERT INTO etudiant (cne, nom, prenom, email, niveau_etudes, id_filiere, id_niveau, cin_utilisateur) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = connexion.prepareStatement(sql);
            pstmt.setString(1, utilisateur.getCne());
            pstmt.setString(2, utilisateur.getNom());
            pstmt.setString(3, utilisateur.getPrenom());
            pstmt.setString(4, utilisateur.getEmail());
            pstmt.setString(5, utilisateur.getNiveauEtudes());
            pstmt.setInt(6, idFiliere);
            pstmt.setInt(7, idNiveau);
            pstmt.setString(8, utilisateur.getCni());
            
            pstmt.executeUpdate();
            pstmt.close();
            
            // Recharger les utilisateurs
            chargerUtilisateurs();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de l'ajout de l'utilisateur : " + e.getMessage(), 
                "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Modifie un utilisateur dans la base de données
     */
    public void modifierUtilisateur(int index, Utilisateur utilisateur) {
        try {
            // Récupérer les IDs de filière et niveau
            int idFiliere = getIdFiliere(utilisateur.getFiliere());
            int idNiveau = getIdNiveau(utilisateur.getNiveauEtudes());
            
            // Récupérer l'ancien CNE
            String ancienCNE = listeUtilisateurs.get(index).getCne();
            
            // Mettre à jour l'utilisateur
            String sql = "UPDATE etudiant SET cne = ?, nom = ?, prenom = ?, email = ?, " +
                         "niveau_etudes = ?, id_filiere = ?, id_niveau = ?, cin_utilisateur = ? " +
                         "WHERE cne = ?";
            
            PreparedStatement pstmt = connexion.prepareStatement(sql);
            pstmt.setString(1, utilisateur.getCne());
            pstmt.setString(2, utilisateur.getNom());
            pstmt.setString(3, utilisateur.getPrenom());
            pstmt.setString(4, utilisateur.getEmail());
            pstmt.setString(5, utilisateur.getNiveauEtudes());
            pstmt.setInt(6, idFiliere);
            pstmt.setInt(7, idNiveau);
            pstmt.setString(8, utilisateur.getCni());
            pstmt.setString(9, ancienCNE);
            
            pstmt.executeUpdate();
            pstmt.close();
            
            // Recharger les utilisateurs
            chargerUtilisateurs();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la modification de l'utilisateur : " + e.getMessage(), 
                "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Supprime un utilisateur de la base de données
     */
    public void supprimerUtilisateur(int index) {
        try {
            // Récupérer le CNE
            String cne = listeUtilisateurs.get(index).getCne();
            
            // Supprimer l'utilisateur
            String sql = "DELETE FROM etudiant WHERE cne = ?";
            
            PreparedStatement pstmt = connexion.prepareStatement(sql);
            pstmt.setString(1, cne);
            
            pstmt.executeUpdate();
            pstmt.close();
            
            // Recharger les utilisateurs
            chargerUtilisateurs();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la suppression de l'utilisateur : " + e.getMessage(), 
                "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Retourne un utilisateur par son index
     */
    public Utilisateur getUtilisateur(int index) {
        if (index >= 0 && index < listeUtilisateurs.size()) {
            return listeUtilisateurs.get(index);
        }
        return null;
    }
    
    /**
     * Cherche un utilisateur par son CNE
     */
    public Utilisateur chercherParCNE(String cne) {
        try {
            String sql = "SELECT e.cne, e.nom, e.prenom, u.cin, e.email, f.nom_filiere, n.nom_niveau " +
                        "FROM etudiant e " +
                        "LEFT JOIN filiere f ON e.id_filiere = f.id_filiere " +
                        "LEFT JOIN niveau_etudes n ON e.id_niveau = n.id_niveau " +
                        "LEFT JOIN utilisateur u ON e.cin_utilisateur = u.cin " +
                        "WHERE e.cne = ?";
            
            PreparedStatement pstmt = connexion.prepareStatement(sql);
            pstmt.setString(1, cne);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                    rs.getString("cne"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("cin"),
                    rs.getString("email"),
                    rs.getString("nom_filiere"),
                    rs.getString("nom_niveau")
                );
                
                rs.close();
                pstmt.close();
                
                return utilisateur;
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la recherche d'utilisateur : " + e.getMessage(), 
                "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupère l'ID d'une filière par son nom
     */
    private int getIdFiliere(String nomFiliere) throws SQLException {
        if (nomFiliere == null || nomFiliere.isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT id_filiere FROM filiere WHERE nom_filiere = ?";
        PreparedStatement pstmt = connexion.prepareStatement(sql);
        pstmt.setString(1, nomFiliere);
        
        ResultSet rs = pstmt.executeQuery();
        int id = 0;
        
        if (rs.next()) {
            id = rs.getInt("id_filiere");
        } else {
            // Créer la filière si elle n'existe pas
            String insertSql = "INSERT INTO filiere (nom_filiere) VALUES (?)";
            PreparedStatement insertStmt = connexion.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, nomFiliere);
            insertStmt.executeUpdate();
            
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
            
            insertStmt.close();
        }
        
        rs.close();
        pstmt.close();
        
        return id;
    }
    
    /**
     * Récupère l'ID d'un niveau d'études par son nom
     */
    private int getIdNiveau(String nomNiveau) throws SQLException {
        if (nomNiveau == null || nomNiveau.isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT id_niveau FROM niveau_etudes WHERE nom_niveau = ?";
        PreparedStatement pstmt = connexion.prepareStatement(sql);
        pstmt.setString(1, nomNiveau);
        
        ResultSet rs = pstmt.executeQuery();
        int id = 0;
        
        if (rs.next()) {
            id = rs.getInt("id_niveau");
        }
        
        rs.close();
        pstmt.close();
        
        return id;
    }
}