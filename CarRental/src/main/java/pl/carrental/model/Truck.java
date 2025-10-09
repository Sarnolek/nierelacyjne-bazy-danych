package pl.carrental.model;

public class Truck extends Vehicle{
    private double loadCapacityKg;

    public Truck(Long id, String plateNumber, String make, String model, int year, String colour, boolean isRented, double dailyPrice, double loadCapacityKg) {
        super(id, plateNumber, make, model, year, colour, isRented, dailyPrice);
        this.loadCapacityKg = getLoadCapacityKg();
    }

    public double getLoadCapacityKg() {
        return loadCapacityKg;
    }

    public void setLoadCapacityKg(double loadCapacityKg){
        this.loadCapacityKg = loadCapacityKg;
    }

}
