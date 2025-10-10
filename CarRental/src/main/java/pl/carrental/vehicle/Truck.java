package pl.carrental.vehicle;

import jakarta.persistence.*;


@Entity
@DiscriminatorValue("TRUCK")
public class Truck extends Vehicle {

    @Column(name = "load_capacity_kg")
    private double loadCapacityKg;

    public Truck(Long vehicleId, String plateNumber, String make, String model, int year, String colour, double dailyPrice, double loadCapacityKg) {
        super(vehicleId, plateNumber, make, model, year, colour, dailyPrice);
        this.loadCapacityKg = getLoadCapacityKg();
    }

    public Truck() {
        super();
    }

    public double getLoadCapacityKg() {
        return loadCapacityKg;
    }

    public void setLoadCapacityKg(double loadCapacityKg){
        this.loadCapacityKg = loadCapacityKg;
    }

}
