package parking.lot.strategy;

import parking.domain.ParkingSpot;
import parking.domain.Vehicle;
import parking.enums.ParkingSpotType;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface for allocating parking spots to vehicles.
 * Implementations define how vehicles are matched to available spots,
 * enabling different parking policies to be plugged into the system.
 */
public interface SpotAllocationStrategy {

    /**
     * Finds one or more parking spots suitable for the given vehicle.
     *
     * @param vehicle        the vehicle requesting a spot
     * @param parkingSpotMap a map of available spots grouped by type
     * @return the list of allocated spots, or an empty list if none are available
     */
    List<ParkingSpot> findParkingSpot(Vehicle vehicle, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap);
}
