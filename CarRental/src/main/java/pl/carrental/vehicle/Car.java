package pl.carrental.vehicle;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {

    @Column(name = "seats")
    private int seats;

    public Car(Long vehicleId, String plateNumber, String make, String model, int year, String colour, boolean isRented, double dailyPrice, int seats) {
        super(vehicleId, plateNumber, make, model, year, colour, isRented, dailyPrice);
        this.seats = seats;
    }

    public Car() {
        super();
    }

    public int getSeats(){
        return seats;
    }

    public void setSeats(int seats){
        this.seats = seats;
    }

}
