public class Motorcycle extends Vehicle {
    private String category; //driving licence

    public Motorcycle(String brand, String model, int year, double price, boolean rented, String category) {
        super(brand, model, year, price, rented);
        this.category = category;
    }

    public Motorcycle(String id, String brand, String model, int year, double price, boolean rented, String category) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
