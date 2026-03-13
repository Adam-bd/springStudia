import java.util.List;

public interface IVehicleRepository {
    Vehicle rentVehicle(String id);
    boolean returnVehicle(String id);
    List<Vehicle> getVehicles();

    void save();

    void save(String fileName);

    void load(String fileName);
}
