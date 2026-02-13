package parking_lot_management_system.models;

import java.util.List;
import java.util.ArrayList;

import parking_lot_management_system.models.enums.FineScheme;

public class ParkingLot {
  private FineScheme fineScheme;
  private List<ParkingFloor> floor;

  public FineScheme getFineScheme() {
    return fineScheme;
  }

  public List<ParkingFloor> getFloor() {
    return floor;
  }

}
