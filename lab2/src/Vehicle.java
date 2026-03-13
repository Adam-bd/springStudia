import java.util.Objects;
import java.util.UUID;

public abstract class Vehicle {
    private final String id;
    private static int counter = 0;
    private String brand;
    private String model;
    private int year;
    private double price;
    private boolean rented;

    public Vehicle(String brand, String model, int year, double price, boolean rented) {
        counter++;
        this.id = "" + counter;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }

    public Vehicle(String id, String brand, String model, int year, double price, boolean rented) {
        this.id = id;
        counter = Math.max(counter, Integer.parseInt(id));
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }

    public Vehicle(Vehicle other) {
        this.id = other.id;
        this.brand = other.brand;
        this.model = other.model;
        this.year = other.year;
        this.price = other.price;
        this.rented = other.rented;
    }

    public abstract Vehicle cloneVehicle();

    public abstract String getType();

    public String toCSV() {
        return getType() + ";" + id + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented;
    }

    public static Vehicle fromCSVLine(String line) {
        String[] data = line.split(";");
        return switch (data[0]) {
            case "CAR" -> new Car(data[1], data[2], data[3], Integer.parseInt(data[4]), Double.parseDouble(data[5]), Boolean.parseBoolean(data[6]));
            case "MOTORCYCLE" -> new Motorcycle(data[1], data[2], data[3], Integer.parseInt(data[4]), Double.parseDouble(data[5]), Boolean.parseBoolean(data[6]), data[7]);
            default -> null;
        };
    }

    @Override
    public String toString() {
        String str = " is available.";
        if(rented) str = " isn't available.";

        return "ID " + id + ": " + brand + " " + model + " from " + year + ". The renting price is " + price + " PLN. This " + getType().toLowerCase() + str;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public String getId(){
        return id;
    }
}
