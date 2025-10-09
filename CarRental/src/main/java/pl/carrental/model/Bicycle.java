package pl.carrental.model;

public class Bicycle extends Vehicle{
private String type;

    public Bicycle(Long id, String plateNumber, String make, String model, int year, String colour, boolean isRented, double dailyPrice, String type) {
        super(id, plateNumber, make, model, year, colour, isRented, dailyPrice);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
