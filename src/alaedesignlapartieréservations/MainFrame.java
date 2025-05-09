package alaedesignlapartieréservations;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("UNILIB Document Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0xE8ECEF));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JButton livreButton = new JButton("Livres");
        livreButton.addActionListener(e -> switchPanel(new LivrePanel()));
        sidebar.add(livreButton);

        JButton rapportButton = new JButton("Rapport Licence");
        rapportButton.addActionListener(e -> switchPanel(new RapportLicencePanel()));
        sidebar.add(rapportButton);

        JButton theseButton = new JButton("Thèse de Doctorat");
        theseButton.addActionListener(e -> switchPanel(new TheseDoctoratPanel()));
        sidebar.add(theseButton);

        JButton memoireButton = new JButton("Mémoire de Master");
        memoireButton.addActionListener(e -> switchPanel(new MemoireMasterPanel()));
        sidebar.add(memoireButton);

        add(sidebar, BorderLayout.WEST);

        // Content Panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new LivrePanel(), BorderLayout.CENTER); // Default panel
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void switchPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}