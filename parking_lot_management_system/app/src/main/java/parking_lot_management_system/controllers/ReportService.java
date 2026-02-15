package parking_lot_management_system.controllers;

import java.util.List;
import java.util.Map;
import parking_lot_management_system.models.*;

public class ReportService {

    public static void showCurrentVehicles(Map<String, Ticket> tickets) {
        System.out.println("---- CURRENT VEHICLES ----");
        tickets.forEach((k, t) -> System.out.println(k + " -> Spot: " + t.getParkingSpot().getSpotId()));
    }

    public static void showOccupancy(List<ParkingFloor> floors) {
        System.out.println("---- OCCUPANCY REPORT ----");
        for (ParkingFloor floor : floors) {
            long occupied = floor.getParkingSpots().stream().filter(ParkingSpot::isOccupied).count();
            System.out.println("Floor " + floor.getFloorNumber()
                    + ": " + occupied + "/" + floor.getParkingSpots().size());
        }
    }
}
