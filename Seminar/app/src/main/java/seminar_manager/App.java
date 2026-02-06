package seminar_manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import seminar_manager.database.DatabaseManager;
import seminar_manager.database.dao.*;
import seminar_manager.models.*;
import seminar_manager.models.enums.*;
import seminar_manager.views.*;

import java.awt.event.*;

public class App {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDatabase();
        // System.out.println("Database initialized.");

        // Home home = new Home();
        // home.addWindowListener(new WindowAdapter() {
        // @Override
        // public void windowClosing(WindowEvent e) {
        // // Close database connection when done
        // dbManager.closeConnection();
        // }

        // });
        Runtime.getRuntime().addShutdownHook(new Thread(dbManager::closeConnection));

        SwingUtilities.invokeLater(() -> {
            new Home(); // entry UI
        });
    }
}
