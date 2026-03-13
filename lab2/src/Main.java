import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IVehicleRepositoryImpl repository = new IVehicleRepositoryImpl();
        Scanner scanner = new Scanner(System.in);

        int input = 0;
        while(input != 4){
            System.out.print("""
                \n----------Options-------------\s
                If you wish to browse the car rental catalog press. (1)\s
                If you wish to rent a vehicle without browsing the catalog press.(2)\s
                If you wish to return a vehicle you rented press (3).\s
                If you wish to leave press (4)
                """);
            System.out.println("------------------------------\n");

            input = scanner.nextInt();
            if(input == 1) {
                System.out.println("\n------Car rental catalog------");
                List<Vehicle> catalog = repository.getVehicles();
                for(Vehicle v : catalog) {
                    System.out.println(v.toString());
                }
                System.out.println("------------------------------\n");
            } else if(input == 2) {
                System.out.println("Enter the id of the vehicle you are interested in.");
                String id = scanner.next();
                Vehicle vehicle = repository.rentVehicle(id);
                if(vehicle != null) {
                    System.out.println("You have successfully rented the vehicle.");
                } else {
                    System.out.println("Vehicle id invalid or vehicle is already rented.");
                }
            } else if(input == 3) {
                System.out.println("Enter the id of the vehicle you want to return.");
                String id = scanner.next();
                boolean isReturned = repository.returnVehicle(id);
                if(isReturned){
                    System.out.println("You have successfully returned the vehicle.");
                } else {
                    System.out.println("Return failed. Vehicle id is invalid or it wasn't rented.");
                }

            } else if(input <= 0 || input > 4){
                System.out.println("You entered an invalid number.");
            }
        }
    }
}