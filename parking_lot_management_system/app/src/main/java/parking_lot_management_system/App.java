package parking_lot_management_system;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import parking_lot_management_system.database.DatabaseManager;
import parking_lot_management_system.models.ParkingLot;
import parking_lot_management_system.views.MainFrame;

import java.sql.SQLException;

/**
 * Main application class for Parking Lot Management System
 */
public class App {

    public static void main(String[] args) {
        // Initialize database first
        System.out.println("Initializing Parking Lot Management System...");

        try {
            // Initialize database connection and create tables
            DatabaseManager.initialize();

            // Initialize parking lot structure (5 floors, 3 rows per floor, 4 spots per
            // row)
            // This creates 60 parking spots distributed across all spot types
            DatabaseManager.initializeParkingLot(5, 3, 4);

            // Get parking lot singleton and load data from database
            ParkingLot parkingLot = ParkingLot.getInstance();
            parkingLot.loadFromDatabase();

            System.out.println("System initialized successfully!");

            // Launch GUI on Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    // Set system look and feel
                    javax.swing.UIManager.setLookAndFeel(
                            javax.swing.UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // If system L&F fails, use default
                    System.err.println("Could not set system look and feel: " + e.getMessage());
                }

                // Create and show main frame
                new MainFrame();
            });

            // Add shutdown hook to close database connection properly
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down Parking Lot Management System...");
                DatabaseManager.closeConnection();
                System.out.println("System shutdown complete.");
            }));

        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();

            // Show error dialog
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize database:\n" + e.getMessage() +
                                "\n\nPlease ensure SQLite JDBC driver is in classpath.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        } catch (Exception e) {
            System.err.println("Unexpected error during initialization: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}