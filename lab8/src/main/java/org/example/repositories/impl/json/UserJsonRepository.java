package org.example.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("json")
public class UserJsonRepository implements org.example.repositories.UserRepository {
    private List<User> users;
    private final JsonFileStorage<User> storage;
    public UserJsonRepository(@Value("${carrent.json.users-file}") String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<User>>() {}.getType());
        this.users = new ArrayList<>(storage.load());
    }


    @Override
    public List<User> findAll() {
        List<User> copy = new ArrayList<>();
        for (User user : users) {
            copy.add(user.copy());
        }
        return copy;
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        User toSave = user.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
        } else {
            users.removeIf(v -> v.getId().equals(toSave.getId()));
        }
        users.add(toSave);
        storage.save(users);
        return toSave.copy();
    }

    @Override
    public void deleteById(String id) {
        users.removeIf(user -> user.getId().equals(id));
        storage.save(users);
    }
}
