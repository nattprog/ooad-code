package parking_lot_management_system.models;

import parking_lot_management_system.models.enums.SpotType;

public class ParkingSpot {
  private String spotId;
  private int spotNumber;
  private int rowNumber;
  private int floorNumber;
  private SpotType spotType;
  private boolean isOccupied;
  private Vehicle currentVehicle;

  // create new parking spot
  public ParkingSpot(int spotNumber, int rowNumber, int floorNumber, SpotType spotType) {
    this.spotNumber = spotNumber;
    this.rowNumber = rowNumber;
    this.floorNumber = floorNumber;
    this.spotType = spotType; // contains hourly rate
    this.isOccupied = false;
    this.currentVehicle = null;

    this.spotId = "F" + Integer.toString(floorNumber) + "-" + "R" + Integer.toString(rowNumber) + "-" + "S"
        + Integer.toString(spotNumber);
  }

  // from db
  public ParkingSpot(String spotId, int spotNumber, int rowNumber, int floorNumber, SpotType spotType,
      boolean isOccupied, Vehicle currentVehicle) {
    this.spotId = spotId;
    this.spotNumber = spotNumber;
    this.rowNumber = rowNumber;
    this.floorNumber = floorNumber;
    this.spotType = spotType;
    this.isOccupied = isOccupied;
    this.currentVehicle = currentVehicle;
  }

  public String getSpotId() {
    return spotId;
  }

  public void setSpotId(String spotId) {
    this.spotId = spotId;
  }

  public SpotType getSpotType() {
    return spotType;
  }

  public void setSpotType(SpotType spotType) {
    this.spotType = spotType;
  }

  public boolean isOccupied() {
    return isOccupied;
  }

  public void setOccupied(boolean isOccupied) {
    this.isOccupied = isOccupied;
  }

  public Vehicle getCurrentVehicle() {
    return currentVehicle;
  }

  public void setCurrentVehicle(Vehicle currentVehicle) {
    this.currentVehicle = currentVehicle;
  }
}
