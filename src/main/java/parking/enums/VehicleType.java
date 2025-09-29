package parking.enums;

/**
 * Enum representing the types of vehicles supported by the parking lot system.
 */
public enum VehicleType {
    /**
     * Motorcycle, which prefers compact spots but can also use regular spots.
     */
    MOTORCYCLE,

    /**
     * Car, which can only use regular spots.
     */
    CAR,

    /**
     * Van, which requires two adjacent regular spots in order to park.
     */
    VAN
}
