package parking.lot;

import parking.enums.VehicleType;
import parking.spot.ParkingSpot;

import java.util.List;

public class ParkingLot {
    private final ParkingLotAdmin parkingLotAdmin;

    public ParkingLot(int numOfRows, String rowSequence) {
        this.parkingLotAdmin = new ParkingLotAdmin(numOfRows, rowSequence);
    }

    public List<ParkingSpot> parkVehicle(String identifier, VehicleType vehicleType) {
        return parkingLotAdmin.parkVehicle(identifier, vehicleType);
    }

    public void removeVehicle(String identifier) {
        parkingLotAdmin.removeVehicle(identifier);
    }

    public void printLotSummary() {
        parkingLotAdmin.printLotSummary();
    }

    public long getParkingLotSize() {
        return parkingLotAdmin.getTotalSpots();
    }
}
