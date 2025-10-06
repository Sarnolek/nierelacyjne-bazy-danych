package org.example.carrental.model;

public abstract class Vehicle {
    private Long id;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status = VehicleStatus.AVAILABLE;
    // field version ?
    // konstruktor bezargumentowy ?

    public Vehicle(Long id, String make, String model, int year){
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.status = VehicleStatus.AVAILABLE;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake(){
        return make;
    }

    public void setMake(String make){
        this.make = make;
    }

    public String getModel(){
        return model;
    }

    public void setModel(String model){
        this.model = model;
    }

    public int getYear(){
        return year;
    }

    public void setYear(int year){
        this.year = year;
    }

    public VehicleStatus getStatus(){
        return status;
    }

    public void setStatus(VehicleStatus status){
        this.status = status;
    }

    public boolean isAvailable(){
        return status == VehicleStatus.AVAILABLE;
    }

    public void markRented(){
        this.status = VehicleStatus.RENTED;
    }

    public void markAvailable(){
        this.status = VehicleStatus.AVAILABLE;
    }

    // toString
}
