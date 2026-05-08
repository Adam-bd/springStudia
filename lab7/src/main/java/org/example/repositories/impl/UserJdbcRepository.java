package org.example.repositories.impl;

import org.example.JdbcConnectionManager;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserJdbcRepository implements UserRepository {

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, login, password_hash, role FROM users";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()) {

            while(rs.next()) {
                User user = User.builder()
                        .id(rs.getString("id"))
                        .login(rs.getString("login"))
                        .passwordHash(rs.getString("password_hash"))
                        .role(Role.valueOf(rs.getString("role")))
                        .build();
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT id, login, password_hash, role FROM users WHERE id = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    User user = User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password_hash"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding user by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT id, login, password_hash, role FROM users WHERE login = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    User user = User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password_hash"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding user by login: " + login, e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if(user == null) {
            throw new IllegalArgumentException("User can't be null!");
        }

        User toSave = user.copy();
        if(toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
        }

        String sql = """
                INSERT INTO users (id, login, password_hash, role)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    login = EXCLUDED.login,
                    password_hash = EXCLUDED.password_hash,
                    role = EXCLUDED.role;
                """;

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, toSave.getId());
            statement.setString(2, toSave.getLogin());
            statement.setString(3, toSave.getPasswordHash());
            statement.setString(4, toSave.getRole().toString());
            statement.executeUpdate();
            return toSave.copy();
        } catch(SQLException e) {
            throw new RuntimeException("Error occurred while saving users", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE from users WHERE id = ?";
        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting user by id: " + id, e);
        }
    }
}
