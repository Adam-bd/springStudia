package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VehicleJsonRepository implements VehicleRepository {
    private List<Vehicle> vehicles = new ArrayList<>();
    private final JsonFileStorage<Vehicle> storage = new JsonFileStorage<>("vehicles.json", new TypeToken<List<Vehicle>>() {}.getType());

    public VehicleJsonRepository() {
        this.vehicles = new ArrayList<>(storage.load());
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            copy.add(vehicle.copy());
        }
        return copy;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.getId().equals(id))
                .findFirst()
                .map(Vehicle::copy);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle cannot be null");
        }
        Vehicle toSave = vehicle.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
        } else {
            vehicles.removeIf(v -> v.getId().equals(toSave.getId()));
        }
        vehicles.add(toSave);
        storage.save(vehicles);
        return toSave.copy();
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(vehicle ->vehicle.getId().equals(id));
        storage.save(vehicles);
    }


}
