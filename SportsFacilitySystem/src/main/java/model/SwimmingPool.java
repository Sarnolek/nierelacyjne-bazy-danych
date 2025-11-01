package model;

public class SwimmingPool extends SportsFacility{
    private int poolLength;
    private int numberOfLanes;

    public SwimmingPool(String name, double pricePerHour, int capacity, int poolLength, int numberOfLanes) {
        super(name, pricePerHour, capacity);
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
