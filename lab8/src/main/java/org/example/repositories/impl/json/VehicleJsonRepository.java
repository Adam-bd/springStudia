package org.example.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("json")
public class VehicleJsonRepository implements VehicleRepository {
    private List<Vehicle> vehicles;
    private final JsonFileStorage<Vehicle> storage;
    public VehicleJsonRepository(@Value("${carrent.json.vehicles-file}") String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<Vehicle>>() {}.getType());
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
