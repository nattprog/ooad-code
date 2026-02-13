package parking_lot_management_system.models.enums;

import java.math.BigDecimal;

public enum FineScheme {
  FIXED {
    public BigDecimal calculateFine(int violatingHours) {
      BigDecimal amount = new BigDecimal("0.00");
      if (violatingHours > 0) {
        amount = amount.add(new BigDecimal("50.00"));
      }
      return amount;
    }
  },
  PROGRESSIVE {
    public BigDecimal calculateFine(int violatingHours) {
      BigDecimal amount = new BigDecimal("0.00");
      if (violatingHours > 0) {
        amount = amount.add(new BigDecimal("50.00"));
      }
      if (violatingHours > 24) {
        amount = amount.add(new BigDecimal("100.00"));
      }
      if (violatingHours > 48) {
        amount = amount.add(new BigDecimal("150.00"));
      }
      if (violatingHours > 72) {
        amount = amount.add(new BigDecimal("200.00"));
      }
      return amount;
    }
  },
  HOURLY {
    public BigDecimal calculateFine(int violatingHours) {
      BigDecimal amount = new BigDecimal("0.00");
      if (violatingHours > 0) {

        amount = new BigDecimal("20.00").multiply(BigDecimal.valueOf(violatingHours));
      }
      return amount;
    }
  };

  public abstract BigDecimal calculateFine(int violatingHours);
}
