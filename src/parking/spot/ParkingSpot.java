package parking.spot;

import parking.domain.Vehicle;
import parking.enums.VehicleType;

public interface ParkingSpot {

    boolean isAvailable();

    String getParkingSpotId();
    VehicleType getVehicleType();

    void parkVehicle(Vehicle vehicle);
    void removeVehicle();
}
