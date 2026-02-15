package parking_lot_management_system.views;

import parking_lot_management_system.models.*;
import parking_lot_management_system.models.enums.FineScheme;
import parking_lot_management_system.models.enums.SpotType;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AdminPanel extends JPanel {

    private ParkingLot lot;
    private JComboBox<String> fineSchemeCombo;
    private JButton applySchemeButton;
    private JTextArea infoArea;
    private JButton viewSpotsButton;
    private JButton viewStatsButton;

    public AdminPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for fine scheme selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Fine Scheme Configuration"));

        topPanel.add(new JLabel("Current Fine Scheme:"));

        fineSchemeCombo = new JComboBox<>(
                Arrays.stream(FineScheme.values())
                        .map(scheme -> scheme.name().charAt(0) + scheme.name().substring(1).toLowerCase())
                        .toArray(String[]::new));
        topPanel.add(fineSchemeCombo);

        applySchemeButton = new JButton("Apply Fine Scheme");
        applySchemeButton.addActionListener(e -> applyFineScheme());
        topPanel.add(applySchemeButton);

        topPanel.add(Box.createHorizontalStrut(20));

        JLabel noteLabel = new JLabel("(Applied to future entries only)");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC));
        noteLabel.setForeground(Color.GRAY);
        topPanel.add(noteLabel);

        add(topPanel, BorderLayout.NORTH);

        // Button panel for admin actions
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Admin Actions"));

        viewSpotsButton = new JButton("View All Parking Spots");
        viewSpotsButton.addActionListener(e -> viewAllSpots());
        buttonPanel.add(viewSpotsButton);

        viewStatsButton = new JButton("View System Statistics");
        viewStatsButton.addActionListener(e -> viewSystemStats());
        buttonPanel.add(viewStatsButton);

        add(buttonPanel, BorderLayout.WEST);

        // Info display area
        infoArea = new JTextArea(20, 60);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Admin Information"));
        add(scrollPane, BorderLayout.CENTER);

        // Set current fine scheme
        updateCurrentSchemeDisplay();
    }

    private void applyFineScheme() {
        int index = fineSchemeCombo.getSelectedIndex();
        FineScheme selectedScheme = FineScheme.values()[index];
        lot.setFineScheme(selectedScheme);

        JOptionPane.showMessageDialog(this,
                "Fine scheme updated to: " + selectedScheme.name() + "\n" +
                        "This will apply to all future parking entries.",
                "Fine Scheme Updated",
                JOptionPane.INFORMATION_MESSAGE);

        updateCurrentSchemeDisplay();
    }

    private void updateCurrentSchemeDisplay() {
        FineScheme current = lot.getFineScheme();

        StringBuilder info = new StringBuilder();
        info.append("========== CURRENT FINE SCHEME ==========\n\n");
        info.append("Scheme: ").append(current.name()).append("\n\n");

        info.append("Fine Calculation Examples:\n");
        info.append("-".repeat(50)).append("\n");

        switch (current) {
            case FIXED:
                info.append("Fixed Fine Scheme:\n");
                info.append("  - Any overstay: RM 50.00 (flat rate)\n");
                info.append("\nExamples:\n");
                info.append("  26 hours parked: RM 50.00\n");
                info.append("  30 hours parked: RM 50.00\n");
                info.append("  50 hours parked: RM 50.00\n");
                break;
            case PROGRESSIVE:
                info.append("Progressive Fine Scheme:\n");
                info.append("  - First 24h over: RM 50.00\n");
                info.append("  - 24-48h over: +RM 100.00\n");
                info.append("  - 48-72h over: +RM 150.00\n");
                info.append("  - >72h over: +RM 200.00\n");
                info.append("\nExamples:\n");
                info.append("  26 hours parked (2h over): RM 50.00\n");
                info.append("  30 hours parked (6h over): RM 50.00\n");
                info.append("  50 hours parked (26h over): RM 150.00\n");
                info.append("  80 hours parked (56h over): RM 300.00\n");
                info.append("  100 hours parked (76h over): RM 500.00\n");
                break;
            case HOURLY:
                info.append("Hourly Fine Scheme:\n");
                info.append("  - RM 20.00 per hour over 24 hours\n");
                info.append("\nExamples:\n");
                info.append("  26 hours parked (2h over): RM 40.00\n");
                info.append("  30 hours parked (6h over): RM 120.00\n");
                info.append("  50 hours parked (26h over): RM 520.00\n");
                break;
        }

        info.append("\n").append("=".repeat(50)).append("\n");
        infoArea.setText(info.toString());
    }

    private void viewAllSpots() {
        StringBuilder spotInfo = new StringBuilder();
        spotInfo.append("========== ALL PARKING SPOTS ==========\n\n");

        for (ParkingFloor floor : lot.getParkingFloors()) {
            spotInfo.append("FLOOR ").append(floor.getFloorNumber()).append("\n");
            spotInfo.append("-".repeat(80)).append("\n");

            spotInfo.append(String.format("%-15s %-15s %-15s %-15s %-20s%n",
                    "Spot ID", "Type", "Rate/Hour", "Status", "Current Vehicle"));
            spotInfo.append("-".repeat(80)).append("\n");

            for (ParkingSpot spot : floor.getParkingSpots()) {
                String spotId = spot.getSpotId();
                String type = spot.getSpotType().getName();
                String rate = String.format("RM %.2f", spot.getSpotType().getHourlyRate());
                String status = spot.isOccupied() ? "Occupied" : "Available";
                String vehicle = spot.isOccupied() && spot.getCurrentVehicle() != null
                        ? spot.getCurrentVehicle().getVehicleId()
                        : "-";

                spotInfo.append(String.format("%-15s %-15s %-15s %-15s %-20s%n",
                        spotId, type, rate, status, vehicle));
            }
            spotInfo.append("\n");
        }

        spotInfo.append("======================================\n");
        infoArea.setText(spotInfo.toString());
    }

    private void viewSystemStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("========== SYSTEM STATISTICS ==========\n\n");

        // Spot type distribution
        stats.append("Parking Spot Distribution:\n");
        java.util.Map<SpotType, Integer> spotTypeCount = new java.util.HashMap<>();
        java.util.Map<SpotType, Integer> spotTypeOccupied = new java.util.HashMap<>();

        for (ParkingFloor floor : lot.getParkingFloors()) {
            for (ParkingSpot spot : floor.getParkingSpots()) {
                SpotType type = spot.getSpotType();
                spotTypeCount.put(type, spotTypeCount.getOrDefault(type, 0) + 1);
                if (spot.isOccupied()) {
                    spotTypeOccupied.put(type, spotTypeOccupied.getOrDefault(type, 0) + 1);
                }
            }
        }

        for (SpotType type : SpotType.values()) {
            int total = spotTypeCount.getOrDefault(type, 0);
            int occupied = spotTypeOccupied.getOrDefault(type, 0);
            double rate = total > 0 ? (occupied * 100.0 / total) : 0;

            stats.append(String.format("  %s: %d total, %d occupied (%.1f%%)%n",
                    type.getName(), total, occupied, rate));
        }

        stats.append("\n");

        // Vehicle type distribution
        stats.append("Current Vehicles by Type:\n");
        java.util.Map<String, Integer> vehicleTypeCount = new java.util.HashMap<>();

        for (Ticket ticket : lot.getActiveTickets().values()) {
            String vehicleType = ticket.getVehicle().getClass().getSimpleName();
            vehicleTypeCount.put(vehicleType, vehicleTypeCount.getOrDefault(vehicleType, 0) + 1);
        }

        if (vehicleTypeCount.isEmpty()) {
            stats.append("  No vehicles currently parked\n");
        } else {
            vehicleTypeCount.forEach((type, count) -> stats.append(String.format("  %s: %d%n", type, count)));
        }

        stats.append("\n");

        // Floor statistics
        stats.append("Floor-wise Occupancy:\n");
        for (ParkingFloor floor : lot.getParkingFloors()) {
            long occupied = floor.getParkingSpots().stream()
                    .filter(ParkingSpot::isOccupied).count();
            int total = floor.getParkingSpots().size();
            double rate = (occupied * 100.0) / total;

            stats.append(String.format("  Floor %d: %d/%d occupied (%.1f%%)%n",
                    floor.getFloorNumber(), occupied, total, rate));
        }

        stats.append("\n");
        stats.append("Current Fine Scheme: ").append(lot.getFineScheme().name()).append("\n");
        stats.append("Active Tickets: ").append(lot.getActiveTickets().size()).append("\n");

        stats.append("\n======================================\n");
        infoArea.setText(stats.toString());
    }
}