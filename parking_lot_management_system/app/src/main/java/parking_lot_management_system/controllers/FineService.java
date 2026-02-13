package parking_lot_management_system.controllers;

import java.util.List;
import java.util.ArrayList;

import parking_lot_management_system.models.*;
import parking_lot_management_system.models.enums.FineType;
import parking_lot_management_system.models.enums.FineScheme;

public class FineService {
  private final int maxDurationHours = 24;

  public List<Fine> getFines(Vehicle vehicle) {

    List<Fine> fines = new ArrayList<Fine>();
    return fines;
  }

  public List<Fine> generateFines(ParkingLot parkingLot, Ticket ticket, ParkingSpot actualParkingSpot) {
    List<Fine> generatedFines = new ArrayList<Fine>();

    FineScheme fineScheme = parkingLot.getFineScheme();
    Vehicle vehicle = ticket.getVehicle();
    ParkingSpot allocatedParkingSpot = ticket.getParkingSpot();
    int duration = ticket.getDurationHours();

    if (duration > maxDurationHours) {
      FineType fineType = FineType.OVERSTAY;
      Fine fine = new Fine(vehicle, fineScheme, fineType, duration - maxDurationHours);
      generatedFines.add(fine);
    }

    if (!allocatedParkingSpot.getSpotId().equals(actualParkingSpot.getSpotId())) {
      FineType fineType = FineType.UNAUTHORIZED_RESERVED;
      Fine fine = new Fine(vehicle, fineScheme, fineType, duration - maxDurationHours);
      generatedFines.add(fine);
    }
    return generatedFines;
  }
}
