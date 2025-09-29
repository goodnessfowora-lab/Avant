package parking.lot.strategy;

import org.junit.jupiter.api.Test;
import parking.domain.Vehicle;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.domain.ParkingSpot;
import parking.exception.DoubleParkingException;
import parking.exception.ParkingUnavailableException;
import parking.exception.InvalidParkingSpotIdException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegularCompactAllocationStrategyTest {

    @Test
    public void testCreateSpot() {
        ParkingSpot regular = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot compact = new ParkingSpot("R1-2", ParkingSpotType.COMPACT);
        assertEquals("R1-1", regular.getParkingSpotId());
        assertEquals("R1-2", compact.getParkingSpotId());
    }

    @Test
    public void testFindSingleAvailableSpotForCar() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot spot = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, List.of(spot));
        Vehicle car = new Vehicle("C1", VehicleType.CAR);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(car, map);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("R1-1", result.get(0).getParkingSpotId());
    }

    @Test
    public void testFindSingleAvailableSpotForMotorcyclePrefersCompact() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot compact = new ParkingSpot("R1-2", ParkingSpotType.COMPACT);
        ParkingSpot regular = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.COMPACT, List.of(compact));
        map.put(ParkingSpotType.REGULAR, List.of(regular));
        Vehicle moto = new Vehicle("M1", VehicleType.MOTORCYCLE);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(moto, map);
        assertNotNull(result);
        assertEquals("R1-2", result.get(0).getParkingSpotId());
    }

    @Test
    public void testFindSingleAvailableSpotForMotorcycleFallsBackToRegular() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot regular = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, List.of(regular));
        Vehicle moto = new Vehicle("M2", VehicleType.MOTORCYCLE);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(moto, map);
        assertNotNull(result);
        assertEquals("R1-1", result.get(0).getParkingSpotId());
    }

    @Test
    public void testFindTwoAdjacentSpotsForVan() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot s2 = new ParkingSpot("R1-2", ParkingSpotType.REGULAR);
        ParkingSpot s3 = new ParkingSpot("R1-4", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, Arrays.asList(s1, s2, s3));
        Vehicle van = new Vehicle("V1", VehicleType.VAN);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(van, map);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("R1-1", result.get(0).getParkingSpotId());
        assertEquals("R1-2", result.get(1).getParkingSpotId());
    }

    @Test
    public void testNoAvailableSpotReturnsNull() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        Vehicle car = new Vehicle("C2", VehicleType.CAR);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(car, map);
        assertNull(result);
    }

    @Test
    public void testNoTwoAdjacentSpotsForVan() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot s2 = new ParkingSpot("R1-3", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, Arrays.asList(s1, s2));
        Vehicle van = new Vehicle("V2", VehicleType.VAN);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(van, map);
        assertNull(result);
    }

    @Test
    public void testAllSpotsOccupiedReturnsNull() throws DoubleParkingException, Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot regular = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot compact = new ParkingSpot("R1-2", ParkingSpotType.COMPACT);
        Vehicle parkedVehicle1 = new Vehicle("X1", VehicleType.CAR);
        Vehicle parkedVehicle2 = new Vehicle("X2", VehicleType.MOTORCYCLE);
        regular.assignVehicle(parkedVehicle1);
        compact.assignVehicle(parkedVehicle2);
        map.put(ParkingSpotType.REGULAR, List.of(regular));
        map.put(ParkingSpotType.COMPACT, List.of(compact));
        Vehicle car = new Vehicle("C3", VehicleType.CAR);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(car, map);
        assertNull(result);
    }

    @Test
    public void testNullVehicleThrowsCustomException() {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        assertThrows(ParkingUnavailableException.class, () -> {
            new RegularCompactAllocationStrategy().findParkingSpot(null, map);
        });
    }

    @Test
    public void testNullParkingSpotMapThrowsCustomException() {
        Vehicle car = new Vehicle("C4", VehicleType.CAR);
        assertThrows(ParkingUnavailableException.class, () -> {
            new RegularCompactAllocationStrategy().findParkingSpot(car, null);
        });
    }

    @Test
    public void testInvalidSpotIdThrowsParkingUnavailableException() {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("INVALID", ParkingSpotType.REGULAR);
        ParkingSpot s2 = new ParkingSpot("R1-2", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, Arrays.asList(s1, s2));
        Vehicle van = new Vehicle("V3", VehicleType.VAN);

        ParkingUnavailableException ex = assertThrows(ParkingUnavailableException.class, () -> {
            new RegularCompactAllocationStrategy().findParkingSpot(van, map);
        });
        assertTrue(ex.getCause() instanceof InvalidParkingSpotIdException);
    }

    @Test
    public void testAdjacentSpotsInDifferentRowsNotConsidered() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot s2 = new ParkingSpot("R2-2", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, Arrays.asList(s1, s2));
        Vehicle van = new Vehicle("V4", VehicleType.VAN);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(van, map);
        assertNull(result);
    }

    @Test
    public void testNonSequentialSpotNumbers() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot s2 = new ParkingSpot("R1-3", ParkingSpotType.REGULAR);
        ParkingSpot s3 = new ParkingSpot("R1-4", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, Arrays.asList(s1, s2, s3));
        Vehicle van = new Vehicle("V5", VehicleType.VAN);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(van, map);
        // Only R1-3 and R1-4 are adjacent
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("R1-3", result.get(0).getParkingSpotId());
        assertEquals("R1-4", result.get(1).getParkingSpotId());
    }

    @Test
    public void testOnlyOneSpotAvailableForVanReturnsNull() throws Exception {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, List.of(s1));
        Vehicle van = new Vehicle("V6", VehicleType.VAN);

        List<ParkingSpot> result = new RegularCompactAllocationStrategy().findParkingSpot(van, map);
        assertNull(result);
    }
}
