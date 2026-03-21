package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IVehicleRepositoryImpl implements IVehicleRepository{
    List<Vehicle> vehicles = new ArrayList<>();

    public IVehicleRepositoryImpl() {
        load();
    }

    @Override
    public boolean rentVehicle(String id) {
        for(Vehicle v : vehicles) {
            if(v.getId().equals(id) && !v.isRented()){
                v.setRented(true);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for(Vehicle v : vehicles) {
            if(id.equals(v.getId()) && v.isRented()){
                v.setRented(false);
                save();
                return true; //udało się zwrócić pojazd
            }
        }
        return false; //nie udało się zwrócić pojazdu
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copiedVehicles = new ArrayList<>();
        for(Vehicle v : vehicles) {
            copiedVehicles.add(v.cloneVehicle());
        }
        return copiedVehicles;
    }

    @Override
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("vehicles.csv"));){
            for(Vehicle v : vehicles){
                bw.write(v.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.csv"));){
            String line;
            while((line = br.readLine()) != null) {
                Vehicle vehicle = Vehicle.fromCSVLine(line);
                if(vehicle != null) {
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vehicle getVehicle(String id){
        for(Vehicle v : vehicles) {
            if(v.getId().equals(id)){
                return v;
            }
        }
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle){
        if(vehicle == null){
            return false;
        }
        vehicles.add(vehicle);
        save();
        return true;
    }

    @Override
    public boolean remove(String id) {
        if(getVehicle(id) == null || getVehicle(id).isRented()) {
            return false;
        }
        vehicles.remove(getVehicle(id));
        save();
        return true;
    }
}
