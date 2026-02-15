package parking_lot_management_system.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment class represents a payment transaction for parking and fines
 */
public class Payment {
  private final String paymentId;
  private final Ticket ticket;
  private final BigDecimal parkingFee;
  private final BigDecimal fineAmount;
  private final BigDecimal totalAmount;
  private final String paymentMethod; // "Cash" or "Card"
  private final LocalDateTime paymentDatetime;

  /**
   * Create a new payment
   */
  public Payment(Ticket ticket, BigDecimal parkingFee, BigDecimal fineAmount, String paymentMethod) {
    this.paymentId = "PAY-" + UUID.randomUUID().toString();
    this.ticket = ticket;
    this.parkingFee = parkingFee;
    this.fineAmount = fineAmount;
    this.totalAmount = parkingFee.add(fineAmount);
    this.paymentMethod = paymentMethod;
    this.paymentDatetime = LocalDateTime.now();
  }

  /**
   * Create payment from database record
   */
  public Payment(String paymentId, Ticket ticket, BigDecimal parkingFee, BigDecimal fineAmount,
      String paymentMethod, LocalDateTime paymentDatetime) {
    this.paymentId = paymentId;
    this.ticket = ticket;
    this.parkingFee = parkingFee;
    this.fineAmount = fineAmount;
    this.totalAmount = parkingFee.add(fineAmount);
    this.paymentMethod = paymentMethod;
    this.paymentDatetime = paymentDatetime;
  }

  // Getters only - Payment is immutable once created

  public String getPaymentId() {
    return paymentId;
  }

  public Ticket getTicket() {
    return ticket;
  }

  public BigDecimal getParkingFee() {
    return parkingFee;
  }

  public BigDecimal getFineAmount() {
    return fineAmount;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public LocalDateTime getPaymentDatetime() {
    return paymentDatetime;
  }

  /**
   * Generate a receipt string
   */
  public String generateReceipt() {
    StringBuilder receipt = new StringBuilder();
    receipt.append("========== PARKING RECEIPT ==========\n");
    receipt.append("Payment ID: ").append(paymentId).append("\n");
    receipt.append("Ticket ID: ").append(ticket.getTicketId()).append("\n");
    receipt.append("Vehicle: ").append(ticket.getVehicle().getVehicleId()).append("\n");
    receipt.append("Spot: ").append(ticket.getParkingSpot().getSpotId()).append("\n");
    receipt.append("Entry: ").append(ticket.getEntryDatetime()).append("\n");
    receipt.append("Exit: ").append(ticket.getExitDatetime()).append("\n");
    receipt.append("Duration: ").append(ticket.getDurationHours()).append(" hour(s)\n");
    receipt.append("-------------------------------------\n");
    receipt.append("Parking Fee: RM ").append(String.format("%.2f", parkingFee)).append("\n");
    if (fineAmount.compareTo(BigDecimal.ZERO) > 0) {
      receipt.append("Fine Amount: RM ").append(String.format("%.2f", fineAmount)).append("\n");
    }
    receipt.append("-------------------------------------\n");
    receipt.append("Total Amount: RM ").append(String.format("%.2f", totalAmount)).append("\n");
    receipt.append("Payment Method: ").append(paymentMethod).append("\n");
    receipt.append("Payment Time: ").append(paymentDatetime).append("\n");
    receipt.append("=====================================\n");
    return receipt.toString();
  }

  @Override
  public String toString() {
    return "Payment{" +
        "paymentId='" + paymentId + '\'' +
        ", parkingFee=" + parkingFee +
        ", fineAmount=" + fineAmount +
        ", totalAmount=" + totalAmount +
        ", paymentMethod='" + paymentMethod + '\'' +
        '}';
  }
}