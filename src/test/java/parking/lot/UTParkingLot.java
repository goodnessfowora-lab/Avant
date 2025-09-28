package parking.lot;

import org.junit.jupiter.api.Test;
import parking.domain.ParkingSpot;
import parking.enums.ParkingLotAdminType;
import parking.enums.VehicleType;
import parking.exception.ParkingUnavailableException;
import parking.exception.IllegalSpotTypeException;
import parking.exception.IllegalParkingLotAdminException;

import static org.junit.jupiter.api.Assertions.*;

public class UTParkingLot {

    private ParkingLot lot;

    @Test
    public void testConstructorAndLotSize() throws IllegalSpotTypeException, IllegalParkingLotAdminException {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        assertEquals(4, lot.getParkingLotSize());
    }

    @Test
    public void testParkVehicleCar() throws Exception {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        ParkingSpot spot = lot.parkVehicle("C1", VehicleType.CAR);
        assertNotNull(spot);
        assertEquals("R1-1", spot.getParkingSpotId());
        assertFalse(spot.isAvailable());
    }

    @Test
    public void testParkVehicleMotorcycle() throws Exception {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        ParkingSpot spot = lot.parkVehicle("M1", VehicleType.MOTORCYCLE);
        assertNotNull(spot);
        assertEquals("R1-2", spot.getParkingSpotId());
        assertFalse(spot.isAvailable());
    }

    @Test
    public void testParkVehicleVan() throws Exception {
        lot = new ParkingLot(2, "COMPACT, REGULAR, COMPACT, REGULAR, REGULAR", ParkingLotAdminType.COMPACT_REGULAR);
        // Should allocate two adjacent REGULAR spots
        ParkingSpot spot = lot.parkVehicle("V1", VehicleType.VAN);
        assertNotNull(spot);
        assertEquals("R1-4", spot.getParkingSpotId());
        assertFalse(spot.isAvailable());
    }

    @Test
    public void testNoDoubleParkingThrows() throws Exception {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        lot.parkVehicle("C1", VehicleType.CAR);
        assertDoesNotThrow(() -> lot.parkVehicle("C1", VehicleType.CAR));
    }

    @Test
    public void testParkingUnavailableThrows() throws Exception {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        lot.parkVehicle("C1", VehicleType.CAR);
        lot.parkVehicle("C2", VehicleType.CAR);
        lot.parkVehicle("M1", VehicleType.MOTORCYCLE);
        lot.parkVehicle("M2", VehicleType.MOTORCYCLE);
        assertThrows(ParkingUnavailableException.class, () -> lot.parkVehicle("C3", VehicleType.CAR));
    }

    @Test
    public void testRemoveVehicleFreesSpot() throws Exception {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        ParkingSpot spot = lot.parkVehicle("C1", VehicleType.CAR);
        assertFalse(spot.isAvailable());
        lot.removeVehicle("C1");
        assertTrue(spot.isAvailable());
    }

    @Test
    public void testRemoveNonexistentVehicleDoesNothing() throws IllegalSpotTypeException, IllegalParkingLotAdminException {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        assertDoesNotThrow(() -> lot.removeVehicle("X"));
    }

    @Test
    public void testPrintLotSummary() throws IllegalSpotTypeException, IllegalParkingLotAdminException {
        lot = new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.COMPACT_REGULAR);
        // Just ensure no exceptions are thrown
        lot.printLotSummary();
    }

    @Test
    public void testUnsupportedParkingLotAdminTypeThrows() {
        assertThrows(IllegalParkingLotAdminException.class, () ->
                new ParkingLot(2, "REGULAR,COMPACT", ParkingLotAdminType.valueOf("UNSUPPORTED_TYPE"))
        );
    }
}
