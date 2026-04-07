package org.example;

import org.example.models.User;
import org.example.repositories.UserRepository;
import org.example.repositories.impl.UserJsonRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    @Test
    void shouldAuthenticateUserWithCorrectLoginAndPassword() {
        UserRepository userRepository = new UserJsonRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("admin", "admin123");

        assertNotNull(user);
        assertEquals("admin", user.getLogin());
    }

    @Test
    void shouldNotAuthenticateUserWithWrongPassword() {
        UserRepository userRepository = new UserJsonRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("admin", "zlehaslo");

        assertNull(user);
    }

    @Test
    void shouldNotAuthenticateNonExistingUser() {
        UserRepository userRepository = new UserJsonRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("brak", "admin123");

        assertNull(user);
    }

    @Test
    void hashPasswordShouldReturnSameHashForSameInput() {
        String hash1 = Authentication.hashPassword("admin123");
        String hash2 = Authentication.hashPassword("admin123");

        assertEquals(hash1, hash2);
    }
}