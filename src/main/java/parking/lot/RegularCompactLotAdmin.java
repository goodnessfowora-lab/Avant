package parking.lot;

import parking.domain.ParkingLotSummary;
import parking.domain.ParkingSpot;
import parking.domain.Vehicle;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.exception.DoubleParkingException;
import parking.exception.IllegalSpotTypeException;
import parking.exception.ParkingUnavailableException;
import parking.lot.strategy.RegularCompactAllocationStrategy;
import parking.lot.strategy.SpotAllocationStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegularCompactLotAdmin implements ParkingLotAdmin {
    private final Map<ParkingSpotType, List<ParkingSpot>> parkingSpotTypeMap; // spots grouped by type (REGULAR, COMPACT, etc.)
    private final Map<String, List<ParkingSpot>> vehicleSpotsMap;   // mapping of vehicles to the spots they occupy
    private final SpotAllocationStrategy allocationStrategy;

    /**
     * Construct a parking lot with a fixed number of rows and a sequence of spot types per row.
     * Example rowSequence = "REGULAR, COMPACT"
     */
    public RegularCompactLotAdmin(int numOfRows, String rowSequence) throws IllegalSpotTypeException {
        this.parkingSpotTypeMap = new HashMap<>();
        this.vehicleSpotsMap = new HashMap<>();
        this.allocationStrategy = new RegularCompactAllocationStrategy();

        String[] spotArrangement = rowSequence.split(",");
        ParkingSpotType[] validSpotArrangement = new ParkingSpotType[spotArrangement.length];
        for(int i=0; i<spotArrangement.length; i++) {
            try {
                validSpotArrangement[i] = ParkingSpotType.valueOf(spotArrangement[i].strip());
            } catch (IllegalArgumentException e) {
                throw new IllegalSpotTypeException("Invalid spot type in row sequence: " + spotArrangement[i], e);
            }
        }

        // Build all parking spots row by row
        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 0; col < validSpotArrangement.length; col++) {
                ParkingSpotType spotType = validSpotArrangement[col];
                String spotId = String.format("R%d-%d", row, col + 1);
                ParkingSpot spot = new ParkingSpot(spotId, spotType);
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

    @Override
    public List<ParkingSpot> parkVehicle(String identifier, VehicleType vehicleType) throws DoubleParkingException, ParkingUnavailableException {
        // If already parked, return existing allocation
        if (vehicleSpotsMap.containsKey(identifier)) {
            return vehicleSpotsMap.get(identifier);
        }

        Vehicle vehicle = new Vehicle(identifier, vehicleType);

        // Find available spots for the vehicle
        List<ParkingSpot> parkingSpots = allocationStrategy.findParkingSpot(vehicle, parkingSpotTypeMap);
        if (parkingSpots != null) {
            // Mark each spot as occupied
            for (ParkingSpot parkingSpot : parkingSpots) {
                parkingSpot.assignVehicle(vehicle);
            }
            // Track allocation
            vehicleSpotsMap.put(identifier, parkingSpots);
        }
        return parkingSpots;
    }

    /**
     * Removes a vehicle from the lot and frees its spots.
     * @param identifier vehicle identifier to remove
     */
    @Override
    public void removeVehicle(String identifier) {
        // Free all spots allocated to this vehicle
        List<ParkingSpot> usedSpots = vehicleSpotsMap.remove(identifier);
        if (usedSpots != null) {
            usedSpots.forEach(ParkingSpot::removeVehicle);
        }
    }

    /** @return read-only view of all spots grouped by type */
    @Override
    public Map<ParkingSpotType, List<ParkingSpot>> getSpotsByType() {
        return Collections.unmodifiableMap(parkingSpotTypeMap);
    }

    /** @return read-only view of all vehicles currently parked */
    @Override
    public Map<String, List<ParkingSpot>> getVehicleSpotsMap() {
        return Collections.unmodifiableMap(vehicleSpotsMap);
    }

    // ===============================
    // Query Lot Status
    // ===============================

    /**
     * Prints the current summary to standard output.
     * Useful for debugging and demos.
     */
    @Override
    public void printLotSummary() {
        ParkingLotSummary summary = generateLotSummary();

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

    /**
     * Builds a full immutable summary of the lot's current state.
     * @return ParkingLotSummary object containing all computed stats
     */
    @Override
    public ParkingLotSummary generateLotSummary() {
        if (parkingSpotTypeMap == null || parkingSpotTypeMap.isEmpty()) {
            return new ParkingLotSummary(0, 0, 0, Collections.emptyMap(), true, true, 0, Collections.emptyMap());
        }

        Map<ParkingSpotType, ParkingLotSummary.SpotTypeStatus> byType = new HashMap<>();
        Map<String, ParkingLotSummary.RowStatus> byRow = new LinkedHashMap<>();
        long total = 0, available = 0, occupied = 0, vanCount = 0;

        for (Map.Entry<ParkingSpotType, List<ParkingSpot>> entry : parkingSpotTypeMap.entrySet()) {
            ParkingSpotType type = entry.getKey();
            List<ParkingSpot> spots = entry.getValue();
            if (spots == null) continue;

            long typeTotal = 0, typeAvailable = 0;
            for (ParkingSpot spot : spots) {
                if (spot == null) continue;
                total++;
                typeTotal++;
                String spotId = spot.getParkingSpotId();
                String row = (spotId != null && spotId.contains("-")) ? spotId.split("-")[0] : "UNKNOWN";

                boolean isAvail = spot.isAvailable();
                if (isAvail) {
                    available++;
                    typeAvailable++;
                } else {
                    occupied++;
                    if (spot.getVehicleType() == VehicleType.VAN) vanCount++;
                }

                // Row summary
                ParkingLotSummary.RowStatus rowStatus = byRow.getOrDefault(row, new ParkingLotSummary.RowStatus(0, 0, 0));
                long rowTotal = rowStatus.getTotal() + 1;
                long rowAvailable = rowStatus.getAvailable() + (isAvail ? 1 : 0);
                long rowOccupied = rowTotal - rowAvailable;
                byRow.put(row, new ParkingLotSummary.RowStatus(rowTotal, rowAvailable, rowOccupied));
            }
            long typeOccupied = typeTotal - typeAvailable;
            byType.put(type, new ParkingLotSummary.SpotTypeStatus(typeTotal, typeAvailable, typeOccupied));
        }

        boolean isFull = available == 0;
        boolean isEmpty = occupied == 0;

        return new ParkingLotSummary(total, available, occupied, byType, isFull, isEmpty, vanCount, byRow);
    }



}
