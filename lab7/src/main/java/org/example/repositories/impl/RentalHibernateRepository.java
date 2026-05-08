package org.example.repositories.impl;

import org.example.HibernateConfig;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalHibernateRepository implements RentalRepository {

    @Override
    public List<Rental> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM Rental", Rental.class).list();
        }
    }

    @Override
    public Optional<Rental> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Rental rental = session.get(Rental.class, id);
            return Optional.ofNullable(rental);
        }
    }

    @Override
    public Rental save(Rental rental) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Rental toSave = rental.copy();
            if (toSave.getId() == null || toSave.getId().isBlank()) {
                toSave.setId(UUID.randomUUID().toString());
            }

            Rental savedRental = session.merge(toSave);
            transaction.commit();
            return savedRental;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error occurred while saving rental in Hibernate", e);
        }
    }

    @Override
    public void deleteById(String id) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Rental rental = session.get(Rental.class, id);
            if (rental != null) {
                session.remove(rental);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error occurred while deleting rental by id: " + id, e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            String hql = "FROM Rental r WHERE r.vehicle.id = :vId AND r.returnDateTime IS NULL";
            Query<Rental> query = session.createQuery(hql, Rental.class);
            query.setParameter("vId", vehicleId);
            return query.uniqueResultOptional();
        }
    }
}
