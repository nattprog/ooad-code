package parking_lot_management_system.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import parking_lot_management_system.database.DatabaseManager;
import parking_lot_management_system.models.*;

/**
 * Service class for fine management operations
 */
public class FineService {

    /**
     * Get all unpaid fines for a vehicle
     */
    public static List<Fine> getUnpaidFines(Vehicle vehicle) {
        try {
            return DatabaseManager.getUnpaidFines(vehicle.getVehicleId());
        } catch (SQLException e) {
            System.err.println("Error retrieving unpaid fines: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Calculate total unpaid fine amount for a vehicle
     */
    public static BigDecimal getTotalUnpaidFineAmount(Vehicle vehicle) {
        List<Fine> fines = getUnpaidFines(vehicle);
        BigDecimal total = BigDecimal.ZERO;

        for (Fine fine : fines) {
            total = total.add(fine.getAmount());
        }

        return total;
    }

    /**
     * Save fines to database
     */
    public static void saveFines(List<Fine> fines, String ticketId) {
        for (Fine fine : fines) {
            try {
                DatabaseManager.saveFine(fine, ticketId);
            } catch (SQLException e) {
                System.err.println("Error saving fine: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Mark fines as paid
     */
    public static void markFinesAsPaid(List<Fine> fines) {
        try {
            DatabaseManager.markFinesAsPaid(fines);
        } catch (SQLException e) {
            System.err.println("Error marking fines as paid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get formatted fine report for display
     */
    public static String generateFineReport(List<Fine> fines) {
        if (fines.isEmpty()) {
            return "No unpaid fines.";
        }

        StringBuilder report = new StringBuilder();
        report.append("========== UNPAID FINES ==========\n");

        BigDecimal total = BigDecimal.ZERO;
        for (Fine fine : fines) {
            report.append("Fine ID: ").append(fine.getFineId()).append("\n");
            report.append("Type: ").append(fine.getFineType()).append("\n");
            report.append("Description: ").append(fine.getDescription()).append("\n");
            report.append("Amount: RM ").append(String.format("%.2f", fine.getAmount())).append("\n");
            report.append("-----------------------------------\n");
            total = total.add(fine.getAmount());
        }

        report.append("Total Fine Amount: RM ").append(String.format("%.2f", total)).append("\n");
        report.append("==================================\n");

        return report.toString();
    }
}