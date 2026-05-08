package org.example.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.JdbcConnectionManager;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VehicleJdbcRepository implements VehicleRepository {

    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        }

        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle WHERE id = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicle by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if(vehicle == null) {
            throw new IllegalArgumentException("Vehicle can't be null!");
        }

        Vehicle toSave = vehicle.copy();
        if(toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(generateNextAvailableId());
        }

        String sql = """
            INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)
            ON CONFLICT (id) DO UPDATE SET
                category = EXCLUDED.category,
                brand = EXCLUDED.brand,
                model = EXCLUDED.model,
                year = EXCLUDED.year,
                plate = EXCLUDED.plate,
                price = EXCLUDED.price,
                attributes = EXCLUDED.attributes;
            """;

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, toSave.getId());
            statement.setString(2, toSave.getCategory());
            statement.setString(3, toSave.getBrand());
            statement.setString(4, toSave.getModel());
            statement.setInt(5, toSave.getYear());
            statement.setString(6, toSave.getPlate());
            statement.setDouble(7, toSave.getPrice());

            // Zamiana mapy atrybutów na JSON String
            statement.setString(8, gson.toJson(
                    vehicle.getAttributes() != null ? vehicle.getAttributes() : new HashMap<>()
            ));

            statement.executeUpdate();
            return toSave.copy();
        } catch(SQLException e) {
            throw new RuntimeException("Error occurred while saving vehicle" , e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE from vehicle WHERE id = ?";
        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting vehicle by id: " + id, e);
        }
    }
    private Vehicle mapRow(ResultSet rs) throws SQLException {
        String attrJson = rs.getString("attributes");
        Map<String, Object> attributes = gson.fromJson(attrJson, mapType);

        return Vehicle.builder()
                .id(rs.getString("id"))
                .category(rs.getString("category"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .year(rs.getInt("year"))
                .plate(rs.getString("plate"))
                .price(rs.getDouble("price"))
                .attributes(attributes != null ? attributes : new HashMap<>())
                .build();
    }

    private String generateNextAvailableId() {
        String sql = "SELECT id FROM vehicle";
        List<Integer> existingIds = new ArrayList<>();

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String idStr = rs.getString("id");
                try {
                    existingIds.add(Integer.parseInt(idStr));
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while generating vehicle id", e);
        }
        Collections.sort(existingIds);

        int nextId = 1;
        for (Integer id : existingIds) {
            if (id == nextId) {
                nextId++;
            } else if (id > nextId) {
                break;
            }
        }
        return String.valueOf(nextId);
    }
}