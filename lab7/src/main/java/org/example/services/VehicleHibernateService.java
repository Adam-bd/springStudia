package org.example.services;

import org.example.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.UserHibernateRepository;
import org.example.repositories.impl.VehicleHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class VehicleHibernateService implements VehicleServiceInterface {
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;
    private final RentalHibernateRepository rentalRepo;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepo, UserHibernateRepository userRepo, RentalHibernateRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findAll();
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            List<Vehicle> allVehicles = vehicleRepo.findAll();
            return allVehicles.stream()
                    .filter(vehicle -> rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isEmpty())
                    .toList();
        }
    }

    @Override
    public Vehicle findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findById(id).orElse(null);
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);
            Vehicle savedVehicle = vehicleRepo.save(vehicle);
            tx.commit();
            return savedVehicle;
        } catch (Exception e) {
            rollback(tx);
            throw e;
        }
    }

    @Override
    public void removeVehicle(String vehicleId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);
            vehicleRepo.deleteById(vehicleId);
            tx.commit();
        } catch (Exception e) {
            rollback(tx);
            throw e;
        }
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
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
