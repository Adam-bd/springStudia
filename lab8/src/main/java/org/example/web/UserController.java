package org.example.web;

import org.example.models.User;
import org.example.services.UserServiceInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    public final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable String id) {
        User user = userService.findById(id);
        if(user == null) {
            throw new IllegalArgumentException("Użytkownik o podanym ID nie istnieje.");
        }
        return user;
    }

}
