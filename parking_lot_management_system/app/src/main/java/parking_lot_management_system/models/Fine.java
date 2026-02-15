package parking_lot_management_system.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import parking_lot_management_system.models.enums.FineScheme;
import parking_lot_management_system.models.enums.FineType;
import parking_lot_management_system.models.enums.SpotType;

public class Fine {
  private static final int MAX_DURATION_HOURS = 24;
  private final Integer fineId;
  private final Vehicle vehicle;
  private final FineScheme fineScheme;
  private final FineType fineType;
  private final Integer violatingHours;
  private final BigDecimal amount;

  /**
   * Create new fine (not yet in database)
   */
  public Fine(Vehicle vehicle, FineScheme fineScheme, FineType fineType, Integer violatingHours) {
    this.fineId = null;
    this.vehicle = vehicle;
    this.fineScheme = fineScheme;
    this.fineType = fineType;
    this.violatingHours = violatingHours;
    this.amount = this.fineScheme.calculateFine(violatingHours);
  }

  /**
   * Create instance from database entry
   */
  public Fine(Integer fineId, Vehicle vehicle, FineScheme fineScheme, FineType fineType, Integer violatingHours) {
    this.fineId = fineId;
    this.vehicle = vehicle;
    this.fineScheme = fineScheme;
    this.fineType = fineType;
    this.violatingHours = violatingHours;
    this.amount = this.fineScheme.calculateFine(violatingHours);
  }

  // Getters only - Fine is immutable

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

  /**
   * Generate fines based on ticket information
   */
  public static List<Fine> generateFines(ParkingLot parkingLot, Ticket ticket) {
    List<Fine> generatedFines = new ArrayList<>();

    FineScheme fineScheme = parkingLot.getFineScheme();
    Vehicle vehicle = ticket.getVehicle();
    ParkingSpot allocatedParkingSpot = ticket.getParkingSpot();
    int duration = ticket.getDurationHours();

    // Check for overstaying (more than 24 hours)
    if (duration > MAX_DURATION_HOURS) {
      FineType fineType = FineType.OVERSTAY;
      int violatingHours = duration - MAX_DURATION_HOURS;
      Fine fine = new Fine(vehicle, fineScheme, fineType, violatingHours);
      generatedFines.add(fine);
    }

    // Check for unauthorized use of reserved spot
    if (allocatedParkingSpot.getSpotType() == SpotType.RESERVED) {
      FineType fineType = FineType.UNAUTHORIZED_RESERVED;
      // Use full duration for reserved spot violation
      Fine fine = new Fine(vehicle, fineScheme, fineType, duration);
      generatedFines.add(fine);
    }

    return generatedFines;
  }

  @Override
  public String toString() {
    return "Fine{" +
        "fineId=" + fineId +
        ", vehicle=" + vehicle.getVehicleId() +
        ", fineScheme=" + fineScheme +
        ", fineType=" + fineType +
        ", violatingHours=" + violatingHours +
        ", amount=" + amount +
        '}';
  }

  /**
   * Get description of the fine
   */
  public String getDescription() {
    return switch (fineType) {
      case OVERSTAY -> "Overstay fine: Parked for " + (violatingHours + MAX_DURATION_HOURS) +
          " hours (exceeded " + MAX_DURATION_HOURS + " hour limit)";
      case UNAUTHORIZED_RESERVED -> "Unauthorized use of reserved parking spot";
    };
  }
}