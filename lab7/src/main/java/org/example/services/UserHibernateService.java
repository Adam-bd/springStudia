package org.example.services;

import org.example.HibernateConfig;
import org.example.models.User;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.UserHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserHibernateService implements UserServiceInterface{
    private final RentalHibernateRepository rentalRepo;
    private final UserHibernateRepository userRepo;

    public UserHibernateService(RentalHibernateRepository rentalRepo, UserHibernateRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<User> findAllUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return userRepo.findAll();
        }
    }

    @Override
    public User findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return userRepo.findById(id).orElse(null);
        }
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            if (id.equals(loggedUserId)) {
                throw new IllegalStateException("Users cannot delete themselves");
            }

            User user = userRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            boolean hasActiveRentals = rentalRepo.findAll().stream()
                    .anyMatch(rental -> id.equals(rental.getUserId()) && rental.isActive());

            if (hasActiveRentals) {
                throw new IllegalStateException("Cannot delete user with active rentals");
            }

            userRepo.deleteById(user.getId());
            tx.commit();
        } catch (Exception e) {
            try {
                rollback(tx);
            } catch (Exception ex) {
                // ignore
            }
            throw new RuntimeException("Error occurred while deleting user", e);
        }
    }

    private void setSession(Session session) {
        rentalRepo.setSession(session);
        userRepo.setSession(session);
    }

    private void rollback(Transaction tx) {
        if(tx != null &&  tx.isActive()) {
            tx.rollback();
        }
    }
}
