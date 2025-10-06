package org.example.carrental.model;

public class Car extends Vehicle{
    private int seats;

    // konstruktor bezargumentowy?

    public Car(Long id, String make, String model, int year, int seats){
        super(id, make, model, year);
        this.seats = seats;
    }

    public int getSeats(){
        return seats;
    }

    public void setSeats(int seats){
        this.seats = seats;
    }

    //  toString
}
