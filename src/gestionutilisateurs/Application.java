package gestionutilisateurs;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Point d'entrée de l'application
 */
public class Application {
    /**
     * Méthode principale
     */
    public static void main(String[] args) {
        // Appliquer le look and feel du système
        try {
            // Utiliser le look and feel Nimbus qui est plus moderne
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                // Si Nimbus n'est pas disponible, utiliser le look and feel du système
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Lancer l'interface dans le thread EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                InterfaceBibliotheque fenetre = new InterfaceBibliotheque();
                fenetre.setVisible(true);
            }
        });
    }
}