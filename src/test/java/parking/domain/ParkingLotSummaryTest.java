package parking.domain;

import org.junit.Test;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingLotSummaryTest {

    @Test
    public void testConstructorAndGetters() {
        Map<String, ParkingLotSummary.SpotTypeStatus> byType = Map.of(
                "COMPACT", new ParkingLotSummary.SpotTypeStatus(10, 4, 6)
        );
        Map<String, ParkingLotSummary.RowStatus> byRow = Map.of(
                "A", new ParkingLotSummary.RowStatus(5, 2, 3)
        );

        ParkingLotSummary summary = new ParkingLotSummary(
                20, 8, 12, byType, true, false, 2, byRow
        );

        assertEquals(20, summary.getTotalSpots());
        assertEquals(8, summary.getAvailableSpots());
        assertEquals(12, summary.getOccupiedSpots());
        assertEquals(byType, summary.getByType());
        assertTrue(summary.isFull());
        assertFalse(summary.isEmpty());
        assertEquals(2, summary.getVanCount());
        assertEquals(byRow, summary.getByRow());
    }

    @Test
    public void testImmutabilityOfByTypeAndByRow() {
        Map<String, ParkingLotSummary.SpotTypeStatus> byType = new HashMap<>();
        byType.put("LARGE", new ParkingLotSummary.SpotTypeStatus(5, 1, 4));
        Map<String, ParkingLotSummary.RowStatus> byRow = new HashMap<>();
        byRow.put("B", new ParkingLotSummary.RowStatus(3, 0, 3));

        ParkingLotSummary summary = new ParkingLotSummary(
                10, 1, 9, byType, false, false, 1, byRow
        );

        assertThrows(UnsupportedOperationException.class, () -> summary.getByType().put("SMALL", new ParkingLotSummary.SpotTypeStatus(2, 2, 0)));
        assertThrows(UnsupportedOperationException.class, () -> summary.getByRow().put("C", new ParkingLotSummary.RowStatus(2, 2, 0)));
    }

    @Test
    public void testSpotTypeStatusGetters() {
        ParkingLotSummary.SpotTypeStatus status = new ParkingLotSummary.SpotTypeStatus(7, 3, 4);
        assertEquals(7, status.getTotal());
        assertEquals(3, status.getAvailable());
        assertEquals(4, status.getOccupied());
    }

    @Test
    public void testRowStatusGetters() {
        ParkingLotSummary.RowStatus status = new ParkingLotSummary.RowStatus(6, 2, 4);
        assertEquals(6, status.getTotal());
        assertEquals(2, status.getAvailable());
        assertEquals(4, status.getOccupied());
    }

    @Test
    public void testNullByTypeThrowsException() {
        Map<String, ParkingLotSummary.RowStatus> byRow = Map.of();
        assertThrows(NullPointerException.class, () -> new ParkingLotSummary(1, 1, 0, null, false, true, 0, byRow));
    }

    @Test
    public void testNullByRowThrowsException() {
        Map<String, ParkingLotSummary.SpotTypeStatus> byType = Map.of();
        assertThrows(NullPointerException.class, () -> new ParkingLotSummary(1, 1, 0, byType, false, true, 0, null));
    }

    @Test
    public void testEmptyMapsAllowed() {
        ParkingLotSummary summary = new ParkingLotSummary(
                0, 0, 0, Map.of(), false, true, 0, Map.of()
        );
        assertTrue(summary.getByType().isEmpty());
        assertTrue(summary.getByRow().isEmpty());
    }

    @Test
    public void testNegativeValuesRejected() {
        Map<String, ParkingLotSummary.SpotTypeStatus> byType = Map.of();
        Map<String, ParkingLotSummary.RowStatus> byRow = Map.of();
        assertThrows(IllegalArgumentException.class, () -> new ParkingLotSummary(-1, 0, 0, byType, false, true, 0, byRow));
        assertThrows(IllegalArgumentException.class, () -> new ParkingLotSummary(1, -1, 0, byType, false, true, 0, byRow));
        assertThrows(IllegalArgumentException.class, () -> new ParkingLotSummary(1, 0, -1, byType, false, true, 0, byRow));
        assertThrows(IllegalArgumentException.class, () -> new ParkingLotSummary(1, 0, 0, byType, false, true, -1, byRow));
        assertThrows(IllegalArgumentException.class, () -> new ParkingLotSummary.SpotTypeStatus(-1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new ParkingLotSummary.RowStatus(0, -1, 0));
    }

    @Test
    public void testOriginalMapsModificationDoesNotAffectSummary() {
        Map<String, ParkingLotSummary.SpotTypeStatus> byType = new HashMap<>();
        byType.put("COMPACT", new ParkingLotSummary.SpotTypeStatus(1, 1, 0));
        Map<String, ParkingLotSummary.RowStatus> byRow = new HashMap<>();
        byRow.put("A", new ParkingLotSummary.RowStatus(1, 1, 0));

        ParkingLotSummary summary = new ParkingLotSummary(
                1, 1, 0, byType, false, false, 0, byRow
        );

        byType.put("LARGE", new ParkingLotSummary.SpotTypeStatus(2, 2, 0));
        byRow.put("B", new ParkingLotSummary.RowStatus(2, 2, 0));

        assertFalse(summary.getByType().containsKey("LARGE"));
        assertFalse(summary.getByRow().containsKey("B"));
    }

}