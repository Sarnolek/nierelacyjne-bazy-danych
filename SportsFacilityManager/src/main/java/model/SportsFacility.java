package model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@BsonDiscriminator(key = "_t")
public abstract class SportsFacility {

    @BsonId
    private UUID id;

    @BsonProperty("name")
    private String name;

    @BsonProperty("price_per_hour")
    private double pricePerHour;

    @BsonProperty("capacity")
    private int capacity;

    @BsonProperty("is_rented")
    private int isRented;

    public SportsFacility(){}

    public SportsFacility(String name,
                          double pricePerHour,
                          int capacity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.capacity = capacity;
        this.isRented = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getIsRented() {
        return isRented;
    }

    public void setIsRented(int isRented) {
        this.isRented = isRented;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + name + '\'' +
                ", pricePerHour=" + pricePerHour + '\'' +
                ", capacity='" + capacity +
                ", isRented=" + isRented +
                '}';
    }
}
