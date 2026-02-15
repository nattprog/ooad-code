package parking_lot_management_system.models;

import parking_lot_management_system.models.enums.SpotType;

public class Handicapped extends Vehicle {
  public Handicapped(String vehicleId) {
    super(vehicleId);
  };

  @Override
  public final boolean checkSpotValidity(SpotType spotType) {
    return true;
  };
}


