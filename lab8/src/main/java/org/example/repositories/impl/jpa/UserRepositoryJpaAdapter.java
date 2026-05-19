package org.example.repositories.impl.jpa;

import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class UserRepositoryJpaAdapter implements UserRepository {
    UserJpaRepository delegate;

    public UserRepositoryJpaAdapter(UserJpaRepository delegate) {
        this.delegate = delegate;
    }


    @Override
    public List<User> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return delegate.findByLogin(login);
    }

    @Override
    public User save(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        return delegate.save(user);
    }

    @Override
    public void deleteById(String id) {
        delegate.deleteById(id);
    }
}
