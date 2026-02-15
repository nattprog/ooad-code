package parking_lot_management_system.views;

import parking_lot_management_system.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EntryPanel extends JPanel {

    private ParkingLot lot;

    private JTextField plateField;
    private JComboBox<String> vehicleTypeCombo;
    private JComboBox<String> spotCombo;
    private JButton parkButton;
    private JTextArea outputArea;

    private Vehicle currentVehicle;
    private List<ParkingSpot> availableSpots;

    public EntryPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        inputPanel.add(new JLabel("License Plate:"));
        plateField = new JTextField();
        inputPanel.add(plateField);

        inputPanel.add(new JLabel("Vehicle Type:"));
        vehicleTypeCombo = new JComboBox<>(new String[] { "Motorcycle", "Car", "SUV", "Handicapped" });
        inputPanel.add(vehicleTypeCombo);

        inputPanel.add(new JLabel("Select Spot:"));
        spotCombo = new JComboBox<>();
        inputPanel.add(spotCombo);

        add(inputPanel, BorderLayout.NORTH);

        parkButton = new JButton("Park Vehicle");
        add(parkButton, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        vehicleTypeCombo.addActionListener(e -> updateAvailableSpots());
        parkButton.addActionListener(e -> parkVehicle());
    }

    private void updateAvailableSpots() {
        String type = (String) vehicleTypeCombo.getSelectedItem();
        String plate = plateField.getText().trim();

        if (plate.isEmpty())
            return;

        switch (type) {
            case "Motorcycle" -> currentVehicle = new Motorcycle(plate);
            case "Car" -> currentVehicle = new Car(plate);
            case "Truck" -> currentVehicle = new Truck(plate);
            case "Handicapped" -> currentVehicle = new Handicapped(plate);
            default -> currentVehicle = new Car(plate);
        }

        availableSpots = lot.getAvailableParkingSpots(lot, currentVehicle);
        spotCombo.removeAllItems();

        for (ParkingSpot s : availableSpots) {
            spotCombo.addItem(s.getSpotId() + " (" + s.getSpotType() + ")");
        }
    }

    private void parkVehicle() {
        if (currentVehicle == null || availableSpots == null || availableSpots.isEmpty()) {
            outputArea.setText("No available spots or vehicle not selected.");
            return;
        }

        int index = spotCombo.getSelectedIndex();
        if (index < 0 || index >= availableSpots.size()) {
            outputArea.setText("Invalid spot selection.");
            return;
        }

        ParkingSpot spot = availableSpots.get(index);
        // String ticket = lot.parkVehicle(currentVehicle, spot);
        Boolean success = lot.claimParkingSpot(spot, currentVehicle);

        // outputArea.setText("Vehicle parked successfully!\nTicket: " + ticket);

        // // Show Entry ticket popup
        // Receipt.generateEntry(currentVehicle.getPlate(),
        // spot.getId(),
        // currentVehicle.getClass().getSimpleName(),
        // currentVehicle.getEntryTime());
    }
}
