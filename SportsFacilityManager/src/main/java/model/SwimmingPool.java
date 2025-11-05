package model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class SwimmingPool extends SportsFacility{

    @BsonProperty("pool_length")
    private int poolLength;
    @BsonProperty("number_of_lines")
    private int numberOfLanes;

//    public SwimmingPool() { super();
//    }

    public SwimmingPool(String name,
                        double pricePerHour,
                        int capacity,
                        int poolLength,
                        int numberOfLanes) {
        super(name, pricePerHour, capacity);
        this.poolLength = poolLength;
        this.numberOfLanes = numberOfLanes;
    }

    @BsonCreator
    public SwimmingPool(@BsonProperty("pool_length") int poolLength, @BsonProperty("number_of_lines") int numberOfLanes) {
        super();
        this.poolLength = poolLength;
        this.numberOfLanes = numberOfLanes;
    }

    public int getPoolLength() {
        return poolLength;
    }

    public void setPoolLength(int poolLength) {
        this.poolLength = poolLength;
    }

    public int getNumberOfLanes() {
        return numberOfLanes;
    }

    public void setNumberOfLanes(int numberOfLanes) {
        this.numberOfLanes = numberOfLanes;
    }

    @Override
    public String toString() {
        return "SwimmingPool{" +
                super.toString() +
                ", poolLength=" + poolLength +
                ", numberOfLanes=" + numberOfLanes +
                '}';
    }
}
