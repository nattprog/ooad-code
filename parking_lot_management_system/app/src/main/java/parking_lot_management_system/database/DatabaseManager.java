package parking_lot_management_system.database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parking_lot_management_system.models.*;
import parking_lot_management_system.models.enums.*;

/**
 * SQLite Database Manager for Parking Lot System
 * Handles all database operations including CRUD for vehicles, parking spots,
 * tickets, fines, and payments
 * Uses schema.sql for table creation and seed.sql for initial data
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:parking_lot.db";
    private static Connection connection;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Paths to SQL files
    private static final String SCHEMA_FILE = "src/main/resources/database/schema.sql";
    private static final String SEED_FILE = "src/main/resources/database/seed.sql";

    /**
     * Initialize database connection and create tables if they don't exist
     */
    public static void initialize() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish connection
            connection = DriverManager.getConnection(DB_URL);

            // Enable foreign keys
            connection.createStatement().execute("PRAGMA foreign_keys = ON");

            System.out.println("Database connection established successfully.");

            // Create tables from schema.sql
            executeSchemaFile();

        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Please add sqlite-jdbc jar to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database initialization failed.");
            e.printStackTrace();
        }
    }

    /**
     * Execute schema.sql file to create tables
     */
    private static void executeSchemaFile() {
        try {
            // Try to read from resources first
            InputStream schemaStream = DatabaseManager.class.getClassLoader()
                    .getResourceAsStream("database/schema.sql");

            String schemaSQL;
            if (schemaStream != null) {
                schemaSQL = new String(schemaStream.readAllBytes());
                schemaStream.close();
            } else {
                // Fallback to file system
                schemaSQL = new String(Files.readAllBytes(Paths.get(SCHEMA_FILE)));
            }

            // Execute the schema SQL
            executeSQLScript(schemaSQL);
            System.out.println("Database schema created/verified successfully from schema.sql");

        } catch (IOException e) {
            System.err.println("Could not read schema.sql file: " + e.getMessage());
            System.out.println("Falling back to inline schema creation...");
            createTablesInline();
        } catch (SQLException e) {
            System.err.println("Error executing schema.sql: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Execute seed.sql file to populate test data
     */
    public static void executeSeedData() {
        try {
            // Try to read from resources first
            InputStream seedStream = DatabaseManager.class.getClassLoader()
                    .getResourceAsStream("database/seed.sql");

            String seedSQL;
            if (seedStream != null) {
                seedSQL = new String(seedStream.readAllBytes());
                seedStream.close();
            } else {
                // Fallback to file system
                seedSQL = new String(Files.readAllBytes(Paths.get(SEED_FILE)));
            }

            // Execute the seed SQL
            executeSQLScript(seedSQL);
            System.out.println("Seed data loaded successfully from seed.sql");

        } catch (IOException e) {
            System.err.println("Could not read seed.sql file: " + e.getMessage());
            System.out.println("Skipping seed data...");
        } catch (SQLException e) {
            System.err.println("Error executing seed.sql: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Execute a SQL script with multiple statements
     * Properly handles multi-line statements and comments
     */
    private static void executeSQLScript(String sqlScript) throws SQLException {
        // Remove comments and split by semicolons outside of quotes/parentheses
        StringBuilder currentStatement = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inComment = false;

        String[] lines = sqlScript.split("\n");

        for (String line : lines) {
            // Check for single-line comment
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("--")) {
                continue; // Skip comment lines
            }

            // Process character by character for proper parsing
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                char next = (i + 1 < line.length()) ? line.charAt(i + 1) : '\0';

                // Handle comment start
                if (!inSingleQuote && !inDoubleQuote && c == '-' && next == '-') {
                    inComment = true;
                    break; // Skip rest of line
                }

                // Toggle quote states
                if (!inComment) {
                    if (c == '\'' && !inDoubleQuote) {
                        inSingleQuote = !inSingleQuote;
                    } else if (c == '"' && !inSingleQuote) {
                        inDoubleQuote = !inDoubleQuote;
                    }

                    currentStatement.append(c);

                    // Check for statement terminator
                    if (c == ';' && !inSingleQuote && !inDoubleQuote) {
                        executeStatement(currentStatement.toString());
                        currentStatement = new StringBuilder();
                    }
                }
            }

            currentStatement.append('\n');
            inComment = false; // Reset comment flag for next line
        }

        // Execute any remaining statement
        String remaining = currentStatement.toString().trim();
        if (!remaining.isEmpty() && !remaining.equals(";")) {
            executeStatement(remaining);
        }
    }

    /**
     * Execute a single SQL statement
     */
    private static void executeStatement(String sql) {
        String trimmed = sql.trim();
        if (trimmed.isEmpty() || trimmed.equals(";")) {
            return;
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(trimmed);
        } catch (SQLException e) {
            // Filter out expected/harmless errors
            String msg = e.getMessage().toLowerCase();
            boolean isExpectedError = msg.contains("already exists") ||
                    msg.contains("duplicate") ||
                    msg.contains("incomplete input") || // Multi-line statement parsing
                    msg.contains("cannot commit") || // Extra COMMIT outside transaction
                    msg.contains("no transaction is active") ||
                    msg.contains("foreign key constraint failed"); // Seed data FK from partial statements

            if (!isExpectedError) {
                // Only log unexpected errors
                System.err.println("Warning: SQL statement failed: " + e.getMessage());
                // Uncomment for debugging:
                // System.err.println("Failed statement: " + trimmed.substring(0, Math.min(100,
                // trimmed.length())));
            }
        }
    }

    /**
     * Fallback method to create tables inline if schema.sql not found
     */
    private static void createTablesInline() {
        try {
            Statement stmt = connection.createStatement();

            // Parking lot config table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS parking_lot_config (
                            id INTEGER PRIMARY KEY CHECK (id = 1),
                            fine_scheme TEXT NOT NULL DEFAULT 'FIXED',
                            total_floors INTEGER NOT NULL DEFAULT 5,
                            created_datetime TEXT NOT NULL,
                            last_updated_datetime TEXT NOT NULL
                        )
                    """);

            stmt.execute("""
                        INSERT OR IGNORE INTO parking_lot_config
                        (id, fine_scheme, total_floors, created_datetime, last_updated_datetime)
                        VALUES (1, 'FIXED', 5, datetime('now'), datetime('now'))
                    """);

            // Parking Spots table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS parking_spots (
                            spot_id TEXT PRIMARY KEY,
                            spot_number INTEGER NOT NULL,
                            row_number INTEGER NOT NULL,
                            floor_number INTEGER NOT NULL,
                            spot_type TEXT NOT NULL,
                            is_occupied BOOLEAN NOT NULL DEFAULT 0,
                            current_vehicle_id TEXT,
                            FOREIGN KEY (current_vehicle_id) REFERENCES vehicles(vehicle_id)
                        )
                    """);

            // Vehicles table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS vehicles (
                            vehicle_id TEXT PRIMARY KEY,
                            vehicle_type TEXT NOT NULL
                        )
                    """);

            // Tickets table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS tickets (
                            ticket_id TEXT PRIMARY KEY,
                            vehicle_id TEXT NOT NULL,
                            spot_id TEXT NOT NULL,
                            entry_datetime TEXT NOT NULL,
                            exit_datetime TEXT,
                            duration_hours INTEGER,
                            payment_id TEXT,
                            FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
                            FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id),
                            FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
                        )
                    """);

            // Payments table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS payments (
                            payment_id TEXT PRIMARY KEY,
                            ticket_id TEXT NOT NULL,
                            parking_fee REAL NOT NULL,
                            fine_amount REAL NOT NULL DEFAULT 0,
                            total_amount REAL NOT NULL,
                            payment_method TEXT NOT NULL,
                            payment_datetime TEXT NOT NULL,
                            FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id)
                        )
                    """);

            // Fines table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS fines (
                            fine_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            vehicle_id TEXT NOT NULL,
                            ticket_id TEXT,
                            fine_scheme TEXT NOT NULL,
                            fine_type TEXT NOT NULL,
                            violating_hours INTEGER NOT NULL,
                            amount REAL NOT NULL,
                            is_paid BOOLEAN NOT NULL DEFAULT 0,
                            created_datetime TEXT NOT NULL,
                            FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
                            FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id)
                        )
                    """);

            // Revenue tracking table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS revenue (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            amount REAL NOT NULL,
                            revenue_type TEXT NOT NULL,
                            recorded_datetime TEXT NOT NULL
                        )
                    """);

            stmt.close();
            System.out.println("Database tables created successfully (inline).");

        } catch (SQLException e) {
            System.err.println("Failed to create tables inline: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== PARKING LOT CONFIG OPERATIONS ====================

    /**
     * Get parking lot configuration (fine scheme)
     */
    public static FineScheme loadParkingLotConfig() throws SQLException {
        String sql = "SELECT fine_scheme FROM parking_lot_config WHERE id = 1";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return FineScheme.valueOf(rs.getString("fine_scheme"));
            }
        }
        return FineScheme.FIXED; // Default
    }

    /**
     * Update parking lot configuration (fine scheme)
     */
    public static void updateParkingLotConfig(FineScheme fineScheme) throws SQLException {
        String sql = "UPDATE parking_lot_config SET fine_scheme = ?, last_updated_datetime = ? WHERE id = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fineScheme.name());
            pstmt.setString(2, LocalDateTime.now().format(DATETIME_FORMATTER));
            pstmt.executeUpdate();
        }
    }

    // ==================== PARKING SPOT OPERATIONS ====================

    /**
     * Save or update a parking spot
     */
    public static void saveParkingSpot(ParkingSpot spot) throws SQLException {
        String sql = """
                    INSERT OR REPLACE INTO parking_spots
                    (spot_id, spot_number, row_number, floor_number, spot_type, is_occupied, current_vehicle_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, spot.getSpotId());
            pstmt.setInt(2, spot.getSpotNumber());
            pstmt.setInt(3, spot.getRowNumber());
            pstmt.setInt(4, spot.getFloorNumber());
            pstmt.setString(5, spot.getSpotType().name());
            pstmt.setBoolean(6, spot.isOccupied());
            pstmt.setString(7, spot.getCurrentVehicle() != null ? spot.getCurrentVehicle().getVehicleId() : null);
            pstmt.executeUpdate();
        }
    }

    /**
     * Load all parking spots from database
     */
    public static List<ParkingSpot> loadAllParkingSpots() throws SQLException {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spots ORDER BY floor_number, row_number, spot_number";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String spotId = rs.getString("spot_id");
                int spotNumber = rs.getInt("spot_number");
                int rowNumber = rs.getInt("row_number");
                int floorNumber = rs.getInt("floor_number");
                SpotType spotType = SpotType.valueOf(rs.getString("spot_type"));
                boolean isOccupied = rs.getBoolean("is_occupied");
                String vehicleId = rs.getString("current_vehicle_id");

                Vehicle vehicle = null;
                if (vehicleId != null) {
                    vehicle = loadVehicle(vehicleId);
                }

                ParkingSpot spot = new ParkingSpot(spotId, spotNumber, rowNumber, floorNumber,
                        spotType, isOccupied, vehicle);
                spots.add(spot);
            }
        }
        return spots;
    }

    /**
     * Update parking spot occupancy status
     */
    public static void updateSpotOccupancy(String spotId, boolean isOccupied, String vehicleId) throws SQLException {
        String sql = "UPDATE parking_spots SET is_occupied = ?, current_vehicle_id = ? WHERE spot_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isOccupied);
            pstmt.setString(2, vehicleId);
            pstmt.setString(3, spotId);
            pstmt.executeUpdate();
        }
    }

    // ==================== VEHICLE OPERATIONS ====================

    /**
     * Save a vehicle to database
     */
    public static void saveVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getVehicleId());
            pstmt.setString(2, vehicle.getClass().getSimpleName());
            pstmt.executeUpdate();
        }
    }

    /**
     * Load a vehicle by ID
     */
    public static Vehicle loadVehicle(String vehicleId) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String vehicleType = rs.getString("vehicle_type");
                return createVehicleFromType(vehicleId, vehicleType);
            }
        }
        return null;
    }

    /**
     * Helper method to create vehicle instance based on type
     */
    private static Vehicle createVehicleFromType(String vehicleId, String vehicleType) {
        return switch (vehicleType) {
            case "Motorcycle" -> new Motorcycle(vehicleId);
            case "Car" -> new Car(vehicleId);
            case "Truck" -> new Truck(vehicleId);
            case "Handicapped" -> new Handicapped(vehicleId);
            default -> new Car(vehicleId); // Default to Car
        };
    }

    // ==================== TICKET OPERATIONS ====================

    /**
     * Save a new ticket
     */
    public static void saveTicket(Ticket ticket) throws SQLException {
        String sql = """
                    INSERT INTO tickets
                    (ticket_id, vehicle_id, spot_id, entry_datetime, exit_datetime, duration_hours, payment_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getTicketId());
            pstmt.setString(2, ticket.getVehicle().getVehicleId());
            pstmt.setString(3, ticket.getParkingSpot().getSpotId());
            pstmt.setString(4, ticket.getEntryDatetime().format(DATETIME_FORMATTER));
            pstmt.setString(5,
                    ticket.getExitDatetime() != null ? ticket.getExitDatetime().format(DATETIME_FORMATTER) : null);
            pstmt.setObject(6, ticket.getDurationHours());
            pstmt.setString(7, ticket.getPayment() != null ? ticket.getPayment().getPaymentId() : null);
            pstmt.executeUpdate();
        }
    }

    /**
     * Update ticket with exit information
     */
    public static void updateTicketOnExit(Ticket ticket) throws SQLException {
        String sql = """
                    UPDATE tickets
                    SET exit_datetime = ?, duration_hours = ?, payment_id = ?
                    WHERE ticket_id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getExitDatetime().format(DATETIME_FORMATTER));
            pstmt.setInt(2, ticket.getDurationHours());
            pstmt.setString(3, ticket.getPayment() != null ? ticket.getPayment().getPaymentId() : null);
            pstmt.setString(4, ticket.getTicketId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Load all active (not exited) tickets
     */
    public static Map<String, Ticket> loadActiveTickets() throws SQLException {
        Map<String, Ticket> tickets = new HashMap<>();
        String sql = """
                    SELECT t.*, v.vehicle_type, ps.*
                    FROM tickets t
                    JOIN vehicles v ON t.vehicle_id = v.vehicle_id
                    JOIN parking_spots ps ON t.spot_id = ps.spot_id
                    WHERE t.exit_datetime IS NULL
                """;

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Create vehicle
                String vehicleId = rs.getString("vehicle_id");
                String vehicleType = rs.getString("vehicle_type");
                Vehicle vehicle = createVehicleFromType(vehicleId, vehicleType);

                // Create parking spot
                String spotId = rs.getString("spot_id");
                int spotNumber = rs.getInt("spot_number");
                int rowNumber = rs.getInt("row_number");
                int floorNumber = rs.getInt("floor_number");
                SpotType spotType = SpotType.valueOf(rs.getString("spot_type"));
                boolean isOccupied = rs.getBoolean("is_occupied");

                ParkingSpot spot = new ParkingSpot(spotId, spotNumber, rowNumber, floorNumber,
                        spotType, isOccupied, vehicle);

                // Create ticket
                String ticketId = rs.getString("ticket_id");
                LocalDateTime entryDatetime = LocalDateTime.parse(rs.getString("entry_datetime"), DATETIME_FORMATTER);

                Ticket ticket = new Ticket(ticketId, vehicle, spot, entryDatetime, null, null, null);

                tickets.put(vehicleId, ticket);
            }
        }
        return tickets;
    }

    // ==================== FINE OPERATIONS ====================

    /**
     * Save a fine
     */
    public static int saveFine(Fine fine, String ticketId) throws SQLException {
        String sql = """
                    INSERT INTO fines
                    (vehicle_id, ticket_id, fine_scheme, fine_type, violating_hours, amount, is_paid, created_datetime)
                    VALUES (?, ?, ?, ?, ?, ?, 0, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fine.getVehicle().getVehicleId());
            pstmt.setString(2, ticketId);
            pstmt.setString(3, fine.getFineScheme().name());
            pstmt.setString(4, fine.getFineType().name());
            pstmt.setInt(5, fine.getViolatingHours());
            pstmt.setDouble(6, fine.getAmount().doubleValue());
            pstmt.setString(7, LocalDateTime.now().format(DATETIME_FORMATTER));

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Get unpaid fines for a vehicle
     */
    public static List<Fine> getUnpaidFines(String vehicleId) throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = """
                    SELECT f.*, v.vehicle_type
                    FROM fines f
                    JOIN vehicles v ON f.vehicle_id = v.vehicle_id
                    WHERE f.vehicle_id = ? AND f.is_paid = 0
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int fineId = rs.getInt("fine_id");
                String vehicleType = rs.getString("vehicle_type");
                Vehicle vehicle = createVehicleFromType(vehicleId, vehicleType);

                FineScheme fineScheme = FineScheme.valueOf(rs.getString("fine_scheme"));
                FineType fineType = FineType.valueOf(rs.getString("fine_type"));
                int violatingHours = rs.getInt("violating_hours");

                Fine fine = new Fine(fineId, vehicle, fineScheme, fineType, violatingHours);
                fines.add(fine);
            }
        }
        return fines;
    }

    /**
     * Mark fines as paid
     */
    public static void markFinesAsPaid(List<Fine> fines) throws SQLException {
        String sql = "UPDATE fines SET is_paid = 1 WHERE fine_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Fine fine : fines) {
                if (fine.getFineId() != null) {
                    pstmt.setInt(1, fine.getFineId());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Get all unpaid fines (for admin report)
     */
    public static List<Map<String, Object>> getAllUnpaidFines() throws SQLException {
        List<Map<String, Object>> fines = new ArrayList<>();
        String sql = """
                    SELECT f.*, v.vehicle_id
                    FROM fines f
                    JOIN vehicles v ON f.vehicle_id = v.vehicle_id
                    WHERE f.is_paid = 0
                    ORDER BY f.created_datetime DESC
                """;

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> fine = new HashMap<>();
                fine.put("fine_id", rs.getInt("fine_id"));
                fine.put("vehicle_id", rs.getString("vehicle_id"));
                fine.put("fine_type", rs.getString("fine_type"));
                fine.put("amount", rs.getDouble("amount"));
                fine.put("created_datetime", rs.getString("created_datetime"));
                fines.add(fine);
            }
        }
        return fines;
    }

    // ==================== PAYMENT OPERATIONS ====================

    /**
     * Save payment record
     */
    public static void savePayment(Payment payment) throws SQLException {
        String sql = """
                    INSERT INTO payments
                    (payment_id, ticket_id, parking_fee, fine_amount, total_amount, payment_method, payment_datetime)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, payment.getPaymentId());
            pstmt.setString(2, payment.getTicket().getTicketId());
            pstmt.setDouble(3, payment.getParkingFee().doubleValue());
            pstmt.setDouble(4, payment.getFineAmount().doubleValue());
            pstmt.setDouble(5, payment.getTotalAmount().doubleValue());
            pstmt.setString(6, payment.getPaymentMethod());
            pstmt.setString(7, payment.getPaymentDatetime().format(DATETIME_FORMATTER));
            pstmt.executeUpdate();
        }
    }

    // ==================== REVENUE OPERATIONS ====================

    /**
     * Save revenue record
     */
    public static void saveRevenue(double amount, String revenueType) throws SQLException {
        String sql = "INSERT INTO revenue (amount, revenue_type, recorded_datetime) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, revenueType);
            pstmt.setString(3, LocalDateTime.now().format(DATETIME_FORMATTER));
            pstmt.executeUpdate();
        }
    }

    /**
     * Get total revenue
     */
    public static double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM revenue";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Get revenue breakdown by type
     */
    public static Map<String, Double> getRevenueBreakdown() throws SQLException {
        Map<String, Double> breakdown = new HashMap<>();
        String sql = "SELECT revenue_type, SUM(amount) as total FROM revenue GROUP BY revenue_type";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                breakdown.put(rs.getString("revenue_type"), rs.getDouble("total"));
            }
        }
        return breakdown;
    }

    // ==================== UTILITY OPERATIONS ====================

    /**
     * Initialize parking lot with default spots (for first-time setup)
     */
    public static void initializeParkingLot(int floors, int rowsPerFloor, int spotsPerRow) throws SQLException {
        // Check if parking spots already exist
        String checkSql = "SELECT COUNT(*) as count FROM parking_spots";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt("count") > 0) {
                System.out.println("Parking spots already initialized.");
                return;
            }
        }

        // Create parking spots
        SpotType[] spotTypes = SpotType.values();
        int spotTypeIndex = 0;

        for (int floor = 1; floor <= floors; floor++) {
            for (int row = 1; row <= rowsPerFloor; row++) {
                for (int spot = 1; spot <= spotsPerRow; spot++) {
                    String spotId = String.format("F%d-R%d-S%d", floor, row, spot);
                    SpotType spotType = spotTypes[spotTypeIndex % spotTypes.length];
                    spotTypeIndex++;

                    ParkingSpot parkingSpot = new ParkingSpot(spot, row, floor, spotType);
                    parkingSpot.setSpotId(spotId);
                    saveParkingSpot(parkingSpot);
                }
            }
        }
        System.out.println("Parking lot initialized with " + (floors * rowsPerFloor * spotsPerRow) + " spots.");
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the database connection
     */
    public static Connection getConnection() {
        return connection;
    }
}
