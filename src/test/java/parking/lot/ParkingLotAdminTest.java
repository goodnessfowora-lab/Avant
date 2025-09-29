package parking.lot;

import org.junit.jupiter.api.Test;
import parking.domain.ParkingLotSummary;
import parking.domain.ParkingSpot;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.exception.DoubleParkingException;
import parking.exception.IllegalSpotTypeException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingLotAdminTest {

    private ParkingLotAdmin admin;

    @Test
    public void testInitialization() throws IllegalSpotTypeException {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        Map<ParkingSpotType, List<ParkingSpot>> spots = admin.getSpotsByType();
        assertEquals(2, spots.get(ParkingSpotType.REGULAR).size());
        assertEquals(2, spots.get(ParkingSpotType.COMPACT).size());
        assertTrue(admin.getVehicleSpotsMap().isEmpty());
    }

    @Test
    public void testParkCar() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        List<ParkingSpot> allocated = admin.parkVehicle("C1", VehicleType.CAR);
        assertNotNull(allocated);
        assertEquals(1, allocated.size());
        assertEquals(ParkingSpotType.REGULAR, allocated.get(0).getSpotType());
        assertFalse(allocated.get(0).isAvailable());
        assertEquals(allocated, admin.getVehicleSpotsMap().get("C1"));
        // Should not double park
        assertEquals(allocated, admin.parkVehicle("C1", VehicleType.CAR));
    }

    @Test
    public void testParkMotorcyclePrefersCompact() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        List<ParkingSpot> allocated = admin.parkVehicle("M1", VehicleType.MOTORCYCLE);
        assertNotNull(allocated);
        assertEquals(1, allocated.size());
        assertEquals(ParkingSpotType.COMPACT, allocated.get(0).getSpotType());
    }

    @Test
    public void testParkVanNeedsTwoAdjacentRegular() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT,COMPACT");
        List<ParkingSpot> allocated = admin.parkVehicle("V1", VehicleType.VAN);
        assertNull(allocated);

        admin = new RegularCompactLotAdmin(2, "REGULAR,REGULAR,COMPACT");
        allocated = admin.parkVehicle("V1", VehicleType.VAN);
        assertNotNull(allocated);
        assertEquals(2, allocated.size());
        assertEquals(ParkingSpotType.REGULAR, allocated.get(0).getSpotType());
        assertEquals(ParkingSpotType.REGULAR, allocated.get(1).getSpotType());
        assertEquals("R1-1", allocated.get(0).getParkingSpotId());
        assertEquals("R1-2", allocated.get(1).getParkingSpotId());
        assertFalse(allocated.get(0).isAvailable());
        assertFalse(allocated.get(1).isAvailable());
    }

    @Test
    public void testNoSpotAvailableReturnsNull() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        // Fill all spots
        admin.parkVehicle("C1", VehicleType.CAR);
        admin.parkVehicle("C2", VehicleType.CAR);
        admin.parkVehicle("M1", VehicleType.MOTORCYCLE);
        admin.parkVehicle("M2", VehicleType.MOTORCYCLE);
        // Now no spot for another car
        assertNull(admin.parkVehicle("C3", VehicleType.CAR));
    }

    @Test
    public void testRemoveVehicleFreesSpot() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        List<ParkingSpot> allocated = admin.parkVehicle("C1", VehicleType.CAR);
        assertNotNull(allocated);
        admin.removeVehicle("C1");
        assertTrue(allocated.get(0).isAvailable());
        assertFalse(admin.getVehicleSpotsMap().containsKey("C1"));
    }

    @Test
    public void testRemoveNonexistentVehicleDoesNothing() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        assertDoesNotThrow(() -> admin.removeVehicle("X"));
    }

    @Test
    public void testSummaryReflectsState() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        admin.parkVehicle("C1", VehicleType.CAR);
        admin.parkVehicle("M1", VehicleType.MOTORCYCLE);
        ParkingLotSummary summary = admin.generateLotSummary();
        assertEquals(4, summary.getTotalSpots());
        assertEquals(2, summary.getAvailableSpots());
        assertEquals(2, summary.getOccupiedSpots());
        assertFalse(summary.isFull());
        assertFalse(summary.isEmpty());
        assertEquals(0, summary.getVanCount());
        assertEquals(2, summary.getByType().size());
        assertEquals(2, summary.getByRow().size());
    }

    @Test
    public void testInvalidSpotTypeThrows() {
        assertThrows(IllegalSpotTypeException.class, () -> new RegularCompactLotAdmin(1, "REGULAR,INVALID"));
    }

    @Test
    public void testDoubleParkingThrows() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        List<ParkingSpot> allocated = admin.parkVehicle("C1", VehicleType.CAR);
        assertNotNull(allocated);
        ParkingSpot spot = allocated.get(0);
        assertThrows(DoubleParkingException.class, () -> spot.assignVehicle(new parking.domain.Vehicle("C2", VehicleType.CAR)));
    }

    @Test
    public void testPrintLotSummaryDoesNotThrow() throws Exception {
        admin = new RegularCompactLotAdmin(2, "REGULAR,COMPACT");
        assertDoesNotThrow(() -> admin.printLotSummary());
    }
}
