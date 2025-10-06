package org.example.carrental.model;

public class Truck extends Vehicle{
    private double loadCapacityKg;

    // k. bezarg.?
    Truck(Long id, String make, String model, int year, double getLoadCapacityKg){
        super(id, make, model, year);
        this.loadCapacityKg = getLoadCapacityKg;
    }

    public double getLoadCapacityKg() {
        return loadCapacityKg;
    }

    public void setLoadCapacityKg(double loadCapacityKg){
        this.loadCapacityKg = loadCapacityKg;
    }

    // toString
}
