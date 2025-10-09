package pl.carrental.model;

public enum ClientType {
    TYPE1(1, 0.10),
    TYPE2(2, 0.20),
    TYPE3(3, 0.30);

    private final int maxVehicles;
    private final double discount;

    ClientType(int maxVehicles, double discount){
        this.maxVehicles = maxVehicles;
        this.discount = discount;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public double getDiscount() {
        return discount;
    }
}
