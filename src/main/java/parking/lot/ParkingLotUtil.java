package parking.lot;

import parking.domain.Vehicle;
import parking.enums.ParkingLotConstants;
import parking.enums.VehicleType;
import parking.spot.CompactSpot;
import parking.spot.ParkingSpot;
import parking.spot.RegularSpot;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ParkingLotUtil {
    public static List<ParkingSpot> findParkingSpot(Vehicle vehicle, Map<String, List<ParkingSpot>> parkingSpotMap) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        Objects.requireNonNull(parkingSpotMap, "parkingSpotMap must not be null");

        if (VehicleType.CAR.equals(vehicle.getType())) {
            return findSingleAvailableSpot(ParkingLotConstants.REGULAR, parkingSpotMap);
        } else if (VehicleType.MOTORCYCLE.equals(vehicle.getType())) {
            List<ParkingSpot> spot = findSingleAvailableSpot(ParkingLotConstants.COMPACT, parkingSpotMap);
            return spot != null ? spot : findSingleAvailableSpot(ParkingLotConstants.REGULAR, parkingSpotMap);
        } else {
            return findTwoAdjacentSpots(parkingSpotMap);
        }
    }

    public static ParkingSpot createSpot(String spotType, String spotId) {
        Objects.requireNonNull(spotType, "spotType must not be null");
        Objects.requireNonNull(spotId, "spotId must not be null");
        return switch (spotType) {
            case ParkingLotConstants.REGULAR -> new RegularSpot(spotId);
            case ParkingLotConstants.COMPACT -> new CompactSpot(spotId);
            default -> throw new IllegalArgumentException("Unknown spot type: " + spotType);
        };
    }

    private static List<ParkingSpot> findSingleAvailableSpot(String lotType, Map<String, List<ParkingSpot>> parkingSpotMap) {
        return parkingSpotMap.getOrDefault(lotType, Collections.emptyList())
                .stream()
                .filter(ParkingSpot::isAvailable)
                .findFirst()
                .map(Collections::singletonList)
                .orElse(null);
    }

    private static List<ParkingSpot> findTwoAdjacentSpots(Map<String, List<ParkingSpot>> parkingSpotMap) {
        Map<String, List<ParkingSpot>> availableSpotsMap =
                parkingSpotMap.getOrDefault(ParkingLotConstants.REGULAR, Collections.emptyList())
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
