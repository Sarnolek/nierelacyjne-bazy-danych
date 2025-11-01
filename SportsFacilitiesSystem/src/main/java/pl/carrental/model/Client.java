package pl.carrental.model;

import java.util.ArrayList;
import java.util.List;


public class Client {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private ClientType clientType;
    private double balance;
    private List<Rental> rents = new ArrayList<>();

    public Client(Long id, String firstName, String lastName, String email, ClientType clientType, double balance){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.clientType = clientType;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Rental> getRents() {
        return rents;
    }

    public void setRents(List<Rental> rents) {
        this.rents = rents;
    }

    public boolean hasEnoughBalance(double rentalPrice){
  return this.balance >= rentalPrice;
    }

    public void addToBalance(double moneyAmount){
        this.balance += moneyAmount;
    }

    public int getAmountOfRents(){
        return rents.size();
    }

    public boolean hasSlotForRent(){
        return getAmountOfRents() < clientType.getMaxVehicles();
    }

    public void addRent(Rental rent){
        rents.add(rent);
    }

    public void removeRent(Rental rent){
        rents.remove(rent);
    }

}
