package parking_lot_management_system.views;

import parking_lot_management_system.database.DatabaseManager;
import parking_lot_management_system.models.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;

public class ReportPanel extends JPanel {

    private ParkingLot lot;
    private JTextArea reportArea;
    private JButton currentVehiclesButton;
    private JButton occupancyButton;
    private JButton revenueButton;
    private JButton finesButton;
    private JButton allReportsButton;

    public ReportPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Report Options"));

        currentVehiclesButton = new JButton("Current Vehicles");
        currentVehiclesButton.addActionListener(e -> showCurrentVehiclesReport());
        buttonPanel.add(currentVehiclesButton);

        occupancyButton = new JButton("Occupancy Report");
        occupancyButton.addActionListener(e -> showOccupancyReport());
        buttonPanel.add(occupancyButton);

        revenueButton = new JButton("Revenue Report");
        revenueButton.addActionListener(e -> showRevenueReport());
        buttonPanel.add(revenueButton);

        finesButton = new JButton("Outstanding Fines");
        finesButton.addActionListener(e -> showFinesReport());
        buttonPanel.add(finesButton);

        allReportsButton = new JButton("All Reports");
        allReportsButton.addActionListener(e -> showAllReports());
        buttonPanel.add(allReportsButton);

        add(buttonPanel, BorderLayout.WEST);

        // Report display area
        reportArea = new JTextArea(25, 60);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Report Details"));
        add(scrollPane, BorderLayout.CENTER);

