import parking.exception.DoubleParkingException;
import parking.exception.IllegalParkingLotAdminException;
import parking.exception.IllegalSpotTypeException;
import parking.exception.ParkingUnavailableException;
import parking.lot.ParkingLot;
import parking.enums.ParkingLotAdminType;
import parking.enums.VehicleType;
import parking.domain.ParkingSpot;

import java.util.Scanner;

public class ParkingLotApplication {

    enum RunningMode {
        LIVE, TEST
    }

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 4) {
            System.err.println("Usage: java ParkingLotApplication <numOfRows> <rowSequence> [<parkingLotAdminType>] [<LIVE|TEST>]");
            System.exit(1);
        }

        int numOfRows;
        try {
            numOfRows = Integer.parseInt(args[0]);
            if (numOfRows <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: <numOfRows> must be a positive integer.");
            System.err.println("Parking lot creation failed.");
            System.exit(1);
            return;
        }

        String rowSequence = args[1].trim();
        if (rowSequence.isEmpty()) {
            System.err.println("Error: <rowSequence> must be a non-empty string.");
            System.err.println("Parking lot creation failed.");
            System.exit(1);
        }

        ParkingLotAdminType adminType = ParkingLotAdminType.COMPACT_REGULAR;
        if (args.length >= 3) {
            try {
                adminType = ParkingLotAdminType.valueOf(args[2].trim());
            } catch (IllegalArgumentException e) {
                adminType = ParkingLotAdminType.UNSUPPORTED_TYPE;
            }
        }

        RunningMode mode = RunningMode.TEST;
        if (args.length == 4) {
            try {
                mode = RunningMode.valueOf(args[3].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Error: <LIVE|TEST> must be specified as the fourth argument.");
                System.exit(1);
            }
        }

        try {
            ParkingLot lot = new ParkingLot(numOfRows, rowSequence, adminType);
            System.out.println("Parking lot created with total spots: " + lot.getParkingLotSize());
            System.out.println("Printing initial lot summary:");
            System.out.println();
            lot.printLotSummary();

            if (mode == RunningMode.LIVE) {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.println("\nSelect an option:");
                    System.out.println("1. Print Lot Summary");
                    System.out.println("2. Park Vehicle");
                    System.out.println("3. Remove Vehicle");
                    System.out.println("4. Exit");
                    System.out.print("Enter choice: ");
                    String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1" -> lot.printLotSummary();
                        case "2" -> {
                            System.out.print("Enter vehicle identifier: ");
                            String id = scanner.nextLine().trim();
                            System.out.print("Enter vehicle type (CAR, MOTORCYCLE, VAN): ");
                            String typeStr = scanner.nextLine().trim().toUpperCase();
                            try {
                                VehicleType type = VehicleType.valueOf(typeStr);
                                ParkingSpot spot = lot.parkVehicle(id, type);
                                System.err.println("Vehicle parked at spot: " + spot.getParkingSpotId());
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid vehicle type.");
                            } catch (DoubleParkingException | ParkingUnavailableException e) {
                                System.err.println("Error: " + e.getMessage());
                            }
                        }
                        case "3" -> {
                            System.out.print("Enter vehicle identifier to remove: ");
                            String id = scanner.nextLine().trim();
                            lot.removeVehicle(id);
                            System.out.println("Vehicle removed (if present).");
                        }
                        case "4" -> {
                            System.out.println("Exiting application.");
                            System.exit(0);
                        }
                        default -> System.out.println("Invalid option. Please try again.");
                    }
                }
            }
        } catch (IllegalSpotTypeException | IllegalParkingLotAdminException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Parking lot creation failed.");
            System.exit(1);
        }
    }
}
