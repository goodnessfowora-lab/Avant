package parking.lot;

import parking.domain.ParkingLotSummary;
import parking.domain.Vehicle;
import parking.enums.VehicleType;
import parking.spot.ParkingSpot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static parking.lot.ParkingLotUtil.createSpot;
import static parking.lot.ParkingLotUtil.findParkingSpot;

/**
 * ParkingLotAdmin is responsible for managing the parking lot:
 *  - Initializes spots by type and row
 *  - Handles parking and removing vehicles
 *  - Provides queries for the current status of the lot
 */
public class ParkingLotAdmin {
    private final long totalSpots; // total number of spots in the lot
    private final Map<String, List<ParkingSpot>> parkingSpotTypeMap; // spots grouped by type (REGULAR, COMPACT, etc.)
    private final Map<Vehicle, List<ParkingSpot>> vehicleSpotsMap;   // mapping of vehicles to the spots they occupy

    /**
     * Construct a parking lot with a fixed number of rows and a sequence of spot types per row.
     * Example rowSequence = "REGULAR, COMPACT"
     */
    public ParkingLotAdmin(int numOfRows, String rowSequence) {
        this.parkingSpotTypeMap = new HashMap<>();
        this.vehicleSpotsMap = new HashMap<>();

        String[] spotArrangement = rowSequence.split(",");
        this.totalSpots = (long) numOfRows * spotArrangement.length;

        // Build all parking spots row by row
        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 0; col < spotArrangement.length; col++) {
                String spotType = spotArrangement[col].strip();
                String spotId = String.format("R%d-%d", row, col + 1);

                ParkingSpot spot = createSpot(spotType, spotId);

                // Add the spot into the master type-based map
                parkingSpotTypeMap
                        .computeIfAbsent(spotType, k -> new ArrayList<>())
                        .add(spot);
            }
        }
    }

    /**
     * Attempts to park a vehicle into the lot.
     * @param identifier unique identifier of the vehicle
     * @param vehicleType type of vehicle (CAR, MOTORCYCLE, VAN, etc.)
     * @return list of allocated parking spots, or null if none available
     */
    public List<ParkingSpot> parkVehicle(String identifier, VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle(identifier, vehicleType);

        // If already parked, return existing allocation
        if (vehicleSpotsMap.containsKey(vehicle)) {
            return vehicleSpotsMap.get(vehicle);
        }

        // Find available spots for the vehicle
        List<ParkingSpot> parkingSpots = findParkingSpot(vehicle, parkingSpotTypeMap);
        if (parkingSpots != null) {
            // Mark each spot as occupied
            parkingSpots.forEach(parkingSpot -> parkingSpot.parkVehicle(vehicle));
            // Track allocation
            vehicleSpotsMap.put(vehicle, parkingSpots);
        }
        return parkingSpots;
    }

    /**
     * Removes a vehicle from the lot and frees its spots.
     * @param identifier vehicle identifier to remove
     */
    public void removeVehicle(String identifier) {
        // Look up the vehicle(s) with this identifier
        List<Vehicle> parkedVehicles = vehicleSpotsMap.keySet()
                .stream()
                .filter(vehicle -> identifier.equals(vehicle.getIdentifier()))
                .toList();

        if (parkedVehicles.size() > 1) {
            throw new RuntimeException("Multiple vehicles with same identifier " + identifier);
        }

        if (parkedVehicles.isEmpty()) return;

        Vehicle parkedVehicle = parkedVehicles.getFirst();

        // Free all spots allocated to this vehicle
        List<ParkingSpot> usedSpots = vehicleSpotsMap.remove(parkedVehicle);
        if (usedSpots != null) {
            usedSpots.forEach(ParkingSpot::removeVehicle);
        }
    }

    /** @return total number of spots configured in this lot */
    public long getTotalSpots() {
        return totalSpots;
    }

    /** @return read-only view of all spots grouped by type */
    public Map<String, List<ParkingSpot>> getSpotsByType() {
        return Collections.unmodifiableMap(parkingSpotTypeMap);
    }

    /** @return read-only view of all vehicles currently parked */
    public Map<Vehicle, List<ParkingSpot>> getVehicleSpotsMap() {
        return Collections.unmodifiableMap(vehicleSpotsMap);
    }

    // ===============================
    // Query Lot Status
    // ===============================

    /**
     * Builds a full immutable summary of the lot's current state.
     * @return ParkingLotSummary object containing all computed stats
     */
    public ParkingLotSummary getLotSummary() {
        // Overall totals
        long total = getTotalSpots();
        long available = parkingSpotTypeMap.values().stream()
                .flatMap(List::stream)
                .filter(ParkingSpot::isAvailable)
                .count();
        long occupied = total - available;

        // Per-type summary
        Map<String, ParkingLotSummary.SpotTypeStatus> byType = parkingSpotTypeMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            long avail = e.getValue().stream().filter(ParkingSpot::isAvailable).count();
                            return new ParkingLotSummary.SpotTypeStatus(
                                    e.getValue().size(), avail, e.getValue().size() - avail
                            );
                        }
                ));

        // Full/empty checks
        boolean isFull = available == 0;
        boolean isEmpty = occupied == 0;

        // Count vans
        long vanCount = parkingSpotTypeMap.values().stream()
                .flatMap(List::stream)
                .filter(spot -> !spot.isAvailable() && spot.getVehicleType() == VehicleType.VAN)
                .count();

        // Per-row summary (group spots by "R{row}" prefix in their IDs)
        Map<String, ParkingLotSummary.RowStatus> byRow = parkingSpotTypeMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                        spot -> spot.getParkingSpotId().split("-")[0],
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                spots -> {
                                    long avail = spots.stream().filter(ParkingSpot::isAvailable).count();
                                    return new ParkingLotSummary.RowStatus(spots.size(), avail, spots.size() - avail);
                                }
                        )
                ));

        return new ParkingLotSummary(total, available, occupied, byType, isFull, isEmpty, vanCount, byRow);
    }

    /**
     * Prints the current summary to standard output.
     * Useful for debugging and demos.
     */
    public void printLotSummary() {
        ParkingLotSummary summary = getLotSummary();

        System.out.println("=== Parking Lot Summary ===");
        System.out.printf("Overall -> Total: %d, Available: %d, Occupied: %d%n",
                summary.getTotalSpots(), summary.getAvailableSpots(), summary.getOccupiedSpots());

        summary.getByType().forEach((type, status) ->
                System.out.printf("[%s] -> Total: %d, Available: %d, Occupied: %d%n",
                        type, status.getTotal(), status.getAvailable(), status.getOccupied()));

        System.out.println("Lot full? " + summary.isFull());
        System.out.println("Lot empty? " + summary.isEmpty());
        System.out.println("Vans parked: " + summary.getVanCount());

        System.out.println("=== Row Summary ===");
        summary.getByRow().forEach((row, status) ->
                System.out.printf("%s -> Total: %d, Available: %d, Occupied: %d%n",
                        row, status.getTotal(), status.getAvailable(), status.getOccupied()));
    }
}
