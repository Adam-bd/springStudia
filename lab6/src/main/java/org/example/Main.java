package org.example;

import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.*;
import org.example.services.*;

public class Main {
    public static void main(String[] args) {
        String mode = "json";
        if(args.length > 0) {
            mode = args[0].toLowerCase();
        }

        VehicleRepository vehicleRepository;
        UserRepository userRepository;
        RentalRepository rentalRepository;
        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();

        if(mode.equals("jdbc")) {
            System.out.println("--- Uruchamianie w trybie BAZY DANYCH (JDBC) ---");
            vehicleRepository = new VehicleJdbcRepository();
            userRepository = new UserJdbcRepository();
            rentalRepository = new RentalJdbcRepository();
        } else {
            System.out.println("--- Uruchamianie w trybie PLIKÓW (JSON) ---");
            vehicleRepository = new VehicleJsonRepository();
            userRepository = new UserJsonRepository();
            rentalRepository = new RentalJsonRepository();
        }

        VehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
        AuthService authService = new AuthService(userRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);
        VehicleService vehicleService = new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
        RentalService rentalService = new RentalService(rentalRepository, vehicleRepository);
        UserService userService = new UserService(userRepository, rentalService);

        UI ui = new UI(
                authService,
                vehicleService,
                rentalService,
                userService,
                categoryConfigService
        );

        ui.start();
    }
}