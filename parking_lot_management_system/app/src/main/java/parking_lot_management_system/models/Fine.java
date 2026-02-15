package parking_lot_management_system.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import parking_lot_management_system.models.enums.FineScheme;
import parking_lot_management_system.models.enums.FineType;
import parking_lot_management_system.models.enums.SpotType;

public class Fine {
  private static final int maxDurationHours = 24;
  private final Integer fineId;
  private final Vehicle vehicle;
  private final FineScheme fineScheme;
  private final FineType fineType;
  private final Integer violatingHours;
  private final BigDecimal amount;

  // new fine
  public Fine(Vehicle vehicle, FineScheme fineScheme, FineType fineType, Integer violatingHours) {
    this.fineId = null;
    this.vehicle = vehicle;
    this.fineScheme = fineScheme;
    this.fineType = fineType;
    this.violatingHours = violatingHours;
    this.amount = this.fineScheme.calculateFine(violatingHours);
  }

  // create instance from db entry
  Fine(Integer fineId, Vehicle vehicle, FineScheme fineScheme, FineType fineType, Integer violatingHours) {
    this.fineId = fineId;
    this.vehicle = vehicle;
    this.fineScheme = fineScheme;
    this.fineType = fineType;
    this.violatingHours = violatingHours;
    this.amount = this.fineScheme.calculateFine(violatingHours);
  }

  // no setters bc it should only be set once, otherwise might result in amount
  // not being calced properly.
  // i.e. not mutable

  public Integer getFineId() {
    return fineId;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public FineScheme getFineScheme() {
    return fineScheme;
  }

  public FineType getFineType() {
    return fineType;
  }

  public Integer getViolatingHours() {
    return violatingHours;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public static List<Fine> generateFines(ParkingLot parkingLot, Ticket ticket) {
    List<Fine> generatedFines = new ArrayList<Fine>();

    FineScheme fineScheme = parkingLot.getFineScheme();
    Vehicle vehicle = ticket.getVehicle();
    ParkingSpot allocatedParkingSpot = ticket.getParkingSpot();
    int duration = ticket.getDurationHours();

    if (duration > maxDurationHours) {
      FineType fineType = FineType.OVERSTAY;
      Fine fine = new Fine(vehicle, fineScheme, fineType, duration - maxDurationHours);
      generatedFines.add(fine);
    }

    if (allocatedParkingSpot.getSpotType() == SpotType.RESERVED) {
      FineType fineType = FineType.UNAUTHORIZED_RESERVED;
      Fine fine = new Fine(vehicle, fineScheme, fineType, duration - maxDurationHours);
      generatedFines.add(fine);
    }
    return generatedFines;
  }
}
