package parking_lot_management_system.models;

import java.math.BigDecimal;

import parking_lot_management_system.models.enums.FineScheme;

public class Fine {
  private Vehicle vehicle;
  private FineScheme fineScheme;
  private Integer violatingHours;
  private BigDecimal amount;

  Fine(Vehicle vehicle, FineScheme fineScheme, Integer violatingHours) {
    this.vehicle = vehicle;
    this.fineScheme = fineScheme;
    this.violatingHours = violatingHours;
    this.amount = this.fineScheme.calculateFine(violatingHours);
  }
}
