package parking_lot_management_system.models;

import java.time.LocalDateTime;

public class Ticket {
  private final String ticketId;
  private Vehicle vehicle;
  private ParkingSpot parkingSpot;
  private LocalDateTime entryDatetime;
  private LocalDateTime exitDatetime;
  private Payment payment;

  public Ticket(Vehicle vehicle, ParkingSpot parkingSpot) {
    this.vehicle = vehicle;
    this.parkingSpot = parkingSpot;
    this.entryDatetime = LocalDateTime.now();

    String formattedTime = entryDatetime.withSecond(0).withNano(0).toString();

    this.ticketId = "T-" + vehicle.getVehicleId() + "-" + parkingSpot.getSpotId() + "-" + formattedTime;
  }

  public String getTicketId() {
    return ticketId;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public ParkingSpot getParkingSpot() {
    return parkingSpot;
  }

  public LocalDateTime getEntryDatetime() {
    return entryDatetime;
  }

  public LocalDateTime getExitDatetime() {
    return exitDatetime;
  }

  public Payment getPayment() {
    return payment;
  }

  public Ticket(String ticketId, Vehicle vehicle, ParkingSpot parkingSpot, LocalDateTime entryDatetime,
      LocalDateTime exitDatetime,
      Payment payment) {
    this.ticketId = ticketId;
    this.vehicle = vehicle;
    this.parkingSpot = parkingSpot;
    this.entryDatetime = entryDatetime;
    this.exitDatetime = exitDatetime;
    this.payment = payment;
  }

  public int duration() {
    return 0;
  }
}
