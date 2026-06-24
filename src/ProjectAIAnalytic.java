import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProjectAIAnalytic {

    public double predictDelayProbability(int projectId) {
        int totalTasks = 0;
        int exceededTasks = 0;

        String query = "SELECT estimated_hours, actual_hours FROM Tasks WHERE project_id = ?";
        
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, projectId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        totalTasks++;
                        int estimated = rs.getInt("estimated_hours");
                        int actual = rs.getInt("actual_hours");
                        
                        if (actual > estimated) {
                            exceededTasks++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("AI Analytics Error: " + e.getMessage());
            return -1.0; 
        }

        if (totalTasks == 0) return 0.0;
        return (double) exceededTasks / totalTasks;
    }
}