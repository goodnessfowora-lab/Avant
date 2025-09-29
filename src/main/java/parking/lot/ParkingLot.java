package parking.lot;

import parking.enums.ParkingLotAdminType;
import parking.enums.VehicleType;
import parking.domain.ParkingSpot;
import parking.exception.DoubleParkingException;
import parking.exception.IllegalParkingLotAdminException;
import parking.exception.IllegalSpotTypeException;
import parking.exception.ParkingUnavailableException;

import java.util.List;

public class ParkingLot {
    private final long totalSpots; // total number of spots in the lot
    private final ParkingLotAdmin parkingLotAdmin;

    public ParkingLot(int numOfRows, String rowSequence, ParkingLotAdminType parkingLotAdminType) throws IllegalSpotTypeException, IllegalParkingLotAdminException {
        String[] spotArrangement = rowSequence.split(",");
        this.totalSpots = (long) numOfRows * spotArrangement.length;
        switch (parkingLotAdminType) {
            case COMPACT_REGULAR -> this.parkingLotAdmin = new RegularCompactLotAdmin(numOfRows, rowSequence);
            default ->
                    throw new IllegalParkingLotAdminException("Unsupported ParkingLotAdminType: " + parkingLotAdminType);
        }
    }

    public ParkingSpot parkVehicle(String identifier, VehicleType vehicleType) throws DoubleParkingException, ParkingUnavailableException {
        List<ParkingSpot> parkingSpots = parkingLotAdmin.parkVehicle(identifier, vehicleType);
        if (parkingSpots == null || parkingSpots.isEmpty()) {
            throw new ParkingUnavailableException("No available spots for vehicle: " + identifier);
        }

        return parkingSpots.get(0);
    }

    public void removeVehicle(String identifier) {
        parkingLotAdmin.removeVehicle(identifier);
    }

    public void printLotSummary() {
        parkingLotAdmin.printLotSummary();
    }

    public long getParkingLotSize() {
        return totalSpots;
    }
}
