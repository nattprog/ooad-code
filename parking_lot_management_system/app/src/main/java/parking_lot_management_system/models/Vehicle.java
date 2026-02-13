package parking_lot_management_system.models;

import parking_lot_management_system.models.enums.SpotType;

public abstract class Vehicle {
  private String vehicleId;

  public Vehicle(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  public abstract boolean checkSpotValidity(SpotType spotType);

  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

}
