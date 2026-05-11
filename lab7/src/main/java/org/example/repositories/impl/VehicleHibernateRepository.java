package org.example.repositories.impl;

import lombok.Setter;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Setter
public class VehicleHibernateRepository implements VehicleRepository {

    private Session session;

    @Override
    public List<Vehicle> findAll() {
        return session.createQuery("FROM Vehicle", Vehicle.class).list();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(session.get(Vehicle.class, id));
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        Vehicle toSave = vehicle.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(generateNextAvailableId(this.session));
        }
        return session.merge(toSave);
    }

    @Override
    public void deleteById(String id) {
        Vehicle vehicle = session.get(Vehicle.class, id);

        if (vehicle != null) {
            session.remove(vehicle);
        }
    }

    private String generateNextAvailableId(Session session) {
        List<String> stringIds = session.createQuery("SELECT v.id FROM Vehicle v", String.class).list();
        List<Integer> numericIds = new ArrayList<>();

        for (String idStr : stringIds) {
            try {
                numericIds.add(Integer.parseInt(idStr));
            } catch (NumberFormatException ignored) {}
        }

        Collections.sort(numericIds);
        int nextId = 1;
        for (Integer id : numericIds) {
            if (id == nextId) nextId++;
            else if (id > nextId) break;
        }
        return String.valueOf(nextId);
    }
}
