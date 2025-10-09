package pl.carrental.client;

import jakarta.persistence.*;
import pl.carrental.service.Rental;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(name = "client_id", unique = true, nullable = false, updatable = false)
    private Long clientId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    @Column(name = "balance", nullable = false)
    private double balance;

    @OneToMany(
            mappedBy = "client", // Wskazuje na pole 'client' w klasie Rental
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Rental>rents = new ArrayList<>();

    public Client(Long clientId, String firstName, String lastName, String email, ClientType clientType, double balance){
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.clientType = clientType;
        this.balance = balance;
    }

    // Konstruktor bezargumentowy wymagany przez JPA.
    public Client() {
    }

    public Long getId() {
        return id;
    }

    public Long getClientId() {
        return clientId;
    }

//    public void setId(Long clientId) {
//        this.clientId = clientId;
//    }

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
