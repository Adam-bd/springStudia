package org.example.repositories.impl;

import org.example.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RentalJdbcRepository implements RentalRepository {
    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Rental rental = Rental.builder()
                        .id(rs.getString("id"))
                        .vehicleId(rs.getString("vehicle_id"))
                        .userId(rs.getString("user_id"))
                        .rentDateTime(rs.getString("rent_date"))
                        .returnDateTime(rs.getString("return_date"))
                        .build();
                rentals.add(rental);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }

        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental WHERE id = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    Rental rental = Rental.builder()
                            .id(rs.getString("id"))
                            .vehicleId(rs.getString("vehicle_id"))
                            .userId(rs.getString("user_id"))
                            .rentDateTime(rs.getString("rent_date"))
                            .returnDateTime(rs.getString("return_date"))
                            .build();
                    return Optional.of(rental);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rental by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if(rental == null) {
            throw new IllegalArgumentException("Rental can't be null!");
        }

        Rental toSave = rental.copy();
        if(toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
        }

        String sql = """
                INSERT INTO rental (id, vehicle_id, user_id, rent_date, Return_date)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    vehicle_id = EXCLUDED.vehicle_id,
                    user_id = EXCLUDED.user_id,
                    rent_date = EXCLUDED.rent_date,
                    return_date = EXCLUDED.return_date;
                """;

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, toSave.getId());
            statement.setString(2, toSave.getVehicleId());
            statement.setString(3, toSave.getUserId());
            statement.setString(4, toSave.getRentDateTime());
            statement.setString(5, toSave.getReturnDateTime());

            statement.executeUpdate();
            return toSave.copy();
        } catch(SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE from rental WHERE id = ?";
        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental by id: " + id, e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
       String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental WHERE vehicle_id = ? AND return_date IS NULL";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, vehicleId);

            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    Rental rental = Rental.builder()
                            .id(rs.getString("id"))
                            .vehicleId(rs.getString("vehicle_id"))
                            .userId(rs.getString("user_id"))
                            .rentDateTime(rs.getString("rent_date"))
                            .returnDateTime(rs.getString("return_date"))
                            .build();
                    return Optional.of(rental);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rental without a return date by vehicle id: " + vehicleId, e);
        }
        return Optional.empty();
    }

}
