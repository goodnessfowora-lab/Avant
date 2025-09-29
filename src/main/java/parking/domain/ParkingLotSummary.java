package parking.domain;

import parking.enums.ParkingSpotType;

import java.util.Map;

/**
 * Immutable data object summarizing the state of the parking lot.
 * Provides overall and detailed statistics about parking spots and rows.
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

    /**
     * Constructs a ParkingLotSummary with the provided statistics.
     *
     * @param totalSpots     total number of parking spots
     * @param availableSpots number of available parking spots
     * @param occupiedSpots  number of occupied parking spots
     * @param byType         map of parking spot type to its status
     * @param isFull         true if the parking lot is full
     * @param isEmpty        true if the parking lot is empty
     * @param vanCount       number of vans currently parked
     * @param byRow          map of row identifier to its status
     * @throws IllegalArgumentException if any count is negative
     */
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

    /**
     * @return total number of parking spots in the lot
     */
    public long getTotalSpots() { return totalSpots; }

    /**
     * @return number of available parking spots
     */
    public long getAvailableSpots() { return availableSpots; }

    /**
     * @return number of occupied parking spots
     */
    public long getOccupiedSpots() { return occupiedSpots; }

    /**
     * @return map of parking spot type to its status
     */
    public Map<ParkingSpotType, SpotTypeStatus> getByType() { return byType; }

    /**
     * @return true if the parking lot is full
     */
    public boolean isFull() { return isFull; }

    /**
     * @return true if the parking lot is empty
     */
    public boolean isEmpty() { return isEmpty; }

    /**
     * @return number of vans currently parked
     */
    public long getVanCount() { return vanCount; }

    /**
     * @return map of row identifier to its status
     */
    public Map<String, RowStatus> getByRow() { return byRow; }

    /**
     * Immutable data object representing the status of a specific parking spot type.
     */
    public static final class SpotTypeStatus {
        private final long total;
        private final long available;
        private final long occupied;

        /**
         * Constructs a SpotTypeStatus with the provided statistics.
         *
         * @param total     total number of spots of this type
         * @param available number of available spots of this type
         * @param occupied  number of occupied spots of this type
         * @throws IllegalArgumentException if any count is negative
         */
        public SpotTypeStatus(long total, long available, long occupied) {
            if (total < 0 || available < 0 || occupied < 0) {
                throw new IllegalArgumentException("Counts cannot be negative");
            }
            this.total = total;
            this.available = available;
            this.occupied = occupied;
        }

        /**
         * @return total number of spots of this type
         */
        public long getTotal() { return total; }

        /**
         * @return number of available spots of this type
         */
        public long getAvailable() { return available; }

        /**
         * @return number of occupied spots of this type
         */
        public long getOccupied() { return occupied; }
    }

    /**
     * Immutable data object representing the status of a specific row in the parking lot.
     */
    public static final class RowStatus {
        private final long total;
        private final long available;
        private final long occupied;

        /**
         * Constructs a RowStatus with the provided statistics.
         *
         * @param total     total number of spots in the row
         * @param available number of available spots in the row
         * @param occupied  number of occupied spots in the row
         * @throws IllegalArgumentException if any count is negative
         */
        public RowStatus(long total, long available, long occupied) {
            if (total < 0 || available < 0 || occupied < 0) {
                throw new IllegalArgumentException("Counts cannot be negative");
            }
            this.total = total;
            this.available = available;
            this.occupied = occupied;
        }

        /**
         * @return total number of spots in the row
         */
        public long getTotal() { return total; }

        /**
         * @return number of available spots in the row
         */
        public long getAvailable() { return available; }

        /**
         * @return number of occupied spots in the row
         */
        public long getOccupied() { return occupied; }
    }
}
