package parking_lot_management_system.models;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import parking_lot_management_system.database.DatabaseManager;
import parking_lot_management_system.models.enums.FineScheme;

/**
 * Singleton ParkingLot class - manages the entire parking lot system
 */
public class ParkingLot {
  private static ParkingLot instance;
  private FineScheme fineScheme;
  private List<ParkingFloor> parkingFloors;
  private Map<String, Ticket> activeTickets; // Key: vehicleId, Value: Ticket

  // Private constructor for Singleton
  private ParkingLot() {
    this.fineScheme = FineScheme.FIXED; // Default fine scheme
    this.parkingFloors = new ArrayList<>();
    this.activeTickets = new HashMap<>();
  }

  /**
   * Get singleton instance
   */
  public static ParkingLot getInstance() {
    if (instance == null) {
      instance = new ParkingLot();
    }
    return instance;
  }

  // Getters and Setters

  public Map<String, Ticket> getActiveTickets() {
    return activeTickets;
  }

  public void setActiveTickets(Map<String, Ticket> activeTickets) {
    this.activeTickets = activeTickets;
  }

  public FineScheme getFineScheme() {
    return fineScheme;
  }

  public void setFineScheme(FineScheme fineScheme) {
    this.fineScheme = fineScheme;
  }

  public List<ParkingFloor> getParkingFloors() {
    return parkingFloors;
  }

  public void setParkingFloors(List<ParkingFloor> parkingFloors) {
    this.parkingFloors = parkingFloors;
  }

  /**
   * Get all available parking spots suitable for the vehicle
   * FIXED: Changed condition from !isOccupied to isOccupied
   */
  public List<ParkingSpot> getAvailableParkingSpots(Vehicle vehicle) {
    List<ParkingSpot> availableSpots = new ArrayList<>();

    for (ParkingFloor parkingFloor : this.parkingFloors) {
      for (ParkingSpot parkingSpot : parkingFloor.getParkingSpots()) {
        // Check if spot is NOT occupied and vehicle can park there
        if (!parkingSpot.isOccupied() && vehicle.checkSpotValidity(parkingSpot.getSpotType())) {
          availableSpots.add(parkingSpot);
        }
      }
    }
    return availableSpots;
  }

  /**
   * Claim a parking spot for a vehicle
   */
  public boolean claimParkingSpot(ParkingSpot parkingSpot, Vehicle vehicle) {
    try {
      // Verify spot is available
      if (parkingSpot.isOccupied()) {
        return false;
      }

      // Verify vehicle can park in this spot type
      if (!vehicle.checkSpotValidity(parkingSpot.getSpotType())) {
        return false;
      }

      // Update spot status
      parkingSpot.setOccupied(true);
      parkingSpot.setCurrentVehicle(vehicle);

      // Save vehicle to database
      DatabaseManager.saveVehicle(vehicle);

      // Update spot in database
      DatabaseManager.updateSpotOccupancy(parkingSpot.getSpotId(), true, vehicle.getVehicleId());

      // Create and save ticket
      Ticket ticket = new Ticket(vehicle, parkingSpot);
      DatabaseManager.saveTicket(ticket);

      // Add to active tickets
      activeTickets.put(vehicle.getVehicleId(), ticket);

      return true;

    } catch (SQLException e) {
      System.err.println("Error claiming parking spot: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Release a parking spot (when vehicle exits)
   */
  public boolean releaseSpot(String vehicleId) {
    try {
      Ticket ticket = activeTickets.get(vehicleId);
      if (ticket == null) {
        return false;
      }

      ParkingSpot spot = ticket.getParkingSpot();
      spot.setOccupied(false);
      spot.setCurrentVehicle(null);

      // Update database
      DatabaseManager.updateSpotOccupancy(spot.getSpotId(), false, null);

      // Remove from active tickets
      activeTickets.remove(vehicleId);

      return true;

    } catch (SQLException e) {
      System.err.println("Error releasing spot: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Initialize parking lot from database
   */
  public void loadFromDatabase() {
    try {
      // Load all parking spots and organize by floor
      List<ParkingSpot> allSpots = DatabaseManager.loadAllParkingSpots();

      // Group spots by floor
      Map<Integer, List<ParkingSpot>> spotsByFloor = new HashMap<>();
      for (ParkingSpot spot : allSpots) {
        spotsByFloor.computeIfAbsent(spot.getFloorNumber(), k -> new ArrayList<>()).add(spot);
      }

      // Create ParkingFloor objects
      this.parkingFloors.clear();
      for (Map.Entry<Integer, List<ParkingSpot>> entry : spotsByFloor.entrySet()) {
        ParkingFloor floor = new ParkingFloor();
        floor.setFloorNumber(entry.getKey());
        floor.setParkingSpots(entry.getValue());
        this.parkingFloors.add(floor);
      }

      // Sort floors by floor number
      this.parkingFloors.sort((f1, f2) -> Integer.compare(f1.getFloorNumber(), f2.getFloorNumber()));

      // Load active tickets
      this.activeTickets = DatabaseManager.loadActiveTickets();

      System.out.println("Parking lot loaded from database: " + allSpots.size() + " spots, " +
          activeTickets.size() + " active tickets");

    } catch (SQLException e) {
      System.err.println("Error loading parking lot from database: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Get occupancy statistics
   */
  public Map<String, Integer> getOccupancyStats() {
    Map<String, Integer> stats = new HashMap<>();
    int totalSpots = 0;
    int occupiedSpots = 0;

    for (ParkingFloor floor : parkingFloors) {
      for (ParkingSpot spot : floor.getParkingSpots()) {
        totalSpots++;
        if (spot.isOccupied()) {
          occupiedSpots++;
        }
      }
    }

    stats.put("total", totalSpots);
    stats.put("occupied", occupiedSpots);
    stats.put("available", totalSpots - occupiedSpots);

    return stats;
  }

  /**
   * Get parking spot by ID
   */
  public ParkingSpot getSpotById(String spotId) {
    for (ParkingFloor floor : parkingFloors) {
      for (ParkingSpot spot : floor.getParkingSpots()) {
        if (spot.getSpotId().equals(spotId)) {
          return spot;
        }
      }
    }
    return null;
  }
}