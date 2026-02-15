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

  // Create new parking spot (generates spotId automatically)
  public ParkingSpot(int spotNumber, int rowNumber, int floorNumber, SpotType spotType) {
    this.spotNumber = spotNumber;
    this.rowNumber = rowNumber;
    this.floorNumber = floorNumber;
    this.spotType = spotType;
    this.isOccupied = false;
    this.currentVehicle = null;

    // Generate spotId automatically
    this.spotId = "F" + floorNumber + "-R" + rowNumber + "-S" + spotNumber;
  }

  // Create from database (spotId already exists)
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

  public int getSpotNumber() {
    return spotNumber;
  }

  public void setSpotNumber(int spotNumber) {
    this.spotNumber = spotNumber;
  }

  public int getRowNumber() {
    return rowNumber;
  }

  public void setRowNumber(int rowNumber) {
    this.rowNumber = rowNumber;
  }

  public int getFloorNumber() {
    return floorNumber;
  }

  public void setFloorNumber(int floorNumber) {
    this.floorNumber = floorNumber;
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

  @Override
  public String toString() {
    return spotId + " (" + spotType.getName() + ") - " +
        (isOccupied ? "Occupied" : "Available");
  }
}