        // Show all reports by default
        showAllReports();
    }

    private void showCurrentVehiclesReport() {
        StringBuilder report = new StringBuilder();
        report.append("========== CURRENT VEHICLES ==========\n\n");

        Map<String, Ticket> tickets = lot.getActiveTickets();

        if (tickets.isEmpty()) {
            report.append("No vehicles currently parked.\n");
        } else {
            report.append(String.format("%-15s %-15s %-15s %-20s%n",
                    "License Plate", "Vehicle Type", "Parking Spot", "Entry Time"));
            report.append("-".repeat(70)).append("\n");

            tickets.forEach((plate, ticket) -> {
                String vehicleType = ticket.getVehicle().getClass().getSimpleName();
                String spotId = ticket.getParkingSpot().getSpotId();
                String entryTime = ticket.getEntryDatetime().toString();

                report.append(String.format("%-15s %-15s %-15s %-20s%n",
                        plate, vehicleType, spotId, entryTime));
            });

            report.append("\nTotal Vehicles: ").append(tickets.size()).append("\n");
        }

        report.append("======================================\n");
        reportArea.setText(report.toString());
    }

    private void showOccupancyReport() {
        StringBuilder report = new StringBuilder();
        report.append("========== OCCUPANCY REPORT ==========\n\n");

        // Overall statistics
        Map<String, Integer> stats = lot.getOccupancyStats();
        report.append("Overall Occupancy:\n");
        report.append("  Total Spots: ").append(stats.get("total")).append("\n");
        report.append("  Occupied: ").append(stats.get("occupied")).append("\n");
        report.append("  Available: ").append(stats.get("available")).append("\n");

        if (stats.get("total") > 0) {
            double occupancyRate = (stats.get("occupied") * 100.0) / stats.get("total");
            report.append("  Occupancy Rate: ").append(String.format("%.1f", occupancyRate)).append("%\n");
        }

        report.append("\n");
        report.append(String.format("%-10s %-15s %-15s %-15s%n",
                "Floor", "Total Spots", "Occupied", "Available"));
        report.append("-".repeat(60)).append("\n");

        // Per-floor statistics
        for (ParkingFloor floor : lot.getParkingFloors()) {
            long occupied = floor.getParkingSpots().stream()
                    .filter(ParkingSpot::isOccupied)
                    .count();
            long total = floor.getParkingSpots().size();
            long available = total - occupied;

            report.append(String.format("%-10d %-15d %-15d %-15d%n",
                    floor.getFloorNumber(), total, occupied, available));
        }

        report.append("\n");

        // Occupancy by spot type
        report.append("Occupancy by Spot Type:\n");
        Map<String, long[]> spotTypeStats = new java.util.HashMap<>();

        for (ParkingFloor floor : lot.getParkingFloors()) {
            for (ParkingSpot spot : floor.getParkingSpots()) {
                String typeName = spot.getSpotType().getName();
                spotTypeStats.putIfAbsent(typeName, new long[2]); // [total, occupied]
                spotTypeStats.get(typeName)[0]++;
                if (spot.isOccupied()) {
                    spotTypeStats.get(typeName)[1]++;
                }
            }
        }

        spotTypeStats.forEach((type, counts) -> {
            report.append(String.format("  %s: %d/%d occupied%n",
                    type, counts[1], counts[0]));
        });

        report.append("======================================\n");
        reportArea.setText(report.toString());
    }

    private void showRevenueReport() {
        StringBuilder report = new StringBuilder();
        report.append("========== REVENUE REPORT ==========\n\n");

        try {
            // Total revenue
            double totalRevenue = DatabaseManager.getTotalRevenue();
            report.append("Total Revenue: RM ").append(String.format("%.2f", totalRevenue)).append("\n\n");

            // Revenue breakdown
            Map<String, Double> breakdown = DatabaseManager.getRevenueBreakdown();

            if (!breakdown.isEmpty()) {
                report.append("Revenue Breakdown:\n");
                breakdown.forEach((type, amount) -> {
                    report.append(String.format("  %s: RM %.2f%n", type, amount));
                });
            } else {
                report.append("No revenue recorded yet.\n");
            }

        } catch (SQLException e) {
            report.append("Error retrieving revenue data: ").append(e.getMessage());
            e.printStackTrace();
        }

        report.append("\n====================================\n");
        reportArea.setText(report.toString());
    }

    private void showFinesReport() {
        StringBuilder report = new StringBuilder();
        report.append("========== OUTSTANDING FINES ==========\n\n");

        try {
            List<Map<String, Object>> fines = DatabaseManager.getAllUnpaidFines();

            if (fines.isEmpty()) {
                report.append("No outstanding fines.\n");
            } else {
                report.append(String.format("%-10s %-15s %-25s %-12s %-20s%n",
                        "Fine ID", "Vehicle", "Fine Type", "Amount", "Date"));
                report.append("-".repeat(85)).append("\n");

                double totalFines = 0;
                for (Map<String, Object> fine : fines) {
                    report.append(String.format("%-10d %-15s %-25s RM %-8.2f %-20s%n",
                            fine.get("fine_id"),
                            fine.get("vehicle_id"),
                            fine.get("fine_type"),
                            fine.get("amount"),
                            fine.get("created_datetime")));

                    totalFines += (Double) fine.get("amount");
                }

                report.append("-".repeat(85)).append("\n");
                report.append(String.format("Total Outstanding Fines: RM %.2f%n", totalFines));
            }

        } catch (SQLException e) {
            report.append("Error retrieving fines data: ").append(e.getMessage());
            e.printStackTrace();
        }

        report.append("\n=======================================\n");
        reportArea.setText(report.toString());
    }

    private void showAllReports() {
        StringBuilder allReports = new StringBuilder();

        // Current vehicles
        allReports.append("========== CURRENT VEHICLES ==========\n");
        Map<String, Ticket> tickets = lot.getActiveTickets();
        if (tickets.isEmpty()) {
            allReports.append("No vehicles currently parked.\n");
        } else {
            tickets.forEach((plate, ticket) -> {
                allReports.append(plate).append(" -> ")
                        .append(ticket.getVehicle().getClass().getSimpleName())
                        .append(" @ ").append(ticket.getParkingSpot().getSpotId()).append("\n");
            });
        }
        allReports.append("\n");

        // Occupancy summary
        allReports.append("========== OCCUPANCY SUMMARY ==========\n");
        Map<String, Integer> stats = lot.getOccupancyStats();
        allReports.append("Total Spots: ").append(stats.get("total")).append("\n");
        allReports.append("Occupied: ").append(stats.get("occupied")).append("\n");
        allReports.append("Available: ").append(stats.get("available")).append("\n");
        if (stats.get("total") > 0) {
            double rate = (stats.get("occupied") * 100.0) / stats.get("total");
            allReports.append("Occupancy Rate: ").append(String.format("%.1f%%", rate)).append("\n");
        }
        allReports.append("\n");

        // Revenue summary
        allReports.append("========== REVENUE SUMMARY ==========\n");
        try {
            double totalRevenue = DatabaseManager.getTotalRevenue();
            allReports.append("Total Revenue: RM ").append(String.format("%.2f", totalRevenue)).append("\n");

            Map<String, Double> breakdown = DatabaseManager.getRevenueBreakdown();
            breakdown.forEach((type, amount) -> {
                allReports.append("  ").append(type).append(": RM ")
                        .append(String.format("%.2f", amount)).append("\n");
            });
        } catch (SQLException e) {
            allReports.append("Error loading revenue: ").append(e.getMessage()).append("\n");
        }
        allReports.append("\n");

        // Outstanding fines summary
        allReports.append("========== OUTSTANDING FINES ==========\n");
        try {
            List<Map<String, Object>> fines = DatabaseManager.getAllUnpaidFines();
            if (fines.isEmpty()) {
                allReports.append("No outstanding fines.\n");
            } else {
                double totalFines = 0;
                for (Map<String, Object> fine : fines) {
                    totalFines += (Double) fine.get("amount");
                }
                allReports.append("Count: ").append(fines.size()).append("\n");
                allReports.append("Total Amount: RM ").append(String.format("%.2f", totalFines)).append("\n");
            }
        } catch (SQLException e) {
            allReports.append("Error loading fines: ").append(e.getMessage()).append("\n");
        }

        allReports.append("\n======================================\n");
        reportArea.setText(allReports.toString());
    }
}