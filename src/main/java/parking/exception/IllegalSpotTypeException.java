package parking.exception;

/**
 * Exception thrown when an invalid or unsupported parking spot type
 * is encountered in the parking lot configuration.
 */
public class IllegalSpotTypeException extends Exception {
    public IllegalSpotTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
