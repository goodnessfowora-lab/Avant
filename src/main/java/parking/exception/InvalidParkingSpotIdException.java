package parking.exception;

/**
 * Exception thrown when a parking spot identifier is invalid or cannot be parsed.
 */
public class InvalidParkingSpotIdException extends Exception {
    public InvalidParkingSpotIdException(String message) {
        super(message);
    }
}
