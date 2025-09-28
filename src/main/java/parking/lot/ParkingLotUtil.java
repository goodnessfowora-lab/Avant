package parking.lot;

import parking.domain.Vehicle;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.domain.ParkingSpot;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static parking.enums.ParkingSpotType.COMPACT;
import static parking.enums.ParkingSpotType.REGULAR;

public final class ParkingLotUtil {
    public static List<ParkingSpot> findParkingSpot(Vehicle vehicle, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        Objects.requireNonNull(parkingSpotMap, "parkingSpotMap must not be null");

        if (VehicleType.CAR.equals(vehicle.getType())) {
            return findSingleAvailableSpot(REGULAR, parkingSpotMap);
        } else if (VehicleType.MOTORCYCLE.equals(vehicle.getType())) {
            List<ParkingSpot> spot = findSingleAvailableSpot(COMPACT, parkingSpotMap);
            return spot != null ? spot : findSingleAvailableSpot(REGULAR, parkingSpotMap);
        } else {
            return findTwoAdjacentSpots(parkingSpotMap);
        }
    }

    private static List<ParkingSpot> findSingleAvailableSpot(ParkingSpotType lotType, Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        return parkingSpotMap.getOrDefault(lotType, Collections.emptyList())
                .stream()
                .filter(ParkingSpot::isAvailable)
                .findFirst()
                .map(Collections::singletonList)
                .orElse(null);
    }

    private static List<ParkingSpot> findTwoAdjacentSpots(Map<ParkingSpotType, List<ParkingSpot>> parkingSpotMap) {
        Map<String, List<ParkingSpot>> availableSpotsMap =
                parkingSpotMap.getOrDefault(REGULAR, Collections.emptyList())
                        .stream()
                        .filter(ParkingSpot::isAvailable)
                        .collect(Collectors.groupingBy(
                                spot -> extractRowFromSpotId(spot.getParkingSpotId()),
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

    private static String extractRowFromSpotId(String spotId) {
        String[] parts = spotId.split("-");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid spotId: " + spotId);
        return parts[0];
    }

    private static int extractSpotNum(ParkingSpot spot) {
        return Integer.parseInt(spot.getParkingSpotId().split("-")[1]);
    }
}
