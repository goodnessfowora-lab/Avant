package parking.exception;

/**
 * Exception thrown when a vehicle is detected as attempting to park
 * while already occupying a parking spot.
 */
public class DoubleParkingException extends RuntimeException {
    public DoubleParkingException(String message) {
        super(message);
    }
}
