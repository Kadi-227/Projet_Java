
package javaapplication3;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class EtudiantInterface extends javax.swing.JFrame {

    public EtudiantInterface() {
        initComponents();

        // Texte centré au milieu de la fenêtre
        JLabel welcomeLabel = new JLabel("Bienvenue à votre espace étudiant", SwingConstants.CENTER);
        welcomeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        welcomeLabel.setForeground(new java.awt.Color(44, 62, 80));

        // Remplacer le layout par BorderLayout pour centrer le label
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(welcomeLabel, java.awt.BorderLayout.CENTER);

        // Maximiser la fenêtre
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null); // Centre l'écran si jamais on ne veut pas le maximiser
        
        }

    
    @SuppressWarnings("unchecked")
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

   
    
    public static void main(String args[]) {
        
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
