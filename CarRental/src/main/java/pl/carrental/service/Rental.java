package pl.carrental.service;

import jakarta.persistence.*;
import pl.carrental.vehicle.Vehicle;
import pl.carrental.client.Client;


import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rental_id", unique = true, nullable = false)
    private Long rentalId;

    // W klasie Rental.java
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "duration_in_days", nullable = false)
    private int durationInDays;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "rental_price", nullable = false)
    private double rentalPrice;

    public Rental() {
    }

    public Rental(Long rentalId, Client client, Vehicle vehicle, int durationInDays){
        this.rentalId = rentalId;
        this.client = client;
        this.vehicle = vehicle;
        this.durationInDays = durationInDays;
        this.startDate = LocalDateTime.now();
        this.endDate = this.startDate.plusDays(durationInDays);
        this.rentalPrice = calculateRentalPrice(durationInDays);
    }

    public Long getId() {
        return rentalId;
    }
    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public void finishRent(){
        this.endDate = LocalDateTime.now();
    }

    public boolean isActive(){
        return endDate == null;
    }

    public double calculateRentalPrice(double durationInDays){
        return vehicle.getDailyPrice() * durationInDays * ( 1 - client.getClientType().getDiscount());
    }

    // metoda przedluzenia wypozyczenia
    // metoda pozostalego czasu wypozyczenia

}
