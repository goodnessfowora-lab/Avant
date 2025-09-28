package parking.domain;

import parking.enums.VehicleType;
import parking.enums.ParkingSpotType;
import parking.exception.DoubleParkingException;

public class ParkingSpot {

    private final String spotId;
    private final ParkingSpotType spotType;
    private Vehicle vehicle;

    public ParkingSpot(String spotId, ParkingSpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.vehicle = null;
    }

    public boolean isAvailable() {
        return vehicle == null;
    }

    public String getParkingSpotId() {
        return spotId;
    }

    public ParkingSpotType getSpotType() {
        return spotType;
    }

    public VehicleType getVehicleType() {
        return vehicle != null ? vehicle.getType() : null;
    }

    public void assignVehicle(Vehicle vehicle) throws DoubleParkingException {
        if(!isAvailable()) {
            throw new DoubleParkingException("Parking spot is already occupied by vehicle: " + this.vehicle.getIdentifier());
        }
        this.vehicle = vehicle;
    }

    public void removeVehicle() {
        this.vehicle = null;
    }
}
