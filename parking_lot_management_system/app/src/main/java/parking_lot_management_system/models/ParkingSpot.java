package parking_lot_management_system.models;

import parking_lot_management_system.models.enums.SpotType;

public class ParkingSpot {
  private String spotId;
  private SpotType spotType;
  private boolean isOccupied;
  private Vehicle currentVehicle;

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
