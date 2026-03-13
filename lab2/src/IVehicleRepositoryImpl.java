import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IVehicleRepositoryImpl implements IVehicleRepository{
    List<Vehicle> vehicles = new ArrayList<>();
    private String currentFileName;

    public IVehicleRepositoryImpl() {
        this.currentFileName = "vehicles.txt";
        load(this.currentFileName);
    }

    @Override
    public void save(){
        save(this.currentFileName);
    }

    @Override
    public Vehicle rentVehicle(String id) {
        for(Vehicle v : vehicles) {
            if(v.getId().equals(id) && !v.isRented()){
                v.setRented(true);
                save();
                return v;
            }
        }
        return null;
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
    public void save(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));){
            for(Vehicle v : vehicles){
                bw.write(v.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName));){
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
}
