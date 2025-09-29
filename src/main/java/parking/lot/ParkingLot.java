package parking.lot;

import parking.enums.ParkingLotAdminType;
import parking.enums.VehicleType;
import parking.domain.ParkingSpot;
import parking.exception.DoubleParkingException;
import parking.exception.IllegalParkingLotAdminException;
import parking.exception.IllegalSpotTypeException;
import parking.exception.ParkingUnavailableException;

import java.util.List;

/**
 * Facade representing the parking lot.
 * Provides a simple interface for clients to interact with the system
 * while delegating allocation logic to a {@link ParkingLotAdmin}.
 */
public class ParkingLot {
    private final long totalSpots; // total number of spots in the lot
    private final ParkingLotAdmin parkingLotAdmin;

    /**
     * Constructs a ParkingLot with the given configuration.
     *
     * @param numOfRows           number of rows in the parking lot
     * @param rowSequence         comma-separated row sequence describing spot types per row
     * @param parkingLotAdminType the type of parking lot admin / allocation strategy to use
     * @throws IllegalSpotTypeException        if the row sequence contains an unsupported spot type
     * @throws IllegalParkingLotAdminException if the requested admin type is not supported
     */
    public ParkingLot(int numOfRows, String rowSequence, ParkingLotAdminType parkingLotAdminType)
            throws IllegalSpotTypeException, IllegalParkingLotAdminException {
        String[] spotArrangement = rowSequence.split(",");
        this.totalSpots = (long) numOfRows * spotArrangement.length;

        switch (parkingLotAdminType) {
            case COMPACT_REGULAR -> this.parkingLotAdmin = new RegularCompactLotAdmin(numOfRows, rowSequence);
            default ->
                    throw new IllegalParkingLotAdminException("Unsupported ParkingLotAdminType: " + parkingLotAdminType);
        }
    }

    /**
     * Attempts to park a vehicle identified by {@code identifier} and of the given {@code vehicleType}.
     *
     * @param identifier  unique vehicle identifier
     * @param vehicleType type of the vehicle (MOTORCYCLE, CAR, VAN)
     * @return the primary {@link ParkingSpot} assigned to the vehicle
     * @throws DoubleParkingException     if the vehicle is already parked
     * @throws ParkingUnavailableException if no suitable spot(s) are available
     */
    public ParkingSpot parkVehicle(String identifier, VehicleType vehicleType)
            throws DoubleParkingException, ParkingUnavailableException {
        List<ParkingSpot> parkingSpots = parkingLotAdmin.parkVehicle(identifier, vehicleType);
        if (parkingSpots == null || parkingSpots.isEmpty()) {
            throw new ParkingUnavailableException("No available spots for vehicle: " + identifier);
        }

        return parkingSpots.get(0);
    }

    /**
     * Removes the vehicle (if present) identified by {@code identifier} from the lot.
     *
     * @param identifier unique vehicle identifier
     */
    public void removeVehicle(String identifier) {
        parkingLotAdmin.removeVehicle(identifier);
    }

    /**
     * Prints a human-readable summary of the parking lot to standard output.
     * Delegates the actual summary generation and formatting to the configured admin.
     */
    public void printLotSummary() {
        parkingLotAdmin.printLotSummary();
    }

    /**
     * Returns the configured total number of parking spots for this lot.
     *
     * @return total number of spots
     */
    public long getParkingLotSize() {
        return totalSpots;
    }
}
