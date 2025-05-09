package alaedesignlapartieréservations;

import javax.swing.*;

public class DocumentPanelTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Document Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new DocumentPanel());
            frame.setVisible(true);
        });
    }
}