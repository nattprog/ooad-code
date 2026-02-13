package parking_lot_management_system.models;

import java.math.BigDecimal;

import parking_lot_management_system.models.enums.FineScheme;
import parking_lot_management_system.models.enums.FineType;

public class Fine {
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
}
