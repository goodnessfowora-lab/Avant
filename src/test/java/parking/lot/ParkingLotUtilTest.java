package parking.lot;

import org.junit.Test;
import parking.domain.Vehicle;
import parking.enums.ParkingLotConstants;
import parking.enums.VehicleType;
import parking.spot.ParkingSpot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ParkingLotUtilTest {

    @Test
    public void testCreateSpot() {
        ParkingSpot regular = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-1");
        ParkingSpot compact = ParkingLotUtil.createSpot(ParkingLotConstants.COMPACT, "R1-2");
        assertEquals("R1-1", regular.getParkingSpotId());
        assertEquals("R1-2", compact.getParkingSpotId());
    }

    @Test
    public void testFindSingleAvailableSpotForCar() {
        Map<String, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot spot = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-1");
        map.put(ParkingLotConstants.REGULAR, List.of(spot));
        Vehicle car = new Vehicle("C1", VehicleType.CAR);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(car, map);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("R1-1", result.get(0).getParkingSpotId());
    }

    @Test
    public void testFindSingleAvailableSpotForMotorcyclePrefersCompact() {
        Map<String, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot compact = ParkingLotUtil.createSpot(ParkingLotConstants.COMPACT, "R1-2");
        ParkingSpot regular = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-1");
        map.put(ParkingLotConstants.COMPACT, List.of(compact));
        map.put(ParkingLotConstants.REGULAR, List.of(regular));
        Vehicle moto = new Vehicle("M1", VehicleType.MOTORCYCLE);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(moto, map);
        assertNotNull(result);
        assertEquals("R1-2", result.get(0).getParkingSpotId());
    }

    @Test
    public void testFindTwoAdjacentSpotsForVan() {
        Map<String, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-1");
        ParkingSpot s2 = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-2");
        ParkingSpot s3 = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-4");
        map.put(ParkingLotConstants.REGULAR, Arrays.asList(s1, s2, s3));
        Vehicle van = new Vehicle("V1", VehicleType.VAN);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(van, map);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("R1-1", result.get(0).getParkingSpotId());
        assertEquals("R1-2", result.get(1).getParkingSpotId());
    }

    @Test
    public void testNoAvailableSpotReturnsNull() {
        Map<String, List<ParkingSpot>> map = new HashMap<>();
        Vehicle car = new Vehicle("C2", VehicleType.CAR);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(car, map);
        assertNull(result);
    }

    @Test
    public void testNoTwoAdjacentSpotsForVan() {
        Map<String, List<ParkingSpot>> map = new HashMap<>();
        // Only non-adjacent available spots
        ParkingSpot s1 = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-1");
        ParkingSpot s2 = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-3");
        map.put(ParkingLotConstants.REGULAR, Arrays.asList(s1, s2));
        Vehicle van = new Vehicle("V2", VehicleType.VAN);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(van, map);
        assertNull(result);
    }

    @Test
    public void testAllSpotsOccupiedReturnsNull() {
        Map<String, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot regular = ParkingLotUtil.createSpot(ParkingLotConstants.REGULAR, "R1-1");
        ParkingSpot compact = ParkingLotUtil.createSpot(ParkingLotConstants.COMPACT, "R1-2");
        Vehicle parkedVehicle1 = new Vehicle("X1", VehicleType.CAR);
        Vehicle parkedVehicle2 = new Vehicle("X2", VehicleType.MOTORCYCLE);
        regular.parkVehicle(parkedVehicle1);
        compact.parkVehicle(parkedVehicle2);
        map.put(ParkingLotConstants.REGULAR, List.of(regular));
        map.put(ParkingLotConstants.COMPACT, List.of(compact));
        Vehicle car = new Vehicle("C3", VehicleType.CAR);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(car, map);
        assertNull(result);
    }


}