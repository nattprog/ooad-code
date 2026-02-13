package parking_lot_management_system.models;

import java.util.List;
import java.util.ArrayList;

public class ParkingFloor {
  private int floorNumber;
  private List<ParkingSpot> parkingSpots;

  public List<ParkingSpot> getParkingSpots() {
    return parkingSpots;
  }
}
