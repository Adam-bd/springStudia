package org.example.services.impl;

import jakarta.transaction.Transactional;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.RentalServiceInterface;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {
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

    @Override
    public boolean userHasActiveRental(String userId) {
        return false;
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        if (vehicleRepository.findById(vehicleId).isEmpty()) {
            throw new IllegalArgumentException("Pojazd o podanym ID nie istnieje.");
        }

        if (rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            throw new IllegalStateException("Ten pojazd jest już aktualnie wypożyczony.");
        }

        Rental rental = Rental.builder()
                .user(User.builder().id(userId).build())
                .vehicle(Vehicle.builder().id(vehicleId).build())
                .rentDateTime(LocalDateTime.now().toString())
                .build();
        return rentalRepository.save(rental);

    }


    public Rental returnVehicle(String userId) {
        Optional<Rental> activeRental = findActiveRentalByUserId(userId);
        if(activeRental.isPresent()) {
            Rental rental = activeRental.get();
            rental.setReturnDateTime(LocalDateTime.now().toString());
            return rentalRepository.save(rental);
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
