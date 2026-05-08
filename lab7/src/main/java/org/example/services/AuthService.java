package org.example.services;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> optionalUser = userRepository.findByLogin(login);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Checking if password is correct
            if(BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        // Wrong login or password
        return Optional.empty();
    }

    public boolean register(String login, String password) {
        // A username with this login exists
        if(userRepository.findByLogin(login).isPresent()) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = User.builder()
                .login(login)
                .passwordHash(hashedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return true; // Account created successfully
    }
}
