package org.example.services;

import org.example.HibernateConfig;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.impl.UserHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthHibernateService implements AuthServiceInterface {
    private UserHibernateRepository userRepo;

    public AuthHibernateService(UserHibernateRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean register(String login, String rawPassword) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            if (userRepo.findByLogin(login).isPresent()) {
                return false; // User with this login already exists
            }

            String passwordHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            User newUser = User.builder()
                    .login(login)
                    .passwordHash(passwordHash)
                    .role(Role.USER)
                    .build();

            userRepo.save(newUser);
            tx.commit();
            return true;
        } catch (Exception e) {
            try {
                rollback(tx);
            } catch (Exception ex) {
                // ignore
            }
            return false;
        }
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            Optional<User> userOpt = userRepo.findByLogin(login);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        }
    }


    private void setSession(Session session) {
        userRepo.setSession(session);
    }

    private void rollback(Transaction tx) {
        if(tx != null &&  tx.isActive()) {
            tx.rollback();
        }
    }
}
