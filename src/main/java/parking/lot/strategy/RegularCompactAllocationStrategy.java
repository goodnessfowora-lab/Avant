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
import java.util.stream.Collectors;

import static parking.enums.ParkingSpotType.COMPACT;
import static parking.enums.ParkingSpotType.REGULAR;

public class RegularCompactAllocationStrategy implements SpotAllocationStrategy {

    @Override
    public List<ParkingSpot> findParkingSpot(Vehicle vehicle, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        if (vehicle == null) {
            throw new ParkingUnavailableException("Vehicle is null, cannot allocate parking spot");
        }
        if (parkingSpotMap == null) {
            throw new ParkingUnavailableException("parkingSpotMap must not be null");
        }

        if (VehicleType.CAR.equals(vehicle.getType())) {
            return findSingleAvailableSpot(REGULAR, parkingSpotMap);
        } else if (VehicleType.MOTORCYCLE.equals(vehicle.getType())) {
            List<ParkingSpot> spot = findSingleAvailableSpot(COMPACT, parkingSpotMap);
            return spot != null ? spot : findSingleAvailableSpot(REGULAR, parkingSpotMap);
        } else {
            return findTwoAdjacentSpots(parkingSpotMap);
        }
    }

    private List<ParkingSpot> findSingleAvailableSpot(ParkingSpotType lotType, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        return parkingSpotMap.getOrDefault(lotType, Collections.emptyList())
                .stream()
                .filter(ParkingSpot::isAvailable)
                .findFirst()
                .map(Collections::singletonList)
                .orElse(null);
    }

    private List<ParkingSpot> findTwoAdjacentSpots(Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap)  {
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
        return null;
    }

    private String extractRowFromSpotId(String spotId) throws InvalidParkingSpotIdException {
        String[] parts = spotId.split("-");
        if (parts.length != 2) throw new InvalidParkingSpotIdException("Invalid spotId: " + spotId);
        return parts[0];
    }

    private int extractSpotNum(ParkingSpot spot) {
        return Integer.parseInt(spot.getParkingSpotId().split("-")[1]);
    }
}
