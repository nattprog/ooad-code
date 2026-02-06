package seminar_manager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class DatabaseManager {
  private static DatabaseManager instance;
  private Connection connection;
  private static final String DB_URL = "jdbc:sqlite:seminar_management.db";

  private DatabaseManager() {
    try {
      // Load SQLite JDBC driver
      Class.forName("org.sqlite.JDBC");
      // Create connection
      connection = DriverManager.getConnection(DB_URL);
      System.out.println("Database connection established.");

      // Enable foreign keys
      Statement stmt = connection.createStatement();
      stmt.execute("PRAGMA foreign_keys = ON;");
      stmt.close();

    } catch (ClassNotFoundException e) {
      System.err.println("SQLite JDBC driver not found.");
      e.printStackTrace();
    } catch (SQLException e) {
      System.err.println("Failed to connect to database.");
      e.printStackTrace();
    }
  }

  public static synchronized DatabaseManager getInstance() {
    if (instance == null) {
      instance = new DatabaseManager();
    }
    return instance;
  }

  public Connection getConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(DB_URL);
        // Re-enable foreign keys
        try (Statement stmt = connection.createStatement()) {
          stmt.execute("PRAGMA foreign_keys = ON;");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

  public void initializeDatabase() {
    try {
      executeSQLFileFromResources("/database/schema.sql");
      System.out.println("Database schema initialized successfully.");
    } catch (IOException | SQLException e) {
      System.err.println("Failed to initialize database schema.");
      e.printStackTrace();
    }
  }

  public void seedDatabase() {
    try {
      executeSQLFileFromResources("/database/seed.sql");
      System.out.println("Database schema seeded successfully.");
    } catch (IOException | SQLException e) {
      System.err.println("Failed to initialize database schema.");
      e.printStackTrace();
    }
  }

  private void executeSQLFileFromResources(String resourcePath) throws IOException, SQLException {
    // Load from classpath resources instead of file system
    InputStream inputStream = getClass().getResourceAsStream(resourcePath);

    if (inputStream == null) {
      throw new IOException("Resource not found: " + resourcePath);
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder sql = new StringBuilder();
    String line;

    while ((line = reader.readLine()) != null) {
      // Skip comments and empty lines
      line = line.trim();
      if (line.isEmpty() || line.startsWith("--")) {
        continue;
      }
      sql.append(line).append(" ");
    }
    reader.close();
    inputStream.close();

    // Split by semicolon and execute each statement
    String[] statements = sql.toString().split(";");
    Statement stmt = connection.createStatement();

    for (String statement : statements) {
      statement = statement.trim();
      if (!statement.isEmpty()) {
        stmt.execute(statement);
      }
    }
    stmt.close();
  }

  public void closeConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        System.out.println("Database connection closed.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}