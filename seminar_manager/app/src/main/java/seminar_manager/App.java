package seminar_manager;

import seminar_manager.database.DatabaseManager;
import seminar_manager.views.LoginFrame;
import java.awt.event.*;

public class App {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDatabase();
        System.out.println("Database initialized.");
        dbManager.seedDatabase();

        // Start with Login Frame
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Close database connection when application closes
                    dbManager.closeConnection();
                    System.exit(0);
                }
            });
        });
    }
}