package gestionutilisateurs;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Classe pour gérer la connexion à la base de données MySQL
 */
public class ConnexionBD {
    // Paramètres de connexion avec options supplémentaires
    private static final String URL = "jdbc:mysql://localhost:3308/unilib_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root"; // Votre nom d'utilisateur MySQL
    private static final String PASSWORD = "&/zineb/15@11sky"; // Votre mot de passe MySQL (laissez vide si aucun)
    
    private static Connection connexion;
    
    /**
     * Établit une connexion à la base de données
     * @return Connection objet de connexion
     */
    public static Connection getConnexion() {
        if (connexion == null) {
            try {
                // Charger le pilote MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Établir la connexion
                connexion = DriverManager.getConnection(URL, USER, PASSWORD);
                
                System.out.println("Connexion à la base de données établie");
                
                // Créer les tables nécessaires si elles n'existent pas
                createTablesIfNotExist();
                
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, 
                    "Le pilote MySQL n'a pas été trouvé : " + e.getMessage(), 
                    "Erreur de pilote", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors de la connexion à la base de données : " + e.getMessage(), 
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            }
        }
        return connexion;
    }
    
    /**
     * Crée les tables nécessaires si elles n'existent pas
     */
    private static void createTablesIfNotExist() {
        try {
            // Création de la table utilisateur
            try (Statement stmt = connexion.createStatement()) {
                // Création de la table utilisateur
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS utilisateur (" +
                                "  cin VARCHAR(20) PRIMARY KEY, " +
                                "  mot_de_passe VARCHAR(255)" +
                                ")");
                
                // Création de la table filiere
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS filiere (" +
                                "  id_filiere INT AUTO_INCREMENT PRIMARY KEY, " +
                                "  nom_filiere VARCHAR(100) NOT NULL" +
                                ")");
                
                // Création de la table niveau_etudes
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS niveau_etudes (" +
                                "  id_niveau INT AUTO_INCREMENT PRIMARY KEY, " +
                                "  nom_niveau VARCHAR(50) NOT NULL" +
                                ")");
                
                // Création de la table etudiant
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS etudiant (" +
                                "  cne VARCHAR(20) NOT NULL PRIMARY KEY, " +
                                "  nom VARCHAR(100) NOT NULL, " +
                                "  prenom VARCHAR(100) NOT NULL, " +
                                "  email VARCHAR(100), " +
                                "  niveau_etudes VARCHAR(50), " +
                                "  id_filiere INT, " +
                                "  id_niveau INT, " +
                                "  cin_utilisateur VARCHAR(20), " +
                                "  FOREIGN KEY (id_filiere) REFERENCES filiere(id_filiere) ON DELETE SET NULL, " +
                                "  FOREIGN KEY (id_niveau) REFERENCES niveau_etudes(id_niveau) ON DELETE SET NULL, " +
                                "  FOREIGN KEY (cin_utilisateur) REFERENCES utilisateur(cin) ON DELETE SET NULL" +
                                ")");
                
                // Insérer quelques données de base si nécessaire
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM niveau_etudes");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO niveau_etudes (nom_niveau) VALUES ('Licence'), ('Master'), ('Doctorat')");
                }
                
                rs = stmt.executeQuery("SELECT COUNT(*) FROM filiere");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO filiere (nom_filiere) VALUES ('Informatique'), ('Mathématiques'), ('Physique')");
                }
            }
            System.out.println("Tables créées avec succès");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables : " + e.getMessage());
        }
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public static void fermerConnexion() {
        if (connexion != null) {
            try {
                connexion.close();
                connexion = null;
                System.out.println("Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}

