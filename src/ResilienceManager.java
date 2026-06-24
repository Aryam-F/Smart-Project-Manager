import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class ResilienceManager {
    private static final String BACKUP_FILE = "cyber_resilience_backup.log";

    public static void saveDataOffline(String projectName, double budget, String startDate, String endDate, String error) {
        try (FileWriter fw = new FileWriter(BACKUP_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("=== SYSTEM OFFLINE FALLBACK MODE ===");
            pw.println("Timestamp   : " + LocalDateTime.now());
            pw.println("Project Name: " + projectName);
            pw.println("Budget      : " + budget);
            pw.println("Duration    : " + startDate + " to " + endDate);
            pw.println("Trigger Error: " + error);
            pw.println("Status      : Pending Sync when DB is resilient.");
            pw.println("====================================\n");
            
            System.out.println("⚠️ Cyber Resilience: Connection failed! Data secured offline.");
        } catch (IOException e) {
            System.err.println("Critical Error writing to offline log: " + e.getMessage());
        }
    }
}