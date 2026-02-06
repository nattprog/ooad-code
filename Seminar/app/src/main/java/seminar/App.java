package seminar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// import java.sql.Connection;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;

import seminar.database.DatabaseManager;
import seminar.database.dao.*;
import seminar.models.*;
import seminar.models.enums.*;
import seminar.views.*;

public class App {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDatabase();

        System.out.println("Database initialized. Testing connection...");

        Scanner myObj = new Scanner(System.in);
        System.out.println("Title:");
        String title = myObj.nextLine();
        System.out.println("Description:");
        String desc = myObj.nextLine();
        System.out.println("Location:");
        String loc = myObj.nextLine();
        System.out.println("Start (yyyy-MM-dd HH:mm):");
        String start = myObj.nextLine();
        System.out.println("End (yyyy-MM-dd HH:mm):");
        String end = myObj.nextLine();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date startDate = formatter.parse(start);
            Date endDate = formatter.parse(end);

            // Create Seminar object
            Seminar seminar = new Seminar(title, desc, loc, startDate, endDate);

            SeminarDAO seminarDAO = new SeminarDAO();
            boolean created = seminarDAO.createSeminar(seminar);

            if (created) {
                System.out.println("Seminar created successfully!");
            } else {
                System.out.println("Failed to create seminar.");
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format! Please use yyyy-MM-dd HH:mm");
            e.printStackTrace();
        }

        // Close connection when done
        dbManager.closeConnection();
    }
}
