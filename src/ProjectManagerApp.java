import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;

public class ProjectManagerApp extends JFrame {
    private JTextField txtName, txtBudget, txtStart, txtEnd, txtProjectId;
    private JButton btnSave, btnAnalyze;
    private JTextArea txtConsole;

    public ProjectManagerApp() {
        setTitle("Smart Digital Transformation & Automation System");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Project Management & Automation Form"));

        panelForm.add(new JLabel(" Project Name:"));
        txtName = new JTextField(); panelForm.add(txtName);

        panelForm.add(new JLabel(" Budget ($):"));
        txtBudget = new JTextField(); panelForm.add(txtBudget);

        panelForm.add(new JLabel(" Start Date (YYYY-MM-DD):"));
        txtStart = new JTextField("2026-06-01"); panelForm.add(txtStart);

        panelForm.add(new JLabel(" End Date (YYYY-MM-DD):"));
        txtEnd = new JTextField("2026-12-31"); panelForm.add(txtEnd);
        
        panelForm.add(new JLabel(" Target Project ID for AI Analytics:"));
        txtProjectId = new JTextField(); panelForm.add(txtProjectId);

        JPanel panelButtons = new JPanel(new FlowLayout());
        btnSave = new JButton("Save Project to DB");
        btnAnalyze = new JButton("Run AI Delay Prediction");
        panelButtons.add(btnSave); panelButtons.add(btnAnalyze);

        txtConsole = new JTextArea();
        txtConsole.setEditable(false);
        txtConsole.setBackground(Color.BLACK);
        txtConsole.setForeground(Color.GREEN);
        txtConsole.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtConsole);

        add(panelForm, BorderLayout.NORTH);
        add(panelButtons, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        scrollPane.setPreferredSize(new Dimension(600, 200));

        // --- الأحداث (Actions) ---

        btnSave.addActionListener(e -> {
            String name = txtName.getText();
            double budget = Double.parseDouble(txtBudget.getText());
            String start = txtStart.getText();
            String end = txtEnd.getText();

            String insertSQL = "INSERT INTO Projects (project_name, budget, start_date, end_date, status) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), 'Planning')";

            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                    stmt.setString(1, name);
                    stmt.setDouble(2, budget);
                    stmt.setString(3, start);
                    stmt.setString(4, end);
                    stmt.executeUpdate();
                }
                txtConsole.append("✅ [SUCCESS] Project saved to Oracle DB.\n");
            } catch (Exception ex) {
                txtConsole.append("⚠️ [RESILIENCE TRIGGERED] DB Failure. Activating offline backup mode...\n");
                ResilienceManager.saveDataOffline(name, budget, start, end, ex.getMessage());
                txtConsole.append("💾 [OFFLINE] Data backed up safely in local system files.\n");
            }
        });

        btnAnalyze.addActionListener(e -> {
            try {
                int pId = Integer.parseInt(txtProjectId.getText());
                ProjectAIAnalytic ai = new ProjectAIAnalytic();
                double result = ai.predictDelayProbability(pId);

                if (result == -1.0) {
                    txtConsole.append("[AI ERROR] Could not fetch data for Project ID: " + pId + "\n");
                } else {
                    double percentage = result * 100;
                    txtConsole.append("[AI ENGINE] Processing Task Behavior for Project ID: " + pId + "...\n");
                    txtConsole.append(String.format("📊 [PREDICTION] Risk of Project Delay is: %.2f%%\n", percentage));
                }
            } catch (NumberFormatException ex) {
                txtConsole.append("[INPUT ERROR] Enter a valid numerical Project ID.\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProjectManagerApp().setVisible(true);
        });
    }
}