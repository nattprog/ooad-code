package parking_lot_management_system.models;

import java.time.LocalDateTime;
import java.time.Duration;

public class Ticket {
  private final String ticketId;
  private Vehicle vehicle;
  private ParkingSpot parkingSpot;
  private LocalDateTime entryDatetime;
  private LocalDateTime exitDatetime;
  private Integer durationHours;
  private Payment payment;

  public Ticket(Vehicle vehicle, ParkingSpot parkingSpot) {
    this.vehicle = vehicle;
    this.parkingSpot = parkingSpot;
    this.entryDatetime = LocalDateTime.now();

    String formattedTime = entryDatetime.withSecond(0).withNano(0).toString();

    this.ticketId = "T-" + vehicle.getVehicleId() + "-" + parkingSpot.getSpotId() + "-" + formattedTime;
  }

  public Ticket(String ticketId, Vehicle vehicle, ParkingSpot parkingSpot, LocalDateTime entryDatetime,
      LocalDateTime exitDatetime, Integer durationHours, Payment payment) {
    this.ticketId = ticketId;
    this.vehicle = vehicle;
    this.parkingSpot = parkingSpot;
    this.entryDatetime = entryDatetime;
    this.exitDatetime = exitDatetime;
    this.durationHours = durationHours;
    this.payment = payment;
  }

  public String getTicketId() {
    return ticketId;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public void setVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
  }

  public ParkingSpot getParkingSpot() {
    return parkingSpot;
  }

  public void setParkingSpot(ParkingSpot parkingSpot) {
    this.parkingSpot = parkingSpot;
  }

  public LocalDateTime getEntryDatetime() {
    return entryDatetime;
  }

  public void setEntryDatetime(LocalDateTime entryDatetime) {
    this.entryDatetime = entryDatetime;
  }

  public LocalDateTime getExitDatetime() {
    return exitDatetime;
  }

  public void setExitDatetimeAndDurationHours(LocalDateTime exitDatetime) {
    this.exitDatetime = exitDatetime;

    long minutes = Duration.between(this.entryDatetime, this.exitDatetime).toMinutes();

    // Ceiling to next full hour
    this.durationHours = (int) Math.ceil(minutes / 60.0);
  }

  public Integer getDurationHours() {
    return durationHours;
  }

  public void setDurationHours(Integer durationHours) {
    this.durationHours = durationHours;
  }

  public Payment getPayment() {
    return payment;
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

}
