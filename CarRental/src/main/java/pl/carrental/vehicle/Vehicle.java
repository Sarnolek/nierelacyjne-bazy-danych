package pl.carrental.vehicle;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import pl.carrental.service.Rental;

import java.util.*;


@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type")
public abstract class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(name = "vehicle_id",length = 50, nullable = false,  unique = true, updatable = false)
    private Long vehicleId;

    @Column(name = "plate_number", unique = true, nullable = false)
    @NotBlank
    private String plateNumber;

    @Column(name = "make", nullable = false)
    @NotBlank
    private String make;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(nullable = false)
    private String colour;

    @Column(name = "is_rented", nullable = false)
    private boolean isRented;

    @Column(name = "daily_price", nullable = false)
    private double dailyPrice;

    @OneToMany(
            mappedBy = "vehicle",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Rental> rents = new ArrayList<>();

    public Vehicle() {
    }

    public Vehicle(Long vehicleId, String plateNumber, String make, String model, int year, String colour, double dailyPrice){
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.colour = colour;
        this.isRented = false;
        this.dailyPrice = dailyPrice;
    }

    public Long getId() {
        return id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public List<Rental> getRentals() {
        return rents;
    }

    public void setRentals(List<Rental> rentals) {
        this.rents = rentals;
    }

    public void addRental(Rental rental){
        this.rents.add(rental);
        rental.setVehicle(this);
    }

    public void removeRental(Rental rental){
        this.rents.remove(rental);
        rental.setVehicle(null);
    }

}
