package org.example;

import org.example.models.*;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.VehicleJsonRepository;
import org.example.repositories.impl.UserJsonRepository;
import org.example.services.AuthService;
import org.example.services.Service;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VehicleJsonRepository vehicleRepository = new VehicleJsonRepository();
        UserJsonRepository userRepository = new UserJsonRepository();
        RentalJsonRepository rentalRepository = new RentalJsonRepository();
        AuthService authService = new AuthService(userRepository);
        Service service = new Service(rentalRepository, vehicleRepository);
        Scanner scanner = new Scanner(System.in);

        User user = null;

        while(user == null) {
            System.out.println("""
                    Do you want to log in? (y/n)\s
                    If you do not have an account press (r) to register.
                    """);
            String option = scanner.nextLine();

            if (option.equalsIgnoreCase("y")) {
                // Logging in
                while (user == null) {
                    System.out.print("Enter login: ");
                    String login = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    Optional<User> optionalUser = authService.login(login, password);
                    if(optionalUser.isEmpty()) {
                        System.out.println("Invalid login or password. Try again. \n");
                    } else {
                        user = optionalUser.get();
                    }
                }
                System.out.println("\n Hello, " + user.getLogin() + ". You have been successfully logged in.");
            } else if (option.equalsIgnoreCase("n")) {
                return;
            } else if (option.equalsIgnoreCase("r")) {
                // Register
                System.out.print("Enter a login: ");
                String newUserLogin = scanner.nextLine();
                System.out.print("Enter a password: ");
                String newUserPassword = scanner.nextLine();

                if(authService.register(newUserLogin, newUserPassword, Role.USER)) {
                    System.out.println("Account created successfully.");
                } else {
                    System.out.println("This login is already taken!");
                }
            }
        }

        if(user.getRole() == Role.USER) {
            runUserMenu(scanner, service, user);
        } else if(user.getRole() == Role.ADMIN) {
            runAdminMenu(scanner, service);
        }

        scanner.close();
    }

    public static void runUserMenu(Scanner scanner, Service service, User user) {
        int input = 0;
        while(input != 5) {
            System.out.print("""
                    \n-----------Options------------\s
                    If you wish to browse the car rental catalog press (1)\s
                    If you wish to rent a vehicle without browsing the catalog press (2)\s
                    If you wish to view the vehicles you rented press (3)\s
                    If you wish to return a vehicle you rented press (4)\s
                    If you wish to log out press (5)
                    """);
            System.out.println("------------------------------\n");

            input = scanner.nextInt();
            scanner.nextLine();
            List<Vehicle> catalog = service.getAvailableVehicles();
            List<Vehicle> userVehicles = service.getRentedVehiclesByUser(user.getId());

            // Viewing the catalog
            if (input == 1) {
                System.out.println("\n------Car rental catalog------");
                for (int i = 0; i < catalog.size(); ++i) {
                    System.out.println("Vehicle no. " + (i + 1) + ": " + catalog.get(i).toString());
                }
                System.out.println("------------------------------\n");

            // Renting a vehicle
            } else if (input == 2) {
                System.out.println("Enter the no. of the vehicle you are interested in.");
                int number = scanner.nextInt();
                scanner.nextLine();
                if (number > 0 && number <= catalog.size()) {
                    String vehicleId = catalog.get(number - 1).getId();

                    if (service.rentVehicle(user.getId(), vehicleId)) {
                        System.out.println("You have successfully rented the vehicle.");
                    }
                } else {
                    System.out.println("Vehicle id invalid or vehicle is already rented.");
                }

            // Viewing rented vehicles
            } else if (input == 3) {
                if (userVehicles.isEmpty()) {
                    System.out.println("You haven't rented any vehicles.");
                } else {
                    System.out.println("\n------Your rented vehicles------");
                    for (int i = 0; i < userVehicles.size(); ++i) {
                        System.out.println("Vehicle no. " + (i + 1) + ": " + userVehicles.get(i).toString());
                    }
                    System.out.println("--------------------------------\n");
                }

            // Returning a vehicle
            }else if (input == 4) {
                if(userVehicles.isEmpty()) {
                    System.out.println("You haven't rented any vehicles to return.");
                } else {
                    System.out.println("Select the no. of the vehicle you want to return:");
                    int number = scanner.nextInt();
                    scanner.nextLine();

                    if(number > 0 && number <= userVehicles.size()) {
                        String vehicleId = userVehicles.get(number - 1).getId();
                        if(service.returnVehicle(user.getId(), vehicleId)) {
                            System.out.println("You have successfully returned the vehicle.");
                        } else {
                            System.out.println("Return failed.");
                        }
                    } else {
                        System.out.println("Invalid number.");
                    }
                }
            } else if (input < 1 || input > 5) {
                System.out.println("You entered an invalid number.");
            }
        }

        System.out.println("You have been successfully logged out.");
    }

    public static void runAdminMenu(Scanner scanner, Service service) {
        int input = 0;
        while(input != 4){
            System.out.print("""
                    \n----------Options-------------\s
                    To add a new vehicle press (1)\s
                    To remove a vehicle press (2)\s
                    To browse all vehicles press (3)\s
                    To log out press (4)
                    """);
            System.out.println("------------------------------\n");

            input = scanner.nextInt();
            scanner.nextLine();
            List<Vehicle> catalog = service.getAllVehicles();

            // Adding a new vehicle
            if(input == 1) {
                boolean checkType = false;
                String type = "";
                // Forcing the admin to enter the correct vehicle type
                while(!checkType) {
                    System.out.print("Enter the type of the vehicle: ");
                    type = scanner.nextLine();
                    if(type.equalsIgnoreCase("CAR") || type.equalsIgnoreCase("MOTORCYCLE") || type.equalsIgnoreCase("BUS")) {
                        checkType = true;
                    }
                }

                System.out.print("Enter the brand of the vehicle: ");
                String brand = scanner.nextLine();
                System.out.print("Enter the model: ");
                String model = scanner.next();
                scanner.nextLine();
                System.out.print("Enter the year:");
                int year = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter the licence plate: ");
                String plate = scanner.nextLine();
                System.out.print("Enter the renting price:");
                double price = scanner.nextDouble();
                scanner.nextLine();

                Vehicle vehicle = Vehicle.builder()
                        .category(type)
                        .brand(brand)
                        .model(model)
                        .year(year)
                        .plate(plate)
                        .price(price)
                        .build();

                System.out.println("Do you want to add any attributes? (y/n)");
                String answer = scanner.nextLine();
                if(answer.equalsIgnoreCase("y")) {
                    System.out.print("How many attributed do you want to add? ");
                    int number = scanner.nextInt();
                    scanner.nextLine();
                    for(int i = 0; i < number && number < 10; ++i) {
                        System.out.print("Enter the attribute nr " + (i + 1) + " key: ");
                        String key = scanner.nextLine();
                        System.out.print("Enter the attribute nr " + (i + 1) + " value: ");
                        String value = scanner.nextLine();
                        vehicle.addAttribute(key, value);
                    }
                }

                if(service.addVehicle(vehicle) != null) {
                    System.out.println("You have successfully added the vehicle.");
                } else {
                    System.out.println("Warning! The vehicle wasn't added!");
                }

            // Removing a vehicle
            } else if(input == 2) {
                for (int i = 0; i < catalog.size(); ++i) {
                    System.out.println("Vehicle no. " + (i + 1) + ": " + catalog.get(i).toString());
                }
                System.out.print("Enter the no. of the vehicle you want to remove: ");
                int number = scanner.nextInt();
                scanner.nextLine();
                if(service.removeVehicle(catalog.get(number - 1).getId())) {
                    System.out.println("You have successfully removed the vehicle.");
                } else {
                    System.out.println("Warning! The vehicle wasn't removed!");
                }

            // Viewing the vehicles
            } else if(input == 3) {
                System.out.println("\n------Car rental catalog------");
                for (Vehicle v : catalog) {
                    System.out.println(v.toString());
                }
                System.out.println("------------------------------\n");

            } else if(input <= 0 || input > 4) {
                System.out.println("You entered an invalid number.");
            }
        }
        System.out.println("You have been successfully logged out.");
    }
}