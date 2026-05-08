package org.example.repositories.impl;

import org.hibernate.query.Query;
import org.example.HibernateConfig;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserHibernateRepository implements UserRepository {

    @Override
    public List<User> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    @Override
    public Optional<User> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User toSave = user.copy();
            if (toSave.getId() == null || toSave.getId().isBlank()) {
                toSave.setId(UUID.randomUUID().toString());
            }

            User savedUser = session.merge(toSave);
            transaction.commit();
            return savedUser;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error occurred while saving user in Hibernate", e);
        }
    }

    @Override
    public void deleteById(String id) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error occurred while deleting user by id: " + id, e);
        }
    }
}
