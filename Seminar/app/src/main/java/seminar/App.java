package seminar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import seminar.database.DatabaseManager;
import seminar.database.dao.*;
import seminar.models.*;
import seminar.models.enums.*;

public class App {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDatabase();

        System.out.println("Database initialized. Testing connection...");

        try {
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            stmt.close();
            System.out.println("Connection test successful.");
        } catch (SQLException e) {
            System.err.println("Connection test failed.");
            e.printStackTrace();
        }

        // Test creating a student
        Student student = new Student("user1", "pass123", "John Doe",
                "john@example.com", "S001");
        System.out.println("1\n");
        StudentDAO studentDAO = new StudentDAO();
        System.out.println("2\n");
        boolean created = studentDAO.createStudent(student);
        System.out.println("3\n");

        if (created) {
            System.out.println("Student created successfully with ID: " + student.getUserId());
        } else {
            System.out.println("Failed to create student.");
        }

        // Close connection when done
        dbManager.closeConnection();
    }
}
