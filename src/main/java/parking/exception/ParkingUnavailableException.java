package parking.exception;

/**
 * Exception thrown when a vehicle cannot be parked because
 * no suitable parking spot is available.
 */
public class ParkingUnavailableException extends RuntimeException {
    public ParkingUnavailableException(String message) {
        super(message);
    }

    public ParkingUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
