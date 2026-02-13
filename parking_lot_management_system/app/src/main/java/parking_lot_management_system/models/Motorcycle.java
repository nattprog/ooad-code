package parking_lot_management_system.models;

import parking_lot_management_system.models.enums.SpotType;

public class Motorcycle extends Vehicle {
  public Motorcycle(String vehicleId) {
    super(vehicleId);
  };

  @Override
  public final boolean checkSpotValidity(SpotType spotType) {
    return spotType == SpotType.COMPACT;
  };
}
