package pl.carrental.vehicle;


import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Bicycle")
public class Bicycle extends Vehicle {

@Column(name = "bicycle_type")
private String type;

    public Bicycle() {
        super();
    }

    public Bicycle(Long vehicleId, String plateNumber, String make, String model, int year, String colour, boolean isRented, double dailyPrice, String type) {
        super(vehicleId, plateNumber, make, model, year, colour, isRented, dailyPrice);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
