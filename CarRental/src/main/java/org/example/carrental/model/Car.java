package org.example.carrental.model;

public class Car extends Vehicle{
    private int seats;

    public Car(Long id, String plateNumber, String make, String model, int year, String colour, boolean isRented, double dailyPrice, int seats) {
        super(id, plateNumber, make, model, year, colour, isRented, dailyPrice);
        this.seats = seats;
    }

    public int getSeats(){
        return seats;
    }

    public void setSeats(int seats){
        this.seats = seats;
    }

}
