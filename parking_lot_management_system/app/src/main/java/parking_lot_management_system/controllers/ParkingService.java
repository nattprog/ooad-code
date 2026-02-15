package parking_lot_management_system.controllers;

import java.util.List;
import java.util.ArrayList;

import parking_lot_management_system.models.ParkingLot;
import parking_lot_management_system.models.ParkingFloor;
import parking_lot_management_system.models.ParkingSpot;
import parking_lot_management_system.models.Vehicle;
import parking_lot_management_system.models.enums.SpotType;

// vehicle assignment
public class ParkingService {
  // public ParkingLot createParkingLot() {
  // ParkingLot parkingLot = new ParkingLot();

  // return parkingLot;
  // }

  public List<ParkingSpot> getAvailableParkingSpots(ParkingLot parkingLot, SpotType... spotTypes) {
    List<ParkingSpot> availableSpots = new ArrayList<ParkingSpot>();

    for (ParkingFloor parkingFloor : parkingLot.getParkingFloors()) {
      for (ParkingSpot parkingSpot : parkingFloor.getParkingSpots()) {

        if (!parkingSpot.isOccupied())
          continue;

        for (SpotType type : spotTypes) {
          if (parkingSpot.getSpotType() == type) {
            availableSpots.add(parkingSpot);
            break;
          }
        }
      }
    }
    return availableSpots;
  }

  public boolean claimParkingSpot(ParkingSpot parkingSpot, Vehicle vehicle) {
    try {
      parkingSpot.setOccupied(true);
      parkingSpot.setCurrentVehicle(vehicle);
      return true;
    } catch (Exception e) {
      return false;
      // TODO: handle exception
    }
  }
}

// public ParkingSpot(int spotNumber, int rowNumber, int floorNumber, SpotType
// spotType) {
// this.spotNumber = spotNumber;
// this.rowNumber = rowNumber;
// this.floorNumber = floorNumber;
// this.spotType = spotType; // contains hourly rate
// this.isOccupied = false;
// this.currentVehicle = null;

// // this.spotId = "F" + Integer.toString(floorNumber) + "-" + "R" +
// Integer.toString(rowNumber) + "-" + "S"
// // + Integer.toString(spotNumber);
// }