package parking_lot_management_system.models;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import parking_lot_management_system.models.enums.FineScheme;

public class ParkingLot {
  private static ParkingLot instance;
  private FineScheme fineScheme;
  private List<ParkingFloor> parkingFloors;
  private Map<String, Ticket> activeTickets;

  public Map<String, Ticket> getActiveTickets() {
    return activeTickets;
  }

  public void setActiveTickets(Map<String, Ticket> activeTickets) {
    this.activeTickets = activeTickets;
  }

  public FineScheme getFineScheme() {
    return fineScheme;
  }

  public void setFineScheme(FineScheme fineScheme) {
    this.fineScheme = fineScheme;
  }

  public List<ParkingFloor> getParkingFloors() {
    return parkingFloors;
  }

  public void setParkingFloors(List<ParkingFloor> parkingFloors) {
    this.parkingFloors = parkingFloors;
  }

  public static ParkingLot getInstance() {
    if (instance == null)
      instance = new ParkingLot();
    return instance;
  }

  public List<ParkingSpot> getAvailableParkingSpots(ParkingLot parkingLot, Vehicle vehicle) {
    List<ParkingSpot> availableSpots = new ArrayList<ParkingSpot>();

    for (ParkingFloor parkingFloor : parkingLot.getParkingFloors()) {
      for (ParkingSpot parkingSpot : parkingFloor.getParkingSpots()) {

        if (!parkingSpot.isOccupied())
          continue;

        if (vehicle.checkSpotValidity(parkingSpot.getSpotType())) {
          availableSpots.add(parkingSpot);
          break;
        }
      }
    }
    return availableSpots;
  }

  public boolean claimParkingSpot(ParkingSpot parkingSpot, Vehicle vehicle) {
    try {
      parkingSpot.setOccupied(true);
      parkingSpot.setCurrentVehicle(vehicle);
      return true;
    } catch (Exception e) {
      return false;
      // TODO: handle exception
    }
  }
}
