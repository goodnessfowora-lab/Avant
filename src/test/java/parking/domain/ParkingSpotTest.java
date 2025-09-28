package parking.domain;

import org.junit.Test;
import parking.enums.VehicleType;
import parking.exception.DoubleParkingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static parking.enums.ParkingSpotType.COMPACT;
import static parking.enums.ParkingSpotType.REGULAR;

public class ParkingSpotTest {

    @Test
    public void testSpotIsAvailableInitially() {
        ParkingSpot spot = new ParkingSpot("R1-1", COMPACT);
        assertTrue(spot.isAvailable());
    }

    @Test
    public void testAssignVehicleAndRemoveVehicle() throws DoubleParkingException {
        ParkingSpot spot = new ParkingSpot("R1-2", REGULAR);
        Vehicle car = new Vehicle("C1", VehicleType.CAR);

        spot.assignVehicle(car);
        assertFalse(spot.isAvailable());
        assertEquals(VehicleType.CAR, spot.getVehicleType());

        spot.removeVehicle();
        assertTrue(spot.isAvailable());
    }

    @Test
    public void testDoubleParkingThrowsException() throws DoubleParkingException {
        ParkingSpot spot = new ParkingSpot("R1-3", REGULAR);
        Vehicle car1 = new Vehicle("C2", VehicleType.CAR);
        Vehicle car2 = new Vehicle("C3", VehicleType.CAR);

        spot.assignVehicle(car1);
        assertThrows(DoubleParkingException.class, () -> spot.assignVehicle(car2));
    }
}
