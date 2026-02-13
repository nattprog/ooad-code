package parking_lot_management_system.models;

import parking_lot_management_system.models.enums.SpotType;

public class Car extends Vehicle {
  public Car(String vehicleId) {
    super(vehicleId);
  };

  @Override
  public final boolean checkSpotValidity(SpotType spotType) {
    return spotType == SpotType.COMPACT || spotType == SpotType.REGULAR;
  };
}
