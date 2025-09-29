package parking.domain;

import parking.enums.VehicleType;

/**
 * Represents a vehicle that can be parked in the parking lot.
 * Each vehicle has a unique identifier and a type.
 */
public class Vehicle {
    private final String identifier;
    private final VehicleType type;

    /**
     * Constructs a Vehicle with the given identifier and type.
     *
     * @param identifier the unique identifier for the vehicle (e.g., license plate)
     * @param type the type of the vehicle
     */
    public Vehicle(String identifier, VehicleType type) {
        this.identifier = identifier;
        this.type = type;
    }

    /**
     * Returns the unique identifier of the vehicle.
     *
     * @return the vehicle identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the type of the vehicle.
     *
     * @return the vehicle type
     */
    public VehicleType getType() {
        return type;
    }
}
