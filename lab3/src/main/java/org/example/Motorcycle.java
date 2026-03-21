package org.example;

public class Motorcycle extends Vehicle {
    private MotorcycleCategory category; //driving licence

    public Motorcycle(String brand, String model, int year, double price, boolean rented, MotorcycleCategory category) {
        super(brand, model, year, price, rented);
        this.category = category;
    }

    public Motorcycle(String id, String brand, String model, int year, double price, boolean rented, MotorcycleCategory category) {
        super(id, brand, model, year, price, rented);
        this.category = category;
    }

    public Motorcycle(Motorcycle other) {
        super(other);
        this.category = other.category;
    }

    @Override
    public String getType(){
        return "MOTORCYCLE";
    }

    @Override
    public Vehicle cloneVehicle() {
        return new Motorcycle(this);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ";" + category;
    }

    @Override
    public String toString(){
        return super.toString() + " Attention! Driving licence category needed to rent this motorcycle: " + category;
    }

    public MotorcycleCategory getCategory() {
        return category;
    }

    public void setCategory(MotorcycleCategory category) {
        this.category = category;
    }
}
