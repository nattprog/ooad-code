package parking_lot_management_system.models.enums;

import java.math.BigDecimal;

public enum SpotType {
  COMPACT("Compact", "2.00"),
  REGULAR("Regular", "5.00"),
  HANDICAPPED("Handicapped", "2.00"),
  RESERVED("Reserved", "10.00");

  private final String name;
  private final BigDecimal hourlyRate;

  public String getName() {
    return name;
  }

  public BigDecimal getHourlyRate() {
    return hourlyRate;
  }

  SpotType(String name, String hourlyRate) {
    this.name = name;
    this.hourlyRate = new BigDecimal(hourlyRate);
  }
}
