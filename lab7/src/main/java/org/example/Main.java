package org.example;

import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.*;
import org.example.services.*;

public class Main {
    public static void main(String[] args) {
        // Domyślnie ustawiamy hibernate dla wygody testowania (możesz zmienić z powrotem na json)
        String mode = "hibernate";
        if(args.length > 0) {
            mode = args[0].toLowerCase();
        }

        // 1. Inicjalizacja rzeczy niezależnych od trybu (np. konfiguracja kategorii aut)
        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();
        VehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);

        // 2. Deklarujemy zmienne dla serwisów korzystając z INTERFEJSÓW!
        AuthServiceInterface authService;
        VehicleServiceInterface vehicleService;
        RentalServiceInterface rentalService;
        UserServiceInterface userService;

        // 3. W zależności od trybu tworzymy odpowiednie Repozytoria i Serwisy
        if(mode.equals("hibernate")) {
            System.out.println("--- Uruchamianie w trybie HIBERNATE ---");

            // Repozytoria
            RentalHibernateRepository rentalRepo = new RentalHibernateRepository();
            VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository();
            UserHibernateRepository userRepo = new UserHibernateRepository();

            // Serwisy
            authService = new AuthHibernateService(userRepo);
            rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);
            userService = new UserHibernateService(rentalRepo, userRepo);
            vehicleService = new VehicleHibernateService(vehicleRepo, userRepo, rentalRepo);

        } else if(mode.equals("jdbc")) {
            System.out.println("--- Uruchamianie w trybie BAZY DANYCH (JDBC) ---");

            VehicleRepository vehicleRepo = new VehicleJdbcRepository();
            UserRepository userRepo = new UserJdbcRepository();
            RentalRepository rentalRepo = new RentalJdbcRepository();

            authService = new AuthService(userRepo);
            rentalService = new RentalService(rentalRepo, vehicleRepo);
            userService = new UserService(userRepo, rentalService);
            vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);

        } else {
            System.out.println("--- Uruchamianie w trybie PLIKÓW (JSON) ---");

            VehicleRepository vehicleRepo = new VehicleJsonRepository();
            UserRepository userRepo = new UserJsonRepository();
            RentalRepository rentalRepo = new RentalJsonRepository();

            authService = new AuthService(userRepo);
            rentalService = new RentalService(rentalRepo, vehicleRepo);
            userService = new UserService(userRepo, rentalService);
            vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        }

        // 4. Uruchamiamy interfejs użytkownika
        // UI przyjmuje interfejsy, więc nie obchodzi go, co zrobiliśmy w instrukcji if wyżej!
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