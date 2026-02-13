package parking_lot_management_system.models;

public abstract class Vehicle {
  private String vehicleId;
  private Boolean isHandicapped;

  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  public Boolean getIsHandicapped() {
    return isHandicapped;
  }

  public void setIsHandicapped(Boolean isHandicapped) {
    this.isHandicapped = isHandicapped;
  }

}
