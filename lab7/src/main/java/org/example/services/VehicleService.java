package org.example.services;

import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.util.ArrayList;
import java.util.List;

public class VehicleService {

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;


    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);
        return vehicle;
    }

    public void removeVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        boolean rented = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        if(rented) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }
        vehicleRepository.deleteById(vehicleId);
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public List<Vehicle> findAvailableVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        for(Vehicle v: vehicleRepository.findAll()) {
            if(rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty()) {
                vehicles.add(v);
            }
        }
        return vehicles;
    }

    public Vehicle findById(String vehicleId) {
        if(vehicleRepository.findById(vehicleId).isPresent()) {
            return vehicleRepository.findById(vehicleId).get();
        }
        return null;
    }
}
