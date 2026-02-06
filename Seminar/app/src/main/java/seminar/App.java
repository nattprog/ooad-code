package seminar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import seminar.database.DatabaseManager;
import seminar.database.dao.*;
import seminar.models.*;
import seminar.models.enums.*;
import seminar.views.*;

import java.awt.event.*;

public class App {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDatabase();
        System.out.println("Database initialized.");

        Home home = new Home();
        home.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Close database connection when done
                dbManager.closeConnection();
            }

        });

    }
}
