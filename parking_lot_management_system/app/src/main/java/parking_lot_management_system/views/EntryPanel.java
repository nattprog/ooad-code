package parking_lot_management_system.views;

import parking_lot_management_system.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EntryPanel extends JPanel {

    private ParkingLot lot;

    private JTextField plateField;
    private JComboBox<String> vehicleTypeCombo;
    private JButton searchButton;
    private JComboBox<String> spotCombo;
    private JButton parkButton;
    private JTextArea outputArea;

    private Vehicle currentVehicle;
    private List<ParkingSpot> availableSpots;

    public EntryPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("License Plate:"));
        plateField = new JTextField();
        inputPanel.add(plateField);

        inputPanel.add(new JLabel("Vehicle Type:"));
        vehicleTypeCombo = new JComboBox<>(new String[] { "Motorcycle", "Car", "Truck", "Handicapped" });
        inputPanel.add(vehicleTypeCombo);

        inputPanel.add(new JLabel("Available Spots:"));
        spotCombo = new JComboBox<>();
        spotCombo.setEnabled(false);
        inputPanel.add(spotCombo);

        add(inputPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        searchButton = new JButton("Search Available Spots");
        searchButton.addActionListener(e -> searchAvailableSpots());
        buttonPanel.add(searchButton);

        parkButton = new JButton("Park Vehicle");
        parkButton.setEnabled(false);
        parkButton.addActionListener(e -> parkVehicle());
        buttonPanel.add(parkButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Output area
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Parking Information"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void searchAvailableSpots() {
        String plate = plateField.getText().trim().toUpperCase();

        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a license plate number.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if vehicle is already parked
        if (lot.getActiveTickets().containsKey(plate)) {
            JOptionPane.showMessageDialog(this,
                    "Vehicle " + plate + " is already parked in the lot.",
                    "Already Parked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create vehicle based on type
        String type = (String) vehicleTypeCombo.getSelectedItem();
        currentVehicle = createVehicle(plate, type);

        // Search for available spots
        availableSpots = lot.getAvailableParkingSpots(currentVehicle);

        spotCombo.removeAllItems();

        if (availableSpots.isEmpty()) {
            outputArea.setText("No available spots found for " + type + " vehicles.\n\n" +
                    "Please try again later or contact parking management.");
            spotCombo.setEnabled(false);
            parkButton.setEnabled(false);
        } else {
            for (ParkingSpot spot : availableSpots) {
                String spotInfo = String.format("%s (%s) - RM %.2f/hour",
                        spot.getSpotId(),
                        spot.getSpotType().getName(),
                        spot.getSpotType().getHourlyRate());
                spotCombo.addItem(spotInfo);
            }

            spotCombo.setEnabled(true);
            parkButton.setEnabled(true);

            outputArea.setText("Found " + availableSpots.size() + " available spot(s) for " +
                    type + " vehicles.\n\n" +
                    "Please select a spot and click 'Park Vehicle'.");
        }
    }

    private void parkVehicle() {
        if (currentVehicle == null || availableSpots == null || availableSpots.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please search for available spots first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int index = spotCombo.getSelectedIndex();
        if (index < 0 || index >= availableSpots.size()) {
            JOptionPane.showMessageDialog(this,
                    "Invalid spot selection.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ParkingSpot selectedSpot = availableSpots.get(index);
        boolean success = lot.claimParkingSpot(selectedSpot, currentVehicle);

        if (success) {
            Ticket ticket = lot.getActiveTickets().get(currentVehicle.getVehicleId());

            // Display entry ticket
            StringBuilder ticketInfo = new StringBuilder();
            ticketInfo.append("========== PARKING TICKET ==========\n");
            ticketInfo.append("Ticket ID: ").append(ticket.getTicketId()).append("\n");
            ticketInfo.append("Vehicle: ").append(currentVehicle.getVehicleId()).append("\n");
            ticketInfo.append("Vehicle Type: ").append(currentVehicle.getClass().getSimpleName()).append("\n");
            ticketInfo.append("Parking Spot: ").append(selectedSpot.getSpotId()).append("\n");
            ticketInfo.append("Spot Type: ").append(selectedSpot.getSpotType().getName()).append("\n");
            ticketInfo.append("Hourly Rate: RM ")
                    .append(String.format("%.2f", selectedSpot.getSpotType().getHourlyRate())).append("\n");
            ticketInfo.append("Entry Time: ").append(ticket.getEntryDatetime()).append("\n");
            ticketInfo.append("====================================\n");
            ticketInfo.append("\nPlease keep this ticket for exit.\n");
            ticketInfo.append("Maximum parking duration: 24 hours.\n");
            ticketInfo.append("Overstaying will incur fines.");

            outputArea.setText(ticketInfo.toString());

            JOptionPane.showMessageDialog(this,
                    "Vehicle parked successfully!\nSpot: " + selectedSpot.getSpotId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Reset form
            plateField.setText("");
            vehicleTypeCombo.setSelectedIndex(0);
            spotCombo.removeAllItems();
            spotCombo.setEnabled(false);
            parkButton.setEnabled(false);
            currentVehicle = null;
            availableSpots = null;

        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to park vehicle. The spot may have been taken.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vehicle createVehicle(String plate, String type) {
        return switch (type) {
            case "Motorcycle" -> new Motorcycle(plate);
            case "Car" -> new Car(plate);
            case "Truck" -> new Truck(plate);
            case "Handicapped" -> new Handicapped(plate);
            default -> new Car(plate);
        };
    }
}