package parking_lot_management_system.views;

import java.util.List;
import java.util.ArrayList;

import parking_lot_management_system.models.*;
import parking_lot_management_system.models.enums.SpotType;
import parking_lot_management_system.database.*;

import javax.swing.*;
import java.awt.*;

public class ExitPanel extends JPanel {

    private ParkingLot lot;
    private JTextField plateField;
    private JTextField parkedSpot;
    private JComboBox<String> paymentMethodCombo;
    private JButton exitButton;
    private JTextArea outputArea;

    public ExitPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("License Plate:"));
        plateField = new JTextField();
        inputPanel.add(plateField);

        inputPanel.add(new JLabel("Parked Spot:"));
        parkedSpot = new JTextField();
        inputPanel.add(parkedSpot);

        inputPanel.add(new JLabel("Payment Method:"));
        paymentMethodCombo = new JComboBox<>(new String[] { "Cash", "Card" });
        inputPanel.add(paymentMethodCombo);

        add(inputPanel, BorderLayout.NORTH);

        // Exit button
        exitButton = new JButton("Exit Vehicle");
        add(exitButton, BorderLayout.CENTER);

        // Output area
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Button listener
        exitButton.addActionListener(e -> exitVehicle());
    }

    private void exitVehicle() {
        String plate = plateField.getText().trim();
        String method = (String) paymentMethodCombo.getSelectedItem();

        if (plate.isEmpty()) {
            outputArea.setText("Please enter license plate.");
            return;
        }

        if (lot.getActiveTickets().containsKey(plate)) {

            Ticket t = lot.getActiveTickets().get(plate);
            Vehicle v = lot.getActiveTickets().get(plate).getVehicle();

            t.setExitDatetimeAndDurationHours(java.time.LocalDateTime.now());

            List<Fine> newFines = Fine.generateFines(lot, t);

            // long minutes = java.time.Duration.between(t.getEntryDatetime(),
            // java.time.LocalDateTime.now()).toMinutes();
            // long hours = (minutes + 59) / 60;

            // double fee = v.getSpot().getType().getRate() * hours;
            // boolean reservedMisuse = v.getSpot().getType() == SpotType.RESERVED;
            // double fine = lot.getFineScheme().calculateFine(hours, reservedMisuse);
            // double total = fee + fine;
            // TODO: display fine and sum total

            // Capture spotId before freeing
            String spotId = t.getParkingSpot().getSpotId();

            // Exit vehicle
            t.getParkingSpot().setOccupied(false);
            lot.getActiveTickets().remove(plate);

            // Save revenue
            // DatabaseManager.saveRevenue(total);

            // Show popup receipt
            // Receipt.generateExit(plate, hours, fee, fine, total, method, spotId);

            outputArea.setText("Vehicle exited successfully.");

        } else {
            outputArea.setText("Vehicle not found in parking lot.");
        }
    }
}
