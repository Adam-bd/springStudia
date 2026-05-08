package org.example.repositories.impl;

import org.example.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VehicleHibernateRepository implements VehicleRepository {

    @Override
    public List<Vehicle> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM Vehicle", Vehicle.class).list();
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Vehicle vehicle = session.get(Vehicle.class, id);
            return Optional.ofNullable(vehicle);
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Vehicle toSave = vehicle.copy();
            if (toSave.getId() == null || toSave.getId().isBlank()) {
                toSave.setId(generateNextAvailableId(session));
            }

            Vehicle savedVehicle = session.merge(toSave);
            transaction.commit();
            return savedVehicle;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error occurred while saving vehicle in Hibernate", e);
        }
    }

    @Override
    public void deleteById(String id) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Vehicle vehicle = session.get(Vehicle.class, id);
            if (vehicle != null) {
                session.remove(vehicle);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error occurred while deleting vehicle by id: " + id, e);
        }
    }

    private String generateNextAvailableId(Session session) {
        List<String> stringIds = session.createQuery("SELECT v.id FROM Vehicle v", String.class).list();
        List<Integer> numericIds = new ArrayList<>();

        for (String idStr : stringIds) {
            try {
                numericIds.add(Integer.parseInt(idStr));
            } catch (NumberFormatException ignored) {}
        }

        Collections.sort(numericIds);
        int nextId = 1;
        for (Integer id : numericIds) {
            if (id == nextId) nextId++;
            else if (id > nextId) break;
        }
        return String.valueOf(nextId);
    }
}
