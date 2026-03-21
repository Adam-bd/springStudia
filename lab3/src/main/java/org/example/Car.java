package org.example;

public class Car extends  Vehicle{

    public Car(String brand, String model, int year, double price, boolean rented) {
        super(brand, model, year, price, rented);
    }

    public Car(String id, String brand, String model, int year, double price, boolean rented) {
        super(id, brand, model, year, price, rented);
    }

    public Car(Car other) {
        super(other);
    }

    @Override
    public String getType(){
        return "CAR";
    }

    @Override
    public Vehicle cloneVehicle(){
        return new Car(this);
    }
}
