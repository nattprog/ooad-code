package seminar_manager;

import seminar_manager.database.DatabaseManager;
import seminar_manager.views.LoginFrame;
import java.awt.event.*;

public class SeedDb {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDatabase();
        dbManager.seedDatabase();
        System.out.println("Database seeded.");
        dbManager.closeConnection();

    }
}