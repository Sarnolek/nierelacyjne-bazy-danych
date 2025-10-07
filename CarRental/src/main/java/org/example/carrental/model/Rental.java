package org.example.carrental.model;

import java.time.LocalDateTime;

public class Rental {
    private Long id;
    private Client client;
    private Vehicle vehicle;
    private int durationInDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double rentalPrice;

    public Rental(Long id, Client client, Vehicle vehicle, int durationInDays){
        this.id = id;
        this.client = client;
        this.vehicle = vehicle;
        this.durationInDays = durationInDays;
        this.startDate = LocalDateTime.now();
        this.endDate = this.startDate.plusDays(durationInDays);
        this.rentalPrice = calculateRentalPrice(durationInDays);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
