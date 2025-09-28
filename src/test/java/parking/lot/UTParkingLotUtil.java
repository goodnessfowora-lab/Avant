package parking.lot;

import org.junit.Test;
import parking.domain.Vehicle;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.domain.ParkingSpot;
import parking.exception.DoubleParkingException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UTParkingLotUtil {

    @Test
    public void testCreateSpot() {
        ParkingSpot regular = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot compact = new ParkingSpot("R1-2", ParkingSpotType.COMPACT);
        assertEquals("R1-1", regular.getParkingSpotId());
        assertEquals("R1-2", compact.getParkingSpotId());
    }

    @Test
    public void testFindSingleAvailableSpotForCar() {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot spot = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, List.of(spot));
        Vehicle car = new Vehicle("C1", VehicleType.CAR);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(car, map);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("R1-1", result.get(0).getParkingSpotId());
    }

    @Test
    public void testFindSingleAvailableSpotForMotorcyclePrefersCompact() {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot compact = new ParkingSpot("R1-2", ParkingSpotType.COMPACT);
        ParkingSpot regular = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.COMPACT, List.of(compact));
        map.put(ParkingSpotType.REGULAR, List.of(regular));
        Vehicle moto = new Vehicle("M1", VehicleType.MOTORCYCLE);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(moto, map);
        assertNotNull(result);
        assertEquals("R1-2", result.get(0).getParkingSpotId());
    }

    @Test
    public void testFindTwoAdjacentSpotsForVan() {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot s2 = new ParkingSpot("R1-2", ParkingSpotType.REGULAR);
        ParkingSpot s3 = new ParkingSpot("R1-4", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, Arrays.asList(s1, s2, s3));
        Vehicle van = new Vehicle("V1", VehicleType.VAN);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(van, map);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("R1-1", result.get(0).getParkingSpotId());
        assertEquals("R1-2", result.get(1).getParkingSpotId());
    }

    @Test
    public void testNoAvailableSpotReturnsNull() {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        Vehicle car = new Vehicle("C2", VehicleType.CAR);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(car, map);
        assertNull(result);
    }

    @Test
    public void testNoTwoAdjacentSpotsForVan() {
        Map<ParkingSpotType, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot s1 = new ParkingSpot("R1-1", ParkingSpotType.REGULAR);
        ParkingSpot s2 = new ParkingSpot("R1-3", ParkingSpotType.REGULAR);
        map.put(ParkingSpotType.REGULAR, Arrays.asList(s1, s2));
        Vehicle van = new Vehicle("V2", VehicleType.VAN);

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(van, map);
        assertNull(result);
    }

    @Test
    public void testAllSpotsOccupiedReturnsNull() throws DoubleParkingException {
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

        List<ParkingSpot> result = ParkingLotUtil.findParkingSpot(car, map);
        assertNull(result);
    }
}
