package parking.lot;

import parking.domain.ParkingLotSummary;
import parking.domain.ParkingSpot;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.exception.DoubleParkingException;
import parking.exception.ParkingUnavailableException;

import java.util.List;
import java.util.Map;

/**
 * Interface defining the operations for administering a parking lot.
 *
 * Implementations encapsulate different strategies for allocating and
 * managing parking spots while keeping the core ParkingLot API consistent.
 */
public interface ParkingLotAdmin {

    /**
     * Attempts to park a vehicle in the lot based on its type and identifier.
     *
     * @param identifier  the unique identifier of the vehicle
     * @param vehicleType the type of the vehicle
     * @return the list of parking spots assigned to the vehicle
     * @throws DoubleParkingException     if the vehicle is already parked
     * @throws ParkingUnavailableException if no suitable spot is available
     */
    List<ParkingSpot> parkVehicle(String identifier, VehicleType vehicleType)
            throws DoubleParkingException, ParkingUnavailableException;

    /**
     * Removes a vehicle from the lot using its identifier.
     *
     * @param identifier the unique identifier of the vehicle to remove
     */
    void removeVehicle(String identifier);

    /**
     * Retrieves all parking spots grouped by type.
     *
     * @return a map of spot type to the list of spots
     */
    Map<ParkingSpotType, List<ParkingSpot>> getSpotsByType();

    /**
     * Retrieves a mapping of vehicles to the parking spots they occupy.
     *
     * @return a map of vehicle identifiers to their assigned spots
     */
    Map<String, List<ParkingSpot>> getVehicleSpotsMap();

    /**
     * Prints a summary of the parking lot to the console.
     */
    void printLotSummary();

    /**
     * Generates a snapshot summary of the current state of the lot.
     *
     * @return a ParkingLotSummary containing occupancy and availability information
     */
    ParkingLotSummary generateLotSummary();
}
