package org.example.services;

import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Service {
    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;

    public Service(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // Dla admina
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public boolean removeVehicle(String vehicleId) {
        if(rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            return false;
        }
        if(vehicleRepository.findById(vehicleId).isPresent()) {
            vehicleRepository.deleteById(vehicleId);
            return true;
        }

        return false;
    }

    // Dla usera
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(vehicle -> rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isEmpty())
                .collect(Collectors.toList());
    }

    public boolean rentVehicle(String userId, String vehicleId) {
        if(vehicleRepository.findById(vehicleId).isPresent() && rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isEmpty()) {
            Rental rental = Rental.builder()
                    .userId(userId)
                    .vehicleId(vehicleId)
                    .rentDateTime(LocalDateTime.now().toString())
                    .build();
            rentalRepository.save(rental);
            return true;
        }
        return false;
    }

    public boolean returnVehicle(String userId, String vehicleId) {
        var activeRental = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);

        if(activeRental.isPresent() && activeRental.get().getUserId().equals(userId)) {
            Rental rental = activeRental.get();
            rental.setReturnDateTime(LocalDateTime.now().toString());
            rentalRepository.save(rental);
            return true;
        }
        return false;
    }
}
