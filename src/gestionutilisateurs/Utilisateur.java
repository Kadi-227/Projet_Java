package gestionutilisateurs;

/**
 * Classe représentant un utilisateur
 */
public class Utilisateur {
    // Attributs
    private String cne;
    private String nom;
    private String prenom;
    private String cni;
    private String email;
    private String filiere;
    private String niveauEtudes;
    
    /**
     * Constructeur par défaut
     */
    public Utilisateur() {
        this.cne = "";
        this.nom = "";
        this.prenom = "";
        this.cni = "";
        this.email = "";
        this.filiere = "";
        this.niveauEtudes = "";
    }
    
    /**
     * Constructeur avec tous les attributs
     */
    public Utilisateur(String cne, String nom, String prenom, String cni, 
                      String email, String filiere, String niveauEtudes) {
        this.cne = cne;
        this.nom = nom;
        this.prenom = prenom;
        this.cni = cni;
        this.email = email;
        this.filiere = filiere;
        this.niveauEtudes = niveauEtudes;
    }
    
    // Getters
    public String getCne() { return cne; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getCni() { return cni; }
    public String getEmail() { return email; }
    public String getFiliere() { return filiere; }
    public String getNiveauEtudes() { return niveauEtudes; }
    
    // Setters
    public void setCne(String cne) { this.cne = cne; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setCni(String cni) { this.cni = cni; }
    public void setEmail(String email) { this.email = email; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public void setNiveauEtudes(String niveauEtudes) { this.niveauEtudes = niveauEtudes; }
    
    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}