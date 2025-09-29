package parking.lot.strategy;

import parking.domain.ParkingSpot;
import parking.domain.Vehicle;
import parking.enums.ParkingSpotType;

import java.util.List;
import java.util.Map;

public interface SpotAllocationStrategy {
    List<ParkingSpot> findParkingSpot(Vehicle vehicle, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap);
}
