package parking.domain;

import parking.enums.ParkingSpotType;

import java.util.Map;

/**
 * Immutable data object summarizing the state of the parking lot.
 */
public final class ParkingLotSummary {
    private final long totalSpots;
    private final long availableSpots;
    private final long occupiedSpots;
    private final Map<ParkingSpotType, SpotTypeStatus> byType;
    private final boolean isFull;
    private final boolean isEmpty;
    private final long vanCount;
    private final Map<String, RowStatus> byRow;

    public ParkingLotSummary(
            long totalSpots,
            long availableSpots,
            long occupiedSpots,
            Map<ParkingSpotType, SpotTypeStatus> byType,
            boolean isFull,
            boolean isEmpty,
            long vanCount,
            Map<String, RowStatus> byRow
    ) {
        if (totalSpots < 0 || availableSpots < 0 || occupiedSpots < 0 || vanCount < 0) {
            throw new IllegalArgumentException("Counts cannot be negative");
        }
        this.totalSpots = totalSpots;
        this.availableSpots = availableSpots;
        this.occupiedSpots = occupiedSpots;
        this.byType = Map.copyOf(byType);   // defensive copy
        this.isFull = isFull;
        this.isEmpty = isEmpty;
        this.vanCount = vanCount;
        this.byRow = Map.copyOf(byRow);     // defensive copy
    }

    public long getTotalSpots() { return totalSpots; }
    public long getAvailableSpots() { return availableSpots; }
    public long getOccupiedSpots() { return occupiedSpots; }
    public Map<ParkingSpotType, SpotTypeStatus> getByType() { return byType; }
    public boolean isFull() { return isFull; }
    public boolean isEmpty() { return isEmpty; }
    public long getVanCount() { return vanCount; }
    public Map<String, RowStatus> getByRow() { return byRow; }

    public static final class SpotTypeStatus {
        private final long total;
        private final long available;
        private final long occupied;

        public SpotTypeStatus(long total, long available, long occupied) {
            if (total < 0 || available < 0 || occupied < 0) {
                throw new IllegalArgumentException("Counts cannot be negative");
            }
            this.total = total;
            this.available = available;
            this.occupied = occupied;
        }

        public long getTotal() { return total; }
        public long getAvailable() { return available; }
        public long getOccupied() { return occupied; }
    }

    public static final class RowStatus {
        private final long total;
        private final long available;
        private final long occupied;

        public RowStatus(long total, long available, long occupied) {
            if (total < 0 || available < 0 || occupied < 0) {
                throw new IllegalArgumentException("Counts cannot be negative");
            }
            this.total = total;
            this.available = available;
            this.occupied = occupied;
        }

        public long getTotal() { return total; }
        public long getAvailable() { return available; }
        public long getOccupied() { return occupied; }
    }
}

