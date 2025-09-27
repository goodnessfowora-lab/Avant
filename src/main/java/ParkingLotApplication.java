import parking.lot.ParkingLot;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class ParkingLotApplication {
    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(4, "REGULAR, REGULAR, COMPACT");
        System.out.println("Parking lot created with total spots: " + lot.getParkingLotSize());
        System.out.println();
        lot.printLotSummary();
    }
}