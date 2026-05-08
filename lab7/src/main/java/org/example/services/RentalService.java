package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public List<Rental> findUserRentals(String userId) {
        List<Rental> rentals = new ArrayList<>();
        for(Rental r: rentalRepository.findAll()) {
            if(r.getUserId().equals(userId)) {
                rentals.add(r);
            }
        }
        return rentals;
    }

    public void rentVehicle(String userId, String vehicleId) {
        if (vehicleRepository.findById(vehicleId).isEmpty()) {
            throw new IllegalArgumentException("Pojazd o podanym ID nie istnieje.");
        }

        if (rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            throw new IllegalStateException("Ten pojazd jest już aktualnie wypożyczony.");
        }

        Rental rental = Rental.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .rentDateTime(LocalDateTime.now().toString())
                .build();
        rentalRepository.save(rental);

    }


    public void returnVehicle(String userId) {
        Optional<Rental> activeRental = findActiveRentalByUserId(userId);
        if(activeRental.isPresent()) {
            Rental rental = activeRental.get();
            rental.setReturnDateTime(LocalDateTime.now().toString());
            rentalRepository.save(rental);
        } else {
            throw new IllegalStateException("Nie masz aktualnie wypożyczonego pojazdu.");
        }
    }

    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(rental -> rental.getUserId().equals(userId) & rental.isActive())
                .findFirst();
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }
}
