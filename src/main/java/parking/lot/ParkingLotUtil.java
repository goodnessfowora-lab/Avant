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
import java.util.stream.Collectors;

public final class ParkingLotUtil {
    public static List<ParkingSpot> findParkingSpot(Vehicle vehicle, Map<String, List<ParkingSpot>> parkingSpotMap) {
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
                                spot -> spot.getParkingSpotId().split("-")[0],
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

    private static int extractSpotNum(ParkingSpot spot) {
        return Integer.parseInt(spot.getParkingSpotId().split("-")[1]);
    }
}
