package parking_lot_management_system.views;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import parking_lot_management_system.controllers.FineService;
import parking_lot_management_system.database.DatabaseManager;
import parking_lot_management_system.models.*;

import javax.swing.*;
import java.awt.*;

public class ExitPanel extends JPanel {

    private ParkingLot lot;
    private JTextField plateField;
    private JButton searchButton;
    private JTextArea billArea;
    private JComboBox<String> paymentMethodCombo;
    private JButton exitButton;

    public ExitPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        inputPanel.add(new JLabel("License Plate:"));
        plateField = new JTextField();
        inputPanel.add(plateField);

        inputPanel.add(new JLabel("Payment Method:"));
        paymentMethodCombo = new JComboBox<>(new String[] { "Cash", "Card" });
        inputPanel.add(paymentMethodCombo);

        add(inputPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        searchButton = new JButton("Search Vehicle");
        searchButton.addActionListener(e -> searchVehicle());
        buttonPanel.add(searchButton);

        exitButton = new JButton("Process Exit & Payment");
        exitButton.setEnabled(false);
        exitButton.addActionListener(e -> exitVehicle());
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Bill display area
        billArea = new JTextArea(20, 60);
        billArea.setEditable(false);
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(billArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Billing Information"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void searchVehicle() {
        String plate = plateField.getText().trim().toUpperCase();

        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter license plate number.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if vehicle is parked
        if (!lot.getActiveTickets().containsKey(plate)) {
            billArea.setText("Vehicle " + plate + " not found in parking lot.\n\n" +
                    "The vehicle is either not parked here or has already exited.");
            exitButton.setEnabled(false);
            return;
        }

        // Get ticket and calculate bill
        Ticket ticket = lot.getActiveTickets().get(plate);
        Vehicle vehicle = ticket.getVehicle();
        ParkingSpot spot = ticket.getParkingSpot();

        // Set exit time and calculate duration
        ticket.setExitDatetimeAndDurationHours(java.time.LocalDateTime.now());
        int durationHours = ticket.getDurationHours();

        // Calculate parking fee
        BigDecimal hourlyRate = spot.getSpotType().getHourlyRate();

        // Special handling for handicapped vehicles in handicapped spots
        if (vehicle instanceof Handicapped
                && spot.getSpotType() == parking_lot_management_system.models.enums.SpotType.HANDICAPPED) {
            hourlyRate = BigDecimal.ZERO; // Free for handicapped in handicapped spots
        }

        BigDecimal parkingFee = hourlyRate.multiply(BigDecimal.valueOf(durationHours));

        // Check for new fines (overstay or reserved spot misuse)
        List<Fine> newFines = Fine.generateFines(lot, ticket);

        // Get unpaid fines from previous parkings
        List<Fine> unpaidFines = FineService.getUnpaidFines(vehicle);

        // Combine all fines
        BigDecimal totalFineAmount = BigDecimal.ZERO;
        for (Fine fine : unpaidFines) {
            totalFineAmount = totalFineAmount.add(fine.getAmount());
        }
        for (Fine fine : newFines) {
            totalFineAmount = totalFineAmount.add(fine.getAmount());
        }

        // Calculate total
        BigDecimal totalAmount = parkingFee.add(totalFineAmount);

        // Display bill
        StringBuilder bill = new StringBuilder();
        bill.append("========== PARKING BILL ==========\n");
        bill.append("Vehicle: ").append(plate).append("\n");
        bill.append("Parking Spot: ").append(spot.getSpotId()).append("\n");
        bill.append("Spot Type: ").append(spot.getSpotType().getName()).append("\n");
        bill.append("-----------------------------------\n");
        bill.append("Entry Time: ").append(ticket.getEntryDatetime()).append("\n");
        bill.append("Exit Time: ").append(ticket.getExitDatetime()).append("\n");
        bill.append("Duration: ").append(durationHours).append(" hour(s)\n");
        bill.append("-----------------------------------\n");
        bill.append("Parking Fee Calculation:\n");
        bill.append("  ").append(durationHours).append(" hour(s) × RM ")
                .append(String.format("%.2f", hourlyRate)).append("/hour\n");
        bill.append("Parking Fee: RM ").append(String.format("%.2f", parkingFee)).append("\n");

        if (!newFines.isEmpty()) {
            bill.append("\n--- NEW FINES ---\n");
            for (Fine fine : newFines) {
                bill.append(fine.getDescription()).append("\n");
                bill.append("  Amount: RM ").append(String.format("%.2f", fine.getAmount())).append("\n");
            }
        }

        if (!unpaidFines.isEmpty()) {
            bill.append("\n--- UNPAID FINES FROM PREVIOUS VISITS ---\n");
            for (Fine fine : unpaidFines) {
                bill.append("Fine #").append(fine.getFineId()).append(": ")
                        .append(fine.getFineType()).append("\n");
                bill.append("  Amount: RM ").append(String.format("%.2f", fine.getAmount())).append("\n");
            }
        }

        if (totalFineAmount.compareTo(BigDecimal.ZERO) > 0) {
            bill.append("\nTotal Fines: RM ").append(String.format("%.2f", totalFineAmount)).append("\n");
        }

        bill.append("-----------------------------------\n");
        bill.append("TOTAL AMOUNT DUE: RM ").append(String.format("%.2f", totalAmount)).append("\n");
        bill.append("===================================\n");

        if (durationHours > 24) {
            bill.append("\n⚠ WARNING: Vehicle exceeded maximum parking duration!\n");
        }

        billArea.setText(bill.toString());
        exitButton.setEnabled(true);
    }

    private void exitVehicle() {
        String plate = plateField.getText().trim().toUpperCase();
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();

        if (plate.isEmpty() || !lot.getActiveTickets().containsKey(plate)) {
            JOptionPane.showMessageDialog(this,
                    "Please search for a valid vehicle first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get ticket
        Ticket ticket = lot.getActiveTickets().get(plate);
        Vehicle vehicle = ticket.getVehicle();
        ParkingSpot spot = ticket.getParkingSpot();

        // Ensure exit time is set
        if (ticket.getExitDatetime() == null) {
            ticket.setExitDatetimeAndDurationHours(java.time.LocalDateTime.now());
        }

        int durationHours = ticket.getDurationHours();

        // Calculate parking fee
        BigDecimal hourlyRate = spot.getSpotType().getHourlyRate();

        // Special handling for handicapped
        if (vehicle instanceof Handicapped
                && spot.getSpotType() == parking_lot_management_system.models.enums.SpotType.HANDICAPPED) {
            hourlyRate = BigDecimal.ZERO;
        }

        BigDecimal parkingFee = hourlyRate.multiply(BigDecimal.valueOf(durationHours));

        // Get all fines
        List<Fine> newFines = Fine.generateFines(lot, ticket);
        List<Fine> unpaidFines = FineService.getUnpaidFines(vehicle);

        // Calculate total fine amount
        BigDecimal totalFineAmount = BigDecimal.ZERO;
        for (Fine fine : unpaidFines) {
            totalFineAmount = totalFineAmount.add(fine.getAmount());
        }
        for (Fine fine : newFines) {
            totalFineAmount = totalFineAmount.add(fine.getAmount());
        }

        // Create payment
        Payment payment = new Payment(ticket, parkingFee, totalFineAmount, paymentMethod);
        ticket.setPayment(payment);

        try {
            // Save new fines to database
            FineService.saveFines(newFines, ticket.getTicketId());

            // Mark all fines as paid
            unpaidFines.addAll(newFines);
            FineService.markFinesAsPaid(unpaidFines);

            // Save payment
            DatabaseManager.savePayment(payment);

            // Update ticket in database
            DatabaseManager.updateTicketOnExit(ticket);

            // Save revenue
            DatabaseManager.saveRevenue(parkingFee.doubleValue(), "PARKING");
            if (totalFineAmount.compareTo(BigDecimal.ZERO) > 0) {
                DatabaseManager.saveRevenue(totalFineAmount.doubleValue(), "FINE");
            }

            // Release spot
            lot.releaseSpot(vehicle.getVehicleId());

            // Show receipt
            String receipt = payment.generateReceipt();
            billArea.setText(receipt);

            JOptionPane.showMessageDialog(this,
                    "Payment processed successfully!\nVehicle may now exit.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Reset form
            plateField.setText("");
            exitButton.setEnabled(false);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error processing exit: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}