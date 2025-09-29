package parking.exception;

/**
 * Exception thrown when an invalid or unsupported parking lot administrator
 * type is requested or configured.
 */
public class IllegalParkingLotAdminException extends Exception {
    public IllegalParkingLotAdminException(String message) {
        super(message);
    }
}
