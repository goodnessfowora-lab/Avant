package parking.domain;

import parking.enums.VehicleType;
import parking.enums.ParkingSpotType;
import parking.exception.DoubleParkingException;

/**
 * Represents a parking spot in the parking lot.
 * Each spot has a unique ID, a type, and may be occupied by a vehicle.
 */
public class ParkingSpot {

    private final String spotId;
    private final ParkingSpotType spotType;
    private Vehicle vehicle;

    /**
     * Constructs a ParkingSpot with the given ID and type.
     *
     * @param spotId   the unique identifier for the parking spot
     * @param spotType the type of the parking spot
     */
    public ParkingSpot(String spotId, ParkingSpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.vehicle = null;
    }

    /**
     * Checks if the parking spot is available (not occupied by any vehicle).
     *
     * @return true if the spot is available, false otherwise
     */
    public boolean isAvailable() {
        return vehicle == null;
    }

    /**
     * Returns the unique identifier of the parking spot.
     *
     * @return the parking spot ID
     */
    public String getParkingSpotId() {
        return spotId;
    }

    /**
     * Returns the type of the parking spot.
     *
     * @return the parking spot type
     */
    public ParkingSpotType getSpotType() {
        return spotType;
    }

    /**
     * Returns the type of the vehicle currently occupying the spot, or null if the spot is empty.
     *
     * @return the vehicle type, or null if no vehicle is present
     */
    public VehicleType getVehicleType() {
        return vehicle != null ? vehicle.getType() : null;
    }

    /**
     * Assigns a vehicle to this parking spot.
     *
     * @param vehicle the vehicle to assign
     * @throws DoubleParkingException if the spot is already occupied
     */
    public void assignVehicle(Vehicle vehicle) throws DoubleParkingException {
        if(!isAvailable()) {
            throw new DoubleParkingException("Parking spot is already occupied by vehicle: " + this.vehicle.getIdentifier());
        }
        this.vehicle = vehicle;
    }

    /**
     * Removes the vehicle from this parking spot, making it available.
     */
    public void removeVehicle() {
        this.vehicle = null;
    }
}
