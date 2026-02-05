package seminar;

import java.util.Scanner;

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

        Scanner myObj = new Scanner(System.in);
        System.out.println("Username:");
        String usern = myObj.nextLine();
        System.out.println("Password:");
        String pass = myObj.nextLine();
        System.out.println("Full name:");
        String fulln = myObj.nextLine();
        System.out.println("Email:");
        String emai = myObj.nextLine();
        System.out.println("Student:");
        String stud = myObj.nextLine();

        // Test creating a student
        Student student = new Student(usern, pass, fulln,
                emai, stud);
        StudentDAO studentDAO = new StudentDAO();
        boolean created = studentDAO.createStudent(student);

        if (created) {
            System.out.println("Student created successfully with ID: " + student.getUserId());
        } else {
            System.out.println("Failed to create student.");
        }

        // Close connection when done
        dbManager.closeConnection();
    }
}
