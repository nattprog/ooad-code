package parking_lot_management_system.models;

import java.util.List;

public class ParkingFloor {
  private int floorNumber;
  private List<ParkingSpot> parkingSpots;

  public List<ParkingSpot> getParkingSpots() {
    return parkingSpots;
  }

  public int getFloorNumber() {
    return floorNumber;
  }

  public void setFloorNumber(int floorNumber) {
    this.floorNumber = floorNumber;
  }

  public void setParkingSpots(List<ParkingSpot> parkingSpots) {
    this.parkingSpots = parkingSpots;
  }
}
