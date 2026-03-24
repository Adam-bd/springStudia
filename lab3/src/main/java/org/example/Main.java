package org.example;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IVehicleRepositoryImpl vehicleRepository = new IVehicleRepositoryImpl();
        UserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);
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

                    user = authentication.authenticate(login, password); //authenticating the login
                    if (user == null) {
                        System.out.println("Invalid login or password. Try again. \n");
                    }
                }
                System.out.println("\n Hello, " + user.getLogin() + ". You have been successfully logged in.");
            } else if (option.equalsIgnoreCase("n")) {
                return;
            } else if (option.equalsIgnoreCase("r")) {
                System.out.print("Enter a login: ");
                String newUserLogin = scanner.nextLine();
                System.out.print("Enter a password: ");
                String newUserPassword = scanner.nextLine();

                User newUser = new User(newUserLogin, Authentication.hashPassword(newUserPassword), Role.USER, null);
                if (userRepository.addUser(newUser)) {
                    System.out.println("Account created successfully.");
                    user = newUser;
                } else {
                    System.out.println("This login is already taken!");
                }
            }
        }

        if(user.getRole() == Role.USER) {
            runUserMenu(scanner, vehicleRepository, userRepository, user);
        } else if(user.getRole() == Role.ADMIN) {
            runAdminMenu(scanner, vehicleRepository, userRepository);
        }

        scanner.close();
    }

    public static void runUserMenu(Scanner scanner, IVehicleRepositoryImpl vehicleRepository, UserRepository userRepository, User user) {
        int input = 0;
        while(input != 5) {
            System.out.print("""
                    \n-----------Options------------\s
                    If you wish to browse the car rental catalog press (1)\s
                    If you wish to rent a vehicle without browsing the catalog press (2)\s
                    If you wish to return a vehicle you rented press (3)\s
                    If you wish to see your account details press (4)\s
                    If you wish to log out press (5)
                    """);
            System.out.println("------------------------------\n");

            input = scanner.nextInt();

            // Viewing the catalog
            if (input == 1) {
                System.out.println("\n------Car rental catalog------");
                List<Vehicle> catalog = vehicleRepository.getVehicles();
                for (Vehicle v : catalog) {
                    System.out.println(v.toString());
                }
                System.out.println("------------------------------\n");

            // Renting a vehicle
            } else if (input == 2) {
                if(user.getRentedVehicleId() == null) {
                    System.out.println("Enter the id of the vehicle you are interested in.");
                    String id = scanner.next();
                    boolean vehicle = vehicleRepository.rentVehicle(id);
                    if (vehicle) {
                        user.setRentedVehicleId(id);
                        userRepository.update(user);
                        System.out.println("You have successfully rented the vehicle.");
                    } else {
                        System.out.println("Vehicle id invalid or vehicle is already rented.");
                    }
                } else {
                    System.out.println("You already rented out a vehicle. You can rent only one vehicle at a time.");
                }

            // Returning a vehicle
            } else if (input == 3) {
                if(user.getRentedVehicleId() != null) {
                    System.out.println("Enter the id of the vehicle you want to return.");
                    String id = scanner.next();
                    if(user.getRentedVehicleId().equals(id)) {
                        boolean isReturned = vehicleRepository.returnVehicle(id);
                        if (isReturned) {
                            user.setRentedVehicleId(null);
                            userRepository.update(user);
                            System.out.println("You have successfully returned the vehicle.");
                        } else {
                            System.out.println("Return failed. Vehicle id is invalid or it wasn't rented.");
                        }
                    } else {
                        System.out.println("The vehicle id doesn't match the id of the vehicle you rented out.");
                    }
                } else {
                    System.out.println("You haven't rented out a vehicle.");
                }

            // Account details (what car the user rented)
            } else if(input == 4) {
                if(user.getRentedVehicleId() != null) {
                    System.out.println(user.toStringUserAccountDetails() + vehicleRepository.getVehicle(user.getRentedVehicleId()).toStringVehicleAccountDetails());
                } else {
                    System.out.println(user.toStringUserAccountDetails());
                }

            } else if (input <= 0 || input > 5) {
                System.out.println("You entered an invalid number.");
            }
        }

        System.out.println("You have been successfully logged out.");
    }

    public static void runAdminMenu(Scanner scanner, IVehicleRepositoryImpl vehicleRepository, UserRepository userRepository) {
        int input = 0;
        while(input != 6){
            System.out.print("""
                    \n----------Options-------------\s
                    To add a new vehicle press (1)\s
                    To remove a vehicle press (2)\s
                    To browse all vehicles press (3)\s
                    To browse all users and their vehicles press (4)\s
                    To delete a user press (5)\s
                    To log out press (6)
                    """);
            System.out.println("------------------------------\n");

            input = scanner.nextInt();

            // Adding a new vehicle
            if(input == 1) {
                boolean checkType = false;
                String type = "";
                // Forcing the admin to enter the correct vehicle type
                while(!checkType) {
                    System.out.print("Enter the type of the vehicle: ");
                    type = scanner.next();
                    if(type.equalsIgnoreCase("CAR") || type.equalsIgnoreCase("MOTORCYCLE")) {
                        checkType = true;
                    }
                }

                System.out.print("Enter the brand of the vehicle: ");
                String brand = scanner.nextLine();
                System.out.print("Enter the model: ");
                String model = scanner.nextLine();
                System.out.print("Enter the year:");
                int year = scanner.nextInt();
                System.out.print("Enter the renting price:");
                double price = scanner.nextDouble();

                // Adding the car
                if(type.equalsIgnoreCase("CAR")){
                    if(vehicleRepository.add(new Car(brand, model, year, price, false))) {
                        System.out.println("You have successfully added the vehicle.");
                    } else {
                        System.out.println("Warning! The vehicle wasn't added!");
                    }
                } else if(type.equalsIgnoreCase("MOTORCYCLE")){ // Adding the motorcycle
                    System.out.print("Enter the required driving licence category: ");
                    String category = scanner.next();
                    if(vehicleRepository.add(new Motorcycle(brand, model, year, price, false, MotorcycleCategory.valueOf(category.toUpperCase())))) {
                        System.out.println("You have successfully added the vehicle.");
                    } else {
                        System.out.println("Warning! The vehicle wasn't added!");
                    }
                }

            // Removing a vehicle
            } else if(input == 2) {
                System.out.print("Enter the id of the vehicle you want to remove: ");
                String id = scanner.next();
                if(vehicleRepository.remove(id)) {
                    System.out.println("You have successfully removed the vehicle.");
                } else {
                    System.out.println("Warning! The vehicle wasn't removed!");
                }

            // Viewing the vehicles
            } else if(input == 3) {
                System.out.println("\n------Car rental catalog------");
                List<Vehicle> catalog = vehicleRepository.getVehicles();
                for (Vehicle v : catalog) {
                    System.out.println(v.toString());
                }
                System.out.println("------------------------------\n");

            // Viewing the users + vehicles they rented
            } else if(input == 4) {
                System.out.println("\n---Users and the vehicles they rented---");
                List<User> userList = userRepository.getUsers();
                for(User u : userList) {
                    String string = "";
                    if(u.getRentedVehicleId() != null) {
                        string = " " + vehicleRepository.getVehicle(u.getRentedVehicleId()).toString();
                    }
                    System.out.println(u + string);

                }
                System.out.println("------------------------------------------\n");

            // Deleting a user
            } else if(input == 5) {
                System.out.println("Enter the login of the user you want to delete.");
                String userToBeDeletedLogin = scanner.next();
                if(userRepository.deleteUser(userToBeDeletedLogin)) {
                    System.out.println("User has been deleted successfully.");
                } else {
                    System.out.println("Invalid login or user is renting a vehicle!");
                }

            } else if(input <= 0 || input > 6) {
                System.out.println("You entered an invalid number.");
            }
        }
        System.out.println("You have been successfully logged out.");
    }
}