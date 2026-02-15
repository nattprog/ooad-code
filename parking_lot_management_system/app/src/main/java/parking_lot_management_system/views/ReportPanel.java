package parking_lot_management_system.views;

import parking_lot_management_system.models.*;
import parking_lot_management_system.controllers.*;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {

    private ParkingLot lot;
    private JTextArea reportArea;
    private JButton refreshButton;

    public ReportPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new BorderLayout());

        reportArea = new JTextArea(20, 50);
        reportArea.setEditable(false);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        refreshButton = new JButton("Refresh Report");
        add(refreshButton, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> showReport());
    }

    private void showReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("---- CURRENT VEHICLES ----\n");
        lot.getActiveTickets().forEach((plate, vehicle) -> sb.append(plate).append(" -> Spot: ")
                .append(vehicle.getParkingSpot().getSpotId()).append("\n"));

        sb.append("\n---- OCCUPANCY ----\n");
        lot.getParkingFloors().forEach(floor -> {
            long occupied = floor.getParkingSpots().stream().filter(s -> s.isOccupied()).count();
            sb.append("Floor ").append(floor.getFloorNumber())
                    .append(": ").append(occupied).append("/").append(floor.getParkingSpots().size()).append("\n");
        });

        // sb.append("\nTotal Revenue: RM ").append(lot.getTotalRevenue()).append("\n");

        reportArea.setText(sb.toString());
    }
}
