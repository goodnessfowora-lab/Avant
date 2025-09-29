package parking.lot.strategy;

import parking.domain.Vehicle;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.domain.ParkingSpot;
import parking.exception.InvalidParkingSpotIdException;
import parking.exception.ParkingUnavailableException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static parking.enums.ParkingSpotType.COMPACT;
import static parking.enums.ParkingSpotType.REGULAR;

/**
 * Allocation strategy for parking lots with compact and regular spots.
 *
 * <p>Rules:</p>
 * <ul>
 *   <li>Cars → prefer regular spots</li>
 *   <li>Motorcycles → prefer compact, fall back to regular</li>
 *   <li>Vans → require two adjacent regular spots</li>
 * </ul>
 */
public class RegularCompactAllocationStrategy implements SpotAllocationStrategy {

    /**
     * Finds one or more available parking spots for the given vehicle.
     *
     * @param vehicle        the vehicle requesting a spot
     * @param parkingSpotMap map of spots grouped by type
     * @return allocated spot(s), or {@code null} if no suitable spots are available
     * @throws ParkingUnavailableException if inputs are invalid or allocation fails
     */
    @Override
    public List<ParkingSpot> findParkingSpot(Vehicle vehicle, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        if (vehicle == null) {
            throw new ParkingUnavailableException("Vehicle is null, cannot allocate parking spot");
        }
        if (parkingSpotMap == null) {
            throw new ParkingUnavailableException("parkingSpotMap must not be null");
        }

        if (VehicleType.CAR.equals(vehicle.getType())) {
            return findSingleAvailableSpot(REGULAR, parkingSpotMap).orElse(Collections.emptyList());
        } else if (VehicleType.MOTORCYCLE.equals(vehicle.getType())) {
            return findSingleAvailableSpot(COMPACT, parkingSpotMap)
                    .or(() -> findSingleAvailableSpot(REGULAR, parkingSpotMap))
                    .orElse(Collections.emptyList());
        } else {
            return findTwoAdjacentSpots(parkingSpotMap);
        }
    }

    /**
     * Finds the first available spot of the given type.
     *
     * @param lotType        the type of spot to search
     * @param parkingSpotMap map of spots grouped by type
     * @return Optional containing the spot if found, or empty if none available
     */
    private Optional<List<ParkingSpot>> findSingleAvailableSpot(ParkingSpotType lotType, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        return parkingSpotMap.getOrDefault(lotType, Collections.emptyList())
                .stream()
                .filter(ParkingSpot::isAvailable)
                .findFirst()
                .map(Collections::singletonList);
    }

    /**
     * Attempts to find two adjacent available regular spots (for vans).
     *
     * @param parkingSpotMap map of spots grouped by type
     * @return two adjacent spots as a list, or an empty list if none found
     */
    private List<ParkingSpot> findTwoAdjacentSpots(Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        Map<String, List<ParkingSpot>> availableSpotsMap =
                parkingSpotMap.getOrDefault(REGULAR, Collections.emptyList())
                        .stream()
                        .filter(ParkingSpot::isAvailable)
                        .collect(Collectors.groupingBy(
                                spot -> {
                                    try {
                                        return extractRowFromSpotId(spot.getParkingSpotId());
                                    } catch (InvalidParkingSpotIdException e) {
                                        throw new ParkingUnavailableException(e.getMessage(), e);
                                    }
                                },
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        for (List<ParkingSpot> rowSpots : availableSpotsMap.values()) {
            if (rowSpots.size() < 2) continue;

            for (int i = 1; i < rowSpots.size(); i++) {
                int curr = extractSpotNum(rowSpots.get(i));
                int prev = extractSpotNum(rowSpots.get(i - 1));
                if (curr == prev + 1) {
                    return Arrays.asList(rowSpots.get(i - 1), rowSpots.get(i));
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Extracts the row portion of a spot ID.
     *
     * @param spotId the spot identifier (format "R#-#")
     * @return row label (e.g., "R1")
     * @throws InvalidParkingSpotIdException if the ID format is invalid
     */
    private String extractRowFromSpotId(String spotId) throws InvalidParkingSpotIdException {
        String[] parts = spotId.split("-");
        if (parts.length != 2) throw new InvalidParkingSpotIdException("Invalid spotId: " + spotId);
        return parts[0];
    }

    /**
     * Extracts the numeric portion of a spot ID for ordering.
     *
     * @param spot the parking spot
     * @return the numeric column index of the spot
     */
    private int extractSpotNum(ParkingSpot spot) {
        return Integer.parseInt(spot.getParkingSpotId().split("-")[1]);
    }
}
