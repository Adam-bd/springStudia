package org.example.services;

import org.example.models.User;
import org.example.models.Vehicle;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.example.HibernateConfig;
import org.example.models.Rental;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.UserHibernateRepository;
import org.example.repositories.impl.VehicleHibernateRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalHibernateService implements RentalServiceInterface {
    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;

    public RentalHibernateService(RentalHibernateRepository rentalRepo,
                                  VehicleHibernateRepository vehicleRepo,
                                  UserHibernateRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            setSession(session);

            boolean userHasActiveRental = rentalRepo.findAll().stream()
                    .anyMatch(rental -> userId.equals(rental.getUserId()) && rental.isActive());

            if (userHasActiveRental) {
                throw new IllegalStateException("User already has an active rental");
            }

            Vehicle vehicle = vehicleRepo.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            boolean vehicleIsRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent();
            if (vehicleIsRented) {
                throw new IllegalStateException("Vehicle is already rented");
            }

            Rental rental = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .vehicle(vehicle)
                    .user(user)
                    .rentDateTime(LocalDateTime.now().toString())
                    .returnDateTime(null)
                    .build();

            Rental savedRental = rentalRepo.save(rental);
            tx.commit();
            return savedRental;

        } catch (Exception e) {
            try {
                rollback(tx);
            } catch (Exception ex) {
                // ignore
            }
            throw new RuntimeException("Error occurred while renting vehicle", e);
        }
    }

    @Override
    public Rental returnVehicle(String userId) {
        Transaction tx = null;

        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            setSession(session);

            Rental activeRental = rentalRepo.findAll().stream()
                    .filter(rental -> userId.equals(rental.getUserId()) && rental.isActive())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No active rental found for user"));

            activeRental.setReturnDateTime(LocalDateTime.now().toString());
            Rental savedRental = rentalRepo.save(activeRental);
            tx.commit();
            return savedRental;

        } catch (Exception e) {
            try {
                rollback(tx);
            } catch (Exception ex) {
                // ignore
            }
            throw new RuntimeException("Error occurred while returning vehicle", e);
        }
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepo.findAll().stream()
                    .filter(rental -> userId.equals(rental.getUserId()) && rental.isActive())
                    .findFirst();
        }
    }

    @Override
    public List<Rental> findAllRentals() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepo.findAll();
        }
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepo.findAll().stream()
                    .filter(rental -> userId.equals(rental.getUserId()))
                    .toList();
        }
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepo.setSession(session);
        vehicleRepo.setSession(session);
        userRepo.setSession(session);
    }

    private void rollback(Transaction tx) {
        if(tx != null &&  tx.isActive()) {
            tx.rollback();
        }
    }
}
