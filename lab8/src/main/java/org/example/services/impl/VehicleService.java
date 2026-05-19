package org.example.services.impl;

import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.VehicleServiceInterface;
import org.example.services.VehicleValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class VehicleService implements VehicleServiceInterface {

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

    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> rentalRepository
                        .findByVehicleIdAndReturnDateIsNull(v.getId())
                        .isEmpty())
                .toList();
    }

    public Vehicle findById(String vehicleId) {
        if(vehicleRepository.findById(vehicleId).isPresent()) {
            return vehicleRepository.findById(vehicleId).get();
        }
        return null;
    }
}
