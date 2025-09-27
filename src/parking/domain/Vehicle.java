package parking.domain;

import parking.enums.VehicleType;

public class Vehicle {
    private final String identifier;
    private final VehicleType type;

    public Vehicle(String identifier, VehicleType type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public VehicleType getType() {
        return type;
    }
}
