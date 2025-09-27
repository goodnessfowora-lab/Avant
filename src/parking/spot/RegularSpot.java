package parking.spot;

import parking.domain.Vehicle;
import parking.enums.VehicleType;

public class RegularSpot implements ParkingSpot {

    private final String spotId;
    private Vehicle vehicle;

    public RegularSpot(String spotId) {
        this.spotId = spotId;
        this.vehicle = null;
    }

    @Override
    public boolean isAvailable() {
        return vehicle == null;
    }

    @Override
    public String getParkingSpotId() {
        return spotId;
    }

    @Override
    public VehicleType getVehicleType() {
        return vehicle.getType();
    }

    @Override
    public void parkVehicle(Vehicle vehicle) {
        if(isAvailable()) {
            this.vehicle = vehicle;
        } else {
            throw new RuntimeException("Attempting to park vehicle in taken spot");
        }
    }

    @Override
    public void removeVehicle() {
        this.vehicle = null;
    }
}
