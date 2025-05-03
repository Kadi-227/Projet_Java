package gestionutilisateurs;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gérer la liste des utilisateurs
 */
public class GestionnaireUtilisateurs {
    private List<Utilisateur> listeUtilisateurs;
    
    /**
     * Constructeur qui initialise la liste et ajoute un utilisateur d'exemple
     */
    public GestionnaireUtilisateurs() {
        listeUtilisateurs = new ArrayList<>();
        // Ajouter un utilisateur d'exemple
        ajouterUtilisateurExemple();
    }
    
    /**
     * Ajoute un utilisateur d'exemple
     */
    private void ajouterUtilisateurExemple() {
        Utilisateur utilisateur = new Utilisateur(
            "K1280439", 
            "jabir", 
            "Ahmed", 
            "AB123456", 
            "ahmed.jabir@exemple.com", 
            "Informatique", 
            "Licence");
        listeUtilisateurs.add(utilisateur);
    }
    
    /**
     * Retourne la liste des utilisateurs
     */
    public List<Utilisateur> getListeUtilisateurs() {
        return listeUtilisateurs;
    }
    
    /**
     * Ajoute un utilisateur à la liste
     */
    public void ajouterUtilisateur(Utilisateur utilisateur) {
        listeUtilisateurs.add(utilisateur);
    }
    
    /**
     * Modifie un utilisateur existant
     */
    public void modifierUtilisateur(int index, Utilisateur utilisateur) {
        if (index >= 0 && index < listeUtilisateurs.size()) {
            listeUtilisateurs.set(index, utilisateur);
        }
    }
    
    /**
     * Supprime un utilisateur
     */
    public void supprimerUtilisateur(int index) {
        if (index >= 0 && index < listeUtilisateurs.size()) {
            listeUtilisateurs.remove(index);
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
        for (Utilisateur u : listeUtilisateurs) {
            if (u.getCne().equals(cne)) {
                return u;
            }
        }
        return null;
    }
}