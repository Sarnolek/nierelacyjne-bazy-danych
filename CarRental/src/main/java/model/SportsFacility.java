package model;

import java.util.UUID;

public abstract class SportsFacility {
    private UUID id;
    private String name;
    private double pricePerHour;
    private int capacity;

    public SportsFacility(String name, double pricePerHour, int capacity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.capacity = capacity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + name + '\'' +
                ", pricePerHour=" + pricePerHour + '\'' +
                ", capacity='" + capacity +
                '}';
    }
}
