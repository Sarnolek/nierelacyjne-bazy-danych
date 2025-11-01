package model;

import java.util.UUID;

// Na PAS bedzie abstrakcyjny User, a dziewdziczyć będzie Admin, FacilityManager, Client
public class Client {
    private UUID id;
    private String firstName;
    private String lastName;
    // boolean isActive;
    // jeszcze login

    public Client() {
    }

    public Client(String firstName, String lastName) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
