package parking.lot;

import parking.domain.ParkingLotSummary;
import parking.domain.ParkingSpot;
import parking.enums.ParkingSpotType;
import parking.enums.VehicleType;
import parking.exception.DoubleParkingException;
import parking.exception.ParkingUnavailableException;

import java.util.List;
import java.util.Map;

public interface ParkingLotAdmin {
    List<ParkingSpot> parkVehicle(String identifier, VehicleType vehicleType) throws DoubleParkingException, ParkingUnavailableException;
    void removeVehicle(String identifier);
    Map<ParkingSpotType, List<ParkingSpot>> getSpotsByType();
    Map<String, List<ParkingSpot>> getVehicleSpotsMap();
    void printLotSummary();
    ParkingLotSummary generateLotSummary();
}