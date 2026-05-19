package org.example.services.impl;

import jakarta.transaction.Transactional;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.example.services.RentalServiceInterface;
import org.example.services.UserServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RentalServiceInterface rentalService;

    public UserService(UserRepository userRepository, RentalServiceInterface rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public User findById(String userId) {
        for(User u : userRepository.findAll()) {
            if(u.getId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String toDeleteUserId, String loggedInUserId) {
        if(userRepository.findById(toDeleteUserId).isEmpty()) {
            throw new IllegalArgumentException("Użytkownik o podanym ID nie istnieje.");
        }
        if(toDeleteUserId.equals(loggedInUserId)) {
            throw new IllegalStateException("Nie możesz usunąć samego siebie gdy jesteś zalogowany/na.");
        }
        if(rentalService.findActiveRentalByUserId(toDeleteUserId).isPresent()) {
            throw new IllegalStateException("Nie można usunąć użytkownika, ponieważ ma wypożyczony pojazd.");
        }
        userRepository.deleteById(toDeleteUserId);
    }
}
