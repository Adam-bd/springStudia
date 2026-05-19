package org.example.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("json")
public class RentalJsonRepository implements RentalRepository {
    List<Rental> rentals = new ArrayList<>();
    private final JsonFileStorage<Rental> storage;

    public RentalJsonRepository(@Value("${carrent.json.rentals-file}") String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<Rental>>() {}.getType());
        this.rentals = new ArrayList<>(storage.load());
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> copy = new ArrayList<>();
        for(Rental r : rentals) {
            copy.add(r.copy());
        }
        return copy;
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentals.stream()
                .filter(rental -> rental.getId().equals(id))
                .findFirst()
                .map(Rental::copy);
    }

    @Override
    public Rental save(Rental rental) {
        if(rental == null) {
            return null;
        }
        Rental toSave = rental.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
        } else {
            rentals.removeIf(r -> r.getId().equals(toSave.getId()));
        }
        rentals.add(toSave);
        storage.save(rentals);
        return toSave.copy();
    }

    @Override
    public void deleteById(String id) {
        rentals.removeIf(rental -> rental.getId().equals(id));
        storage.save(rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(rental -> rental.getVehicleId().equals(vehicleId))
                .filter(Rental::isActive)
                .findFirst()
                .map(Rental::copy);
    }
}
